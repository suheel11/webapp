package com.neu.edu.user.service;

import com.neu.edu.user.modal.CustomUserDetails;
import com.neu.edu.user.modal.User;
import com.neu.edu.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> users = Optional.ofNullable(userRepository.findByEmail(email));
        users.orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        return users.map(CustomUserDetails::new).get();
    }
}
