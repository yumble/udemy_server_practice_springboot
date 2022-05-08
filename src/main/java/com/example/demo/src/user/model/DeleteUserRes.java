package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DeleteUserRes {
    private int userIdx;
    private String name;
    private String nickName;
    private String email;

    public DeleteUserRes(int userIdx) {
    }
}
