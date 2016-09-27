package jug.solrexample.feed;

import jug.solrexample.music.Song;
import jug.solrexample.music.SongsSource;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Function;


public class MusicFeeder {
    public static void main(String[] args) throws IOException, URISyntaxException, SolrServerException {


        CloudSolrClient solr = new CloudSolrClient.Builder().withZkHost("http://localhost:9983").build();
        solr.setDefaultCollection("songs");

        List<SolrInputDocument> documents = SongsSource.getSongsStream()
                .map(toSolrDocument())
                .toJavaList();

        solr.add(documents);
        solr.commit();

    }

    private static Function<Song, SolrInputDocument> toSolrDocument() {
        return song -> {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("artist", song.getArtist());
            doc.addField("title", song.getTitle());
            doc.addField("genre", song.getGenre().getName());
            doc.addField("chartPosition", song.getChartPosition());
            return doc;
        };
    }


}
