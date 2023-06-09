package com.hammer.pulsar.service;

import com.hammer.pulsar.dao.ArticleDao;
import com.hammer.pulsar.dao.ArticleTagDao;
import com.hammer.pulsar.dto.article.*;
import com.hammer.pulsar.dto.common.Tag;
import com.hammer.pulsar.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

// 실제 로직이 구현된 ArticleService 인터페이스의 구현체 클래스
@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleDao articleDao;
    private final ArticleTagDao articleTagDao;

    private final FileManagementService fileManagementService;

    @Autowired
    public ArticleServiceImpl(ArticleDao articleDao, ArticleTagDao articleTagDao, FileManagementService fileManagementService) {
        this.articleDao = articleDao;
        this.articleTagDao = articleTagDao;
        this.fileManagementService = fileManagementService;
    }

    /**
     * 사용자가 작성한 게시글 정보에 작성자의 회원번호를 추가한 Request를 만들어 테이블에 정보를 추가하는 메서드
     * TODO: 게시글에 루틴 추가 기능 구현하기
     *
     * @param form
     * @return
     */
    @Override
    public int writeArticle(ArticleWriteForm form, MultipartFile[] imgFiles, int memberId) {
        // 작성자 정보를 Request에 담는다
        ArticleWriteRequest request = new ArticleWriteRequest();

        // Body가 null일 경우 NPE 발생
        if(form.getBody().getRoutine() != null)
            request.setRoutineId(form.getBody().getRoutine().getRoutineNo());
        request.setTitle(form.getTitle());
        request.setContent(form.getBody().getContent());
        request.setWriterId(memberId);

        // 게시글 테이블에 새로운 게시글을 추가한다.
        articleDao.insertArticle(request);

        // 첨부된 이미지를 추가한다.
        fileManagementService.uploadArticleImgs(imgFiles, request.getArticleId());

        // 선택한 태그를 추가한다.
        // 태그가 empty일 경우 예외 발생방지
        if(!form.getTagList().isEmpty())
            articleTagDao.insertArticleTags(new ArticleTagUpdateRequest(request.getArticleId(),
                    form.getTagList()
                    .stream()
                    .map(Tag::getTagNo)
                    .collect(Collectors.toList())));

        return request.getArticleId();
    }

    /**
     * 게시글 목록을 모두 조회하는 메서드
     *
     * @return
     */
    @Override
    public List<ArticlePreview> getAllArticles(PaginationCriteria criteria) {
        List<ArticlePreview> previewList = articleDao.selectArticles(criteria);

        for(ArticlePreview preview : previewList) {
            preview.setArticleTag(articleTagDao.selectTagByArticleId(preview.getArticleNo()));
        }

        return previewList;
    }

    /**
     * 게시글 세부 정보를 조회하는 메서드
     *
     * @param articleId
     * @return
     */
    @Override
    public Article getArticle(int articleId) {
        Article article = articleDao.selectArticleByArticleId(articleId);

        if(article == null) throw new NoSuchElementException("존재하지 않는 게시글입니다.");

        // 조회수 증가
        articleDao.updateViewCnt(articleId);
        article.setViewCnt(article.getViewCnt() + 1);

        // 태그 불러오기
        List<Tag> selected = articleTagDao.selectTagByArticleId(articleId);
        // 태그가 empty일 경우 예외 발생방지
        if(!selected.isEmpty())
            article.setTagList(selected);

        return article;
    }

    /**
     * 게시글을 수정하는 메서드
     * 이미지는 첨부만 가능하다.
     * TODO: 게시글에 포함된 루틴을 수정하는 기능 구현하기
     *
     * @param form
     * @param appendedImgFiles
     */
    @Override
    public void modifyArticle(ArticleModifyForm form, MultipartFile[] appendedImgFiles, int memberId) {
        //DB에 저장된 게시글 정보를 불러오기
        Article saved = articleDao.selectArticleByArticleId(form.getArticleId());

        if(saved == null) throw new NoSuchElementException("존재하지 않는 게시글입니다.");
        if(saved.getWriterInfo().getWriterNo() != memberId) throw new UnauthorizedException("권한이 없습니다.");

        // 기존의 값과 변경된 사항들을 저장한 ArticleModifyRequest를 생성한다.
        ArticleModifyRequest request = new ArticleModifyRequest();
        request.setArticleId(form.getArticleId());
        request.setTitle(form.getTitle());

        request.setContent(form.getBody().getContent());

        // 테이블에 저장된 정보를 업데이트한다.
        articleDao.updateArticle(request);

        // 첨부된 이미지를 추가한다.
        fileManagementService.uploadArticleImgs(appendedImgFiles, form.getArticleId());

        // 태그 목록을 수정한다.
        modifyTagList(form.getTagList(), form.getArticleId());
    }

    private void modifyTagList(List<Tag> newTags, int articleId) {
        // 현재 게시글의 태그 목록들을 모두 불러온다.
        List<Tag> savedTags = articleTagDao.selectTagByArticleId(articleId);
        // 태그가 empty일 경우 예외 발생방지
        if(savedTags.isEmpty()) return;

        /*
            기존의 태그와 새로운 태그 목록을 비교한다.
            새로운 태그들은 DB에 추가하고, 기존 태그 목록에만 존재하는 값은 DB에서 삭제한다.
         */

        // 두 태그 목록 사이에 중복되는 요소를 걸러낼 set
        Set<Integer> savedTagsId = new HashSet<>();
        // 새로 추가된 태그들을 저장할 리스트
        List<Integer> appendedTags = new ArrayList<>();

        for(Tag tag : savedTags) {
            savedTagsId.add(tag.getTagNo());
        }

        // 기존과 변동이 없는 태그들을 걸러내기
        for(Tag tag : newTags) {
            // set에 없는 태그는 새롭게 추가된 태그이므로 appendedTags에 저장하기
            if(!savedTagsId.remove(tag.getTagNo())) {
                appendedTags.add(tag.getTagNo());
            }
        }

        // 새롭게 추가된 태그 목록들은 DB에 저장하기
        if(!appendedTags.isEmpty()) {
            articleTagDao.insertArticleTags(new ArticleTagUpdateRequest(articleId, appendedTags));
        }

        // Set에 남아있는 태그는 기존 태그 목록에만 존재하는 요소이므로 삭제하기
        if(!savedTagsId.isEmpty()) {
            articleTagDao.deleteArticleTags(new ArticleTagUpdateRequest(articleId, new ArrayList<>(savedTagsId)));
        }
    }

    /**
     * 게시글을 삭제하는 메서드
     *
     * @param articleId
     */
    @Override
    public void removeArticle(int articleId, int memberId) {
        Article article = articleDao.selectArticleByArticleId(articleId);

        if(article == null) throw new NoSuchElementException("존재하지 않는 게시글입니다.");
        if(article.getWriterInfo().getWriterNo() != memberId) {
            throw new UnauthorizedException("권한이 없습니다.");
        }

        articleDao.deleteArticle(articleId);
    }
}
