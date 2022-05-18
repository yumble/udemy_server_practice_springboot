package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserFeedRes {
    private boolean _isMyFeed; //내 피드를 볼 때는 수정권한 줘야함
    private GetUserInfoRes getUserInfo; //게시글을 제외한 팔로잉 팔로워 수 등등을 포함한 정보
    private List<GetUserPostsRes> getUserPosts; // 게시글을 담은 정보가 있는곳

}
