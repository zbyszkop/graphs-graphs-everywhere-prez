package jug.solrexample.feed;


import javaslang.collection.Stream;
import javaslang.control.Option;
import javaslang.control.Try;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MailFeeder {
    public static final SimpleDateFormat EMAIL_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z", Locale.US);
    private String zkHost;

    public static void main(String[] args) throws Exception {
        new MailFeeder("localhost:9983").feed() ;

    }

    public MailFeeder(String zkHost) {
        this.zkHost = zkHost;
    }

    public void feed() throws Exception {

        CloudSolrClient solr = new CloudSolrClient.Builder().withZkHost(zkHost).build();
        solr.setDefaultCollection("emails");

        Stream<SolrInputDocument> solrDocuments = getSolrDocuments();
        solrDocuments
                .forEach((doc) -> {
                    try {
//                        System.out.println(doc);
                        solr.add(doc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        solr.commit();
    }

    private Stream<SolrInputDocument> getSolrDocuments() throws IOException {
        String emailDir = Thread.currentThread().getContextClassLoader().getResource("easy_ham").getFile();
        Path path = Paths.get(emailDir);
        List<Path> emailFiles = Files.list(path).collect(Collectors.toList());

        return Stream.ofAll(emailFiles)
                .map(emailFile ->
                    Try.of(() ->  getEmail(emailFile))
                    )
            .filter(t -> t instanceof Try.Success) //not interested in unreadable files
            .map(Try::get)
            .map(MailFeeder::mapToSolrDocument);

    }

    private Email getEmail(Path emailFile) throws Exception {
        String fromLine = getLine(emailFile, "From: ");
        String to = getEmails(emailFile, "To: ").stream().findFirst().orElse("");
        String cc = getEmails(emailFile, "Cc: ").stream().findFirst().orElse("");
        String bcc = getEmails(emailFile, "Bcc: ").stream().findFirst().orElse(""); //TODO: add Date
        //Thu, 22 Aug 2002 18:26:25 +0700
        String date = getLine(emailFile, "Date:").split(" ", 2)[1];
        Date parsedDate = EMAIL_DATE_FORMAT.parse(date);
        String subject = getSubject(emailFile);
        String msg = extractMessage(emailFile);

        return new Email(getEmail(fromLine).getOrElse(""), subject, to, cc, bcc, msg, parsedDate);
    }

    private static SolrInputDocument mapToSolrDocument(Email email) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("from", email.getFrom());
        doc.addField("to", email.getTo());
        doc.addField("cc", email.getCc());
        doc.addField("bcc", email.getBcc());
        doc.addField("subject", email.getSubject());
        doc.addField("message", email.getMessage());
        doc.addField("date", email.getDate());

        return doc;
    }

    private static String extractMessage(Path emailFile) throws Exception {
        Stream<String> emailStream = Stream.ofAll(Files.lines(emailFile).collect(Collectors.toList()))
                .dropUntil(line -> line.equals(""));
        return emailStream.mkString("\n");
    }

    private static List<String> getEmails(Path emailFile, String prefix) throws IOException {
        return Arrays.stream(getLine(emailFile, prefix).split(","))
                .map(MailFeeder::getEmail)
                .filter(Option::isDefined)
                .map(Option::get)
                .collect(Collectors.toList());
    }

    private static String getLine(Path emailFile, String prefix) throws IOException {
        return Files.lines(emailFile, Charset.defaultCharset()).filter(line -> line.startsWith(prefix)).findFirst().orElse("");
    }

    private static String getSubject(Path emailFile) throws Exception {
        return Files.lines(emailFile, Charset.defaultCharset())
                .filter(line -> line.startsWith("Subject: "))
                .map(s -> s.substring(9))
                .findFirst()
                .orElse("");
    }

    private static Option<String> getEmail(String raw) {
        Option<String> maybeEmail = Option.ofOptional(
                Arrays.stream(raw.split(" "))
                .filter(s -> s.contains("@"))
                .findFirst());

        return maybeEmail.map(email -> {
            if (email.startsWith("<"))
                return email.substring(1, email.length() - 2);
            else
                return email;
        });
    }
}
