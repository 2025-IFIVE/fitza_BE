package com.ifive.fitza.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "share_coordination_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareCoordinationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_id")
    private ShareCoordination share;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cloth_id")
    private ClothingDetails clothing;

    private Double x;
    private Double y;
    private Double size;
}
