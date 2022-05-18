package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public GetUserInfoRes selectUserInfo(int userIdx){
        //유저 정보 조회.
        String selectUserInfoQuery = "SELECT u.userIdx as userIdx, u.nickName as nickName, u.name as name, u.profileImgUrl as profileImgUrl, u.website as website, u.introduce as introduce,\n" +
                "If(postCount is null, 0, postCount) as postCount,\n" + //null 대신 0 출력
                "If(followerCount is null, 0, followerCount) as followerCount,\n" +
                "If(followingCount is null, 0, followingCount) as followingCount\n" +
                "FROM User as u \n" +
                "left join (select userIdx, count(postIdx) as postCount from Post WHERE status = 'ACTIVE' GROUP BY userIdx) p on p.userIdx = u.userIdx\n" +
                "left join (select followerIdx, count(followIdx) as followerCount from Follow WHERE status = 'ACTIVE' GROUP BY followerIdx) fc on fc.followerIdx = u.userIdx\n" +
                "left join (select followingIdx, count(followIdx) as followingCount from Follow WHERE status = 'ACTIVE' GROUP BY followingIdx) f on f.followingIdx = u.userIdx\n" +
                "WHERE u.userIdx =? and u.status = 'ACTIVE'"
                //user들의 상태가 ACTIVE인것만 추출하는 쿼리문
                //쿼리문에서  ? 에 들어가는 내용은 밑에있는 selectUserInfoParam 변수로 값을 받아 넣어줄 것임.
                ;

        int selectUserInfoParam=userIdx;
        return this.jdbcTemplate.queryForObject(selectUserInfoQuery, //list 면 query 아니면 queryForObject
                (rs,rowNum) -> new GetUserInfoRes(
                        //model에서 작성한 것과 동일하게 작성해야합니다.
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("name"),
                        rs.getString("profileImgUrl"),
                        rs.getString("website"),
                        rs.getString("introduce"),
                        rs.getInt("followerCount"),
                        rs.getInt("followingCount"),
                        rs.getInt("postCount")
                ),selectUserInfoParam);
    }

    public List<GetUserPostsRes> selectUserPosts(int userIdx){
        //게시글 리스트 반환.
        String selectUserPostsQuery =
                "Select p.postIdx as postIdx, pi.imgUrl as postImgUrl\n" +
                "FROM Post as p\n" +
                "join User as u on u.userIdx = p.userIdx\n" +
                "join PostImgUrl as pi on pi.postIdx = p.postIdx and pi.status = 'ACTIVE'\n" +
                "WHERE p.status = 'ACTIVE' and u.userIdx = ?\n" +
                "group by p.postIdx\n" +
                "HAVING min(pi.postImgUrlIdx)\n" +
                "order by p.postIdx;";
        int selectUserPostsParam=userIdx;
        return this.jdbcTemplate.query(selectUserPostsQuery, //list 면 query 아니면 queryForObject
                (rs,rowNum) -> new GetUserPostsRes(
                        rs.getInt("postIdx"),
                        rs.getString("postImgUrl")
                ),selectUserPostsParam);
    }
    public GetUserRes getUsersByIdx(int userIdx){
        String getUsersByIdxQuery = "select userIdx,name,nickName,email from User where userIdx=?";
        int getUsersByIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUsersByIdxQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email")),
                getUsersByIdxParams);
    }
    public int deleteUsersByIdx(int userIdx){
        String deleteUsersByIdxQuery = "update User set status = 'DELETED' where userIdx=?";
        return this.jdbcTemplate.update(deleteUsersByIdxQuery, userIdx);

    }
    public int checkUser(int userIdx){
        String checkUserQuery = "select exists(select userIdx from User where userIdx = ?)";
        int checkUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserQuery, int.class, checkUserParams);
    }
    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into User (name, nickName, phone, email, password) VALUES (?,?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getName(), postUserReq.getNickName(),postUserReq.getPhone(), postUserReq.getEmail(), postUserReq.getPassword()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    public int checkUserExist(int userIdx){
        //validation check
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ?)";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);

    }

    public int modifyUserName(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update User set nickName = ? where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getNickName(), patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }




}
