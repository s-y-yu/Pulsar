package com.hammer.pulsar.controller;

import com.hammer.pulsar.dto.NotDetermined;
import com.hammer.pulsar.dto.article.Article;
import com.hammer.pulsar.dto.article.ArticleModifyForm;
import com.hammer.pulsar.dto.article.ArticlePreview;
import com.hammer.pulsar.dto.article.ArticleWriteForm;
import com.hammer.pulsar.dto.interaction.Comment;
import com.hammer.pulsar.dto.interaction.Like;
import com.hammer.pulsar.dto.interaction.LikeRequest;
import com.hammer.pulsar.service.ArticleService;
import com.hammer.pulsar.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

// 커뮤니티 관련 API 요청을 처리할 REST 컨트롤러
@RestController
@RequestMapping("/community")
public class CommunityRestController {

    // 커뮤니티 관련 서비스 로직을 처리할 클래스
    private final ArticleService articleService;
    private final InteractionService interactionService;

    // 컨트롤러의 생성자
    @Autowired
    public CommunityRestController(ArticleService articleService, InteractionService interactionService) {
        this.articleService = articleService;
        this.interactionService = interactionService;
    }

    // 게시글 목록 조회 API
    @GetMapping()
    public ResponseEntity<List<ArticlePreview>> showAllArticles() {
        List<ArticlePreview> articleList = articleService.getAllArticles();

        return new ResponseEntity<>(articleList, HttpStatus.OK);
    }

    // 글 상세보기 API
    @GetMapping("/{articleId}/static")
    public ResponseEntity<Article> showArticleDetail(@PathVariable int articleId) {
        Article article = articleService.getArticle(articleId);

        return new ResponseEntity<>(article, HttpStatus.OK);
    }

    // 글 작성하기 API
    @PostMapping()
    public ResponseEntity<Integer> writeArticle(ArticleWriteForm form, MultipartFile[] imgFiles,
                                                HttpServletRequest request) {
        // 로그인 회원의 고유번호
        // TODO: 회원번호를 가져온 것은 임시코드이므로 로그인 구현시 수정할 것
        int memberId = (Integer) request.getSession().getAttribute("memberId");

        int articleNo = articleService.writeArticle(form, imgFiles, memberId);

        // TODO: OK보다 CREATED가 어울린다면 변경
        return new ResponseEntity<>(articleNo, HttpStatus.OK);
    }

    // 글 수정하기 API
    @PutMapping("/{articleId}")
    public ResponseEntity<Void> modifyArticle(@PathVariable int articleId, ArticleModifyForm form,
                                              MultipartFile[] imgFiles) {
        articleService.modifyArticle(form, imgFiles);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 글 삭제하기 API
    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> removeArticle(@PathVariable int articleId) {
        articleService.removeArticle(articleId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 추천 수 가져오기 API
    @GetMapping("/{articleId}/active/like")
    public ResponseEntity<Like> showLikes(@PathVariable int articleId, HttpServletRequest request) {
        // 로그인 한 회원의 아이디를 불러오기
        // TODO: 로그인 로직은 아직 구현되지 않았으므로 변경 가능함
        int memberId = (Integer) request.getSession().getAttribute("memberId");

        // 추천수를 가져오기 위해 필요한 정보를 담기
        LikeRequest likeRequest = new LikeRequest();
        likeRequest.setMemberId(memberId);
        likeRequest.setArticleId(articleId);

        // 해당 게시글의 추천수를 가져오기
        Like likes = interactionService.countAllLikes(likeRequest);

        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    // 추천하기 API
    @PostMapping("/{articleId}/active/like")
    public ResponseEntity<Like> toggleLIkeStatus(@PathVariable int articleId, HttpServletRequest request) {
        // 로그인한 회원의 아이디를 불러오기
        // TODO: 로그인 로직은 아직 구현되지 않았으므로 변경 가능함
        int memberId = (Integer) request.getSession().getAttribute("memberId");

        // 추천하기 위해 필요한 정보를 담기
        LikeRequest likeRequest = new LikeRequest();
        likeRequest.setMemberId(memberId);
        likeRequest.setArticleId(articleId);

        // 추천 상태를 변경하기
        Like likes = interactionService.toggleLike(likeRequest);

        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    // 댓글 가져오기 API
    @GetMapping("/{articleId}/active/comment")
    public ResponseEntity<List<Comment>> getAllComments(@PathVariable int articleId) {
        List<Comment> comments = interactionService.getAllComments(articleId);

        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

}
