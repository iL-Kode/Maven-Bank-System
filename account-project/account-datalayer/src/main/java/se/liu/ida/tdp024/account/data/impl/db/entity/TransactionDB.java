package se.liu.ida.tdp024.account.data.impl.db.entity;

import javax.persistence.*;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;


@Entity
@Table(name = "Transactions")
public class TransactionDB implements Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String type;
    private int amount;
    private String created;
    private String status;

    @ManyToOne(targetEntity = AccountDB.class)
    private Account account;

    @Override
    public long getId(){
        return id;
    }

    @Override
    public void setId(long id){
        this.id = id;
    }

    @Override
    public String getType(){
        return type;
    }

    @Override
    public void setType(String type){
        this.type = type;
    }

    @Override
    public int getAmount(){
        return amount;
    }

    @Override
    public void setAmount(int amount){
        this.amount = amount;
    }

    @Override
    public void setCreated(String created){
        this.created = created;
    }

    @Override
    public String getCreated(){
        return created;
    }

    @Override
    public String getStatus(){
        return status;
    }
    @Override
    public void setStatus(String status){
        this.status = status;
    }

    @Override
    public Account getAccount(){
        return account;
    }

    @Override
    public void setAccount(Account account){
        this.account = account;
    }

}
