package com.neekly_report.whirlwind.todo;

import com.neekly_report.whirlwind.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, String> {
    List<Todo> findByUser_tUserUid(String tUserUid);
}
