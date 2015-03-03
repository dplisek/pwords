package org.plech.pwords.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @NotNull
    @Column(name = "username")
    private String username;

    @NotNull
    @Column(name = "passHash")
    private String passHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "public_key", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "public_key", nullable = false)
    public Set<String> publicKeys;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Login> logins;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<Login> getLogins() {
        return logins;
    }

    public void setLogins(Set<Login> logins) {
        this.logins = logins;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassHash() {
        return passHash;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }

    public Set<String> getPublicKeys() {
        return publicKeys;
    }

    public void setPublicKeys(Set<String> publicKeys) {
        this.publicKeys = publicKeys;
    }
}
