package com.neekly_report.whirlwind.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "T_REFRESH_TOKEN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @Column(name = "T_USER_UID")
    private String tUserUid; // userId → tUserUid

    @Column(name = "REFRESH_TOKEN", nullable = false, length = 512)
    private String refreshToken;
}
