import React, { useEffect, useState } from 'react';
import GuardNavbar from "../components/GuardNavbar";
import "../styles/MyPasses.scss";
import "../styles/NewPassModal.scss";
import { Button, Modal, Form } from "react-bootstrap";
import { useForm } from "react-hook-form";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faAdd } from '@fortawesome/free-solid-svg-icons';
import PassComponent, { PassProps } from "../components/PassComponent";
import axios from 'axios';
import Cookies from "js-cookie";
// import Switch from '@mui/material/Switch';
//import Switch from "react-switch";

interface Pass {
  passID?: string;
  firstName?: string;
  lastName?: string;
  usageBased?: boolean;//better name?
  expirationDate?: number;
  usesTotal?: number;
  usesLeft?: number;
  email?: string;
}

interface CreatePassReq {
  sessionKey?: string;
  usageBased?: boolean;
  firstName?: string;
  lastName?: string;
  email?: string;
  expirationDate?: string | number;
  usesLeft?: number;
  usesTotal?: number;
}

interface CreatePassResp {
  success?: boolean;
  message?: string;
  passID?: string;
}

interface PassListResp {
  passList: Pass[];
}

interface PassListReq {
  sessionKey: string;
}

interface RevokePassReq {
  passID?: string;
  sessionKey?: string;
}

//await axios.post("http://localhost:8080/edit-pass", passData)
//await axios.post("http://localhost:8080/revoke-pass", passData)

