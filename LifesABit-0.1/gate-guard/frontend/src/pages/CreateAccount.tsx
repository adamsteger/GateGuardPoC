import React, { useEffect, useState, useCallback } from 'react';
import GuardNavbar from '../components/GuardNavbar';
import axios from "axios";
import { Button, Modal, Form, Row, Col, Container } from "react-bootstrap";
import { useForm } from "react-hook-form";
import { Link, useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import '../styles/CreateAccount.scss';
import { faUser, faLock } from '@fortawesome/free-solid-svg-icons';
import sha512 from 'crypto-js/sha512';
import { toast } from 'react-toastify';
import debounce from "lodash/debounce";
import Cookies from "js-cookie";

interface CreateAccountRequest {
  username?: string;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  emailAddress?: string;
  hashedPassword?: string;
  verificationCode?: string;
  confirmPassword?: string;
}

interface CreateAccountResponse {
  name: string;
  type: "admin" | "member";
  sessionKey: string;
  success: boolean;
  message: string;
}

const CreateAccount: React.FC = () => {
  const { register, handleSubmit, watch, getValues, setValue, formState: { errors } } = useForm();
  const [passwordsDontMatch, setPasswordsDontMatch] = useState<boolean>(false);
  const [usernameTaken, setUsernameTaken] = useState<boolean>(false);
  const navigate = useNavigate();

  const onSubmit = async (data: CreateAccountRequest) => {
    setUsernameTaken(false);
    if (data.hashedPassword != data.confirmPassword) {
      setPasswordsDontMatch(true);
      return;
    }
    let hashedPassword = sha512(data.hashedPassword!);
    data.hashedPassword = hashedPassword.toString();
    data.confirmPassword = "";
    try {
      let url = (window.location.protocol == "https:") ? 
                                            "https://" + window.location.hostname + ":8443/GateGuard-0.0.1-SNAPSHOT/new-member" :
                                            "http://" + window.location.hostname + ":8080/new-member";
      await axios.post(url, data)
      .then((result) => {
        navigate("/login");
        if (result.status == 200) {
          Cookies.set('auth', result.data.sessionKey, { expires: new Date(new Date().getTime() + (60 * 60 * 1000)) });
        }
      });
    } catch (e: any) {
      if (e.message.includes("401")) {
        setUsernameTaken(true);
        // toast(result.data.message);
      } else {
        console.log(e);
      }
    }
  }

  const checkPasswords = (e: any) => {
    handlePasswordComparison();
  };

  const handlePasswordComparison = useCallback(
    debounce(() => {
      let passwordOne = getValues("hashedPassword");
      let passwordTwo = getValues("confirmPassword");
      if (passwordOne != "" && passwordTwo != "") {
        setPasswordsDontMatch(passwordOne != passwordTwo);
      }
    }, 500),
    []
  );

  return (
    <>
      <GuardNavbar/>
      <Form className="createAccountForm" onSubmit={handleSubmit(onSubmit)}>
        <Container className="mainContainer">
          <h2 className="centerHeader">Create account</h2>
          <Row>
            <Col>
              <Form.Label>First name</Form.Label>
              <Form.Control type="text" placeholder="First name" {...register("firstName")}></Form.Control>
            </Col>
            <Col>
              <Form.Label>Last name</Form.Label>
              <Form.Control type="text" placeholder="Last name" {...register("lastName")}></Form.Control>
            </Col>
          </Row>

          <Form.Label>Phone number</Form.Label>
          <Form.Control type="text" placeholder="Phone number" {...register("phoneNumber")}></Form.Control>

          <Form.Label>Username</Form.Label>
          <Form.Control type="text" placeholder="Username" {...register("username")}></Form.Control>

          {usernameTaken ? <p className="redText">This username is taken. Please select another one.</p> : <></>}

          <Form.Label>E-mail address</Form.Label>
          <Form.Control type="text" placeholder="E-mail address" {...register("emailAddress")}></Form.Control>

          <Form.Label>Password</Form.Label>
          <Form.Control type="password" placeholder="Password" {...register("hashedPassword")} onBlur={checkPasswords}></Form.Control>

          <Form.Label>Confirm Password</Form.Label>
          <Form.Control type="password" placeholder="Password" {...register("confirmPassword")} onBlur={checkPasswords}></Form.Control>

          {passwordsDontMatch ? <p className="redText">Passwords don't match</p> : <></>}

          <Form.Label>Verification code</Form.Label>
          <Form.Control type="text" placeholder="Verification code" {...register("verificationCode")} maxLength={6}></Form.Control>

          <div className="centerRow">
            <Button className="blueButton center" type="submit" disabled={passwordsDontMatch} >Create account</Button>
          </div>
          <div className="centerRow">
            or
          </div>
          <div className="centerRow">
            <Link to="/login">Already have an account? Sign in</Link>
          </div>
        </Container>
      </Form>
    </>
  );
}

export default CreateAccount;
