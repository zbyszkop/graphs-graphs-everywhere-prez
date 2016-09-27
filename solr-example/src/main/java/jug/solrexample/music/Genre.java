package jug.solrexample.music;

import com.google.gson.annotations.SerializedName;

public enum Genre {
    @SerializedName("alternative rock")
    ALTERNATIVE_ROCK("alternative rock"),
    @SerializedName("blues")
    BLUES("blues"),
    @SerializedName("electro")
    ELECTRO("electro"),
    @SerializedName("hard rock")
    HARD_ROCK("hard rock"),
    @SerializedName("jazz")
    JAZZ("jazz"),
    @SerializedName("metal")
    METAL("metal"),
    @SerializedName("rap")
    RAP("rap");


    private final String name;

    Genre(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }


}
