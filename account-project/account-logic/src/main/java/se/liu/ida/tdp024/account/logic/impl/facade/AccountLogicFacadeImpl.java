package se.liu.ida.tdp024.account.logic.impl.facade;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.ValidationCheckFacade;
import se.liu.ida.tdp024.account.data.impl.db.facade.ValidationCheckFacadeImpl;
import se.liu.ida.tdp024.account.data.api.facade.TransactionEntityFacade;
import se.liu.ida.tdp024.account.data.impl.db.facade.TransactionEntityFacadeDB;
import se.liu.ida.tdp024.account.logic.api.facade.AccountLogicFacade;
import se.liu.ida.tdp024.account.logic.impl.facade.AccountLogicFacadeImpl;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import tdp024.util.api.HTTPHelper;
import tdp024.util.impl.HTTPHelperImpl;
import tdp024.util.api.AccountJsonSerializer;
import tdp024.util.impl.AccountJsonSerializerImpl;
import java.util.List;
import se.liu.ida.tdp024.account.logic.impl.util.BankDTO;
import se.liu.ida.tdp024.account.logic.impl.util.PersonDTO;


public class AccountLogicFacadeImpl implements AccountLogicFacade {

    private HTTPHelper httpHelper = new HTTPHelperImpl();
    private AccountJsonSerializer jsonSerializer = new AccountJsonSerializerImpl();

    private static final String PERSON_API = "http://localhost:8060/person";
    private static final String BANK_API = "http://localhost:8070/bank";
    
    private AccountEntityFacade accountEntityFacade;
    private TransactionEntityFacade transactionEntityFacade = new TransactionEntityFacadeDB();
    private ValidationCheckFacade vfc = new ValidationCheckFacadeImpl();

    public AccountLogicFacadeImpl(AccountEntityFacade accountEntityFacade) {
        this.accountEntityFacade = accountEntityFacade;
    }

    @Override
    public Boolean isPersonValid(String personKey) {
        String response = httpHelper.get(PERSON_API + "/find.key", "key", personKey);
        PersonDTO person = jsonSerializer.fromJson(response, PersonDTO.class);
        if (person == null) {
            return false;
        }
        return person.getKey().equals(personKey);
    }

    @Override
    public String getBankIdByName(String bankName) {
        String response = httpHelper.get(BANK_API + "/find.name", "name", bankName);
        BankDTO bank = jsonSerializer.fromJson(response, BankDTO.class);
        if (bank == null) {
            return "";
        }
        return bank.getKey();
    }

    
    @Override
    public long create(String accountType, String personKey, String bankName) {
        String bankId = getBankIdByName(bankName);
        if (isPersonValid(personKey) && !bankId.equals("")) {
            if (accountType.equals("CHECK") || accountType.equals("SAVINGS")) {
                return accountEntityFacade.createDbAccount(accountType, personKey, bankName);
            }
        }
        return -1;
    }
    
    @Override
    public List<Account> find(String personKey){
        return accountEntityFacade.getDbAccounts(personKey);
    }

    @Override
    public Boolean debit(long accountId, int amount) {
        Boolean success = false;
        if (amount >= 0) {
            success = accountEntityFacade.writeHoldings(accountId, -amount, vfc, 0);
        }
        return success;
    }

    @Override
    public Boolean credit(long accountId, int amount) {
        Boolean success = false;
        if (amount >= 0) {
            success = accountEntityFacade.writeHoldings(accountId, amount, vfc, 0);
        }

        return success;
    }

    @Override
    public List<Transaction> transactions(long accountId) {
        return transactionEntityFacade.getTransactions(accountId);
    }

}
