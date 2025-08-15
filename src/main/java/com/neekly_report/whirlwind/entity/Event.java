package com.neekly_report.whirlwind.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "T_EVENT")
public class Event extends Common {

    @Id
    @Column(name = "T_EVENT_UID", nullable = false, updatable = false)
    private String eventId;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Column(name = "END_TIME")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "T_USER_UID")
    private User user;
}

