package com.ifive.fitza.repository;

import com.ifive.fitza.entity.ProfileEntity;
import com.ifive.fitza.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    Optional<ProfileEntity> findByUser(UserEntity user);

    boolean existsByUser(UserEntity user);
}

