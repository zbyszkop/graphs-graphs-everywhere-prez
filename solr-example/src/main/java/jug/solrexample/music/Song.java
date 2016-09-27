package jug.solrexample.music;

public class Song {
    private String artist;
    private String title;
    private Genre genre;
    private int chartPosition;

    public Song(String artist, String title, Genre genre, int chartPosition) {
        this.artist = artist;
        this.title = title;
        this.genre = genre;
        this.chartPosition = chartPosition;
    }


    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public Genre getGenre() {
        return genre;
    }

    public int getChartPosition() {
        return chartPosition;
    }

    @Override
    public String toString() {
        return "Song{" +
                "artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", genre=" + genre +
                ", chartPosition=" + chartPosition +
                '}';
    }
}
