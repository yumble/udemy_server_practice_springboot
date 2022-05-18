package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserInfoRes {

        private int userIdx; //강의와 달리 내 DB와 맞게 userIdx 값을 추가하였다.
        private String nickName;
        private String name;
        private String profileImgUrl;
        private String website;
        private String introduce; //강의와 달리 introduction이 아닌 내 DB에 맞게 introduce로 반영
        private int followerCount;
        private int followingCount;
        private int postCount;

}
