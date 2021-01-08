package org.querqy.salon.elasticsearch.rewriter;

import static org.querqy.salon.core.rewriter.SentinelRewriterFactory.DEFAULT_BOOST;
import static org.querqy.salon.core.rewriter.SentinelRewriterFactory.DEFAULT_SENTINEL_FIELD;
import static org.querqy.salon.core.rewriter.SentinelRewriterFactory.DEFAULT_SENTINEL_PREFIX;
import static org.querqy.salon.core.rewriter.SentinelRewriterFactory.DEFAULT_SENTINEL_SUFFIX;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.shard.IndexShard;
import org.querqy.salon.core.rewriter.SentinelRewriterFactory;
import querqy.elasticsearch.ESRewriterFactory;
import querqy.rewrite.RewriterFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class loads and configures the {@link SentinelRewriterFactory}.
 *
 *  {@link #validateConfiguration(Map)}, {@link #configure(Map)} (Map)} and {@link #createRewriterFactory(IndexShard)}
 *  are called only once and in this order (when the config is pushed to ES).
 */
public class ESSentinelRewriterFactory extends ESRewriterFactory {

    public ESSentinelRewriterFactory(final String rewriterId) {
        super(rewriterId);
    }

    @Override
    public void configure(final Map<String, Object> config) throws ElasticsearchException {
        /*
        Task:
            Read the sentinel field name, prefix, suffix and boost factor from config args

         */
    }

    /**
     *
     * @param config The config map
     * @return A list of validation error messages
     */
    @Override
    public List<String> validateConfiguration(final Map<String, Object> config) {
        /*
        Task:
            Validate the sentinel field name, prefix, suffix and boost factor in config args
        */
        return Collections.emptyList();
    }

    @Override
    public RewriterFactory createRewriterFactory(final IndexShard indexShard) throws ElasticsearchException {
        return new SentinelRewriterFactory(getRewriterId(), DEFAULT_SENTINEL_FIELD, DEFAULT_SENTINEL_PREFIX,
                DEFAULT_SENTINEL_SUFFIX, DEFAULT_BOOST);
    }
}
