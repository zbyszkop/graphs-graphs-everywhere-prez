package jug.solrexample.feed;

import javaslang.collection.Stream;
import jug.solrexample.user.User;
import jug.solrexample.user.UserSource;
import jug.solrexample.util.SolrClientCommon;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class FriendsFeederFlat {
    public static void main(String[] args) throws IOException, SolrServerException {
        CloudSolrClient solr = SolrClientCommon.getClientForCollection("social_users_flat");

        Stream<User> personStream = UserSource.getUsersStream();

        List<SolrInputDocument> users = personStream
                .flatMap(toSolrDocuments())
                .toJavaList();

        solr.add(users);

        solr.commit();

    }

    private static Function<User, List<SolrInputDocument>> toSolrDocuments() {
        return person ->
            Stream.ofAll(person.getFriends())

                    .map(friend -> {
                        SolrInputDocument doc = new SolrInputDocument();
                        doc.addField("full_name", person.getFullName());
                        doc.addField("email", person.getEmail());
                        doc.addField("friend", friend);
                        return doc;
                    }  )
                    .toJavaList();


    }



}
