package se.liu.ida.tdp024.account.data.impl.db.facade;

import javax.persistence.EntityManager;

import se.liu.ida.tdp024.account.data.api.facade.AccountEntityFacade;
import se.liu.ida.tdp024.account.data.api.facade.ValidationCheckFacade;
import se.liu.ida.tdp024.account.data.impl.db.facade.AccountEntityFacadeDB;

public class ValidationCheckFacadeImpl implements ValidationCheckFacade {

    private AccountEntityFacade accountEntityFacade = new AccountEntityFacadeDB();

    @Override
    public boolean isValidTransaction(long accountId, int amount, EntityManager em){

        int currentAmount = accountEntityFacade.readHoldings(accountId, em);
        int newAmount = currentAmount + amount;
        return newAmount >= 0;

    }
}