const MyPasses: React.FC = (): JSX.Element => {
  const { register, handleSubmit, watch, formState: { errors } } = useForm();
  const [passList, setPassList] = useState<Pass[]>([]);
  const [newPassModalIsOpen, setNewPassModalIsOpen] = useState(false);
  const [usesPass, setUsesPass] = useState<boolean>(false);
  const [notLoggedIn, setNotLoggedIn] = useState<boolean>(false);

  // const editPass =  async (passData: CreatePassReq) => {
  //   passData.sessionKey = Cookies.get("auth");
  //   try {
  //     await axios.post("http://localhost:8080/edit-pass", passData)
  //     .then((result) => {
  //       if (result.status == 200) {
  //         // setPassList(existingItems => [...existingItems, passData]);
  //         var sessionKey: PassListReq = {sessionKey : Cookies.get('auth')!};
  //         loadPasses(sessionKey); 

  //       }
  //     });
  //   } catch (e: any) {
  //     console.log(e);
  //   }
  //   return {};
  //     setNewPassModalIsOpen(false);
  //     //TODO Clear Form 
  //   }

  const OnSubmitNewPass = async (passData: CreatePassReq) => {
    passData.sessionKey = Cookies.get("auth");
    if (passData.usesTotal) {
      passData.usesLeft = passData.usesTotal;
    } else {
      passData.usesTotal = -1;
      passData.usesLeft = -1;
    }
    passData.expirationDate = Date.parse(passData.expirationDate as string);
    try {
      let url = (window.location.protocol == "https:") ? 
                                            "https://" + window.location.hostname + ":8443/GateGuard-0.0.1-SNAPSHOT/create-pass" :
                                            "http://" + window.location.hostname + ":8080/create-pass";
      await axios.post(url, passData)
        .then((result) => {
          if (result.status == 200) {
            let newPass: Pass = {
              passID: result.data.passID,
              firstName: passData.firstName!,
              lastName: passData.lastName!,
              usageBased: passData.usageBased!,
              expirationDate: (passData.expirationDate as number),
              usesTotal: passData.usesTotal!,
              usesLeft: passData.usesLeft!,
              email: passData.email!
            };
            setPassList(passList => [...passList, newPass]);
            setNewPassModalIsOpen(false);
          }
        });
    } catch (e: any) {
      console.log(e);
    }
    return {};
    setNewPassModalIsOpen(false);
    //TODO Clear Form 
  }

  const loadPasses = async (passListReq: PassListReq): Promise<PassListResp> => {
    try {
      let url = (window.location.protocol == "https:") ? 
                                            "https://" + window.location.hostname + ":8443/GateGuard-0.0.1-SNAPSHOT/load-passes" :
                                            "http://" + window.location.hostname + ":8080/load-passes";
      await axios.post(url, passListReq)
        .then((result) => {
          setPassList(result.data.passList);
        });
    } catch (e: any) {
      if (e.message.includes("401")) {
        setNotLoggedIn(true);
      }
    }
    return { passList: [] };
  }


  const revokePass = async (passID: string) => {
    console.log("Thingy!");
    const tempReq: RevokePassReq = {
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
            for (let i: number = 0; i < passList.length; i++) {
              if (passList[i].passID == passID) {
                continue;
              }
              newList[i] = passList[i];
            }
            setPassList(newList);
          }
        });
    } catch (e: any) {
      console.log(e);
    }
    return {};
  }

  const renderPassList = (passList: Pass[]): JSX.Element => {
    var list: JSX.Element[] = [];
    passList.forEach((item) => {
      list.push(<PassComponent
        firstName={item.firstName!}
        lastName={item.lastName!}
        email={item.email!}
        expirationDate={item.expirationDate!}
        usesLeft={item.usesLeft}
        usesTotal={item.usesTotal}
        usageBased={item.usageBased}
        passID={item.passID}
        revokePassFunc={revokePass} />);
    });
    return <>{list}</>
  }


  // function createTestList(): void {
  //   const tempPass1: Pass = {
  //       passID: "12345",
  //       firstName: "Vaughn", 
  //       lastName: "Eugenio", 
  //       expirationDate: "11/27/2022",
  //       email: "example@email.com",
  //       usageBased: false//better name?
  //   }
  //   passList.push(
  //     tempPass1
  //   );

  // }


  useEffect(() => {
    //const temp = Cookies.get('auth')
    console.log("in MyPasses.tsx-> cookies.get(auth):" + Cookies.get("auth"));
    var sessionKey: PassListReq = { sessionKey: Cookies.get('auth')! };
    loadPasses(sessionKey);
  }, []);



  return (
    <>
      <div className="mypasses">
        <GuardNavbar />
        <header className="mypasses-header">
          <h2>MyPasses</h2>
        </header>
        <body className="mypasses-body">
          {/* <h2>Hello</h2>
          <PassComponent 
            firstName={"Vaughn"} 
            lastName={"Eugenio"} 
            expirationDate={"7/5/22"} 
            usesLeft={5} 
            usesTotal={5} /> */}

          {/* {renderPassList(passList)} */}
          <div className="passList-container">

            {notLoggedIn && <h3 className="notLoggedInMessage">You are not currently logged in, or your session has expired. <br/> Please log in to continue.</h3>}
            {renderPassList(passList)}


          </div>

          {/* <PassComponent 
            firstName={"Vaughn"} 
            lastName={"Eugenio"} 
            expirationDate={"7/5/22"} 
            usesLeft={5} 
            usesTotal={5} />*/}
          <button className="createPass-btn" onClick={() => setNewPassModalIsOpen(true)}>
            {/* <FontAwesomeIcon icon={fa-solid fa-plus} />   WHY NO WORK? */}
            <FontAwesomeIcon className="createPass-icon" icon={faAdd} onClick={() => setNewPassModalIsOpen(true)} />
          </button>


        </body>
        <Modal
          show={newPassModalIsOpen}
          onHide={() => setNewPassModalIsOpen(false)}
          className="newPassModal"
        >
          <Form id="newPassForm" onSubmit={handleSubmit(OnSubmitNewPass)}>
            <Modal.Header closeButton>
              <Modal.Title>New pass</Modal.Title>
            </Modal.Header>
            <Modal.Body>
              <Form.Label>Name</Form.Label>
              <Form.Control className="newPassInput" type="text" placeholder="First name" {...register("firstName")}></Form.Control>
              <Form.Control className="newPassInput" type="text" placeholder="Last name" {...register("lastName")}></Form.Control>

              <Form.Label>Email</Form.Label>
              <Form.Control className="newPassInput" type="text" placeholder="example@example.com" {...register("email")}></Form.Control>

              <Form.Check label="Usage-based" type="switch" {...register("usageBased")} checked={usesPass} onChange={() => setUsesPass(!usesPass)}></Form.Check>
              {/* TODO add switch for date or uses */}
              {usesPass ?
                <>
                  <Form.Label>Total uses</Form.Label>
                  <Form.Control className="newPassInput" type="text" placeholder="e.g. 5" {...register("usesTotal")}></Form.Control>
                </> :
                <>
                  <Form.Label>Expiration</Form.Label>
                  <Form.Control className="newPassInput" type="text" placeholder="MM/DD/YYYY" {...register("expirationDate")}></Form.Control>
                </>
              }
              {/* <Form.Select aria-label="Expr. type">
              <option>Select Type</option>
              <option value="1">Date</option>
              <option value="2">Uses</option>
            </Form.Select>
            */}

              {/* TODO implement FORM */}
            </Modal.Body>
            <Modal.Footer>
              <Button variant="secondary" onClick={() => setNewPassModalIsOpen(false)}>
                Cancel
              </Button>
              <Button type="submit" variant="primary" >
                Create
              </Button>
            </Modal.Footer>
          </Form>
        </Modal>
      </div>
    </>
  );
}

export default MyPasses;