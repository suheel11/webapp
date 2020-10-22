package com.neu.edu.user.repository;

import com.neu.edu.user.modal.QuestionFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<QuestionFiles,String> {

}