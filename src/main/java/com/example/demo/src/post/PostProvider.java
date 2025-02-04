package com.example.demo.src.post;


import com.example.demo.config.BaseException;
import com.example.demo.src.post.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.USERS_EMPTY_USER_ID;
import static com.example.demo.config.BaseResponseStatus.POSTS_EMPTY_POST_ID;

//Provider : Read의 비즈니스 로직 처리
@Service
public class PostProvider {

    private final PostDao postDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PostProvider(PostDao postDao, JwtService jwtService) {
        this.postDao = postDao;
        this.jwtService = jwtService;
    }

    public List<GetPostsRes> retrievePosts(int userIdx) throws BaseException {
        Boolean isMyFeed = true; // 지금 보고 있는 피드가 내 피드라는 boolean의 값

        //validation check
        if (checkUserExist(userIdx) == 0) {
            throw new BaseException(USERS_EMPTY_USER_ID);
        } //보려고하는 user가 없는 사람이라면 에러 throw!!

        try {
            List<GetPostsRes> getPosts = postDao.selectPosts(userIdx); // 해당 유저의 게시물 리스트를 받아오는 객체
            return getPosts;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUserExist(int userIdx) throws BaseException{
        //validation check
        try{
            return postDao.checkUserExist(userIdx);
        } catch (Exception exception){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
    }
    public int checkPostExist(int postIdx) throws BaseException{
        //validation check
        try{
            return postDao.checkPostExist(postIdx);
        } catch (Exception exception){
            throw new BaseException(POSTS_EMPTY_POST_ID);
        }
    }
}
