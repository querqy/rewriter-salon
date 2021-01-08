package org.querqy.salon.elasticsearch.rewriter;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.analysis.common.CommonAnalysisPlugin;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import querqy.elasticsearch.QuerqyPlugin;
import querqy.elasticsearch.QuerqyProcessor;
import querqy.elasticsearch.query.Generated;
import querqy.elasticsearch.query.MatchingQuery;
import querqy.elasticsearch.query.QuerqyQueryBuilder;
import querqy.elasticsearch.query.Rewriter;
import querqy.elasticsearch.rewriterstore.PutRewriterAction;
import querqy.elasticsearch.rewriterstore.PutRewriterRequest;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class ESSentinelRewriterIntegrationTest extends ESSingleNodeTestCase {

    protected void index() {

        indexDocs(
                doc("id", "1",
                        "title", "TV set with wall mount",
                        "description", "Comes with a wall mount. Wall mount fits TV. Impress yourself with your new wall mount"
                ),
                doc("id", "2",
                        "title", "Wall mount",
                        "description", "Size: 27\"")
        );
    }


    @Ignore
    @Test
    public void testBoostDoc2() throws ExecutionException, InterruptedException {

        index();

        final Map<String, Object> content = new HashMap<>();
        content.put("class", ESSentinelRewriterFactory.class.getTypeName());

        final Map<String, Object> config = Collections.emptyMap();
        content.put("config", config);

        final PutRewriterRequest request = new PutRewriterRequest("sentinel1", content);

        client().execute(PutRewriterAction.INSTANCE, request).get();

        QuerqyQueryBuilder querqyQuery = new QuerqyQueryBuilder(getInstanceFromNode(QuerqyProcessor.class));
        querqyQuery.setRewriters(singletonList(new Rewriter("sentinel1")));
        querqyQuery.setMatchingQuery(new MatchingQuery("wall mount"));
        querqyQuery.setMinimumShouldMatch("100%");
        querqyQuery.setQueryFieldsAndBoostings(Arrays.asList("title", "description"));
        querqyQuery.setGenerated(new Generated(Arrays.asList("title", "description", "title_sentinel")));

        SearchRequestBuilder searchRequestBuilder = client().prepareSearch(getIndexName());
        searchRequestBuilder.setQuery(querqyQuery);
        searchRequestBuilder.setExplain(true);

        SearchResponse response = client().search(searchRequestBuilder.request()).get();
        SearchHits hits = response.getHits();

        assertEquals(2L, hits.getTotalHits().value);


        final SearchHit[] docs = hits.getHits();
        assertEquals("2", docs[0].getSourceAsMap().get("id"));
        assertEquals("1", docs[1].getSourceAsMap().get("id"));


    }

    @Ignore
    @Test
    public void testBoostDoc1() throws ExecutionException, InterruptedException {

        index();

        final Map<String, Object> content = new HashMap<>();
        content.put("class", ESSentinelRewriterFactory.class.getTypeName());

        final Map<String, Object> config = Collections.emptyMap();
        content.put("config", config);

        final PutRewriterRequest request = new PutRewriterRequest("sentinel1", content);

        client().execute(PutRewriterAction.INSTANCE, request).get();

        QuerqyQueryBuilder querqyQuery = new QuerqyQueryBuilder(getInstanceFromNode(QuerqyProcessor.class));
        querqyQuery.setRewriters(singletonList(new Rewriter("sentinel1")));
        querqyQuery.setMatchingQuery(new MatchingQuery("tv set with wall mount"));
        querqyQuery.setMinimumShouldMatch("1");
        querqyQuery.setQueryFieldsAndBoostings(Arrays.asList("title", "description"));
        querqyQuery.setGenerated(new Generated(Arrays.asList("title", "description", "title_sentinel")));

        SearchRequestBuilder searchRequestBuilder = client().prepareSearch(getIndexName());
        searchRequestBuilder.setQuery(querqyQuery);
        searchRequestBuilder.setExplain(true);

        SearchResponse response = client().search(searchRequestBuilder.request()).get();
        SearchHits hits = response.getHits();

        assertEquals(2L, hits.getTotalHits().value);


        final SearchHit[] docs = hits.getHits();
        assertEquals("1", docs[0].getSourceAsMap().get("id"));
        assertEquals("2", docs[1].getSourceAsMap().get("id"));

    }



    // ***** ES Test support (see querqy.elasticsearch.rewriter.AbstractRewriterIntegrationTest)

    private static final String INDEX_NAME = "test_index";

    @Override
    protected Collection<Class<? extends Plugin>> getPlugins() {
        return Arrays.asList(QuerqyPlugin.class, CommonAnalysisPlugin.class);
    }



    protected static String getIndexName() {
        return INDEX_NAME;
    }

    public Map<String, Object> doc(Object... kv) {
        if (kv.length % 2 != 0) {
            throw new RuntimeException("Input size must be even");
        }

        Map<String, Object> doc = new HashMap<>();
        for (int i = 0; i < kv.length; i = i + 2) {
            doc.put((String) kv[i], kv[i + 1]);
        }

        return doc;
    }

    @SafeVarargs
    public final void indexDocs(Map<String, Object>... docs) {
        client().admin().indices().prepareCreate(getIndexName())
                .setSettings(readUtf8Resource("elasticsearch/test-settings.json"), XContentType.JSON)
                .addMapping(INDEX_NAME, readUtf8Resource("elasticsearch/test-mapping.json"), XContentType.JSON)
                .get();

        Arrays.stream(docs).forEach(doc ->
                client().prepareIndex(getIndexName(), null)
                        .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                        .setSource(doc)
                        .get());
    }

    private String readUtf8Resource(final String name) {
        final Scanner scanner = new Scanner(getClass().getClassLoader().getResourceAsStream(name),
                StandardCharsets.UTF_8.name()).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }


}