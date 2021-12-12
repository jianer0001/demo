package com.example.demo.repository;

import com.example.demo.entity.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义接口需要继承ElasticsearchRepository<实体类型，主键类型>  基本的crud 分页
 */
@Component
public interface ArticleRepository extends ElasticsearchRepository<Article,Integer> {

    /**
     * 根据标题查询
     * @param title
     * @return
     */
    List<Article> findByTitle(String title);

    /**
     * 根据标题或内容查询
     * @param title
     * @param context
     * @return
     */
    List<Article> findByTitleOrContext(String title,String context);

    /**
     * 根据标题或内容查询（含分页）
     * @param title
     * @param context
     * @param pageable
     * @return
     */
    List<Article> findByTitleOrContext(String title, String context, Pageable pageable);
}


