package com.dudelzak.goit.java.devproject.shortlink;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class CodeGenerator {
    private static final Random random = new Random();
    public String generateCode() {
        return generateCode(6, 8);
    }

    public String generateCode(int min, int max) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = random.nextInt(min, max + 1);

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
