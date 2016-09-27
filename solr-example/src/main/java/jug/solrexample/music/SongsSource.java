package jug.solrexample.music;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javaslang.collection.Stream;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SongsSource {

    public static final String SONGS_PATH = "music";

    public static Stream<Song> getSongsStream() {
        List<Path> list = null;
        try {
            list = Files.list(Paths.get(Thread.currentThread().getContextClassLoader().getResource(SONGS_PATH).toURI()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type listType = new TypeToken<ArrayList<Song>>() {
        }.getType();

        return Stream.ofAll(list)
                .flatMap(file -> {
                            List<String> strings = null;
                            try {
                                strings = Files.readAllLines(file);
                                String json = Stream.ofAll(strings).mkString("\n");
                                return (List<Song>) gson.fromJson(json, listType);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
    }

}
