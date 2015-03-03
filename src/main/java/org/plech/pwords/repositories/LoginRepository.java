package org.plech.pwords.repositories;

import org.plech.pwords.domain.Account;
import org.plech.pwords.domain.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login, Long> {

    Login findByAccountAndHostnameAndUsername(Account account, String hostname, String username);
}
