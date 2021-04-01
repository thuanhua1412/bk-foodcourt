package com.example.my_app.login;

public class User {
    private String email;
    private String accountType;

    public User() {}
    public User(String Email, String AccountType) {
        email = Email;
        accountType = AccountType;
    }
    public String getEmail() { return email;}
    public String getAccountType() {return accountType;}
}
