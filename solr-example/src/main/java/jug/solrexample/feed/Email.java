package jug.solrexample.feed;

import java.util.Date;

public class Email {
    private String from;
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String message;
    private Date date;

    public Email(String from, String subject, String to, String cc, String bcc, String message, Date date) {
        this.from = from;
        this.subject = subject;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.message = message;
        this.date = date;
    }


    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getCc() {
        return cc;
    }

    public String getBcc() {
        return bcc;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Email{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", cc='" + cc + '\'' +
                ", bcc='" + bcc + '\'' +
                ", subject='" + subject + '\'' +
//                ", message='" + message + '\'' +
                ", date=" + date +
                '}';
    }

    public Date getDate() {
        return date;
    }
}
