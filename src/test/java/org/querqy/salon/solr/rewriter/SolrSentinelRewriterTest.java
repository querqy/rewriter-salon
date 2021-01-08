package org.querqy.salon.solr.rewriter;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.common.params.DisMaxParams;
import org.apache.solr.request.SolrQueryRequest;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import querqy.solr.QuerqyDismaxParams;

@SolrTestCaseJ4.SuppressSSL
public class SolrSentinelRewriterTest extends SolrTestCaseJ4 {


    public static void index() {

        assertU(adoc("id", "1",
                "title", "TV set with wall mount",
                "description", "Comes with a wall mount. Wall mount fits TV. Impress yourself with your new wall mount"
                ));

        assertU(adoc("id", "2",
                "title", "Wall mount",
                "description", "Size: 27\""));


        assertU(commit());
    }

    @BeforeClass
    public static void beforeTests() throws Exception {
        initCore("solrconfig.xml", "schema.xml");
        index();
    }

    @Ignore
    @Test
    public void testBoostDoc2() {

        final String q = "wall mount";

        try (final SolrQueryRequest req = req("q", q,
                DisMaxParams.QF, "title description",
                QuerqyDismaxParams.GQF, "title description title_sentinel",
                DisMaxParams.MM, "1",
                "debugQuery", "on",
                "defType", "querqy")) {

            assertQ("Boost not working",
                    req,
                    "//result[@name='response' and @numFound='2']",
                    "//result[@name='response']/doc[1]/str[@name='id'][text()='2']",
                    "//result[@name='response']/doc[2]/str[@name='id'][text()='1']"
            );
        }
    }

    @Ignore
    @Test
    public void testBoostDoc1() {

        final String q = "tv with wall mount";

        try (final SolrQueryRequest req = req("q", q,
                DisMaxParams.QF, "title description",
                QuerqyDismaxParams.GQF, "title description title_sentinel",
                DisMaxParams.MM, "1",
                "debugQuery", "on",
                "defType", "querqy")) {

            assertQ("Boost not working",
                    req,
                    "//result[@name='response' and @numFound='2']",
                    "//result[@name='response']/doc[1]/str[@name='id'][text()='1']",
                    "//result[@name='response']/doc[2]/str[@name='id'][text()='2']"
            );
        }
    }


}