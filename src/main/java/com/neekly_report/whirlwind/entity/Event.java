package com.neekly_report.whirlwind.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "T_EVENT")
@Getter
@Setter
@NoArgsConstructor
public class Event extends Common {

    @Id
    @Column(name = "EVENT_ID", nullable = false, updatable = false)
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
    @JoinColumn(name = "USER_ID")
    private User user;
}

