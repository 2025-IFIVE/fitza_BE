package com.ifive.fitza.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CLOTHINGDETAILS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClothingDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clothid;  // 옷 ID (PK)

    private String type;        // 의류종류
    private String category;    // 카테고리
    private String length;      // 기장
    private String sleeve;      // 소매기장
    private String neckline;    // 넥라인
    private String neck;        // 칼라
    private String fit;         // 핏
    private String color;       // 색상
    private String material;    // 소재
    private String detail;      // 디테일
    private String print;       // 프린트
    private String style;       // 스타일
    private String substyle;    // 서브스타일

    @Column(name = "image_path")
    private String imagePath;   // 이미지 경로

    @Column(name = "cropped_path")
    private String croppedPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private UserEntity user;    // 옷 등록 유저
}
