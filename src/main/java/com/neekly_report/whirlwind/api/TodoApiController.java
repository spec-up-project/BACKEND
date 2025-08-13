package com.neekly_report.whirlwind.api;

import com.neekly_report.whirlwind.dto.ApiResponseDto;
import com.neekly_report.whirlwind.dto.TodoDto;
import com.neekly_report.whirlwind.dto.UserDto;
import com.neekly_report.whirlwind.service.TodoManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "할일 관리 API", description = "할일 생성, 조회, 수정, 완료 처리")
@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoApiController {

    private final TodoManagementService todoManagementService;

    @Operation(summary = "내 할일 전체 조회")
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<TodoDto.Response.TodoItem>>> getMyTodos(
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        List<TodoDto.Response.TodoItem> todos =
                todoManagementService.getUserTodos(userDetail.getTUserUid());

        return ResponseEntity.ok(ApiResponseDto.success(todos));
    }

    @Operation(summary = "상태별 할일 조회")
    @GetMapping("/by-status")
    public ResponseEntity<ApiResponseDto<List<TodoDto.Response.TodoItem>>> getTodosByStatus(
            @Parameter(description = "상태", example = "PENDING")
            @RequestParam String status,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        List<TodoDto.Response.TodoItem> todos =
                todoManagementService.getTodosByStatus(userDetail.getTUserUid(), status);

        return ResponseEntity.ok(ApiResponseDto.success(todos));
    }

    @Operation(summary = "우선순위별 할일 조회")
    @GetMapping("/by-priority")
    public ResponseEntity<ApiResponseDto<List<TodoDto.Response.TodoItem>>> getTodosByPriority(
            @Parameter(description = "우선순위", example = "HIGH")
            @RequestParam String priority,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        List<TodoDto.Response.TodoItem> todos =
                todoManagementService.getTodosByPriority(userDetail.getTUserUid(), priority);

        return ResponseEntity.ok(ApiResponseDto.success(todos));
    }

    @Operation(summary = "기한 초과 할일 조회")
    @GetMapping("/overdue")
    public ResponseEntity<ApiResponseDto<List<TodoDto.Response.TodoItem>>> getOverdueTodos(
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        List<TodoDto.Response.TodoItem> todos =
                todoManagementService.getOverdueTodos(userDetail.getTUserUid());

        return ResponseEntity.ok(ApiResponseDto.success(todos));
    }

    @Operation(summary = "할일 완료 처리")
    @PutMapping("/{todoId}/complete")
    public ResponseEntity<ApiResponseDto<TodoDto.Response.TodoItem>> completeTodo(
            @Parameter(description = "할일 ID") @PathVariable String todoId,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        TodoDto.Response.TodoItem todo =
                todoManagementService.completeTodo(userDetail.getTUserUid(), todoId);

        return ResponseEntity.ok(ApiResponseDto.success(todo, "할일이 완료되었습니다."));
    }

    @Operation(summary = "키워드로 할일 검색")
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDto<List<TodoDto.Response.TodoItem>>> searchTodos(
            @Parameter(description = "검색 키워드") @RequestParam String keyword,
            @AuthenticationPrincipal UserDto.UserDetail userDetail) {

        List<TodoDto.Response.TodoItem> todos =
                todoManagementService.searchTodos(userDetail.getTUserUid(), keyword);

        return ResponseEntity.ok(ApiResponseDto.success(todos));
    }
}
