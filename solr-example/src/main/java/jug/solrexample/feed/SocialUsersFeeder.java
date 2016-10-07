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

public class SocialUsersFeeder {
    public static void main(String[] args) throws IOException, SolrServerException {
        CloudSolrClient solr = SolrClientCommon.getClientForCollection("social_users");

        Stream<User> personStream = UserSource.getUsersStream(2);

        List<SolrInputDocument> users = personStream
                .map(getPersonSolrInputDocument())
                .toJavaList();

        solr.add(users);
        solr.commit();



    }

    private static Function<User, SolrInputDocument> getPersonSolrInputDocument() {
        return user -> {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("full_name", user.getFullName());
            doc.addField("email", user.getEmail());
            doc.addField("friends", user.getFriends());
            return doc;
        };
    }


}
