package org.querqy.salon.solr.rewriter;

import static org.querqy.salon.core.rewriter.SentinelRewriterFactory.DEFAULT_BOOST;
import static org.querqy.salon.core.rewriter.SentinelRewriterFactory.DEFAULT_SENTINEL_FIELD;
import static org.querqy.salon.core.rewriter.SentinelRewriterFactory.DEFAULT_SENTINEL_PREFIX;
import static org.querqy.salon.core.rewriter.SentinelRewriterFactory.DEFAULT_SENTINEL_SUFFIX;

import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.solr.common.util.NamedList;
import org.querqy.salon.core.rewriter.SentinelRewriterFactory;
import querqy.rewrite.RewriterFactory;
import querqy.solr.FactoryAdapter;

/**
 * This class loads the {@link SentinelRewriterFactory} and connects it with the Solr config.
 *  {@link #createFactory(String, NamedList, ResourceLoader)} will be called only once (when the config is loaded)
 */
public class SolrSentinelRewriterFactory implements FactoryAdapter<RewriterFactory> {

    @Override
    public RewriterFactory createFactory(final String rewriterId, final NamedList<?> args,
                                         final ResourceLoader resourceLoader) {
        /*
        Task:
            Read the sentinel field name, prefix, suffix and boost factor from config args
         */
        return new SentinelRewriterFactory(rewriterId, DEFAULT_SENTINEL_FIELD, DEFAULT_SENTINEL_PREFIX,
                DEFAULT_SENTINEL_SUFFIX, DEFAULT_BOOST);
    }

    @Override
    public Class<?> getCreatedClass() {
        return SentinelRewriterFactory.class;
    }
}
