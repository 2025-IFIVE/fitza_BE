package com.ifive.fitza.repository;

import com.ifive.fitza.entity.ShareCoordination;
import com.ifive.fitza.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShareCoordinationRepository extends JpaRepository<ShareCoordination, Long> {
    List<ShareCoordination> findByUser(UserEntity user);
}
