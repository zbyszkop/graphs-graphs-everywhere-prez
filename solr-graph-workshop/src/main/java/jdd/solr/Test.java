package jdd.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;

public class Test {

    public static void main(String[] args) {
        String urlString = "http://localhost:8983/solr/people";
        SolrClient solr = new HttpSolrClient.Builder(urlString).build();
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("name" , "zbyszko");
        doc.addField("surname" , "papierski");
        doc.addField("location" , "Gdańsk");
        try {
            solr.add(doc);
            solr.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
