package com.neekly_report.whirlwind.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "T_TASK")
@Getter
@Setter
@NoArgsConstructor
public class Task extends Common {

    @Id
    @Column(name = "TASK_ID", nullable = false, updatable = false)
    private String taskId;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "DUE_DATE")
    private LocalDateTime dueDate;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
}

