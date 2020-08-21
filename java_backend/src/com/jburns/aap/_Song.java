package com.jburns.aap;

import com.wrapper.spotify.model_objects.specification.Image;

import java.util.LinkedList;
import java.util.List;

public class _Song {

    private String songName;
    private String albumName;
    private String albumYear;
    private int popularity;
    private String[] genres;
    private List<String> artists;
    private Image albumCover;


    public _Song() {
        artists = new LinkedList<>();
    }

    public void setSongName(String songName) {
        if (songName == null) throw new IllegalArgumentException("songName cannot be null");
        this.songName = songName;
    }

    public void setAlbumInfo(String albumName, String albumYear) {
        if (albumName == null || albumYear == null) throw new IllegalArgumentException("albumName cannot be null");
        this.albumName = albumName;
        this.albumYear = albumYear;
    }

    public String getAlbumYear() {
        return this.albumYear;
    }

    public void addArtist(String artist) {
        if (artist == null) throw new IllegalArgumentException("artist cannot be null");
        artists.add(artist);
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int getPopularity() {
        return this.popularity;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public String[] getGenres() {
        return this.genres;
    }

    public void setAlbumCover(Image i) {
        this.albumCover = i;
    }
}
