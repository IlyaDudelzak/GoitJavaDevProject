package com.dudelzak.goit.java.devproject;

import com.dudelzak.goit.java.devproject.shortlink.CodeGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodeGeneratorTest {
    private final CodeGenerator codeGenerator = new CodeGenerator();

    @Test
    void generateCodeTest() {
        String code = codeGenerator.generateCode();
        assertNotNull(code);
        int length = code.length();
        assertTrue(length >= 6 && length <= 8);
        String regex = "^[a-zA-Z0-9]{6,8}$";
        assertTrue(code.matches(regex), code);
    }

    @Test
    void generatedCodeMustBeUniqueTest() {
        String code1 = codeGenerator.generateCode();
        String code2 = codeGenerator.generateCode();

        assertNotNull(code1);
        assertNotNull(code2);

        assertTrue(code1.length() >= 6 && code1.length() <= 8);
        assertTrue(code2.length() >= 6 && code2.length() <= 8);

        assertNotEquals(code1, code2, "Codes must be unique");
    }
}