package com.jburns.aap;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.*;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import org.apache.http.ParseException;

import java.io.IOException;
import java.io.File;
import java.util.Scanner;

public class SpotifyConnector {

    private static final String CLIENT_ID = "274b49bd37574a97ac41109acccb9023";
    private static SpotifyApi spotifyApi = null;
    private static ClientCredentialsRequest clientCredentialsRequest;

    public SpotifyConnector() {
        String url = System.getProperty("user.dir");
        //url = url.concat("\\src\\com\\jburns\\aap");
        //System.out.println(url);
        File cs = new File(url + "\\cs.txt");
        String cstxt = null;
        try {
            Scanner s = new Scanner(cs);
            cstxt = s.nextLine();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        spotifyApi = new SpotifyApi.Builder()
                .setClientId(CLIENT_ID)
                .setClientSecret(cstxt)
                .build();
        clientCredentialsRequest = spotifyApi.clientCredentials().build();
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            // Set access token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());

            System.out.println("Expires in: " + clientCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    public _Playlist getPlaylist(String id) {
        GetPlaylistRequest getPlaylistRequest = spotifyApi.getPlaylist(id).build();

        _Playlist ret = new _Playlist();

        try {
            Playlist playlist = getPlaylistRequest.execute();

            ret.setPlaylistName(playlist.getName());
            ret.setPlaylistDescription(playlist.getDescription());

            Paging<PlaylistTrack> trackContainer = playlist.getTracks();
            PlaylistTrack[] tracks = trackContainer.getItems();

            int[] pops = new int[tracks.length];
            int i = 0;
            for (PlaylistTrack t : tracks) {
                _Song curr = getSong(t);
                pops[i++] = curr.getPopularity();
                ret.addSong(curr);
            }

            int pop = 0;
            for (i = 0; i < pops.length; ++i) pop += pops[i];
            pop = pop/pops.length;

            ret.setAveragePopularity(pop);

            ret.setGenres();

            ret.setYears();

        } catch (SpotifyWebApiException | IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    private _Song getSong(PlaylistTrack track) throws IOException, SpotifyWebApiException {
        _Song s = new _Song();

        Track t = track.getTrack();

        s.setSongName(t.getName());
        AlbumSimplified album = t.getAlbum();
        s.setAlbumInfo(album.getName(), album.getReleaseDate());
        ArtistSimplified[] artists = t.getArtists();
        for (ArtistSimplified as : artists) {
            s.addArtist(as.getName());
            String id = Processor.parseSpotifyArtistURI(as.getUri());
            GetArtistRequest ar = spotifyApi.getArtist(id).build();
            Artist art = ar.execute();
            s.setGenres(art.getGenres());
        }
        s.setPopularity(t.getPopularity());
        Image[] images = t.getAlbum().getImages();
        if (images.length > 0) {
            s.setAlbumCover(images[0]);
        }

        return s;
    }


}
