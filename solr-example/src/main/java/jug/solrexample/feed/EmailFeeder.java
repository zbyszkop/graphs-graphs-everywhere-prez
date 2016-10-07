package jug.solrexample.feed;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javaslang.collection.Stream;
import javaslang.control.Option;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class EmailFeeder {
    public static final SimpleDateFormat EMAIL_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z", Locale.US);
    private String zkHost;

    public static void main(String[] args) throws Exception {
        new EmailFeeder("localhost:9983").feed() ;

    }

    public EmailFeeder(String zkHost) {
        this.zkHost = zkHost;
    }

    public void feed() throws Exception {



        List<Email> emails = getEmails();


        CloudSolrClient solr = new CloudSolrClient.Builder().withZkHost(zkHost).build();
        solr.setDefaultCollection("emails");


        emails.stream().map(EmailFeeder::mapToSolrDocument)
                .forEach((doc) -> {
                    try {
                        solr.add(doc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        solr.commit();
    }

    private List<Email> getEmails() throws IOException {
        Type listType = new TypeToken<ArrayList<Email>>(){}.getType();
        String mailFile = Thread.currentThread().getContextClassLoader().getResource("emails.json").getFile();
        Path path = Paths.get(mailFile);
        String emailsJson = Stream.ofAll(Files.readAllLines(path, Charset.defaultCharset())).mkString();
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(emailsJson, listType);

    }

    private Email getEmail(Path emailFile) throws Exception {
        List<String> lines = Files.readAllLines(emailFile);
        String fromLine = getLine(lines, "From: ");
        String to = getEmails(lines, "To: ").stream().findFirst().orElse("");
        String cc = getEmails(lines, "Cc: ").stream().findFirst().orElse("");
        String bcc = getEmails(lines, "Bcc: ").stream().findFirst().orElse(""); //TODO: add Date
        String date = getLine(lines, "Date:").split(" ", 2)[1];
        Date parsedDate = EMAIL_DATE_FORMAT.parse(date);
        String subject = getSubject(lines);
        String msg = extractMessage(lines);

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

    private static String extractMessage(List<String> lines) throws Exception {
        Stream<String> emailStream = Stream.ofAll(lines)
                .dropUntil(line -> line.equals(""));
        return emailStream.mkString("\n");
    }

    private static List<String> getEmails(List<String> lines, String prefix) throws IOException {
        return Arrays.stream(getLine(lines, prefix).split(","))
                .map(EmailFeeder::getEmail)
                .filter(Option::isDefined)
                .map(Option::get)
                .collect(Collectors.toList());
    }

    private static String getLine(List<String> lines, String prefix) throws IOException {
        return lines.stream().filter(line -> line.startsWith(prefix)).findFirst().orElse("");
    }

    private static String getSubject(List<String> lines) throws Exception {
        return lines.stream()
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
