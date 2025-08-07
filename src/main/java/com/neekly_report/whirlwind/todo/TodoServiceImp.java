package com.neekly_report.whirlwind.todo;

import com.neekly_report.whirlwind.dto.TodoDto;
import com.neekly_report.whirlwind.entity.Todo;

import java.util.List;

public interface TodoServiceImp {
    Todo createTodo(String tUserUid, TodoDto.Request.TodoCreateRequest request);

    List<Todo> getUserTodos(String tUserUid);
}
