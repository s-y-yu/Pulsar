package com.hammer.pulsar.service;

import com.hammer.pulsar.dao.ArticleDao;
import com.hammer.pulsar.dao.CommentDao;
import com.hammer.pulsar.dao.LikeDao;
import com.hammer.pulsar.dto.interaction.Comment;
import com.hammer.pulsar.dto.interaction.CommentWriteRequest;
import com.hammer.pulsar.dto.interaction.Like;
import com.hammer.pulsar.dto.interaction.LikeRequest;
import com.hammer.pulsar.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

// 실제 로직이 구현된 InteractionService 인터페이스의 구현체 클래스
@Service
public class InteractionServiceImpl implements InteractionService {
    private final LikeDao likeDao;
    private final ArticleDao articleDao;
    private final CommentDao commentDao;

    @Autowired
    public InteractionServiceImpl(LikeDao likeDao, ArticleDao articleDao, CommentDao commentDao) {
        this.likeDao = likeDao;
        this.articleDao = articleDao;
        this.commentDao = commentDao;
    }

    /**
     * 게시글의 총 추천수와 회원의 추천 여부를 반환하는 메서드
     *
     * @param request
     * @return
     */
    @Override
    public Like countAllLikes(LikeRequest request) {
        Like result = new Like();

        // 추천 여부 불러오기
        result.setLiked(likeDao.selectLikeByMemberId(request));
        // 총 추천수 불러오기
        result.setLikes(articleDao.countLikes(request.getArticleId()));

        return result;
    }

    /**
     * 회원의 추천 상태를 확인한 후, 추천 상태를 반대로 바꾼다
     * 게시글의 추천수를 변경한다.
     *
     * @param request
     * @return
     */
    @Override
    public Like toggleLike(LikeRequest request) {
        Like result = new Like();

        // 추천 여부 불러오기
        boolean liked = likeDao.selectLikeByMemberId(request);

        // 추천 상태 토글하고 결과 반환
        result.setLiked(toggleStatus(request, liked));
        result.setLikes(articleDao.countLikes(request.getArticleId()));

        return result;
    }

    /**
     * 현재 회원의 추천 여부를 토글하고 결과를 반환하는 메서드
     *
     * @param request
     * @param liked
     * @return
     */
    private boolean toggleStatus(LikeRequest request, boolean liked) {
        if(liked) {
            likeDao.deleteLike(request);
            articleDao.subtractLikes(request.getArticleId());

            return false;
        } else {
            likeDao.insertLike(request);
            articleDao.addLikes(request.getArticleId());

            return true;
        }
    }

    /**
     * 게시글에 작성된 모든 댓글을 반환하는 메서드
     *
     * @param articleId
     * @return
     */
    @Override
    public List<Comment> getAllComments(int articleId) {
        return commentDao.selectCommentsByArticleId(articleId);
    }

    /**
     * 게시글에 새로운 댓글을 작성하는 메서드
     *
     * @param request
     * @return 작성 후의 댓글 목록을 반환한다.
     */
    @Override
    public List<Comment> writeComment(CommentWriteRequest request) {
        commentDao.insertComment(request);

        return commentDao.selectCommentsByArticleId(request.getArticleId());
    }

    /**
     * 게시글의 댓글을 삭제하는 메서드
     *
     * @param articleId
     * @param commentId
     * @return 삭제 후의 댓글 목록을 반환한다.
     */
    @Override
    public List<Comment> removeComment(int articleId, int commentId, int memberId) {
        Comment comment = commentDao.selectCommentByCommentId(commentId);

        if(comment == null) throw new NoSuchElementException("존재하지 않는 댓글입니다.");
        if(comment.getWriterInfo().getWriterNo() != memberId) throw new UnauthorizedException("권한이 없습니다.");

        commentDao.deleteComment(commentId);

        return commentDao.selectCommentsByArticleId(articleId);
    }
}
