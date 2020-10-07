package com.neu.edu.user.repository;

import com.neu.edu.user.modal.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, String> {

}
