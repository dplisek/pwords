package org.plech.pwords.api;

import org.plech.pwords.domain.Account;
import org.plech.pwords.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("account")
public class AccountRestController {

    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping("login")
    @Transactional
    public void login(String username, String password, String publicKey) throws NoSuchAlgorithmException, UnsupportedEncodingException, HttpStatusCodeException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Invalid username or password.");
        }
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes("UTF-8"));
        String testedPass = DatatypeConverter.printHexBinary(md.digest());
        if (!testedPass.equalsIgnoreCase(account.getPassHash())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Invalid username or password.");
        }
        account.getPublicKeys().add(publicKey);
    }
}
