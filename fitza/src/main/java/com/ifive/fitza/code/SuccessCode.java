package com.ifive.fitza.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum SuccessCode {
    /**
     * User
     */
    SUCCESS_REGISTER(HttpStatus.OK, "회원가입을 성공했습니다."),
    SUCCESS_LOGIN(HttpStatus.OK, "로그인을 성공했습니다. 헤더 토큰을 확인하세요."),
    SUCCESS_RETRIEVE_USER(HttpStatus.OK, "유저 정보를 성공적으로 조회했습니다."),
    SUCCESS_REISSUE(HttpStatus.OK, "토큰 재발급을 성공했습니다."),
    SUCCESS_LOGOUT(HttpStatus.OK, "로그아웃을 성공했습니다."),
    SUCCESS_DELETE_USER(HttpStatus.OK, "회원 탈퇴를 성공했습니다."),
    SUCCESS_UPDATE(HttpStatus.OK, "업데이트가 성공적으로 완료되었습니다."),
    SUCCESS_DELETE_CLOTHING(HttpStatus.OK, "옷 삭제가 성공했습니다."),
    SUCCESS_ANALYZE_BODY(HttpStatus.OK, "체형 분석 및 저장이 완료되었습니다."),
    SUCCESS_RETRIEVE_BODYINFO(HttpStatus.OK, "체형 정보 조회를 성공했습니다."),
    SUCCESS_CREATE_COORDINATION(HttpStatus.OK, "코디 저장 성공"),
    SUCCESS_UPDATE_COORDINATION(HttpStatus.OK, "코디 수정 성공"),
    SUCCESS_DELETE_COORDINATION(HttpStatus.OK, "코디 삭제 성공"),
    SUCCESS_ADD_FRIEND(HttpStatus.OK, "친구 요청을 성공했습니다."),
    SUCCESS_RESPOND_FRIEND(HttpStatus.OK, "친구 요청 응답이 처리되었습니다."),
    SUCCESS_GET_FRIEND_LIST(HttpStatus.OK, "친구 목록을 성공적으로 조회했습니다."),
    SUCCESS_DELETE_FRIEND(HttpStatus.OK, "친구 목록을 성공적으로 삭제했습니다.");


    private final HttpStatus status;
    private final String message;
}