package jug.solrexample.feed;


import javaslang.collection.Stream;
import javaslang.control.Option;
import javaslang.control.Try;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Feeder {
    public static void main(String[] args) throws Exception {
        String emailDir = Thread.currentThread().getContextClassLoader().getResource("easy_ham").getFile();
        Path path = Paths.get(emailDir);
        List<Path> emailFiles = Files.list(path).collect(Collectors.toList());

        List<Email> collectedEmails = emailFiles.stream().map(emailFile ->
                Try.of(() -> {
                            String fromLine = getLine(emailFile, "From: ");
                            List<String> to = getEmails(emailFile, "To: ");
                            List<String> cc = getEmails(emailFile, "Cc: ");
                            List<String> bcc = getEmails(emailFile, "Bcc: ");
                            String subject = getSubject(emailFile);
                            String msg = extractMessage(emailFile);

                    return new Email(getEmail(fromLine).getOrElse(""), subject, to, cc, bcc, msg);
                }
                )
        )
            .filter(t -> t instanceof Try.Success) //not interested in unreadable files
            .map(Try::get)
            .collect(Collectors.toList());

        collectedEmails.stream().map(Feeder::mapToSolrDocument);

    }

    private static SolrInputDocument mapToSolrDocument(Email email) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("from", email.getFrom());
        doc.addField("to", email.getTo());
        doc.addField("cc", email.getCc());
        doc.addField("bcc", email.getBcc());
        doc.addField("subject", email.getSubject());
        doc.addField("message", email.getMessage());

        return doc;
    }

    private static String extractMessage(Path emailFile) throws Exception {
        Stream<String> emailStream = Stream.ofAll(Files.lines(emailFile).collect(Collectors.toList()))
                .dropUntil(line -> line.equals(""));
        return emailStream.mkString("\n");
    }

    private static List<String> getEmails(Path emailFile, String prefix) throws IOException {
        return Arrays.stream(getLine(emailFile, prefix).split(","))
                .map(Feeder::getEmail)
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
