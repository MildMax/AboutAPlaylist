package com.jburns.aap;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.*;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.pkce.AuthorizationCodePKCERequest;
import com.wrapper.spotify.requests.data.artists.GetArtistRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import org.apache.http.ParseException;

import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.Scanner;

public class SpotifyConnector {

    private static final String CLIENT_ID = "274b49bd37574a97ac41109acccb9023";
    private static final URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8888/callback");
    private static final String codeVerifier = "NlJx4kD4opk4HY7zBM6WfUHxX7HoF8A2TUhOIPGA74w";
    private static final String codeChallenge = "w6iZIj99vHGtEx_NVl9u3sthTN646vvkiP8OMCGfPmo";
    private static SpotifyApi spotifyApi = null;
    private static AuthorizationCodePKCERequest authorizationCode;
    private static AuthorizationCodeUriRequest authorizationCodeUriRequest;

    public SpotifyConnector() {

        spotifyApi = new SpotifyApi.Builder()
                .setClientId(CLIENT_ID)
                .setRedirectUri(redirectUri)
                .build();
        try {
            authorizationCodeUriRequest = spotifyApi.authorizationCodePKCEUri(codeChallenge).build();

            final URI uri = authorizationCodeUriRequest.execute();

            // get permissions and code for authorization
            Desktop.getDesktop().browse(uri);

            ServerSocket codeSock = new ServerSocket(8888);
            Socket curr = codeSock.accept();
            InputStream in = curr.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = reader.readLine();    // reads a line of text

            System.out.println(line);
            String[] vals = line.split(" ");
            String[] resourceVals = vals[1].split("=");
            String code = resourceVals[resourceVals.length - 1];

            OutputStream out = curr.getOutputStream();
            PrintWriter writer = new PrintWriter(out, true);
            writer.println("HTTP/1.1 200 OK\r\n\r\nThanks, feel free to close this window and return to the About A Playlist main page!!!\r\n\r\n");

            in.close();
            reader.close();
            out.close();
            writer.close();
            curr.close();
            codeSock.close();

            authorizationCode = spotifyApi.authorizationCodePKCE(code, codeVerifier).build();

            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCode.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());


            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | ParseException | org.apache.hc.core5.http.ParseException e) {
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

        } catch (SpotifyWebApiException | IOException | org.apache.hc.core5.http.ParseException e) {
            e.printStackTrace();
        }

        return ret;
    }

    private _Song getSong(PlaylistTrack track) throws IOException, SpotifyWebApiException, org.apache.hc.core5.http.ParseException {
        _Song s = new _Song();

        Track t = (Track) track.getTrack();

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
