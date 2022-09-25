package com.boshen.kob.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {
        PasswordEncoder pe = new BCryptPasswordEncoder();
        String encodedPassWord = pe.encode("119119");
        System.out.println(pe.matches("119119","$2a$10$jGwRsh2GE.RLPDuRRfc7oePF82zxDV/5qCTaF9k8gkkpC7EPtl6fK"));
        System.out.println(encodedPassWord);

    }

    @Test
    void testforSec(){
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication);
    }

}
