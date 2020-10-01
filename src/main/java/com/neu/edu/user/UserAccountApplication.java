package com.neu.edu.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@ComponentScan({"com.neu.edu.user.repository"})
public class UserAccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserAccountApplication.class, args);
    }

}
