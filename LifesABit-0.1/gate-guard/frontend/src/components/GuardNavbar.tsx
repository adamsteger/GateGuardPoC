import React, {useState, useEffect} from 'react';
import ReactDOM from 'react-dom';
import '../styles/GuardNavbar.scss';
import {Navbar, Nav, NavDropdown, Container} from "react-bootstrap";
import gate_guard_logo from "../resources/gate_guard.png";
import { Link, NavLink, useNavigate } from 'react-router-dom';
import axios from "axios";
import Cookies from "js-cookie";

interface UserInfoRequest {
  sessionKey?: string;
}

interface UserInfoResponse {
  firstName?: string;
  isAdmin?: boolean;
}

interface SignOutRequest {
  sessionKey?: string;
}

interface SignOutResponse {
  success?: boolean;
}

function GuardNavbar() {

  const [greetingName, setGreetingName] = useState<string>("");
  const [isAdmin, setIsAdmin] = useState<boolean>(false);
  const [loggedIn, setLoggedIn] = useState<boolean>(false);
  const navigate = useNavigate();

  const getUserInfo = async (data: UserInfoRequest) => {
    try {
      let url = (window.location.protocol == "https:") ? 
                                            "https://" + window.location.hostname + ":8443/GateGuard-0.0.1-SNAPSHOT/user-info" :
                                            "http://" + window.location.hostname + ":8080/user-info";
      await axios.post(url, data)
      .then((result) => {
        if (result.status == 200) {
          setGreetingName(result.data.firstName);
          setIsAdmin(result.data.isAdmin);
          setLoggedIn(true);
        }
      });
    } catch (e: any) {
      if (e.message.includes("401")) {
        setGreetingName("");
        setLoggedIn(false);
        setIsAdmin(false);
      } else {
        console.log(e);
      }
    }
  }

  const logOutFunc = async () => {
    try {
      let url = (window.location.protocol == "https:") ? 
                                            "https://" + window.location.hostname + ":8443/GateGuard-0.0.1-SNAPSHOT/log-out" :
                                            "http://" + window.location.hostname + ":8080/log-out";
      await axios.post(url, {sessionKey: Cookies.get("auth")})
      .then((result) => {
        if (result.status == 200) {
          navigate("/login");
          setGreetingName("");
        }
      });
    } catch (e: any) {
      if (e.message.includes("401")) {
        setGreetingName("");
      } else {
        console.log(e);
      }
    }
  }

  useEffect(() => {
    document.title = "Gate Guard";
    let sessionKey = Cookies.get("auth");
    if (sessionKey != null) {
      getUserInfo({sessionKey: sessionKey});
    }
  }, []);
  return (
    <>
      <Navbar bg="dark" expand="lg" variant="dark">
        <Container id="nav-container">
          <Navbar.Brand id="nav-bar">
            <Link to="/" className="noTextDeco">
              <img src={gate_guard_logo} id="gate-guard-logo" alt="Gate guard logo"/>
              Gate Guard
            </Link>
          </Navbar.Brand>
          <Navbar.Toggle aria-controls="basic-navbar-nav" />
          <Navbar.Collapse className="justify-content-end">
            <Nav className="me-auto">
              <Nav.Link as={NavLink} to="/" className="noTextDeco">Start here</Nav.Link>
              {/* <Nav.Link as={NavLink} to="/updates" className="noTextDeco">Updates</Nav.Link> */}
              {loggedIn && 
                <Nav.Link as={NavLink} to="/mypasses" className="noTextDeco">MyPasses</Nav.Link>
              }
              {loggedIn && 
                <Nav.Link as={NavLink} to="/notifications" className="noTextDeco">Notifications</Nav.Link>
              }
              {loggedIn && isAdmin &&
                <Nav.Link as={NavLink} to="/settings" className="noTextDeco">Settings</Nav.Link>
              }
              {loggedIn ?
                <Nav.Link as={NavLink} to="/login" className="noTextDeco" onClick={logOutFunc}>Log Out</Nav.Link>
                :
                <Nav.Link as={NavLink} to="/login" className="noTextDeco">Log In</Nav.Link>
              }
              {/* <NavDropdown title="Programs" id="basic-nav-dropdown">
              <NavDropdown.Item href="#action/3.1">Action</NavDropdown.Item>
              <NavDropdown.Item href="#action/3.2">Another action</NavDropdown.Item>
              <NavDropdown.Item href="#action/3.3">Something</NavDropdown.Item>
              <NavDropdown.Divider />
              <NavDropdown.Item href="#action/3.4">Separated link</NavDropdown.Item>
            </NavDropdown> */}
            {greetingName && <Nav.Link className="navGreetings">Hello, {greetingName}</Nav.Link>}
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>
    </>
  );
}

export default GuardNavbar;
