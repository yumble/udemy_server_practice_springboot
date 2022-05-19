package com.example.demo.src.post;


import com.example.demo.config.BaseException;
import com.example.demo.src.post.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class PostService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostDao postDao;
    private final PostProvider postProvider;
    private final JwtService jwtService;


    @Autowired
    public PostService(PostDao postDao, PostProvider postProvider, JwtService jwtService) {
        this.postDao = postDao;
        this.postProvider = postProvider;
        this.jwtService = jwtService;

    }

    public PostPostsRes createPost(int userIdx, PostPostsReq postPostsReq) throws BaseException {
        try {
            int postIdx = postDao.insertPost(userIdx,postPostsReq.getContent());
            //insertPosts에는 게시글의 내용만 넣을 예정
            // img를 넣지 않는 이유는 이미지는 여러 개라서 밑에 코드처럼
            // 반복문을 이용해서 리스트로 반환.

            for(int i=0; i<postPostsReq.getPostImgUrls().size();i++){
                postDao.insertPostImgs(postIdx, postPostsReq.getPostImgUrls().get(i));
            }

            return new PostPostsRes(postIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyPost(int userIdx, int postIdx, PatchPostsReq patchPostsReq) throws BaseException {
        //service에서 controller로 전달해야하는 return 값 없어서 void로 설정.

        //validation chk : user 존재 여부
        if(postProvider.checkUserExist(userIdx) == 0) {
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        //validation chk : 게시글 존재 여부
        if(postProvider.checkPostExist(postIdx) == 0) {
            throw new BaseException(POSTS_EMPTY_POST_ID);
        }

        try {
            int result = postDao.updatePost(postIdx,patchPostsReq.getContent());
            //dao 에게 게시물의 인덱스와 내용 전달. 이미지는 수정할 수 없기에 인자로 전달하지 않음.
            // result가 0이라는 말 데이터베이스 접근 오류이거나 데이터가 정상적으로 수정되지 않았음을 의미.
            if(result ==0){
                throw new BaseException(MODIFY_FAIL_POST);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deletePost(int postIdx) throws BaseException {
        try {
            int result = postDao.deletePost(postIdx);
            if(result ==0){
                throw new BaseException(DELETE_FAIL_POST);
            }
            int additional_result = postDao.deletePostImgUrl(postIdx);
            if(additional_result ==0){
                throw new BaseException(DELETE_FAIL_POST);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
