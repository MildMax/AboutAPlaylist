import React from 'react';
import CanvasJSReact from "./canvasjs.react";
const CanvasJS = CanvasJSReact.CanvasJS;
const CanvasJSChart = CanvasJSReact.CanvasJSChart;

class Graph extends React.Component {
    render() {

        CanvasJS.addColorSet("spotify_green", ["#1ED760"]);

        let year_data = this.props.data["years"];
        let year_count = {};
        for (let i = 0; i < year_data.length; ++i) {
            let y = parseInt(year_data[i]);
            if (!(y in year_count)) {
                year_count[y] = 1;
            } else {
                year_count[y] = year_count[y] + 1;
            }
        }

        let _years = [];
        for (const [_x, y] of Object.entries(year_count)) {
            let x = parseInt(_x);
            _years.push({x , y});
        }

        this.state = {
            yearss: year_count,
            __years: _years,
        }


        const options = {
            animationEnabled: false,
            exportEnabled: false,
            height: 400,
            theme: "dark1", //"light1", "dark1", "dark2"
            backgroundColor: "#313131",
            title:{
                text: "Songs Released Per Year"
            },
            axisY: {
                includeZero: true,
                title: "Total Songs"
            },
            axisX: {
                minimum: 1950,
                maximum: 2025,
                valueFormatString: "####",
                title: "Year of Release",
            },
            colorSet: "spotify_green",
            data: [{
                type: "column", //change type to bar, line, area, pie, etc
                //indexLabel: "{y}", //Shows y value on all Data Points
                indexLabelFontColor: "#1ED760",
                indexLabelPlacement: "outside",
                dataPoints: _years,
                xValueFormatString: "####"
            }]
        }

        return (
            <CanvasJSChart options={options}/>
        );
    }
}
export default Graph;