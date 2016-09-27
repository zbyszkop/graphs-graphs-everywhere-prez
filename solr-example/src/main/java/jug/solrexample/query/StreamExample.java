package jug.solrexample.query;

import org.apache.solr.client.solrj.io.Tuple;
import org.apache.solr.client.solrj.io.graph.GatherNodesStream;
import org.apache.solr.client.solrj.io.stream.RankStream;
import org.apache.solr.client.solrj.io.stream.StreamContext;
import org.apache.solr.client.solrj.io.stream.expr.StreamFactory;

import java.io.IOException;

public class StreamExample {

    public static final String ZK_HOST = "localhost:9983";

    public static void main(String[] args) throws IOException {
        StreamFactory streamFactory = new StreamFactory()
                .withCollectionZkHost("song_likes", ZK_HOST)
                .withFunctionName("top", RankStream.class)
                .withFunctionName("gatherNodes", GatherNodesStream.class)
                ;



        RankStream rankStream = (RankStream) streamFactory.constructStream("top(n=5,\n" +
                "    gatherNodes(song_likes,\n" +
                "        gatherNodes(song_likes,\n" +
                "            gatherNodes(song_likes, walk=\"roger.clarke@example.com->email\",gather=\"title_artist\", trackTraversal=\"true\"),\n" +
                "            walk=\"node->title_artist\", gather=\"email\", trackTraversal=\"true\"\n" +
                "            ),\n" +
                "        walk=\"node->email\", gather=\"title_artist\", trackTraversal=\"true\",count(*)\n" +
                "        ),\n" +
                "    sort=\"count(*) desc\"\n" +
                "    )");
//
        rankStream.setStreamContext(new StreamContext());
        rankStream.open();
//
        Tuple read = rankStream.read();
        System.out.println(read.get("node"));
        System.out.println(read.get("field"));
        System.out.println(read.get("level"));
        System.out.println(read.get("count(*)"));

//(song_likes, walk=\"roger.clarke@example.com->email\",gather=\"title_artist\", trackTraversal=\"true\")"),

//        StreamExpression gatherNodes = new StreamExpression("gatherNodes");
//        gatherNodes.addParameter("song_likes");
//        gatherNodes.addParameter(new StreamExpressionNamedParameter("gather" , "title_artist"));
//        gatherNodes.addParameter(new StreamExpressionNamedParameter("walk","roger.clarke@example.com->email"));
//
//        GatherNodesStream gatherNodesStream = new GatherNodesStream(
//                gatherNodes,
//                streamFactory);
//        gatherNodesStream.setStreamContext(new StreamContext());
//
//        gatherNodesStream.open();
//
//        while(true) {
//            Tuple read = gatherNodesStream.read();
//
//            if (read.EOF) break;
//            System.out.println(read.get("node"));
//        }

    }
}
