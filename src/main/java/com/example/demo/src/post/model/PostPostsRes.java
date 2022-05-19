package com.example.demo.src.post.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostPostsRes {

    private int postIdx;
    //게시글 생성을 완료했다면 방금 생성된 postidx 를 반환하기 위함
}
