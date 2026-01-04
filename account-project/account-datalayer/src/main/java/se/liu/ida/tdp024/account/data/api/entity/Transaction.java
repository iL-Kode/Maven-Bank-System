package se.liu.ida.tdp024.account.data.api.entity;

import java.io.Serializable;
import java.util.Date;

public interface Transaction extends Serializable {
    public long getId();
    public void setId(long id);

    public int getAmount();
    public void setAmount(int amount);

    public Account getAccount();
    public void setAccount(Account account);

    public String getStatus();
    public void setStatus(String status);

    public String getCreated();
    public void setCreated(String created);

    public String getType();
    public void setType(String type);
}
