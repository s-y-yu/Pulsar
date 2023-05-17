package com.hammer.pulsar.dao;

import com.hammer.pulsar.dto.article.Comment;

import java.util.List;

// 댓글 정보를 저장하는 Comment 테이블과 통신하는 DAO
public interface CommentDao {
    // 댓글을 저장하는 메서드
    public int insertComment(String comment);

    // 선택한 게시글에 작성된 모든 댓글을 조회하는 메서드
    public List<Comment> selectCommentsByArticleId(int articleId);

    // 선택한 댓글을 삭제하는 메서드
    public int deleteComment(int commentId);
}