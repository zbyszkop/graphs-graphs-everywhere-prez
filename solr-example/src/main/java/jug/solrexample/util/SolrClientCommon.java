package jug.solrexample.util;

import org.apache.solr.client.solrj.impl.CloudSolrClient;

public class SolrClientCommon {

    public static CloudSolrClient getClientForCollection(String col) {
        CloudSolrClient solr = new CloudSolrClient.Builder().withZkHost("localhost:9983").build();
        solr.setDefaultCollection(col);
        return solr;
    }
}
