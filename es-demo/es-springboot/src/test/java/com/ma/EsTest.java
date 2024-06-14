package com.ma;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ma.config.ElasticSearchConfig;
import com.ma.pojo.User;
import org.apache.lucene.util.QueryBuilder;
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
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class EsTest {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    // 测试索引创建
    @Test
    public void testCreateIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("kuang_index");
        Settings settings = Settings.builder().put("number_of_shards", 3).put("number_of_replicas", 2).build();
        request.settings(settings);
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    // 判断索引存不存在
    @Test
    public void getIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("kuang_index");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }


    // 删除索引
    @Test
    public void delIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("kuang_index");
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }


    @Test
    public void addDoc() throws IOException {
        // 创建一个User对象
        User liuyou = new User("liuyou", 17);
        IndexRequest request = new IndexRequest("liuyou_index");
        request.id("2");
        request.timeout(TimeValue.timeValueMillis(1000));
        request.source(JSON.toJSONString(liuyou), XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
        System.out.println(response);
    }

    @Test
    public void getDoc() throws IOException {
        GetRequest request = new GetRequest("liuyou_index", "1");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
        User user = JSONObject.parseObject(response.getSourceAsString(), User.class);
        ;
        System.out.println(request);
        client.close();
    }

    @Test
    public void existsDoc() throws IOException {
        GetRequest request = new GetRequest("liuyou_index", "1");
        String[] includes = new String[]{"name"};
        FetchSourceContext fsc = new FetchSourceContext(true, includes, null);
        request.fetchSourceContext(fsc);
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
    }

    @Test
    public void updateDoc() throws IOException {
        UpdateRequest request = new UpdateRequest("liuyou_index", "1");
        User user = new User("lmk", 11);
        request.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
        client.close();
    }

    @Test
    public void delDoc() throws IOException {
        DeleteRequest request = new DeleteRequest("liuyou_index", "1");
        request.timeout("1s");
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }


    @Test
    public void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "liuyou");
//        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();

        searchSourceBuilder.highlighter(new HighlightBuilder());
//        searchSourceBuilder.from(0);
//        searchSourceBuilder.size(10);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchSourceBuilder.query(termQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = search.getHits();
        System.out.println(JSON.toJSONString(hits));
        System.out.println("=========");

        for (SearchHit hit : hits.getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

    @Test
    public void batchDoc() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        ArrayList<User> users = new ArrayList<>();
        users.add(new User("liuyou-1", 1));
        users.add(new User("liuyou-2", 2));
        users.add(new User("liuyou-3", 3));
        users.add(new User("liuyou-4", 4));
        users.add(new User("liuyou-5", 5));
        users.add(new User("liuyou-6", 6));

        for (int i = 0; i < users.size(); i++) {
            bulkRequest.add(new IndexRequest("bulk").id("" + i + 1).
                    source(JSON.toJSONString(users.get(i)), XContentType.JSON));
        }

        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkResponse.status());

    }

}
