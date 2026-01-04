package se.liu.ida.tdp024.account.data.test.facade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Test;

import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.facade.TransactionEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.util.EMF;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.util.StorageFacadeDB;

public class TransactionEntityFacadeTest {

    private StorageFacade storageFacade = new StorageFacadeDB();

    @After
    public void tearDown() {
        storageFacade.emptyStorage();
    }

    @Test
    public void createTransaction_succeeds_and_returnsEntity() {
        EntityManager em = EMF.getEntityManager();
        AccountEntityFacade accFacade = new AccountEntityFacadeDB();
        long accountId = accFacade.createDbAccount("CHECK", "1", "SWEDBANK");

        TransactionEntityFacade txFacade = new TransactionEntityFacadeDB();
        Transaction tx = txFacade.createTransaction(accountId, "CREDIT", 50, "OK", em);

        assertNotNull("Transaction should be created", tx);
        assertEquals(50, tx.getAmount());
        assertEquals("OK", tx.getStatus());
        assertEquals("CREDIT", tx.getType());
    }

    @Test
    public void getTransactions_returns_list_with_createdTransaction() {
        EntityManager em = EMF.getEntityManager();
        AccountEntityFacade accFacade = new AccountEntityFacadeDB();
        long accountId = accFacade.createDbAccount("SAVINGS", "2", "NORDEA");

        TransactionEntityFacade txFacade = new TransactionEntityFacadeDB();
        txFacade.createTransaction(accountId, "DEBIT", 30, "OK", em);

        List<Transaction> transactions = txFacade.getTransactions(accountId);
        assertEquals(1, transactions.size());
        assertEquals(30, transactions.get(0).getAmount());
    }

}
