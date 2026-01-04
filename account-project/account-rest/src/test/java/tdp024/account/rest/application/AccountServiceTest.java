package tdp024.account.rest.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import se.liu.ida.tdp024.account.data.api.util.StorageFacade;
import se.liu.ida.tdp024.account.data.impl.db.util.StorageFacadeDB;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;
import se.liu.ida.tdp024.account.logic.impl.facade.AccountLogicFacadeImpl;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;
import se.liu.ida.tdp024.account.data.api.entity.Account;

public class AccountServiceTest {

    private final AccountService accountService = new AccountService();
    private final StorageFacade storageFacade = new StorageFacadeDB();

    @After
    public void tearDown() {
        if (storageFacade != null) {
            storageFacade.emptyStorage();
        }
    }

    @Test
    public void testCreateSuccess() {
        ResponseEntity<String> response = accountService.create("1", "SWEDBANK", "SAVINGS");
        assertEquals("OK", response.getBody());
    }

    @Test
    public void testCreateFail() {
        ResponseEntity<String> response = accountService.create("100", "SWEDBANK", "SAVINGS");
        assertEquals("FAILED", response.getBody());
    }

    @Test
    public void testFindPersonAccountsSuccess() {
        ResponseEntity<String> response = accountService.find("1");
        String body = response.getBody();
        assertTrue(body != null && !body.isEmpty());
    }

    @Test
    public void testFindPersonAccountsFail() {
        ResponseEntity<String> response = accountService.find("DjungelJorge");
        String body = response.getBody();
        assertTrue(body.equals("[]") || body.isEmpty());
    }
    @Test
    public void testCreditAndDebitSuccessAndFail() {
        ResponseEntity<String> createResponse = accountService.create("1", "SWEDBANK", "SAVINGS");
        assertEquals("OK", createResponse.getBody());

        AccountLogicFacade logic = new AccountLogicFacadeImpl(new AccountEntityFacadeDB());
        java.util.List<Account> accounts = logic.find("1");
        assertTrue(accounts != null && !accounts.isEmpty());
        long accountId = accounts.get(0).getId();

        ResponseEntity<String> creditResponse = accountService.credit((int) accountId, 100);
        assertEquals("OK", creditResponse.getBody());

        ResponseEntity<String> debitResponse = accountService.debit((int) accountId, 50);
        assertEquals("OK", debitResponse.getBody());

        ResponseEntity<String> debitFail = accountService.debit((int) accountId, 1000);
        assertEquals("FAILED", debitFail.getBody());
    }

    @Test
    public void testTransactionsContainEntries() {
        ResponseEntity<String> createResponse = accountService.create("1", "SWEDBANK", "SAVINGS");
        assertEquals("OK", createResponse.getBody());

        AccountLogicFacade logic = new AccountLogicFacadeImpl(new AccountEntityFacadeDB());
        java.util.List<Account> accounts = logic.find("1");
        assertTrue(accounts != null && !accounts.isEmpty());
        long accountId = accounts.get(0).getId();

        accountService.credit((int) accountId, 10);
        accountService.debit((int) accountId, 5);

        ResponseEntity<String> txResponse = accountService.transactions(accountId);
        String body = txResponse.getBody();
        assertTrue(body != null && !body.isEmpty());
        assertTrue(body.contains("CREDIT") && body.contains("DEBIT"));
    }
}
