package se.liu.ida.tdp024.account.data.impl.db.facade;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.entity.TransactionDB;
import se.liu.ida.tdp024.account.data.impl.db.util.EMF;

import se.liu.ida.tdp024.account.kafka.KafkaLogProducer;

public class TransactionEntityFacadeDB implements TransactionEntityFacade {

    @Override
    public Transaction createTransaction(long accountId, String type, int amount, String status, EntityManager em){
        AccountEntityFacade accountFacade = new AccountEntityFacadeDB();
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateString = date.format(formatter);


        Boolean transactionActive = true;

        if(!em.getTransaction().isActive()) {
            // No transaction is active, commit
            em.getTransaction().begin();
            transactionActive = false;
        }

        Account account = accountFacade.getAccount(accountId, em);

        TransactionDB transaction = new TransactionDB();
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setCreated(dateString);
        transaction.setStatus(status);
        transaction.setAccount(account);

        em.persist(transaction);
        if (!transactionActive) {
            em.getTransaction().commit();
        }

        accountFacade.addTransaction(account.getId(), transaction.getId(), em);

    

        String logMessage = "Transaction created: " +
                 "Account ID: " + accountId +
                 ", Type: " + type +
                 ", Amount: " + amount +
                 ", Status: " + status +
                 ", Created: " + dateString;

        KafkaLogProducer.sendMessage("transactions-log", logMessage);

        return transaction;

    }

    @Override
    public  List<Transaction> getTransactions(long accountId) {
        EntityManager em = EMF.getEntityManager();

        AccountEntityFacade accountFacade = new AccountEntityFacadeDB();
        Account account = accountFacade.getAccount(accountId, em);

        TypedQuery<Transaction> query = em.createQuery("SELECT a FROM TransactionDB a WHERE a.account=:account", Transaction.class);
        query.setParameter("account", accountFacade.getAccount(accountId, em));
        return query.getResultList();
    }
}