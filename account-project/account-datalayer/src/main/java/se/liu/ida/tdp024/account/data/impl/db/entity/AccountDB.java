package se.liu.ida.tdp024.account.data.impl.db.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import se.liu.ida.tdp024.account.data.api.entity.Account;
import se.liu.ida.tdp024.account.data.api.entity.Transaction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Accounts")
public class AccountDB implements Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String personKey;
    private String accountType;
    private String bankKey;
    private int holdings;
    private List<Transaction> transactions = new ArrayList<Transaction>();

    @Override
    public long getId(){
        return id;
    }

    @Override
    public void setId(long id){
        this.id = id;
    }

    @Override
    public String getPersonKey(){
        return personKey;
    }

    @Override
    public void setPersonKey(String personKey){
        this.personKey = personKey;
    }

    @Override
    public String getAccountType(){
        return accountType;
    }

    @Override
    public void setAccountType(String accountType){
        this.accountType = accountType;
    }

    @Override
    public String getBankKey(){
        return bankKey;
    }

    @Override
    public void setBankKey(String bankKey){
        this.bankKey = bankKey;
    }

    @Override
    public int getHoldings(){
        return holdings;
    }

    @Override
    public void setHoldings(int holdings){
        this.holdings = holdings;
    }

    @Override
    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
}
