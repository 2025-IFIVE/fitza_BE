package com.ifive.fitza.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coordination_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoordinationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private CalendarCoordination calendar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cloth_id")
    private ClothingDetails clothing;

    private Double x;
    private Double y;
    private Double size;
}
