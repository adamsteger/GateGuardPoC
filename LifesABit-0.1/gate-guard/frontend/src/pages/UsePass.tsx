import React from 'react';
import {useState, useEffect} from 'react';
import { useForm } from "react-hook-form";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSpinner } from '@fortawesome/free-solid-svg-icons';
import axios from "axios";
import Cookies from "js-cookie";
import {Button} from "react-bootstrap";
import "../styles/usePasses.scss";
import { toast } from 'react-toastify';
//import Table from "./Table";

interface VerifyPassRequest {
  passID?: string;
}

interface VerifyPassResponse {
  isValid?: boolean;
  usageBased?: boolean;
  expirationDate?: number;
  usesLeft?: number;
  usesTotal?: number;
  message?: string;
}

interface UsePassRequest extends VerifyPassRequest {}

const UsePass: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(true);
  const [expirationDate, setExpirationDate] = useState<number | undefined>();
  const [isValid, setIsValid] = useState<boolean>(false);
  const [usageBased, setUsageBased] = useState<boolean>();
  const [usesLeft, setUsesLeft] = useState<number>();
  const [usesTotal, setUsesTotal] = useState<number>();
  const [message, setMessage] = useState<string>();

  let urlName = window.location.href;
  //https://www.gate-guard.com/use-pass?passID=10289391829

  let startIndex = urlName.indexOf('=');
  let passID1 = urlName.substring(startIndex+1);
  
  const verifyPassFunc = async (data: VerifyPassRequest) => {
    try {
      let url = (window.location.protocol == "https:") ? 
                                            "https://" + window.location.hostname + ":8443/GateGuard-0.0.1-SNAPSHOT/verify-pass" :
                                            "http://" + window.location.hostname + ":8080/verify-pass";
      await axios.post(url, data)
      .then((result) => {
        setIsValid(result.data.isValid);
        setUsageBased(result.data.usageBased);
        setMessage(result.data.message);
        if (result.data.usageBased) {
          setUsesLeft(result.data.usesLeft);
          setUsesTotal(result.data.usesTotal);
        } else {
          setExpirationDate(result.data.expirationDate);
        }
        setLoading(false);
      });
    } catch (e: any) {
      if (e.message.includes("401")) {
      } else {
        console.log(e);
      }
    }
  }

  const doUsePassFunc = async (data: UsePassRequest) => {
    try {
      let url = (window.location.protocol == "https:") ? 
                                            "https://" + window.location.hostname + ":8443/GateGuard-0.0.1-SNAPSHOT/use-pass" :
                                            "http://" + window.location.hostname + ":8080/use-pass";
      await axios.post(url, data)
      .then((result) => {
        setIsValid(result.data.isValid);
        setUsageBased(result.data.usageBased);
        setMessage(result.data.message);
        if (result.data.usageBased) {
          setUsesLeft(result.data.usesLeft);
          setUsesTotal(result.data.usesTotal);
        } else {
          setExpirationDate(result.data.expirationDate);
        }
        setLoading(false);
      });
    } catch (e: any) {
      if (e.message.includes("401")) {
      } else {
        console.log(e);
      }
    }
  }

  const doUseButton = () => {
    if (usageBased && usesLeft! > 0) {
      toast("Pass used!");
      doUsePassFunc({passID: passID1});
    } else if (!usageBased) {
      toast("Pass used!");
      doUsePassFunc({passID: passID1});
    }
  }

  useEffect(() => {
    if (startIndex != -1) {
      verifyPassFunc({passID: passID1});
    }
  }, []);

 
  return (
    <div className={`usePassContainerDiv ${loading ? "background-gray" : (isValid ? "background-green" : "background-red")}`}>
      <h2 className="usePassLabel">
        {loading ? <div className="loadingDiv">
          <h1>Loading</h1>
          <FontAwesomeIcon className="spinner" icon={faSpinner}/>
        </div> : (isValid ? <h1>Pass is valid</h1> : <h1>Pass is invalid/expired!</h1>)}
        {!loading && usageBased && <>
          {usesLeft} / {usesTotal} uses
        </>}
        {!loading && !usageBased && <>
          Expires: {new Date(expirationDate!).toLocaleDateString("en-US")}
        </>}

        <br/>
        {isValid && <Button variant="light" onClick={doUseButton}>Use Pass Now</Button>}
      </h2>

     
</div>
      
  );
  
}

export default UsePass;