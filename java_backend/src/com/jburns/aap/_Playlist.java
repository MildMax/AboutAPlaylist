package com.jburns.aap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class _Playlist {

    private String playlistName;
    private String playlistDescription;
    private int averagePopularity;
    private String[] genres;
    private List<_Song> songList;
    int[] years;

    public _Playlist() {
        songList = new LinkedList<>();
    }

    public void setPlaylistName(String playlistName) {
        if (playlistName == null) throw new IllegalArgumentException("playlistName cannot be null");
        this.playlistName = playlistName;
    }

    public void setPlaylistDescription(String playlistDescription) {
        if (playlistDescription == null) throw new IllegalArgumentException("playlistDescription cannot be null");
        this.playlistDescription = playlistDescription;
    }

    public void addSong(_Song song) {
        if (song == null) throw new IllegalArgumentException("song cannot be null");
        songList.add(song);
    }

    public void setAveragePopularity(int popularity) {
        this.averagePopularity = popularity;
    }

    public void setYears() {
        years = new int[songList.size()];
        int i = 0;
        for(_Song s : songList) {
            String curr = s.getAlbumYear();
            String[] curr_date = curr.split("-");
            years[i] = Integer.parseInt(curr_date[0]);
            ++i;
        }
    }

    public void setGenres() {
        HashMap<String, Integer> g = new HashMap<>();
        for (_Song song : songList) {
            for (String ge : song.getGenres()) {
                if (!g.containsKey(ge)) {
                    g.put(ge, 1);
                } else {
                    g.put(ge, g.get(ge) + 1);
                }
            }
        }

        PriorityQueue<GenreCounter> gens = new PriorityQueue<>();
        for (String genrs : g.keySet()) {
            int count = g.get(genrs);
            if (gens.size() < 5) {
                gens.add(new GenreCounter(genrs, count));
            } else if (gens.peek().count < count) {
                gens.poll();
                gens.add(new GenreCounter(genrs, count));
            }
        }

        genres = new String[5];
        int i = 0;
        while (!gens.isEmpty()) {
            genres[i++] = gens.poll().genre;
        }

        String[] gTemp = new String[genres.length];
        int j = 0;
        for (i = genres.length-1; i >= 0; --i) {
            if (genres[i] == null) continue;
            gTemp[j] = genres[i];
            ++j;
        }
        genres = gTemp;
    }

    private static class GenreCounter implements Comparable<GenreCounter> {

        String genre;
        int count;

        GenreCounter(String genre, int count) {
            this.genre = genre;
            this.count = count;
        }

        @Override
        public int compareTo(GenreCounter o) {
            if (this.count < o.count) return -1;
            else if (this.count > o.count) return 1;
            else return 0;
        }
    }

    @Override
    public String toString() {
        String ret = playlistName;
        //for (String s : trackNames) ret = ret.concat("<->" + s);
        return ret;
    }

}
