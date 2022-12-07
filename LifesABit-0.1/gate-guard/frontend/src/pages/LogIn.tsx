import React, { useEffect, useState } from 'react';
import GuardNavbar from '../components/GuardNavbar';
import axios from "axios";
import { Button, Modal, Form } from "react-bootstrap";
import { useForm } from "react-hook-form";
import { Link, useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import '../styles/LogIn.scss';
import { faUser, faLock } from '@fortawesome/free-solid-svg-icons';
import sha512 from 'crypto-js/sha512';
import { toast } from 'react-toastify';
import Cookies from "js-cookie";

interface LogInRequest {
  username?: string;
  hashedPassword?: string;
}

interface LogInResponse {
  sessionKey: string;
}

const LogIn: React.FC = () => {
  const { register, handleSubmit, watch, formState: { errors } } = useForm();
  const [wrongLogin, setWrongLogin] = useState<boolean>(false);
  const navigate = useNavigate();

  const onSubmit = async (data: LogInRequest) => {
    setWrongLogin(false);
    let hashedPassword = sha512(data.hashedPassword!);
    data.hashedPassword = hashedPassword.toString();
    try {
      let url = (window.location.protocol == "https:") ? 
                                            "https://" + window.location.hostname + ":8443/GateGuard-0.0.1-SNAPSHOT/log-in" :
                                            "http://" + window.location.hostname + ":8080/log-in";
      await axios.post(url, data)
      .then((result) => {
        if (result.status == 200) {
          Cookies.set('auth', result.data.sessionKey, { expires: new Date(new Date().getTime() + (60 * 60 * 1000)) });
          navigate("/mypasses");
        }
      });
    } catch (e: any) {
      if (e.message.includes("401")) {
        setWrongLogin(true);
      } else {
        console.log(e);
      }
    }
  }

  return (
    <>
      <GuardNavbar/>
      <Form className="logInForm" onSubmit={handleSubmit(onSubmit)}>
        <Form.Label>Username</Form.Label>
        <div className="iconInInput">
          <FontAwesomeIcon icon={faUser} className="icon"/>
          <Form.Control type="text" placeholder="Username" {...register("username")}></Form.Control>
        </div>

        <Form.Label>Password</Form.Label>
        <div className="iconInInput">
          <FontAwesomeIcon icon={faLock} className="icon"/>
          <Form.Control type="password" placeholder="Password" {...register("hashedPassword")}></Form.Control>
        </div>

        {wrongLogin && <p className="redText">Incorrect username or password.</p>}

        <div className="centerRow">
          <Button className="blueButton center" type="submit">Sign in</Button>
        </div>
        <div className="centerRow">
          or
        </div>
        <div className="centerRow">
          <Link to="/create-account">Create new account</Link>
        </div>
      </Form>
    </>
  );
}

export default LogIn;
