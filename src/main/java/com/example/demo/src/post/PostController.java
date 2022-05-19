package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.post.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final PostProvider postProvider;
    @Autowired
    private final PostService postService;
    @Autowired
    private final JwtService jwtService;


    public PostController(PostProvider postProvider, PostService postService, JwtService jwtService) {
        this.postProvider = postProvider;
        this.postService = postService;
        this.jwtService = jwtService;
    }
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetPostsRes>> getPosts(@RequestParam int userIdx) {
        //홈화면에서 스크롤을 하면서 게시글을 여러개 보기때문에 리스트로 한다.~~
        try{
            List<GetPostsRes> getPostsRes = postProvider.retrievePosts(userIdx);
            return new BaseResponse<>(getPostsRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostPostsRes> createPost(@RequestBody PostPostsReq postPostsReq) {

        try{
            //validation check 게시글 글자수 제한
            if(postPostsReq.getContent().length()>450) {
                return new BaseResponse<>(BaseResponseStatus.POST_POSTS_INVALID_CONTENTS);
            }
            //validation check 게시글 이미지 있어야 한다.
            if(postPostsReq.getPostImgUrls().size()<1) {
                return new BaseResponse<>(BaseResponseStatus.POST_POSTS_EMPTY_IMGURL);
            }
            PostPostsRes postPostsRes = postService.createPost(postPostsReq.getUserIdx(),postPostsReq);
            //getUserIdx 쓰는 이유 나중에 jwx 로 idx 받아서 사용할 것이기에 편리성을 위해
            return new BaseResponse<>(postPostsRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    } // postman test Post > localhost:9000/posts
    // Body -> raw -> "JSON" -> { "userIdx":1, "content":"Test", "postImgUrls":["testUrl1","testUrl2"] }
    // 응답결과 요청에 성공했습니다. + 방금 들어간 postIdx 반환 시 성공 + datagrip에서도 확인해보자.

    @ResponseBody
    @PatchMapping("/{postIdx}") //patch method
    public BaseResponse<String> modifyPost(@PathVariable ("postIdx") int postIdx, @RequestBody PatchPostsReq patchPostsReq) {
        //응답모델 (PatchPostsRes)를 만드는 대신 <String> 즉 문자열로 반환하는 것으로 만들어보자.

        //Validation chk
        if(patchPostsReq.getContent().length()>450) {
            return new BaseResponse<>(BaseResponseStatus.POST_POSTS_INVALID_CONTENTS);
        } // 수정 후에 게시글의 내용이 허용된 글자수를 넘어서면 throw.

        try{
            //게시글을 수정 후 오류가 안나면 string을 출력하는 logic 이라 result 변수 생성 + return 값 string
            postService.modifyPost(patchPostsReq.getUserIdx(), postIdx, patchPostsReq);
            //getUserIdx 쓰는 이유 나중에 jwx 로 idx 받아서 사용할 것이기에 편리성을 위해
            String result = "게시글이 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }// postman test patch > localhost:9000/posts/1
    // Body -> raw -> "JSON" -> { "userIdx":1, "content":"test post modify" }
    // 게시글이 수정되었습니다. 반환 시 성공 + data grip 에서도 확인해보자.

    @ResponseBody
    @PatchMapping("/{postIdx}/status")
    //postIdx까지만 쓰면 게시글 수정과 겹쳐서 status를 추가로 적어줌.
    public BaseResponse<String> deletePost(@PathVariable ("postIdx") int postIdx) {
        //삭제는 전달받을 것이 없으므로 모델 생성 할 필요 x
        try{
            postService.deletePost(postIdx);
            String result = "게시글이 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }// postman test patch > localhost:9000/posts/1/status
    // 게시글이 삭제되었습니다. 반환 시 성공 + data grip 에서도 확인해보자.

}