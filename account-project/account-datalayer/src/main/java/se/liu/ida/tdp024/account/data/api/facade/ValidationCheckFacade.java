package se.liu.ida.tdp024.account.data.api.facade;

import javax.persistence.EntityManager;

public interface ValidationCheckFacade {
    boolean isValidTransaction( long accountId, int amount, EntityManager em);
}