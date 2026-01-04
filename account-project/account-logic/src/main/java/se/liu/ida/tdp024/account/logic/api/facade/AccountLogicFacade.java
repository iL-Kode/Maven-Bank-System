package se.liu.ida.tdp024.account.logic.api.facade;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;
import java.util.List;


public interface AccountLogicFacade {

    Boolean isPersonValid(String personKey);

    String getBankIdByName(String bankName);

    long create(String accountType, String personKey, String bankName);

    List<Account> find(String person); // Return an array of Accounts

    Boolean debit(long id, int amount);

    Boolean credit(long id, int amount);

    List<Transaction> transactions(long id);

}
