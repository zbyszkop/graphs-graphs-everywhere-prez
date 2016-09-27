package jug.solrexample.user;

import com.google.common.collect.Lists;
import javaslang.collection.Stream;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class UserSource {

    private static final String USERS_FILE = "friends.txt";
    public static final int NAMES_NO = 50;

    public static Stream<String> getEmailStream() {
        return Stream.ofAll(getNames())
                .map(UserSource::generateEmail)
                .take(NAMES_NO);

    }

    public static Stream<User> getUsersStream() {
        final Random random = new Random(System.currentTimeMillis());

        final List<String> emails = getEmailStream().toJavaList();
        return getEmailStream()
                .map(name -> {
                    String email = generateEmail(name);
                    int noOfFriends = random.nextInt(NAMES_NO / 2);
                    Set<String> friends =
                            Stream.range(0, noOfFriends)
                                    .map(i -> {
                                        int frNo = random.nextInt(NAMES_NO);
                                        return emails.get(frNo);
                                    })
                                    .toJavaSet();
                    return new User(name, email, friends);
                });

    }

    private static List<String> getNames() {
        final List<String> names = Lists.newArrayList();
        try {
            names.addAll(IOUtils.readLines(Thread.currentThread().getContextClassLoader().getResourceAsStream(USERS_FILE), Charset.defaultCharset()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return names;
    }

    private static String generateEmail(String name) {
        String[] split = name.split(" ");
        return (split[0] + "." + split[1] + "@example.com").toLowerCase();
    }

}
