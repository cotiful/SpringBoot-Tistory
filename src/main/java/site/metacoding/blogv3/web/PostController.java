package site.metacoding.blogv3.web;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpsRedirectSpec;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
import site.metacoding.blogv3.config.auth.LoginUser;
import site.metacoding.blogv3.domain.category.Category;
import site.metacoding.blogv3.domain.love.Love;
import site.metacoding.blogv3.domain.user.User;
import site.metacoding.blogv3.handler.ex.CustomException;
import site.metacoding.blogv3.service.PostService;
import site.metacoding.blogv3.web.dto.love.LoveResponseDto;
import site.metacoding.blogv3.web.dto.post.PostDetailRespDto;
import site.metacoding.blogv3.web.dto.post.PostRespDto;
import site.metacoding.blogv3.web.dto.post.PostWriteReqDto;

@RequiredArgsConstructor
@Controller
public class PostController {

    private final PostService postService;
    // CategoryService 사용하지 말고
    // PostService 사용하세요. 이유는 나중에 category, post글 다 같이 가지고 가야 하기 때문임!!

    @PostMapping("/s/api/post/{postId}/love")
    // 누가 어떤 포스트를 좋아하는지
    public ResponseEntity<?> love(@PathVariable Integer postId, @AuthenticationPrincipal LoginUser loginUser) {
        LoveResponseDto dto = postService.좋아요(postId, loginUser.getUser());
        // 이 상태로 하면 love에 프라이머리키가 없어서 나중에 취소를 못함. 하지만 f5한 번 누르면 가능 그렇지만 f5 하기 전에는 취소가안됨
        // body를 받지 않았기 때문에
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    // 삭제할 때는 loveId만 찾아서 삭제시켜주면됨.
    @DeleteMapping("/s/api/post/{postId}/love/{loveId}")
    // 누가 어떤 포스트를 좋아하는지
    public ResponseEntity<?> unLove(@PathVariable Integer loveId, @AuthenticationPrincipal LoginUser loginUser) {
        // 로그인한 유저의 userId
        // 러브에 있는 userId 를 두개 비교
        // -> 권한 체크를 해줘야 함
        postService.좋아요취소(loveId, loginUser.getUser());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/s/api/post/{id}")
    public ResponseEntity<?> postDelete(@PathVariable Integer id, @AuthenticationPrincipal LoginUser loginUser) {

        User principal = loginUser.getUser();

        postService.게시글삭제(id, principal);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/post/{id}")
    public String detail(@PathVariable Integer id, Model model, @AuthenticationPrincipal LoginUser loginUser) {

        PostDetailRespDto postDetailRespDto = null;

        if (loginUser == null) {
            postDetailRespDto = postService.게시글상세보기(id);
        } else {
            postDetailRespDto = postService.게시글상세보기(id, loginUser.getUser());
        }

        model.addAttribute("data", postDetailRespDto);

        return "/post/detail";
    }

    @PostMapping("/s/post")
    public String write(PostWriteReqDto postWriteReqDto, @AuthenticationPrincipal LoginUser loginUser) {

        postService.게시글쓰기(postWriteReqDto, loginUser.getUser());

        return "redirect:/user/" + loginUser.getUser().getId() + "/post";
    }

    @GetMapping("/s/post/write-form")
    public String writeForm(@AuthenticationPrincipal LoginUser loginUser, Model model) {
        List<Category> categorys = postService.게시글쓰기화면(loginUser.getUser());

        if (categorys.size() == 0) {
            throw new CustomException("카테고리 등록이 필요해요");
        }

        model.addAttribute("categorys", categorys);
        return "/post/writeForm";
    }

    @GetMapping("/user/{pageOwnerId}/post")
    public String postList(Integer categoryId, @PathVariable Integer pageOwnerId,
            @AuthenticationPrincipal LoginUser loginUser,
            Model model,
            @PageableDefault(size = 3) Pageable pageable) {
        // SELECT * FROM category WHERE userId = :id
        // 카테고리 가져가세요!!
        PostRespDto postRespDto = null;

        if (categoryId == null) {
            postRespDto = postService.게시글목록보기(pageOwnerId, pageable);
        } else {
            postRespDto = postService.게시글카테고리별보기(pageOwnerId, categoryId, pageable);
        }

        model.addAttribute("postRespDto", postRespDto);
        return "/post/list";
    }
}
