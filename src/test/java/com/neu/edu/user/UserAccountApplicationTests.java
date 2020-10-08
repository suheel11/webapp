package com.neu.edu.user;

import com.neu.edu.user.modal.User;
import com.neu.edu.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import static org.junit.jupiter.api.Assertions.*;

@Testable
class UserAccountApplicationTests {

    @Test
    public void passwordValidFalseTest() {
        User u2 = new User();
        u2.setAccountCreated("date1");
        u2.setPassword("testpassword");
        u2.setAccountUpdated("date2");
        u2.setLastName("vallamkonda");
        u2.setName("suheel");
        u2.setUserId("userid2");
        u2.setEmail("s@s.com");
        System.out.print(u2.getPassword());
        UserService userService = new UserService();
        boolean res= userService.validatePassword(u2.getPassword());
        assertFalse(res);
    }

    @Test
    public void passwordValidTrueTest() {
        User u1 = new User();
        u1.setAccountCreated("date1");
        u1.setPassword("Suheel@1995");
        u1.setAccountUpdated("date2");
        u1.setLastName("vallamkonda");
        u1.setName("suheel");
        u1.setUserId("userid2");
        u1.setEmail("s@s.com");
        System.out.print(u1.getPassword());
        UserService userService = new UserService();
        boolean res= userService.validatePassword(u1.getPassword());
        assertTrue(res);
    }
}
