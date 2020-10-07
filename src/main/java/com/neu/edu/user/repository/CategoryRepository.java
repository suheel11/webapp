package com.neu.edu.user.repository;

import com.neu.edu.user.modal.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,String> {
}
