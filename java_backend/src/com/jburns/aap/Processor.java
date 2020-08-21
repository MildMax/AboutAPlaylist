package com.jburns.aap;

public class Processor {

    public static String parseSpotifyURL(String url) {
        String startSeq = "playlist/";
        String endSeq = "?";

        int start = -1;
        int end = -1;

        int diff = startSeq.length();
        for (int i = diff; i < url.length(); ++i) {
            if (url.substring(i-diff, i).compareTo(startSeq) == 0) {
                start = i;
            }
            if (url.substring(i-1,i).compareTo(endSeq) == 0) {
                end = i-1;
            }

            if (start != -1 && end != -1) break;
        }

        if (start == -1 || end == -1) {
            throw new IllegalArgumentException("Playlist does not exist");
        }

        return url.substring(start, end);

    }

    public static String parseSpotifyArtistURI(String uri) {
        String startSeq = ":artist:";
        int start = -1;

        int diff = startSeq.length();
        for (int i = diff; i < uri.length(); ++i) {
            if (uri.substring(i-diff, i).compareTo(startSeq) == 0) {
                start = i;
                break;
            }
        }

        if (start == -1) {
            throw new IllegalArgumentException("Playlist does not exist");
        }

        return uri.substring(start);
    }

    //url:
    //https://open.spotify.com/playlist/
    //4qMSNUurUwslbJn9fi9E07?si=1kbFOXPTTC-nApCniEAAXg

    //id:
    //4qMSNUurUwslbJn9fi9E07
}
