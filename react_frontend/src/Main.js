import spotify_logo from "./spotify_logo.png";
import React from "react";

function Title(props) {
    return (
        <div className="Header">
            <img src={spotify_logo} alt='logo'/>
            <h1>About A Playlist</h1>
        </div>
    )
}

function Description(props) {
    return (
        <div className="Description">
            <span id="line1">Want to learn more about a playlist on Spotify?</span>
            <span>Enter a playlist URL below and search!</span>
        </div>
    )
}

class Main extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            value:"Enter a Spotify URL to Search",
            //value:"https://open.spotify.com/playlist/4qMSNUurUwslbJn9fi9E07?si=DSAFIr03QBWzvyS5FB2eEQ",
            client: null,
        }
    }

    componentDidMount() {

        this.state.client = new WebSocket("ws://localhost:8888");
        //this.state.client = new WebSocket("ws://192.168.56.1:8888");

        this.state.client.onopen = () => {
            //alert("connection open")

        };

        this.state.client.onerror = () => {
            alert("error connecting to server");
        };

        this.state.client.onclose = () => {
            //alert("closing the client connection")
        }

        this.state.client.onmessage = (data) => {
            //alert(data.data);
            if (data.data === "N/A") {
                alert("Invalid playlist provided");
                this.revertForm();
                return;
            }
            this.props.history.push('/info', {_info : data.data});
            this.state.client.close();
            window.location.reload();
        }
    }

    handleChange(event) {
        this.setState({value: event.target.value});
    }

    handleSubmit(event) {
        event.preventDefault();
        this.state.client.send(this.state.value);
    }

    clearForm(props) {
        this.setState({value: ''});
    }

    revertForm() {
        this.setState({value: 'Enter a Spotify URL to Search'})
    }

    render() {
        return (
            <div>
                <Title />
                <Description />
                <div className="Search">
                    <form onSubmit={
                        (event) =>  {
                            this.handleSubmit(event);
                        }
                    }>
                        <label>
                            <input type="text" value={this.state.value}
                                   onClick={(event) => this.clearForm(event)}
                                   onChange={(event) => this.handleChange(event)} />
                        </label>
                        <button type="submit">
                            Go!
                        </button>
                    </form>
                </div>
            </div>
        )
    }
}
export default Main;