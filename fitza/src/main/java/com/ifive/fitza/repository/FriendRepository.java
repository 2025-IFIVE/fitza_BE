package com.ifive.fitza.repository;

import com.ifive.fitza.entity.FriendEntity;
import com.ifive.fitza.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<FriendEntity, Long> {

    // 중복 신청 방지용
    Optional<FriendEntity> findByUserAndFriend(UserEntity user, UserEntity friend);

    // 받은 친구 요청
    List<FriendEntity> findByFriendAndStatus(UserEntity friend, String status);

    // 보낸 친구 요청
    List<FriendEntity> findByUserAndStatus(UserEntity user, String status);

    // 친구 목록 (status = ACCEPTED)
    List<FriendEntity> findByUserOrFriendAndStatus(UserEntity user, UserEntity friend, String status);
    List<FriendEntity> findByStatusAndUserOrFriend(String status, UserEntity user1, UserEntity user2);

    @Query("SELECT f FROM FriendEntity f WHERE f.status = 'ACCEPTED' AND (f.user = :me OR f.friend = :me)")
    List<FriendEntity> findAcceptedFriends(@Param("me") UserEntity me);
}
