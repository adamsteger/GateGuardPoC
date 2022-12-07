import React, { useEffect, useState } from 'react';
import '../styles/MusicControls.scss';
import {Button, Row, Col} from "react-bootstrap";
import axios from 'axios';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPlay } from '@fortawesome/free-solid-svg-icons';

interface SampleRequest {
  thisIsAString?: string;
  thisIsABool?: boolean;
}

interface SampleResponse {
  thisIsAnInt?: number;
}

const SampleComponent: React.FC = () => {
  const [theInt, setTheInt] = useState<number>(50);
  
  const doSampleRequest = async (data: SampleRequest): Promise<SampleResponse> => {
    try {
      let url = (window.location.protocol == "https:") ? 
                                            "https://" + window.location.hostname + ":8443/GateGuard-0.0.1-SNAPSHOT/samplerequest" :
                                            "http://" + window.location.hostname + ":8080/samplerequest";
      await axios.post(url, data)
      .then((result) => {
        setTheInt(result.data.thisIsAnInt);
      });
    } catch (e: any) {
      console.log(e);
    }
    return {};
  }

  return (
    <>
      <div>
        
      </div>
    </>
  );
}

export default SampleComponent;
