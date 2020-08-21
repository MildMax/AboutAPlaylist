import React from 'react';
import "./App.css";
import Graph from "./Graph";
import { Scrollbars } from 'react-custom-scrollbars';

class DisplayTrackDetails extends React.Component {

    constructor(props) {
        super(props);
        let temp = this.props._current;
        let n = temp["songName"];

        this.state = {
            data: temp,
            name: n,
        }
    }

    getSong() {
        return this.props._current["songName"];
    }

    getArtists() {
        let str = "";
        let artists = this.props._current["artists"];
        for (let i = 0; i < artists.length; ++i) {
            str += artists[i] + ", ";
        }
        str = str.substring(0, str.length-2)
        return str;
    }

    getAlbum() {
        return this.props._current["albumName"];
    }

    getReleaseYear() {
        return this.props._current["albumYear"];
    }

    render () {
        return (
            <style className="TrackInfo">
                <p>{this.getSong()}</p>
                <p>{this.getArtists()}</p>
                <p>{this.getAlbum()}</p>
                <p>{this.getReleaseYear()}</p>
            </style>
        )
    }

}

class _Main extends React.Component {

    constructor(props) {
        super(props);

        let temp = JSON.parse(this.props.location.state._info);
        let name = temp["playlistName"];
        let current = temp["songList"][0];

        this.state = {
            _data: temp,
            playlistName: name,
            currentTrack: current,
            hist : props.history,
        }

    }

    MakeList() {
        let songList = this.state._data["songList"];
        let rows = [];
        for (let i = 0; i < songList.length; ++i) {
            rows.push(this.MakeEntry(songList[i]));
        }
        return rows;
    }

    MakeEntry(data) {
        return (
            <input className="SongName"
                   readOnly={true}
                   value={data["songName"]}
                   onClick={() => {this.setState({currentTrack: data})}}
            />
        )
    }

    DisplayPlaylistInfo() {
        let desc = this.state._data["playlistDescription"];
        if (desc !== "") {
            return desc;
        } else {
            return "*Playlist description not available*";
        }
    }

    CalculateStats() {
        return (
            <style className="Stats">
                <h2>Playlist Statistics: </h2>
                <p>Unique Artists: {this.FindUniqueArtists()}</p>
                <p>Unique Albums: {this.FindUniqueAlbums()}</p>
                <p>Popularity: {this.FindPopularity()}</p>
                <p>Span of Releases: {this.FindSpan()}</p>
                <p>Common Genres: {this.FindGenres()}</p>
            </style>
        )
    }

    FindUniqueArtists() {
        let songs = this.state._data["songList"]
        let artists = [];
        let unique = 0;
        let i = 0;
        for (i = 0; i < songs.length; ++i) {
            let a = songs[i]["artists"];
            let j = 0;
            for (j = 0; j < a.length; ++j) {
                if (!artists.includes(a[j])) {
                    artists.push(a[j]);
                    unique++;
                }
            }
        }
        return unique;
    }

    FindUniqueAlbums() {
        let songs = this.state._data["songList"]
        let albums = [];
        let unique = 0;
        let i = 0;
        for (i = 0; i < songs.length; ++i) {
            let a = songs[i]["albumName"];
            if (!albums.includes(a)) {
                albums.push(a);
                unique++;
            }
        }
        return unique;
    }

    FindSpan() {
        let songs = this.state._data["songList"];
        let start = -1;
        let end = -1;
        let i = 0;
        for (i = 0; i < songs.length; ++i) {
            let year = songs[i]["albumYear"]
            let info = year.split("-");

            let yearInt = parseInt(info[0]);
            if (start === -1 && end === -1) {
                start = yearInt;
                end = yearInt;
            } else {
                if (yearInt < start) {
                    start = yearInt;
                }
                if (yearInt > end) {
                    end = yearInt;
                }

            }
        }

        return start.toString() + "-" + end.toString();
    }

    FindPopularity() {
        let pop = this.state._data["averagePopularity"];
        if (pop < 20) return "Nobody listens to this";
        else if (pop < 40) return "A few people listen to this";
        else if (pop < 60) return "A moderate amount of people listen to this";
        else if (pop < 80) return "A good amount of people listen to this";
        else return "A lot of people listen to this";
    }

    FindGenres() {
        let genres = this.state._data["genres"];
        return genres.join(", ");
    }

    GoToMainPage() {
        this.state.hist.push('/');
        window.location.reload();
    }

    render() {
        return (
            <div>
                <h1 className="InfoHeader">{this.state.playlistName}</h1>
                <div className="MainContainer">

                    <div className={"SongList"}>
                        <Scrollbars
                            style={{ width: 275, height: 300}}
                            renderThumbVertical={props => <div {...props} className="thumb-vertical" />}>
                            {this.MakeList()}
                        </Scrollbars>
                    </div>
                    <div className="SecondContainer">
                        <div className="TrackContainer">
                            <img src={this.state.currentTrack["albumCover"]["url"]} />
                            <DisplayTrackDetails _current={this.state.currentTrack}/>
                        </div>
                        <div className="PlaylistInfo">
                            <p>{this.DisplayPlaylistInfo()}</p>
                        </div>
                    </div>
                    <div>
                        <div className="Stats_d"> {this.CalculateStats()} </div>
                        <button
                            className="PageButton"
                            onClick={() => this.GoToMainPage()}>
                            Return to Main Page
                        </button>
                        <div>Developed and Designed by Joseph Burns</div>
                    </div>
                    <div className="Graph">
                        <Graph data={this.state._data}/>
                    </div>

                </div>
            </div>
        )
    }
}
export default _Main;