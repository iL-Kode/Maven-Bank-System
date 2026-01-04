package se.liu.ida.tdp024.account.data.api.facade;

import java.util.List;
import javax.persistence.EntityManager;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;

public interface TransactionEntityFacade {
    public Transaction createTransaction(long accountId, String type, int amount, String status, EntityManager em);

    public  List<Transaction> getTransactions(long accountId);
}