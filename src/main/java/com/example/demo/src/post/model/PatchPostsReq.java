package com.example.demo.src.post.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PatchPostsReq {

    private int userIdx;
    private String content;
    //게시물 수정 이미지는 수정 불가
    //어떤 유저가 수정할건지
    //내용은 어떻게 수정할건지에 대한 인자만 필요.
}
