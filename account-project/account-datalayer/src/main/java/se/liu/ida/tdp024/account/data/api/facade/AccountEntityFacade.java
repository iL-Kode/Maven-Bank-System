package se.liu.ida.tdp024.account.data.api.facade;

import javax.persistence.EntityManager;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;

import java.util.List;

public interface AccountEntityFacade {
    Account getAccount(long accountId, EntityManager manager);
    Boolean isAccountInDb(long accountId);
    long createDbAccount(String accountType, String personKey, String bankKey);
    List<Account> getDbAccounts(String personKey);
    int readHoldings(long accountId, EntityManager manager);
    Boolean writeHoldings(long accountId, int amount, ValidationCheckFacade checker, int debitLimit);
    Boolean addTransaction(long id, long transaction_id, EntityManager em);
}
