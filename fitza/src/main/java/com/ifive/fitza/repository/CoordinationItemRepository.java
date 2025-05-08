package com.ifive.fitza.repository;

import com.ifive.fitza.entity.CalendarCoordination;
import com.ifive.fitza.entity.CoordinationItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoordinationItemRepository extends JpaRepository<CoordinationItem, Long> {
    List<CoordinationItem> findByCalendar(CalendarCoordination calendar);
    void deleteByCalendar(CalendarCoordination calendar);
}
