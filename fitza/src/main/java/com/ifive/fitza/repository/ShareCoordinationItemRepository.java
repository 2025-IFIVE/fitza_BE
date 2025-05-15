package com.ifive.fitza.repository;

import com.ifive.fitza.entity.ShareCoordination;
import com.ifive.fitza.entity.ShareCoordinationItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShareCoordinationItemRepository extends JpaRepository<ShareCoordinationItem, Long> {
    List<ShareCoordinationItem> findByShare(ShareCoordination share);
    void deleteByShare(ShareCoordination share);
}
