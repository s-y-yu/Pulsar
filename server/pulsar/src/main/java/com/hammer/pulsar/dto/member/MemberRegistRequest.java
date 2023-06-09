package com.hammer.pulsar.dto.member;

// DB에 새로운 회원 정보를 추가하기 위한 정보를 담고 있는 DTO 클래스
public class MemberRegistRequest {
    // 저장한 고유번호를 담기 위한 변수
    private int memberId;
    // 이메일
    private String email;
    // 패스워드
    private String password;
    // 닉네임
    private String nickname;
    // DB에 저장된 프로필 이미지 경로
    private String profileImg;

    // 기본 생성자
    public MemberRegistRequest() {}

    // MemberRegistForm, profileImg을 인자로 받는 생성자
    public MemberRegistRequest(MemberRegistForm form, String profileImg) {
        this.email = form.getEmail();
        this.password = form.getPassword();
        this.nickname = form.getNickname();
        this.profileImg = profileImg;
    }

    // Getters, Setters
    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    // toString
    @Override
    public String toString() {
        return "MemberRegistRequest{" +
                "memberId=" + memberId +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", profileImg='" + profileImg + '\'' +
                '}';
    }

}
