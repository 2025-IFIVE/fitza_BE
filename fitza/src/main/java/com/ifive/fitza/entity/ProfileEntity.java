package com.ifive.fitza.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PROFILE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저 1명당 1개의 프로필만 가질 수 있다고 가정
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false, unique = true)
    private UserEntity user;

    @Column(nullable = false)
    private String style; 

    private String comment;

    @Column(name = "image_path")
    private String imagePath;
}

