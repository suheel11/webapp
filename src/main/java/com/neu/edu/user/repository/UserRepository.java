package com.neu.edu.user.repository;

import com.neu.edu.user.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

     User findByEmail(String email);
}
