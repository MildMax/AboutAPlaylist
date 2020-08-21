import React from 'react';
import { Switch, Route } from 'react-router';
import {BrowserRouter as Router} from "react-router-dom";

import "./App.css";

import history from "./history";
import _Main from "./infoMain";
import Main from "./Main";

export default class App extends React.Component {

    state = {
        _info : ""
    }

    render() {
        return (
            <Router history={history}>
                <Switch>
                    <Route exact path='/' component={Main} />
                    <Route exact path='/info' component={_Main} />
                </Switch>
            </Router>
        )
    }
}