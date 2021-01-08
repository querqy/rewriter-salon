# rewriter-salon
Skeleton for writing your own Querqy query rewriter

We will create a SentinelRewriter during the PS Salon Querqy Session (https://plainschwarz.com/ps-salon/).

You will find the rewriter skeleton in `org.querqy.salon.core.rewriter.SentinelRewriter`. The class comment explains how the SentinelRewriter should modify the query.

You can start by removing the `@Ignore` tags in `org.querqy.salon.core.rewriter.SentinelRewriterTest` and then implement the tasks in SentinelRewriter. 
Tests will succeed if you implented the functionality correctly.

`org.querqy.salon.solr.rewriter.SolrSentinelRewriterFactory` and `org.querqy.salon.elasticsearch.rewriter.ESSentinelRewriterFactory` plug the rewriter into Solr 
and Elasticsearch. When you remove the `@Ignore` tags from the corresponding tests, the tests should success if you have implemented the SentinelRewriter correctly. 

You will find some sections marked 'task' in the two factory classes. This is where your code would go if you wanted to configure the SentinelRewriter properties 
instead of using their default values.
