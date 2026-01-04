package se.liu.ida.tdp024.account.data.test.facade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Test;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.ValidationCheckFacade;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.data.impl.db.util.EMF;
import se.liu.ida.tdp024.account.data.impl.db.util.StorageFacadeDB;

public class AccountEntityFacadeTest {

    //---- Unit under test ----//
    private AccountEntityFacade accountEntityFacade = new AccountEntityFacadeDB();
    private StorageFacade storageFacade = new StorageFacadeDB();

    @After
    public void tearDown() {
        storageFacade.emptyStorage();
    }

    @Test
    public void testFindFalse(){
        boolean result = accountEntityFacade.isAccountInDb(1);
        assertFalse("No account should exist for id 1", result);
    }

    @Test
    public void testCreate() {
        String personKey = "5";
        String bankKey = "3";
        String accType = "CHECK";
        long result = accountEntityFacade.createDbAccount(personKey, bankKey, accType);
        assertEquals(1, result);
        assertTrue("Created account should be present", accountEntityFacade.isAccountInDb(result));
    }

    @Test
    public void testFindTrue(){
        accountEntityFacade.createDbAccount("SAVINGS", "person-3", "bank-5");
        boolean result = accountEntityFacade.isAccountInDb(1);
        assertTrue(result);
    }

    @Test
    public void testGetDbAccountsSuccess(){
        String personKey = "1";
        accountEntityFacade.createDbAccount("SAVINGS", personKey, "bank-A");
        accountEntityFacade.createDbAccount("CHECK", personKey, "bank-A");
        List<Account> accounts = accountEntityFacade.getDbAccounts(personKey);
        assertEquals(2, accounts.size());
        assertEquals("CHECK", accounts.get(1).getAccountType());
    }

    @Test
    public void testGetDbAccountsEmpty(){
        accountEntityFacade.createDbAccount("SAVINGS", "999", "bank-X");
        List<Account> accounts = accountEntityFacade.getDbAccounts("999");
        assertEquals(1, accounts.size());
    }

    @Test
    public void testReadHoldings(){
        EntityManager em = EMF.getEntityManager();
        assertEquals(1, accountEntityFacade.createDbAccount("SAVINGS", "person-1", "bank-2"));
        assertEquals(0, accountEntityFacade.readHoldings(1, em));
    }

   @Test
   public void testWriteHoldingsUpdate(){
       EntityManager em = EMF.getEntityManager();
       ValidationCheckFacade checker = new ValidationCheckFacadeTestImpl(true);
       assertEquals(1, accountEntityFacade.createDbAccount("SAVINGS", "p1", "b1"));
       assertTrue(accountEntityFacade.writeHoldings(1, 100, checker, 0));
       assertEquals(100, accountEntityFacade.readHoldings(1, em));
   }

   @Test
   public void testWriteHoldingsFail(){
       EntityManager em = EMF.getEntityManager();
       ValidationCheckFacade checker = new ValidationCheckFacadeTestImpl(false);
       assertEquals(1, accountEntityFacade.createDbAccount("CHECK", "p2", "b2"));
       assertFalse(accountEntityFacade.writeHoldings(1, 100, checker, 0));
       assertEquals(0, accountEntityFacade.readHoldings(1, em));
   }
}