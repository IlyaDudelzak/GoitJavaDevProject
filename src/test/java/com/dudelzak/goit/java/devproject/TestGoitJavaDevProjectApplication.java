package com.dudelzak.goit.java.devproject;

import org.springframework.boot.SpringApplication;

public class TestGoitJavaDevProjectApplication {

    static void main(String[] args) {
        SpringApplication.from(AppLauncher::main).with(TestcontainersConfiguration.class).run(args);
    }

}
