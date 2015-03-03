package org.plech.pwords.api;

import org.plech.pwords.domain.Account;
import org.plech.pwords.domain.Login;
import org.plech.pwords.repositories.AccountRepository;
import org.plech.pwords.repositories.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("login")
public class LoginRestController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LoginRepository loginRepository;

    @RequestMapping(method = RequestMethod.GET)
    @Transactional
    public List<Login> getLogins(String username) {
        Account account = accountRepository.findByUsername(username);
        return new ArrayList<>(account.getLogins());
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public Login getWithPassword(String username, @RequestBody Login login) {
        Account account = accountRepository.findByUsername(username);
        return loginRepository.findByAccountAndHostnameAndUsername(account, login.getHostname(), login.getUsername());
    }

    @RequestMapping(method = RequestMethod.PUT)
    @Transactional
    public void addLogin(String username, @RequestBody Login login) {
        Account account = accountRepository.findByUsername(username);
        account.getLogins().add(login);
        login.setAccount(account);
    }
}
