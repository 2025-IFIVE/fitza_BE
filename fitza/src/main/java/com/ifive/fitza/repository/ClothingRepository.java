package com.ifive.fitza.repository;

import com.ifive.fitza.entity.ClothingDetails;
import com.ifive.fitza.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClothingRepository extends JpaRepository<ClothingDetails, Long> {

    // 특정 사용자(user)가 등록한 옷 전체 조회
    List<ClothingDetails> findByUser(UserEntity user);

    @Query("SELECT c FROM ClothingDetails c WHERE c.user.userid = :userId")
    List<ClothingDetails> findByUserId(@Param("userId") Long userId);
}

