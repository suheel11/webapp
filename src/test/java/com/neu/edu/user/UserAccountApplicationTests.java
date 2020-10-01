package com.neu.edu.user;

import com.neu.edu.user.modal.User;
import com.neu.edu.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserAccountApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    public void getUserTest() {
        User u2 = new User();
        u2.setAccountCreated("date1");
        u2.setPassword("testpassword");
        u2.setAccountUpdated("date2");
        u2.setLastName("vallamkonda");
        u2.setName("suheel");
        u2.setUserId("userid2");
        u2.setEmail("s@s.com");
        boolean res= userService.validatePassword(u2.getPassword());
        assertFalse(res);
    }
}
