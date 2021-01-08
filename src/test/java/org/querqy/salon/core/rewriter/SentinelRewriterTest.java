package org.querqy.salon.core.rewriter;

import org.junit.Ignore;
import org.junit.Test;
import querqy.model.Clause;
import querqy.model.DisjunctionMaxQuery;
import querqy.model.ExpandedQuery;
import querqy.model.Query;
import querqy.model.Term;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static querqy.QuerqyMatchers.*;

public class SentinelRewriterTest {

    @Ignore
    @Test
    public void testSentinelWithSingleQueryToken() {

        final Query query = new Query();
        addTerm(query, "token1");

        final ExpandedQuery expandedQuery = new ExpandedQuery(query);
        final SentinelRewriter rewriter = new SentinelRewriter(null, "###BEGIN_", "_END###", 87f);

        final ExpandedQuery rewritten = rewriter.rewrite(expandedQuery);
        assertThat(
                rewritten.getBoostUpQueries(),
                contains(
                        boostQ(
                                bq(
                                        dmq(
                                                must(),
                                                // test the value and generated=true:
                                                term("###BEGIN_token1_END###", true)
                                        )
                                ),
                                87f
                        )

                ));


    }

    @Ignore
    @Test
    public void testSentinelWithMultipleTokens() {

        final Query query = new Query();
        addTerm(query, "token1");
        addTerm(query, "token2");
        addTerm(query, "token3");

        final ExpandedQuery expandedQuery = new ExpandedQuery(query);
        final SentinelRewriter rewriter = new SentinelRewriter(null, "LEFT_", "_RIGHT", 93f);

        final ExpandedQuery rewritten = rewriter.rewrite(expandedQuery);
        assertThat(
                rewritten.getBoostUpQueries(),
                contains(
                        boostQ(
                                bq(
                                        dmq(
                                                must(),
                                                // test the value and generated=true:
                                                term("LEFT_token1token2token3_RIGHT", true)
                                        )
                                ),
                                93f
                        )

                ));


    }

    @Ignore
    @Test
    public void testSentinelWithFieldname() {
        final Query query = new Query();
        addTerm(query, "a");
        addTerm(query, "bc");

        final ExpandedQuery expandedQuery = new ExpandedQuery(query);
        final SentinelRewriter rewriter = new SentinelRewriter("sentinel_field", "#_", "_#", 1.3f);

        final ExpandedQuery rewritten = rewriter.rewrite(expandedQuery);
        assertThat(
                rewritten.getBoostUpQueries(),
                contains(
                        boostQ(
                                bq(
                                        dmq(
                                                must(),
                                                // test field, value and generated=true:
                                                term("sentinel_field", "#_abc_#", true)
                                        )
                                ),
                                1.3f
                        )

                ));

    }


    private void addTerm(final Query query, final String value) {
        addTerm(query, null, value);
    }

    private void addTerm(final Query query, final String field, final String value) {
        final DisjunctionMaxQuery dmq = new DisjunctionMaxQuery(query, Clause.Occur.SHOULD, true);
        query.addClause(dmq);
        final Term term = new Term(dmq, field, value);
        dmq.addClause(term);
    }



}