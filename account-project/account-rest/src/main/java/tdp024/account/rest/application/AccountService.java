package tdp024.account.rest.application;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import se.liu.ida.tdp024.account.data.api.entity.Account;

import org.springframework.http.ResponseEntity;

import tdp024.util.api.AccountJsonSerializer;
import tdp024.util.impl.AccountJsonSerializerImpl;
import tdp024.util.api.AccountLogger;
import tdp024.util.impl.AccountLoggerImpl;

import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;
import se.liu.ida.tdp024.account.logic.impl.facade.AccountLogicFacadeImpl;
// import se.liu.ida.tdp024.account.data.impl.db.entity.AccountDB;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/account-rest/account")
public class AccountService {
    // --- Here we choose the implementations of the logic and data layer --- //
    private final AccountLogicFacade accountLogicFacade = new AccountLogicFacadeImpl(new AccountEntityFacadeDB());
    // ----------------------------------------------------------------------- //

    private static final AccountLogger accountLogger = new AccountLoggerImpl();
    private static final AccountJsonSerializer jsonSerializer = new AccountJsonSerializerImpl();

    // Skapa ett visst typ av konto, för en person, på en viss bank.
    @GetMapping(path = "/create/", produces = "application/json")
    public ResponseEntity<String> create(@RequestParam(value="person", defaultValue = "" ) String person, @RequestParam(value="bank", defaultValue = "") String bank, @RequestParam(value="accounttype", defaultValue = "") String accounttype) {
        long accountId = accountLogicFacade.create(accounttype, person, bank);

        if (accountId != -1) {
            return ResponseEntity.ok("OK");
        }
        return ResponseEntity.ok("FAILED");
    }

    // Hitta alla konton för en viss person.
    @GetMapping(path = "/find/person", produces = "application/json")
    public ResponseEntity<String> find(@RequestParam(value="person") String person) {
        List<Account> result = accountLogicFacade.find(person);
        if (result != null) {
            return ResponseEntity.ok(jsonSerializer.toJson(result));
        }
        return ResponseEntity.ok("[]");
    }

    // Debitera konto.
    @GetMapping(path = "/debit", produces = "application/json")
    public ResponseEntity<String> debit(@RequestParam(value="id") int id, @RequestParam(value="amount") int amount) {
        if (accountLogicFacade.debit(id, amount)) {
            return ResponseEntity.ok("OK");
        }
        return ResponseEntity.ok("FAILED");
    }

    // Kreditera konto.
    @GetMapping(path = "/credit", produces = "application/json")
    public ResponseEntity<String> credit(@RequestParam(value="id") int id, @RequestParam(value="amount") int amount) {
        if (accountLogicFacade.credit(id, amount)) {
            return ResponseEntity.ok("OK");
        }
        return ResponseEntity.ok("FAILED");
    }

    // Hämta alla transaktioner för ett konto.
    @GetMapping(path = "/transactions", produces = "application/json")
    public ResponseEntity<String> transactions(@RequestParam(value="id") long id) {
        return ResponseEntity.ok(jsonSerializer.toJson(accountLogicFacade.transactions(id)));
    }


}