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
 * @since 2021???12???12??? 01:39:00
 */
@SpringBootTest
public class EsTest {

    @Autowired
    RestHighLevelClient client;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;



    //??????????????????
    @Test
    void contextLoads() throws IOException {
        //?????????????????????
        CreateIndexRequest request = new CreateIndexRequest("jian");
        //????????????????????????????????????????????????
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        //????????????
        System.out.println(response); //org.elasticsearch.client.indices.CreateIndexResponse@6ce3e00
    }

    @Test
    public void test1() throws IOException {
        GetIndexRequest request = new GetIndexRequest("wanli");
        boolean b = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(b); //true
    }

    //????????????
    @Test
    public void test2() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("wanli");
        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(response.isAcknowledged()); //??????true????????????
    }

    @Test
    public void test3() throws IOException {

        User user = new User("??????", 3);

        //????????????
        IndexRequest request = new IndexRequest("test");

        request.id("1")  //??????id????????????????????????id
                .timeout(TimeValue.timeValueSeconds(1))//??????????????????
                .source(mapper.writeValueAsString(user), XContentType.JSON); //??????????????????????????????user???????????????json?????????

        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());        //IndexResponse[index=wanli_index,type=_doc,id=1,version=1,result=created,seqNo=0,primaryTerm=1,shards={"total":2,"successful":1,"failed":0}]

        System.out.println(response.status());//CREATED ????????????
    }

    //????????????????????????
    @Test
    public void test4() throws IOException {
        //?????????????????????????????? ID ?????????????????? get ??????
        GetRequest request = new GetRequest("test", "1");

        String[] includes = Strings.EMPTY_ARRAY;
        String[] excludes = new String[]{"age"};//????????? _source ?????????age???????????????
        //fetchSourceContext????????????????????????????????????,?????????????????????????????????????????????????????????include???exclude???????????????????????????
        request.fetchSourceContext(new FetchSourceContext(true, includes, excludes));

        boolean b = client.exists(request, RequestOptions.DEFAULT);
        System.out.println(b); //true?????????
    }

    //??????????????????
    @Test
    public void test5() throws IOException {
        GetRequest request = new GetRequest("test", "1");

        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());//{"_index":"wanli_index","_type":"_doc","_id":"1","_version":1,"_seq_no":0,"_primary_term":1,"found":true,"_source":{"age":3,"name":"??????"}}

        System.out.println(response.getSource());//??????_source???{name=??????, age=3}
        System.out.println(response.getVersion());//1
        System.out.println(response);//{"_index":"wanli_index","_type":"_doc","_id":"1","_version":1,"_seq_no":0,"_primary_term":1,"found":true,"_source":{"age":3,"name":"??????"}}

    }

    //??????????????????
    @Test
    public void test6() throws IOException {
        UpdateRequest request = new UpdateRequest("test", "1");
        request.timeout("1s");
        User user = new User("??????", 18);

        //doc???????????????????????????????????????
        request.doc(mapper.writeValueAsString(user),XContentType.JSON);

        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        System.out.println(response.status()); //??????ok??????????????????
    }
    //????????????
    @Test
    public void test7() throws IOException {

        DeleteRequest request = new DeleteRequest("test", "1");
        request.timeout("1s");
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(response.status());//??????ok??????????????????
    }

    //??????????????????
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
        //??????????????????
        SearchRequest request = new SearchRequest("wanli_index");

        //??????????????????
        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();

        //????????????
        //HighlightBuilder highlightBuilder = new HighlightBuilder();
        //highlightBuilder.field("name");
        //highlightBuilder.preTags("<span style='color:red'>");
        //highlightBuilder.postTags("</span>");
        //searchSourceBuilder.highlighter(highlightBuilder);
        //?????????
        //sourceBuilder.from(0);
        //sourceBuilder.size(10);

        //QueryBuilders ????????????????????????????????????
        //QueryBuilders.termQuery ????????????
        //QueryBuilders.matchAllQuery() ????????????



        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("name", "shenming1");
        sourceBuilder.query(queryBuilder);//???????????????????????????????????????
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));//??????????????????

        //???????????????????????????
        request.source(sourceBuilder);

        //????????????
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //??????????????????????????????
        System.out.println(mapper.writeValueAsString(response.getHits()));
        //{"fragment":true,"hits":[{"fields":{},"fragment":false,"highlightFields":{},"id":"1","matchedQueries":[],"primaryTerm":0,"rawSortValues":[],"score":1.1727202,"seqNo":-2,"sortValues":[],"sourceAsMap":{"name":"shenming1","age":3},"sourceAsString":"{\"age\":3,\"name\":\"shenming1\"}","sourceRef":{"fragment":true},"type":"_doc","version":-1}],"maxScore":1.1727202,"totalHits":{"relation":"EQUAL_TO","value":1}}


        System.out.println("====================================");
        for (SearchHit searchHit : response.getHits().getHits()) {
            System.out.println(searchHit.getSourceAsMap());//{name=shenming1, age=3}
        }

    }


    /**
     * ??????Document
     */
    @Test
    public void saveDocument() {
        // ???????????????User
        User user = new User();
        user.setId("1");
        user.setUsername("??????");
        user.setAge(18);
        user.setBirth(new Date());
        user.setIntro("????????????");

        userRepository.save(user);
    }


    @Test
    public void deleteDocument() {
        userRepository.deleteById("1");
    }

    @Test
    public void updateDocument() {
        // ??????????????????username??????User
        User user = new User();
        user.setId("1");
        user.setUsername("??????");
        user.setAge(18);
        user.setBirth(new Date());
        user.setIntro("????????????");

        userRepository.save(user);
    }

    @Test
    public void queryDocument() {
        // ??????Document???id
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
        // ??????age????????????
        Iterable<User> users = userRepository.findAll(Sort.by(Sort.Order.desc("age")));

        users.forEach(user -> {
            System.out.println(user);
        });
    }

    @Test
    public void queryDocumentsByPage() {
        User user1 = new User();
        user1.setId("1");
        // ?????????0??????
        Page<User> userPage = userRepository.searchSimilar(user1, new String[]{"id","age"}, PageRequest.of(0, 10));

        userPage.forEach(user -> {
            System.out.println(user);
        });
    }

    /*@Test
    public void queryDocumentsByFuzzy() {
        FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("intro", "??????");

        Iterable<User> users = userRepository.search(fuzzyQueryBuilder);

        users.forEach(user -> {
            System.out.println(user);
        });
    }*/



}
