package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.GetUserFeedRes;
import com.example.demo.src.user.model.GetUserInfoRes;
import com.example.demo.src.user.model.GetUserPostsRes;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.USERS_EMPTY_USER_ID;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    public GetUserRes getUsersByIdx(int userIdx) throws BaseException{
        try{
            GetUserRes getUsersRes = userDao.getUsersByIdx(userIdx);
            return getUsersRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetUserFeedRes retrieveUserFeed(int userIdxByJwt, int userIdx) throws BaseException {
        Boolean isMyFeed = true; // 지금 보고 있는 피드가 내 피드라는 boolean의 값

        //validation check
        if (checkUserExist(userIdx) == 0) {
            throw new BaseException(USERS_EMPTY_USER_ID);
        } //보려고하는 user가 없는 사람이라면 에러 throw!!

        try {
            if (userIdxByJwt != userIdx) {
                isMyFeed = false;
            }
            // login 된 정보 >> userIdxByjwt
            // pathvariable로 받은 정보 즉 클릭한 피드 주인>> userIdx
            // 두 개가 같다면 내 피드라는 isMyFeed 는 true 아니면 false
            GetUserInfoRes getUserInfo = userDao.selectUserInfo(userIdx); // 유저 정보 받아오는 객체
            List<GetUserPostsRes> getUserPosts = userDao.selectUserPosts(userIdx); // 해당 유저의 게시물 리스트를 받아오는 객체

            GetUserFeedRes getUsersRes = new GetUserFeedRes(isMyFeed, getUserInfo, getUserPosts);
            // 객체 생성

            return getUsersRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkEmail(String email) throws BaseException{
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUserExist(int userIdx) throws BaseException{
        //validation check
        try{
            return userDao.checkUserExist(userIdx);
        } catch (Exception exception){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
    }


}
