package se.liu.ida.tdp024.account.data.test.facade;

import javax.persistence.EntityManager;

import se.liu.ida.tdp024.account.data.api.facade.ValidationCheckFacade;

// A mock of the ValidationCheckFacade for testing purposes, returning a fixed boolean value
public class ValidationCheckFacadeTestImpl implements ValidationCheckFacade {

    private final boolean value;

    public ValidationCheckFacadeTestImpl(boolean value) {
        this.value = value;
    }

    @Override
    public boolean isValidTransaction(long accountId, int amount, EntityManager em) {
        return value;
    }

}
