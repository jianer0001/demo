package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author jian
 * @version 1.0.0
 * @since 2021年12月12日 01:39:00
 */
@SpringBootTest
public class EsTest {

    @Autowired
    RestHighLevelClient client;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;



    //创建索引测试
    @Test
    void contextLoads() throws IOException {
        //创建索引的请求
        CreateIndexRequest request = new CreateIndexRequest("jian");
        //执行创建索引的请求，返回一个响应
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        //打印响应
        System.out.println(response); //org.elasticsearch.client.indices.CreateIndexResponse@6ce3e00
    }

    @Test
    public void test1() throws IOException {
        GetIndexRequest request = new GetIndexRequest("wanli");
        boolean b = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(b); //true
    }

    //删除索引
    @Test
    public void test2() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("wanli");
        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(response.isAcknowledged()); //返回true删除成功
    }

    @Test
    public void test3() throws IOException {

        User user = new User("万里", 3);

        //创建请求
        IndexRequest request = new IndexRequest("test");

        request.id("1")  //文档id，不指定会有默认id
                .timeout(TimeValue.timeValueSeconds(1))//分片超时时间
                .source(mapper.writeValueAsString(user), XContentType.JSON); //将文档源设置为索引，user对象转化成json字符串

        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());        //IndexResponse[index=wanli_index,type=_doc,id=1,version=1,result=created,seqNo=0,primaryTerm=1,shards={"total":2,"successful":1,"failed":0}]

        System.out.println(response.status());//CREATED 操作状态
    }

    //判断文档是否存在
    @Test
    public void test4() throws IOException {
        //针对指定的索引和文档 ID 构造一个新的 get 请求
        GetRequest request = new GetRequest("test", "1");

        String[] includes = Strings.EMPTY_ARRAY;
        String[] excludes = new String[]{"age"};//值获取 _source 中包含age的文档信息
        //fetchSourceContext指定需要返回字段的上下文,提供更加完善的过滤逻辑，主要特性为支持include、exclude和支持通篇符过滤。
        request.fetchSourceContext(new FetchSourceContext(true, includes, excludes));

        boolean b = client.exists(request, RequestOptions.DEFAULT);
        System.out.println(b); //true则存在
    }

    //获取文档信息
    @Test
    public void test5() throws IOException {
        GetRequest request = new GetRequest("test", "1");

        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());//{"_index":"wanli_index","_type":"_doc","_id":"1","_version":1,"_seq_no":0,"_primary_term":1,"found":true,"_source":{"age":3,"name":"万里"}}

        System.out.println(response.getSource());//获取_source：{name=万里, age=3}
        System.out.println(response.getVersion());//1
        System.out.println(response);//{"_index":"wanli_index","_type":"_doc","_id":"1","_version":1,"_seq_no":0,"_primary_term":1,"found":true,"_source":{"age":3,"name":"万里"}}

    }

    //修改文档信息
    @Test
    public void test6() throws IOException {
        UpdateRequest request = new UpdateRequest("test", "1");
        request.timeout("1s");
        User user = new User("神明", 18);

        //doc里面填具体的内容和文档格式
        request.doc(mapper.writeValueAsString(user),XContentType.JSON);

        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        System.out.println(response.status()); //返回ok表示操作成功
    }
    //删除文档
    @Test
    public void test7() throws IOException {

        DeleteRequest request = new DeleteRequest("test", "1");
        request.timeout("1s");
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(response.status());//返回ok表示操作成功
    }

    //批量插入文档
    @Test
    public void test8() throws IOException {
        BulkRequest request = new BulkRequest();

        request.timeout("10s");

        ArrayList<User> list = new ArrayList<>();

        list.add(new User("shenming1",3));
        list.add(new User("shenming2",3));
        list.add(new User("shenming3",3));
        list.add(new User("shenming4",3));
        list.add(new User("shenming5",3));

        for (int i = 0; i < list.size(); i++) {
            request.add(
                    new IndexRequest("test")
                            .id(""+(i+1))
                            .source(mapper.writeValueAsString(list.get(i)),XContentType.JSON));
        }
        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        System.out.println(response.status()); //ok

    }

    @Test
    public void test9() throws IOException {
        //构建查询请求
        SearchRequest request = new SearchRequest("wanli_index");

        //构建搜索条件
        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();

        //设置高亮
        //HighlightBuilder highlightBuilder = new HighlightBuilder();
        //highlightBuilder.field("name");
        //highlightBuilder.preTags("<span style='color:red'>");
        //highlightBuilder.postTags("</span>");
        //searchSourceBuilder.highlighter(highlightBuilder);
        //分页：
        //sourceBuilder.from(0);
        //sourceBuilder.size(10);

        //QueryBuilders 工具类实现查询条件的构建
        //QueryBuilders.termQuery 精确查询
        //QueryBuilders.matchAllQuery() 匹配所有



        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("name", "shenming1");
        sourceBuilder.query(queryBuilder);//将精确查询条件放入搜索条件
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));//设置超时时间

        //将查询条件放入请求
        request.source(sourceBuilder);

        //执行请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //打印搜索命中文档结果
        System.out.println(mapper.writeValueAsString(response.getHits()));
        //{"fragment":true,"hits":[{"fields":{},"fragment":false,"highlightFields":{},"id":"1","matchedQueries":[],"primaryTerm":0,"rawSortValues":[],"score":1.1727202,"seqNo":-2,"sortValues":[],"sourceAsMap":{"name":"shenming1","age":3},"sourceAsString":"{\"age\":3,\"name\":\"shenming1\"}","sourceRef":{"fragment":true},"type":"_doc","version":-1}],"maxScore":1.1727202,"totalHits":{"relation":"EQUAL_TO","value":1}}


        System.out.println("====================================");
        for (SearchHit searchHit : response.getHits().getHits()) {
            System.out.println(searchHit.getSourceAsMap());//{name=shenming1, age=3}
        }

    }


    /**
     * 新增Document
     */
    @Test
    public void saveDocument() {
        // 模拟新增的User
        User user = new User();
        user.setId("1");
        user.setUsername("张三");
        user.setAge(18);
        user.setBirth(new Date());
        user.setIntro("我是张三");

        userRepository.save(user);
    }


    @Test
    public void deleteDocument() {
        userRepository.deleteById("1");
    }

    @Test
    public void updateDocument() {
        // 模拟已经更新username后的User
        User user = new User();
        user.setId("1");
        user.setUsername("小明");
        user.setAge(18);
        user.setBirth(new Date());
        user.setIntro("我是张三");

        userRepository.save(user);
    }

    @Test
    public void queryDocument() {
        // 传入Document的id
        User user = userRepository.findById("1").get();

        System.out.println(user);
    }

    @Test
    public void queryAllDocuments() {
        Iterable<User> users = userRepository.findAll();

        users.forEach(user -> {
            System.out.println(user);
        });
    }

    @Test
    public void queryAllDocumentsBySort() {
        // 按照age降序排序
        Iterable<User> users = userRepository.findAll(Sort.by(Sort.Order.desc("age")));

        users.forEach(user -> {
            System.out.println(user);
        });
    }

    @Test
    public void queryDocumentsByPage() {
        User user1 = new User();
        user1.setId("1");
        // 页号从0算起
        Page<User> userPage = userRepository.searchSimilar(user1, new String[]{"id","age"}, PageRequest.of(0, 10));

        userPage.forEach(user -> {
            System.out.println(user);
        });
    }

    /*@Test
    public void queryDocumentsByFuzzy() {
        FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("intro", "李四");

        Iterable<User> users = userRepository.search(fuzzyQueryBuilder);

        users.forEach(user -> {
            System.out.println(user);
        });
    }*/



}
