import React, { useEffect, useState } from 'react';
//import {map} from 'rxjs/add/operator/map';
import { users } from "./Users";
import { Button, Modal } from "react-bootstrap";
import './Search.css';
import "../styles/TableModal.scss"
import PassComponent from "../components/PassComponent.tsx";
import axios from 'axios';
import Cookies from 'js-cookie';


const Table = ({data}) => {
    const [show, setShow] = useState(false);
    const [passList, setPassList] = useState([]);

    const renderPassList = (passList) => {
        var list = [];
        passList.forEach((item) => {
          list.push(<PassComponent
            firstName={item.firstName}
            lastName={item.lastName}
            email={item.email}
            expirationDate={item.expirationDate}
            usesLeft={item.usesLeft}
            usesTotal={item.usesTotal}
            usageBased={item.usageBased}
            passID={item.passID}
            revokePassFunc={revokePass} />);
        });
        return <>{list}</>
    }

    const loadPasses = async (passListReq) => {
        try {
          let url = (window.location.protocol == "https:") ? 
                                            "https://" + window.location.hostname + ":8443/GateGuard-0.0.1-SNAPSHOT/load-passes-admin" :
                                            "http://" + window.location.hostname + ":8080/load-passes-admin";
          await axios.post(url, passListReq)
            .then((result) => {
              setPassList(result.data.passList);
            });
        } catch (e) {
          if (e.message.includes("401")) {
            // Not logged in or not an admin
          }
        }
        return { passList: [] };
    }

    const revokePass = async (passID) => {
        const tempReq = {
          sessionKey: Cookies.get("auth"),
          passID: passID
        };
        try {
          let url = (window.location.protocol == "https:") ? 
                                            "https://" + window.location.hostname + ":8443/GateGuard-0.0.1-SNAPSHOT/revoke-pass" :
                                            "http://" + window.location.hostname + ":8080/revoke-pass";
          await axios.post(url, tempReq)
            .then((result) => {
              if (result.status == 200) {
                let newList = [];
                for (let i = 0; i < passList.length; i++) {
                  if (passList[i].passID == passID) {
                    continue;
                  }
                  newList[i] = passList[i];
                }
                setPassList(newList);
              }
            });
        } catch (e) {
          console.log(e);
        }
        return {};
      }

    return (
        <>
            <table>
                <tbody>
                    <tr>
                        <th>View passes</th>
                        <th>First Name</th>
                        <th>Last Name</th>
                        <th>Email</th>
                    </tr>
                    {data.map((item) => (
                        <tr key={item.id}>
                            <td>
                                <Button variant="light" onClick={() => {
                                    loadPasses({sessionKey: Cookies.get('auth'), userID: item.id});
                                    setShow(true);
                                }}>Passes</Button></td>
                            <td>{item.first_name}</td>
                            <td>{item.last_name}</td>
                            <td>{item.email}</td>
                            <td>
                            {/* <div className="button">
                                <Button>Remove</Button>
                            </div> */}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
            <Modal className="adminEditModal" show={show} onHide={() => {setShow(false)}}>
                <Modal.Header closeButton>
                    <Modal.Title>User's passes</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {renderPassList(passList)}
                </Modal.Body>
                <Modal.Footer>
                <Button variant="secondary" onClick={() => {setShow(false)}}>
                    Close
                </Button>
                <Button variant="primary" onClick={() => {setShow(false)}}>
                    Save Changes
                </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
};

export default Table;