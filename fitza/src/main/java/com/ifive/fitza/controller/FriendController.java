package com.ifive.fitza.controller;

import com.ifive.fitza.code.SuccessCode;
import com.ifive.fitza.dto.FriendRequestDTO;
import com.ifive.fitza.dto.FriendRequestResponseDTO;
import com.ifive.fitza.dto.FriendResponseDTO;
import com.ifive.fitza.response.ResponseDTO;
import com.ifive.fitza.service.FriendService;
import com.ifive.fitza.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;
    private final JWTUtil jwtUtil;

    @PostMapping("/request")
public ResponseEntity<ResponseDTO> sendRequest(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody FriendRequestDTO requestDTO) {

    String username = jwtUtil.getUsername(authHeader.replace("Bearer ", ""));
    friendService.sendFriendRequest(username, requestDTO.getPhone());

    return ResponseEntity
            .status(SuccessCode.SUCCESS_ADD_FRIEND.getStatus().value())
            .body(new ResponseDTO<>(SuccessCode.SUCCESS_ADD_FRIEND, null));
}


    //친구 수락 or 거절
    @PostMapping("/respond/{requestId}")
    public ResponseEntity<ResponseDTO> respondToRequest(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long requestId,
            @RequestParam boolean accept) {

        String username = jwtUtil.getUsername(authHeader.replace("Bearer ", ""));
        friendService.respondToRequest(username, requestId, accept);

        return ResponseEntity
        .status(SuccessCode.SUCCESS_RESPOND_FRIEND.getStatus().value())
        .body(new ResponseDTO<>(SuccessCode.SUCCESS_RESPOND_FRIEND, null));

    }

    //친구 목록 조회
    @GetMapping("/list")
    public ResponseEntity<ResponseDTO> getFriends(
            @RequestHeader("Authorization") String authHeader) {

        String username = jwtUtil.getUsername(authHeader.replace("Bearer ", ""));
        List<FriendResponseDTO> friends = friendService.getFriends(username);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_GET_FRIEND_LIST.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_GET_FRIEND_LIST, friends));
    }

    //친구 삭제
    @DeleteMapping("/delete/{friendId}")
    public ResponseEntity<ResponseDTO> deleteFriend(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long friendId) {

        String username = jwtUtil.getUsername(authHeader.replace("Bearer ", ""));
        friendService.deleteFriend(username, friendId);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_DELETE_FRIEND.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_DELETE_FRIEND, null));
    }

    //받은 친구 요청 목록 확인 
    @GetMapping("/received")
    public ResponseEntity<ResponseDTO> getReceivedRequests(
            @RequestHeader("Authorization") String authHeader) {

        String username = jwtUtil.getUsername(authHeader.replace("Bearer ", ""));
        List<FriendRequestResponseDTO> requests = friendService.getReceivedRequests(username);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_GET_REQUESTS.getStatus().value())
                .body(new ResponseDTO<>(SuccessCode.SUCCESS_GET_REQUESTS, requests));
    }

}

