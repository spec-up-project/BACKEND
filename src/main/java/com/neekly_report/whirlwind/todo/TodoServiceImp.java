package com.neekly_report.whirlwind.todo;

import com.neekly_report.whirlwind.entity.Schedule;
import com.neekly_report.whirlwind.entity.Todo;
import com.neekly_report.whirlwind.schedule.ScheduleDTO;

import java.util.List;

public interface TodoServiceImp {
    Todo createTodo(String tUserUid, TodoDTO.Request.TodoCreateRequest request);

    List<Todo> getUserTodos(String tUserUid);
}
