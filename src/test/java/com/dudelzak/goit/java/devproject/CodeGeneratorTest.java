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
    void generatedCodeFormatTest() {
        for (int i = 0; i < 50; i++) {
            String code = codeGenerator.generateCode();
            assertNotNull(code);
            int length = code.length();
            assertTrue(length >= 6 && length <= 8);
            assertTrue(code.matches("^[a-zA-Z0-9]{6,8}$"));
        }
    }
}