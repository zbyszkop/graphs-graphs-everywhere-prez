package jug.solrexample.feed;


import javaslang.Tuple2;
import javaslang.collection.Stream;
import jug.solrexample.music.Genre;
import jug.solrexample.music.Song;
import jug.solrexample.music.SongsSource;
import jug.solrexample.user.UserSource;
import jug.solrexample.util.SolrClientCommon;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class EventsFeeder {


    public static void main(String[] args) throws IOException, SolrServerException {

        CloudSolrClient solr = SolrClientCommon.getClientForCollection("song_likes") ;

        List<SolrInputDocument> docs = getLikesStream()
                .map(toSolrDocument())
                .toJavaList();

        solr.add(docs);
        solr.commit();

    }

    private static Function<Tuple2<String, Song>, SolrInputDocument> toSolrDocument() {
        return like -> {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("email", like._1());
            doc.addField("title", like._2().getTitle());
            doc.addField("artist", like._2().getArtist());
            doc.addField("title_artist", like._2().getTitle() + " by " + like._2().getArtist());
            return doc;
        };
    }

    private static Stream<Tuple2<String, Song>> getLikesStream() {
        final Random random = new Random();
        return UserSource.getEmailStream()
                .flatMap(email -> {
                            int likedGenres = random.nextInt(Genre.values().length) + 1;
                            return Stream.range(0, likedGenres)
                                    .map(i -> Genre.values()[random.nextInt(likedGenres)])
                                    .flatMap(genre -> {
                                        List<Song> songsForGenre = SongsSource.getSongsStream()
                                                .filter(s -> s.getGenre() == genre)
                                                .toJavaList();
                                        int noOfSongsliked = random.nextInt(songsForGenre.size() / 40);
                                        Collections.shuffle(songsForGenre);
                                        return songsForGenre.subList(0, noOfSongsliked);
                                    })
                                    .map(song -> new Tuple2<>(email, song));
                        }
                );
    }


}
