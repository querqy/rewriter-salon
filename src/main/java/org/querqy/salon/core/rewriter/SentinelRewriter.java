package org.querqy.salon.core.rewriter;

import querqy.CompoundCharSequence;
import querqy.model.AbstractNodeVisitor;
import querqy.model.BoostQuery;
import querqy.model.Clause;
import querqy.model.DisjunctionMaxQuery;
import querqy.model.ExpandedQuery;
import querqy.model.Node;
import querqy.model.QuerqyQuery;
import querqy.model.Query;
import querqy.model.Term;
import querqy.rewrite.ContextAwareQueryRewriter;
import querqy.rewrite.SearchEngineRequestAdapter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
    Your task:

    Create a rewriter that adds a ExpandedQuery.boostUpQuery "(SENTINEL_PREFIX_)ABC(_SENTINEL_SUFFIX)"
    for query "A B C".

 */
public class SentinelRewriter extends AbstractNodeVisitor<Void> implements ContextAwareQueryRewriter {

    private final String sentinelField;
    private final String sentinelPrefix;
    private final String sentinelSuffix;
    private final float boost;
    private final List<CharSequence> terms;

    public SentinelRewriter(final String sentinelField, final String sentinelPrefix, final String sentinelSuffix,
                            final float boost) {
        this.sentinelField = sentinelField;
        this.sentinelPrefix = sentinelPrefix;
        this.sentinelSuffix = sentinelSuffix;
        this.boost = boost;
        this.terms = new LinkedList<>();
    }


    @Override
    public ExpandedQuery rewrite(final ExpandedQuery query, final Map<String, Object> context) {
        // This method is deprecated in ContextAwareQueryRewriter and will be removed shortly
        throw new UnsupportedOperationException();
    }

    @Override
    public ExpandedQuery rewrite(final ExpandedQuery query,
                                 final SearchEngineRequestAdapter searchEngineRequestAdapter) {
        return rewrite(query);
    }

    @Override
    public ExpandedQuery rewrite(final ExpandedQuery query) {

//        final QuerqyQuery<?> userQuery = query.getUserQuery();
//        if (userQuery instanceof Query) {
//
//            visit((Query) userQuery);
//
//            if (!terms.isEmpty()) {
//
//                terms.add(0, sentinelPrefix);
//                terms.add(sentinelSuffix);
//                final CharSequence sentinelValue = new CompoundCharSequence(terms);
//
//                final Query topQuery = new Query();
//                final DisjunctionMaxQuery dmq = new DisjunctionMaxQuery(topQuery, Clause.Occur.MUST, true);
//                dmq.addClause(new Term(dmq, sentinelField, sentinelValue, true));
//                topQuery.addClause(dmq);
//
//                query.addBoostUpQuery(new BoostQuery(topQuery, boost));
//
//            }
//
//        }

        return query;
    }

    @Override
    public Void visit(final Term term) {
        // TODO collect the terms
        return null;
    }
}
