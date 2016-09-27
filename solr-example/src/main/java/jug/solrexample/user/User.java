package jug.solrexample.user;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class User {
    String fullName;
    String email;
    ImmutableSet<String> friends; //for example's sake, those will be emails

    public User(String fullName, String email, Set<String> friends) {

        this.fullName = fullName;
        this.email = email;
        this.friends = ImmutableSet.copyOf(friends);
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getFriends() {
        return friends;
    }

    @Override
    public String toString() {
        return "User{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", friends=" + friends +
                '}';
    }
}
