package org.plech.pwords.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "login", uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "hostname", "username"}))
public class Login {

    @Id @GeneratedValue
    @Column(name = "id")
    private long id;

    @NotNull
    @Column(name = "hostname")
    private String hostname;

    @NotNull
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
