package com.aquashine;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGen {
    @Test
    void gen() {
        BCryptPasswordEncoder e = new BCryptPasswordEncoder();
        System.out.println("HASH=" + e.encode("123456789"));
    }
}