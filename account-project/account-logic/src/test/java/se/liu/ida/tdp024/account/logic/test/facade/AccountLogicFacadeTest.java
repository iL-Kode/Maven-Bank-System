package se.liu.ida.tdp024.account.logic.test.facade;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.impl.db.util.StorageFacadeDB;
import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;
import se.liu.ida.tdp024.account.logic.impl.facade.AccountLogicFacadeImpl;

public class AccountLogicFacadeTest {


    //--- Unit under test ---//
    public AccountLogicFacade accountLogicFacade = new AccountLogicFacadeImpl(new AccountEntityFacadeDB());
    public StorageFacade storageFacade = new StorageFacadeDB();

    @After
    public void tearDown() {
        if (storageFacade != null)
            storageFacade.emptyStorage();
    }

    @Test
    public void testGetBankByNameSuccess() {
        String result = accountLogicFacade.getBankIdByName("NORDEA");
        assertEquals("4", result);
    }

    @Test
    public void testGetBankByNameFalse() {
        String result = accountLogicFacade.getBankIdByName("BANANA");
        assertEquals("", result);
    }

    @Test
    public void testIsValidPersonTrue() {
        boolean result = accountLogicFacade.isPersonValid("5");
        assertEquals(true, result);
    }

    @Test
    public void testIsValidPersonFalse() {
        boolean result = accountLogicFacade.isPersonValid("99");
        assertEquals(false, result);
    }

    @Test
    public void testCreateAccountSuccess(){
        String personKey = "1";
        String bankKey = "SWEDBANK";
        String accType = "SAVINGS";
        long accountId = accountLogicFacade.create(accType, personKey, bankKey);
        assertNotEquals(0, accountId);
        List<Account> accounts = accountLogicFacade.find(personKey);
        Account foundAccount = accounts.get(0);
        assertEquals(personKey, foundAccount.getPersonKey());
        assertEquals(bankKey, foundAccount.getBankKey());
        assertEquals(accType, foundAccount.getAccountType());
    }

}
