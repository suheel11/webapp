package com.neu.edu.user.repository;

import com.neu.edu.user.modal.AnswerFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerFileRepository extends JpaRepository<AnswerFiles,String> {
}