package com.neekly_report.whirlwind.todo;

import com.neekly_report.whirlwind.entity.Todo;
import com.neekly_report.whirlwind.entity.User;
import com.neekly_report.whirlwind.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoDTO.Response.TodoResponse createTodo(String tUserUid, TodoDTO.Request.TodoCreateRequest dto) {
        User user = userRepository.findById(tUserUid)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Todo todo = Todo.builder()
                .status(dto.getStatus() == null ? "INCOMPLETE" : dto.getStatus())
                .priority(dto.getPriority() == null ? String.valueOf(1) : dto.getPriority())
                .category(dto.getCategory())
                .user(user)
                .build();

        Todo saved = todoRepository.save(todo);

        return TodoDTO.Response.TodoResponse.builder()
                .tTodoUid(saved.getTTodoUid())
                .status(saved.getStatus())
                .category(saved.getCategory())
                .priority(saved.getPriority())
                .build();
    }

    public List<TodoDTO.Response.TodoResponse> getUserTodos(String tUserUid) {
        return todoRepository.findByUser_tUserUid(tUserUid)
                .stream()
                .map(t -> TodoDTO.Response.TodoResponse.builder()
                        .tTodoUid(t.getTTodoUid())
                        .status(t.getStatus())
                        .category(t.getCategory())
                        .priority(t.getPriority())
                        .build())
                .collect(Collectors.toList());
    }
}

