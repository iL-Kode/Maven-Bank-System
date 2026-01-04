package se.liu.ida.tdp024.account.data.impl.db.facade;

import java.util.List;
import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.ValidationCheckFacade;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.impl.db.entity.AccountDB;
import se.liu.ida.tdp024.account.data.impl.db.entity.TransactionDB;
import se.liu.ida.tdp024.account.data.impl.db.util.EMF;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.LockModeType;

import java.util.ArrayList;

public class AccountEntityFacadeDB implements AccountEntityFacade {

    private TransactionEntityFacade transactionEntityFacade = new TransactionEntityFacadeDB();

    public Account getAccount(long accountId, EntityManager em) {
        return em.find(AccountDB.class, accountId);
    }

    @Override
    public Boolean isAccountInDb(long accountId) {
        EntityManager em = EMF.getEntityManager();
        Account account = getAccount(accountId, em);
        return account != null;
    }

    @Override
    public long createDbAccount(String accountType, String personKey, String bankKey) {
        EntityManager em = EMF.getEntityManager();
        em.getTransaction().begin();

        AccountDB acc = new AccountDB();
        acc.setPersonKey(personKey);
        acc.setAccountType(accountType);
        acc.setBankKey(bankKey);
        acc.setHoldings(0);

        em.persist(acc);
        em.getTransaction().commit();

        return acc.getId();
    }

    @Override
    public List<Account> getDbAccounts(String personKey) {

        EntityManager em = EMF.getEntityManager();

        TypedQuery<Account> query = em.createQuery("SELECT a FROM AccountDB a WHERE a.personKey=:personKey",
                Account.class);
        query.setParameter("personKey", personKey);
        return query.getResultList();
    }

    @Override
    public int readHoldings(long accountId, EntityManager em) {
        AccountDB account = em.find(AccountDB.class, accountId);
        return account.getHoldings();
    }

    @Override
    public Boolean writeHoldings(long accountId, int amount, ValidationCheckFacade vfc, int debitLimit) {

        EntityManager em = EMF.getEntityManager();
        em.getTransaction().begin();

        try {
            AccountDB account = em.find(AccountDB.class, accountId, LockModeType.PESSIMISTIC_WRITE); // Acquires a lock on the data

            Boolean isValid = vfc.isValidTransaction(accountId, amount, em);
            String type;
            if (amount > debitLimit) {
                type = "CREDIT";
            } else {
                type = "DEBIT";
            }

            if (isValid) {
                account.setHoldings(account.getHoldings() + amount);
                em.merge(account);

                Transaction transaction = transactionEntityFacade.createTransaction(accountId, type, Math.abs(amount),
                        "OK", em);

                em.getTransaction().commit();
                return true;
            } else {
                Transaction transaction = transactionEntityFacade.createTransaction(accountId, type, Math.abs(amount),
                        "FAILED", em);
                em.getTransaction().commit();
                return false;
            }

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }

    }

    @Override
    public Boolean addTransaction(long accountId, long transactionId, EntityManager em) {
        Boolean transactionActive = true;

        if (!em.getTransaction().isActive()) {
            // No transaction is active, commit
            em.getTransaction().begin();
            transactionActive = false;
        }

        AccountDB account = em.find(AccountDB.class, accountId);
        Transaction transaction = em.find(TransactionDB.class, transactionId);
        account.addTransaction(transaction);
        em.merge(account);

        if (!transactionActive) {
            em.getTransaction().commit();
        }
        return true;
    }
}