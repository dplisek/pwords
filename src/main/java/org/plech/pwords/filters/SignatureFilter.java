package org.plech.pwords.filters;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.plech.pwords.domain.Account;
import org.plech.pwords.repositories.AccountRepository;
import org.plech.pwords.wrappers.MultipleReadHttpServletRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sun.security.rsa.RSAPublicKeyImpl;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class SignatureFilter extends OncePerRequestFilter {

    private static final String PARAM_SIGNATURE = "signature";
    private static final String PARAM_USERNAME = "username";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountRepository accountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        request = new MultipleReadHttpServletRequestWrapper(request);
        String username = request.getParameter(PARAM_USERNAME);
        if (username == null) {
            log.error("Missing username.");
            response.sendError(HttpStatus.FORBIDDEN.value(), "Missing username.");
            return;
        }
        Account account = accountRepository.findByUsername(username);
        String sig = request.getParameter(PARAM_SIGNATURE);
        if (sig == null) {
            log.error("Missing request signature.");
            response.sendError(HttpStatus.FORBIDDEN.value(), "Missing request signature.");
            return;
        }
        byte[] completeSigBase = getCompleteSignatureBase(request);
        try {
            Signature signature = Signature.getInstance("SHA1withRSA");
            for (String publicKey : account.getPublicKeys()) {
                signature.initVerify(new RSAPublicKeyImpl(DatatypeConverter.parseHexBinary(publicKey)));
                signature.update(completeSigBase);
                if (signature.verify(DatatypeConverter.parseHexBinary(sig))) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }
            log.error("Bad request signature.");
            response.sendError(HttpStatus.FORBIDDEN.value(), "Bad request signature.");
        } catch (NoSuchAlgorithmException |
                InvalidKeyException |
                SignatureException e) {
            log.error("Couldn't verify request signature.", e);
            response.sendError(HttpStatus.FORBIDDEN.value(), "Couldn't verify request signature.");
        }
    }

    private byte[] getCompleteSignatureBase(HttpServletRequest request) throws IOException {
        List<String> names = Collections.list(request.getParameterNames());
        names.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        StringBuilder sigBase = new StringBuilder();
        for (String name : names) {
            if (PARAM_SIGNATURE.equals(name)) continue;
            sigBase.append(name).append(":").append(request.getParameter(name)).append(";");
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(request.getInputStream(), bos);
        return ArrayUtils.addAll(sigBase.toString().getBytes("UTF-8"), bos.toByteArray());
    }
}
