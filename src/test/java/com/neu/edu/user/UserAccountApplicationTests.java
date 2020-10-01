package com.neu.edu.user;

import com.neu.edu.user.modal.User;
import com.neu.edu.user.repository.UserRepository;
import com.neu.edu.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserAccountApplicationTests {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

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
        when(userRepository.findById("userid2")).thenReturn(Optional.of(u2));
        assertEquals("s@s.com", userService.getUserById("userid2").getEmail());
    }

    @Test
    public void saveUserTest() {
        User u1 = new User();
        u1.setAccountCreated("date1");
        u1.setPassword("testpassword");
        u1.setAccountUpdated("date2");
        u1.setLastName("vallamkonda");
        u1.setName("suheel");
        u1.setUserId("userid");
        u1.setEmail("s@s.com");
        when(userRepository.save(u1)).thenReturn(u1);
        assertEquals(u1, userService.saveUser(u1));
    }

    @Test
    public void updateUserTest() {
        User u1 = new User();
        u1.setAccountCreated("date1");
        u1.setPassword("testpassword");
        u1.setAccountUpdated("date2");
        u1.setLastName("vallamkonda");
        u1.setName("suheel");
        u1.setUserId("userid");
        u1.setEmail("s@s.com");
        User u2 = new User();
        u2.setAccountCreated("date1");
        u2.setPassword("testpassword");
        u2.setAccountUpdated("date2");
        u2.setLastName("vallamkonda vijayakumar");
        u2.setName("suheel");
        u2.setUserId("userid2");
        u2.setEmail("s@s.com");
        when(userRepository.findById("userid")).thenReturn(Optional.of(u1));
        when(userRepository.save(u1)).thenReturn(u1);
        userService.updateUser("userid", u2);
        assertEquals("vallamkonda vijayakumar", u2.getLastName());

    }
}
