package com.neekly_report.whirlwind.todo;

import com.neekly_report.whirlwind.dto.TodoDto;
import com.neekly_report.whirlwind.entity.Todo;
import com.neekly_report.whirlwind.todo.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TodoManagementService {

    private final TodoRepository todoRepository;

    /**
     * 사용자의 모든 할일 조회
     */
    @Transactional(readOnly = true)
    public List<TodoDto.Response.TodoItem> getUserTodos(String userId) {
        log.info("사용자 할일 조회 - 사용자ID: {}", userId);

        List<Todo> todos = todoRepository.findByUser_tUserUid(userId);

        return todos.stream()
                .map(this::toTodoItem)
                .sorted((a, b) -> {
                    // 우선순위순 -> 마감일순 정렬
                    int priorityCompare = getPriorityOrder(a.getPriority()) - getPriorityOrder(b.getPriority());
                    if (priorityCompare != 0) return priorityCompare;

                    if (a.getDueDate() == null && b.getDueDate() == null) return 0;
                    if (a.getDueDate() == null) return 1;
                    if (b.getDueDate() == null) return -1;

                    return a.getDueDate().compareTo(b.getDueDate());
                })
                .toList();
    }

    /**
     * 상태별 할일 조회
     */
    @Transactional(readOnly = true)
    public List<TodoDto.Response.TodoItem> getTodosByStatus(String userId, String status) {
        log.info("상태별 할일 조회 - 사용자ID: {}, 상태: {}", userId, status);

        List<Todo> todos = todoRepository.findByUser_tUserUidAndStatus(userId, status);

        return todos.stream()
                .map(this::toTodoItem)
                .toList();
    }

    /**
     * 우선순위별 할일 조회
     */
    @Transactional(readOnly = true)
    public List<TodoDto.Response.TodoItem> getTodosByPriority(String userId, String priority) {
        log.info("우선순위별 할일 조회 - 사용자ID: {}, 우선순위: {}", userId, priority);

        List<Todo> todos = todoRepository.findByUser_tUserUidAndPriority(userId, priority);

        return todos.stream()
                .map(this::toTodoItem)
                .toList();
    }

    /**
     * 기한 초과 할일 조회
     */
    @Transactional(readOnly = true)
    public List<TodoDto.Response.TodoItem> getOverdueTodos(String userId) {
        log.info("기한 초과 할일 조회 - 사용자ID: {}", userId);

        LocalDateTime now = LocalDateTime.now();
        List<Todo> todos = todoRepository.findByUser_tUserUidAndDueDateBeforeAndStatusNot(userId, now, "COMPLETED");

        return todos.stream()
                .map(this::toTodoItem)
                .toList();
    }

    /**
     * 할일 완료 처리
     */
    public TodoDto.Response.TodoItem completeTodo(String userId, String todoId) {
        log.info("할일 완료 처리 - 사용자ID: {}, 할일ID: {}", userId, todoId);

        Todo todo = todoRepository.findBytTodoUidAndUser_tUserUid(todoId, userId)
                .orElseThrow(() -> new RuntimeException("할일을 찾을 수 없습니다"));

        todo.setStatus("COMPLETED");
        Todo saved = todoRepository.save(todo);

        return toTodoItem(saved);
    }

    /**
     * 할일 상태 변경
     */
    public TodoDto.Response.TodoItem updateTodoStatus(String userId, String todoId, String status) {
        log.info("할일 상태 변경 - 사용자ID: {}, 할일ID: {}, 상태: {}", userId, todoId, status);

        Todo todo = todoRepository.findBytTodoUidAndUser_tUserUid(todoId, userId)
                .orElseThrow(() -> new RuntimeException("할일을 찾을 수 없습니다"));

        todo.setStatus(status);
        Todo saved = todoRepository.save(todo);

        return toTodoItem(saved);
    }

    /**
     * 키워드로 할일 검색
     */
    @Transactional(readOnly = true)
    public List<TodoDto.Response.TodoItem> searchTodos(String userId, String keyword) {
        log.info("할일 검색 - 사용자ID: {}, 키워드: {}", userId, keyword);

        List<Todo> todos = todoRepository.findByUser_tUserUidAndTitleContainingOrDescriptionContaining(
                userId, keyword, keyword);

        return todos.stream()
                .map(this::toTodoItem)
                .toList();
    }

    /**
     * 오늘 할 일 조회
     */
    @Transactional(readOnly = true)
    public List<TodoDto.Response.TodoItem> getTodayTodos(String userId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        List<Todo> todos = todoRepository.findByUser_tUserUidAndDueDateBetween(userId, startOfDay, endOfDay);

        return todos.stream()
                .map(this::toTodoItem)
                .toList();
    }

    /**
     * 이번 주 할일 조회
     */
    @Transactional(readOnly = true)
    public List<TodoDto.Response.TodoItem> getThisWeekTodos(String userId) {
        LocalDateTime startOfWeek = LocalDateTime.now().minusDays(LocalDateTime.now().getDayOfWeek().getValue() - 1)
                .withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);

        List<Todo> todos = todoRepository.findByUser_tUserUidAndDueDateBetween(userId, startOfWeek, endOfWeek);

        return todos.stream()
                .map(this::toTodoItem)
                .toList();
    }

    /**
     * 완료율 계산
     */
    @Transactional(readOnly = true)
    public double getCompletionRate(String userId) {
        List<Todo> allTodos = todoRepository.findByUser_tUserUid(userId);

        if (allTodos.isEmpty()) return 0.0;

        long completedCount = allTodos.stream()
                .mapToLong(todo -> "COMPLETED".equals(todo.getStatus()) ? 1 : 0)
                .sum();

        return (double) completedCount / allTodos.size() * 100;
    }

    private TodoDto.Response.TodoItem toTodoItem(Todo todo) {
        return TodoDto.Response.TodoItem.builder()
                .todoId(todo.getTTodoUid())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .dueDate(todo.getDueDate())
                .status(todo.getStatus())
                .priority(todo.getPriority())
                .category(todo.getCategory())
                .source(todo.getSource())
                .rawText(todo.getRawText())
                .createDate(todo.getCreateDate())
                .modifyDate(todo.getModifyDate())
                .build();
    }

    private int getPriorityOrder(String priority) {
        return switch (priority.toUpperCase()) {
            case "URGENT" -> 1;
            case "HIGH" -> 2;
            case "MEDIUM" -> 3;
            case "LOW" -> 4;
            default -> 5;
        };
    }
}