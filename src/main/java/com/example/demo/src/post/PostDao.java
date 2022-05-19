package com.example.demo.src.post;


import com.example.demo.src.post.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PostDao {

    private JdbcTemplate jdbcTemplate;
    private List<GetPostImgRes> getPostImgRes;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public List<GetPostsRes> selectPosts(int userIdx) {
        //게시글 리스트 반환.
        String selectPostsQuery =
                " SELECT p.postIdx as postIdx, " +
                " u.userIdx as userIdx," +
                " u.nickName as nickName, " +
                " u.profileImgUrl as profileImgUrl, " +
                " p.content as content, " +
                " IF(postLikeCount is null, 0, postLikeCount) as postLikeCount, " +
                " IF(commentCount is null, 0, commentCount) as commentCount, "+
                " case when timestampdiff(second, p.updatedAt, current_timestamp) < 60 "+
                " THEN concat(timestampdiff(second, p.updatedAt, current_timestamp), '초 전')" +
                " WHEN timestampdiff(minute, p.updatedAt, current_timestamp) < 60 " +
                " THEN concat(timestampdiff(minute, p.updatedAt, current_timestamp), '분 전') " +
                " WHEN timestampdiff(hour, p.updatedAt, current_timestamp) < 24 " +
                " THEN concat(timestampdiff(hour, p.updatedAt, current_timestamp), '시간 전')" +
                " WHEN timestampdiff(day, p.updatedAt, current_timestamp) < 365 " +
                " THEN concat(timestampdiff(day, p.updatedAt, current_timestamp), '일 전') " +
                " ELSE timestampdiff(year, p.updatedAt, current_timestamp) " +
                " END AS updatedAt, " +
                " IF(px.status = 'ACTIVE', 'Y', 'N') as likeOrNot "+
                " FROM Post as p " +
                " JOIN User as u on u.userIdx = p.userIdx " +
                " LEFT JOIN (SELECT postIdx, userIdx, status, count(postLikeIdx) as postLikeCount FROM PostLike WHERE status ='ACTIVE' GROUP BY postIdx) as px on p.postIdx = px.postIdx "+
                " LEFT JOIN (SELECT postIdx, COUNT(commentIdx) as commentCount FROM Comment WHERE status='ACTIVE') as c on c.postIdx = p.postIdx "+
                " WHERE p.status = 'ACTIVE' and p.postIdx = ?";
        int selectPostsParam = userIdx;
        return this.jdbcTemplate.query(selectPostsQuery, //list 면 query 아니면 queryForObject
                (rs, rowNum) -> new GetPostsRes(
                        rs.getInt("postIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("content"),
                        rs.getInt("postLikeCount"),
                        rs.getInt("commentCount"),
                        rs.getString("updatedAt"),
                        rs.getString("likeOrNot"),
                        getPostImgRes = this.jdbcTemplate.query(
                                " SELECT pi.postImgUrlIdx, pi.imgUrl \n" +
                                        " FROM PostImgUrl as pi\n" +
                                        " join Post as p on p.postIdx=pi.postIdx\n" +
                                        " WHERE pi.status = 'ACTIVE' and p.postIdx = ?",
                                (rk, rownum) -> new GetPostImgRes(
                                        rk.getInt("postImgUrlIdx"),
                                        rk.getString("imgUrl")
                                ), rs.getInt("postIdx")
                        )
                ), selectPostsParam);
    }

    public int insertPost(int userIdx, String content) {
        String insertPostQuery = "INSERT INTO Post (userIdx, content) VALUES (?,?)"; //게시글 작성 쿼리문
        Object[] insertPostParams = new Object[] {userIdx, content}; // 위의 코드의  ? 에 넣을 인자들을 담은 객체
        this.jdbcTemplate.update(insertPostQuery, insertPostParams);
        // 쿼리문 실행 select가 아닌 insert를 하기에 return 필요 x 그리고 jdbcTemplate.update를 이용해야한다.

        String lastInsertIdxQuery = "SELECT last_insert_id()";
        //방금 넣은 post의 인덱스를 클라이언트에게 전달하기 위한 쿼리문
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery,int.class);
        //select 이므로 return + queryForObject 이용..!!
    }

    public int insertPostImgs(int postIdx, PostImgUrlReq postImgUrlReq) {
        String insertPostImgsQuery = "INSERT INTO PostImgUrl (postIdx, imgUrl) VALUES (?,?)";
        //서비스에서 반복문을 통해 이미지 리스트에 원소 한 개 한 개씩 dao로 오게된다~~~
        Object[] insertPostImgsParams = new Object[] {postIdx, postImgUrlReq.getImgUrl()};
        this.jdbcTemplate.update(insertPostImgsQuery, insertPostImgsParams);

        String lastInsertIdxQuery = "SELECT last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery,int.class);
    }

    public int updatePost(int postIdx, String content) {
        String updatePostQuery = "UPDATE Post SET content=? WHERE postIdx=?";
        //내용과 게시물 인덱스를 받아서 내용 수정
        Object[] updatePostParams = new Object[] {content, postIdx};
        return this.jdbcTemplate.update(updatePostQuery, updatePostParams);

    }
    public int deletePost(int postIdx) {
        //이름은 delete이지만 실제 쿼리문은 status를 inactive로 바꾸는 것을 참조하길 바람.

        String deletePostQuery = "UPDATE Post SET status='INACTIVE' WHERE postIdx=?;";
        Object[] deletePostParams = new Object[] {postIdx};
        return this.jdbcTemplate.update(deletePostQuery, deletePostParams);
    }
    public int deletePostImgUrl(int postIdx) {
        String deletePostQuery = "UPDATE PostImgUrl SET status='INACTIVE' WHERE postIdx=?;";
        Object[] deletePostParams = new Object[] {postIdx};
        return this.jdbcTemplate.update(deletePostQuery, deletePostParams);
    }

    public int checkUserExist(int userIdx) {
        //validation check : 유저 존재 여부
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ?)";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);
    }
    public int checkPostExist(int postIdx) {
        //validation check : 게시글 존재 여은
        String checkPostExistQuery = "select exists(select postIdx from Post where postIdx = ?)";
        int checkPostExistParams = postIdx;
        return this.jdbcTemplate.queryForObject(checkPostExistQuery,
                int.class,
                checkPostExistParams);
    }
}
