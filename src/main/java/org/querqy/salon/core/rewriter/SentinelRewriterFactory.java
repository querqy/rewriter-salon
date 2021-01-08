package org.querqy.salon.core.rewriter;

import querqy.model.ExpandedQuery;
import querqy.model.Term;
import querqy.rewrite.QueryRewriter;
import querqy.rewrite.RewriterFactory;
import querqy.rewrite.SearchEngineRequestAdapter;

import java.util.Set;

/**
 * Factory for the {@link SentinelRewriter}. The factory is plugged into the rewrite chain.
 *
 * {@link #createRewriter(ExpandedQuery, SearchEngineRequestAdapter)} will be called for each search request
 *
 */
public class SentinelRewriterFactory extends RewriterFactory {

    public static final String DEFAULT_SENTINEL_PREFIX = "###_";
    public static final String DEFAULT_SENTINEL_SUFFIX = "_###";
    public static final String DEFAULT_SENTINEL_FIELD = null;
    public static final float DEFAULT_BOOST = 100f;

    protected final String sentinelField;
    protected final String sentinelPrefix;
    protected final String sentinelSuffix;
    protected final float boost;

    public SentinelRewriterFactory(final String rewriterId, final String sentinelField, final String sentinelPrefix,
                                   final String sentinelSuffix, final Float boost) {
        super(rewriterId);
        this.sentinelField = sentinelField != null ? sentinelField : DEFAULT_SENTINEL_FIELD;
        this.sentinelPrefix = sentinelPrefix != null ? sentinelPrefix : DEFAULT_SENTINEL_PREFIX;
        this.sentinelSuffix = sentinelSuffix != null ? sentinelSuffix : DEFAULT_SENTINEL_SUFFIX;
        this.boost = boost != null ? boost : DEFAULT_BOOST;

    }

    @Override
    public QueryRewriter createRewriter(final ExpandedQuery input,
                                        final SearchEngineRequestAdapter searchEngineRequestAdapter) {
        return new SentinelRewriter(sentinelField, sentinelPrefix, sentinelSuffix, boost);
    }

    @Override
    public Set<Term> getGenerableTerms() {
        return QueryRewriter.EMPTY_GENERABLE_TERMS;
    }
}
