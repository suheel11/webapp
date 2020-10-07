package com.neu.edu.user.repository;

import com.neu.edu.user.modal.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, String> {
}
