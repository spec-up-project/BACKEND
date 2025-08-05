package com.neekly_report.whirlwind.todo;

import com.neekly_report.whirlwind.user.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoApiController {

    private final TodoService todoService;

    @Operation(summary = "자연어로 할일 생성", description = "자유 텍스트로부터 추출된 할일을 등록합니다.")
    @PostMapping
    public ResponseEntity<TodoDTO.Response.TodoResponse> createTodo(
            @RequestBody @Valid TodoDTO.Request.TodoCreateRequest request,
            @AuthenticationPrincipal UserDTO.UserDetail userDetail) {
        TodoDTO.Response.TodoResponse todo = todoService.createTodo(userDetail.getTUserUid(), request);
        return ResponseEntity.ok(todo);
    }

    @Operation(summary = "내 할일 전체 조회")
    @GetMapping
    public ResponseEntity<List<TodoDTO.Response.TodoResponse>> getTodos(
            @AuthenticationPrincipal UserDTO.UserDetail userDetail) {
        return ResponseEntity.ok(todoService.getUserTodos(userDetail.getTUserUid()));
    }
}
