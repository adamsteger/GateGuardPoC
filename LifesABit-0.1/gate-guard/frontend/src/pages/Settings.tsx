import React from 'react';
import {useState} from 'react';
import ReactSlider from "react-slider";
import ReactDOM from "react-dom";
import ImageLoader from "react-imageloader";
import styled from "styled-components";
import GuardNavBar from '../components/GuardNavbar';
import Search from './Search';
import './Settings.css';
import { Button } from "react-bootstrap";

const Settings: React.FC = () => {

//Default Values
    const [v1, sV1] = useState(10);
    const [v2, sV2] = useState(25);
    const [v3, sV3] = useState(5);

    const getProgression1 = () => {
      return {backgroundSize: `${v1*100 / 30+1}% 100%`}
    };

    const getProgression2 = () => {
      return {backgroundSize: `${v2*100 / 100}% 100%`}
    };

    const getProgression3 = () => {
      return {backgroundSize: `${v3*100 / 10}% 100%`}
    };

    return (
        <>
        <GuardNavBar/>
        <div className="contentContainer">

             <h1 style={{textAlign: "left"}}>
              Admin Settings </h1>
              <hr 
              style={{
                background: 'white',
                color: 'white',
                borderColor: 'white',
                height: '3px',
              }}
              />
              </div>
             

              <div className="bar_input" style={{marginLeft: "20px"}}>
              <h6 style={{textAlign: "left"}}>
                Maximum Pass Duration<br></br>
                <input type="range" min={1} max={30} value={v1} 
                onChange={(e) => sV1(e.target.valueAsNumber)}
                style={getProgression1()}
                />
                <br></br>
                <span>{v1} Days</span>
               </h6>

                <h6 style={{textAlign: "left"}}>
                Maximum Pass Usage Limit<br></br>
                <input type="range" min={1} max={100} value={v2} 
                onChange={(e) => sV2(e.target.valueAsNumber)}
                style={getProgression2()}
                />
                <br></br>
                <span>{v2} Uses</span>
               </h6>               

                <h6 style={{textAlign: "left"}}>
                Maximum Pass Per User<br></br>
                <input type="range" min={1} max={10} value={v3} 
                onChange={(e) => sV3(e.target.valueAsNumber)}
                style={getProgression3()}
                />
                <br></br>
                <span>{v3} Passes</span>
               </h6>  

              <br></br>
               <h3 style={{textAlign: "left"}}>
                Users          
               </h3> 
               </div>
              <Search />
             
              
              <div className="the_button" style={{marginLeft: "20px"}} >
               <h6 style={{textAlign: "left"}}>
                <br></br>
                <Button variant="light">Save</Button>
               </h6>
              </div>  
        </>
    );
}

export default Settings;