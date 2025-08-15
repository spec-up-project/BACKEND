package com.neekly_report.whirlwind.service;

import com.neekly_report.whirlwind.dto.TodoDto;
import com.neekly_report.whirlwind.entity.Todo;
import com.neekly_report.whirlwind.entity.User;
import com.neekly_report.whirlwind.repository.TodoRepository;
import com.neekly_report.whirlwind.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoDto.Response.TodoResponse createTodo(String tUserUid, TodoDto.Request.TodoCreateRequest dto) {
        User user = userRepository.findById(tUserUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Todo todo = Todo.builder()
                .status(dto.getStatus() == null ? "INCOMPLETE" : dto.getStatus())
                .priority(dto.getPriority() == null ? String.valueOf(1) : dto.getPriority())
                .category(dto.getCategory())
                .user(user)
                .build();

        Todo saved = todoRepository.save(todo);

        return TodoDto.Response.TodoResponse.builder()
                .todoUid(saved.getTodoUid())
                .status(saved.getStatus())
                .category(saved.getCategory())
                .priority(saved.getPriority())
                .build();
    }

    public List<TodoDto.Response.TodoResponse> getUserTodos(String tUserUid) {
        return todoRepository.findByUser_userUid(tUserUid)
                .stream()
                .map(t -> TodoDto.Response.TodoResponse.builder()
                        .todoUid(t.getTodoUid())
                        .status(t.getStatus())
                        .category(t.getCategory())
                        .priority(t.getPriority())
                        .build())
                .collect(Collectors.toList());
    }
}

