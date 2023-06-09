package com.hammer.pulsar.service;

import com.hammer.pulsar.dto.article.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// 게시판 관련 로직을 처리할 서비스의 인터페이스
// 게시판, 글 상세보기: static 관련 로직
public interface ArticleService {
    // 게시글을 작성하는 로직
    public int writeArticle(ArticleWriteForm form, MultipartFile[] imgFiles, int memberId);

    // 게시판에 등록된 글 목록을 보여주는 로직
    public List<ArticlePreview> getAllArticles(PaginationCriteria criteria);

    // 선택한 게시글의 상세 정보를 보여주는 로직
    public Article getArticle(int articleId);

    // 선택한 게시글을 수정하는 로직
    public void modifyArticle(ArticleModifyForm form, MultipartFile[] appendedImgFiles, int memberId);

    // 선택한 게시글을 삭제하는 로직
    public void removeArticle(int articleId, int memberId);

}
