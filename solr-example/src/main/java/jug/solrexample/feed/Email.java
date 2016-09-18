package jug.solrexample.feed;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class Email {
    private String from;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;
    private String message;


    public Email(String from, String subject, List<String> to, List<String> cc, List<String> bcc, String message) {
        this.from = from;
        this.subject = subject;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.message = message;
    }


    public String getFrom() {
        return from;
    }

    public List<String> getTo() {
        return ImmutableList.copyOf(to);
    }

    public String getSubject() {
        return subject;
    }

    public List<String> getCc() {
        return ImmutableList.copyOf(cc);
    }

    public List<String> getBcc() {
        return ImmutableList.copyOf(bcc);
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Email{" +
                "from='" + from + '\'' +
                ", to=" + to +
                ", cc=" + cc +
                ", bcc=" + bcc +
                ", subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
