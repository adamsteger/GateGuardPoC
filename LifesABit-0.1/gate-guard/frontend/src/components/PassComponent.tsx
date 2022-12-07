import React, { useEffect, useState } from 'react';
import '../styles/PassComponentStyles.scss';
import {Button, Row, Col, Modal, Form} from "react-bootstrap";
import axios from 'axios';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { faTrashCan } from '@fortawesome/free-solid-svg-icons';
import Cookies from "js-cookie";
import { useForm } from 'react-hook-form';

export interface PassProps extends Pass {
    revokePassFunc: (passID: string) => any;
}

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

interface CreatePassReq extends Pass {
  sessionKey?: string;
}

const PassComponent = (props: PassProps) => {
  const { register, handleSubmit, watch, formState: { errors } } = useForm();
  const [usesPass, setUsesPass] = useState<boolean>(props.usageBased!);
  const [newPassModalIsOpen, setNewPassModalIsOpen] = React.useState(false);
  const [revokePassModalIsOpen, setRevokePassModalIsOpen] = React.useState(false);


  const editPass =  async (passData: CreatePassReq) => {
    passData.sessionKey = Cookies.get("auth");
    passData.passID = props.passID;
    if (typeof passData.expirationDate == "string") {
      passData.expirationDate = new Date(passData.expirationDate).getTime();
    }
    if (passData.usageBased == undefined) {
      passData.usageBased = (passData.expirationDate != undefined);
    }
    if (passData.usageBased) {
      passData.expirationDate = -1;
    } else {
      passData.usesLeft = -1;
      passData.usesTotal = -1;
    }
    try {
      let url = (window.location.protocol == "https:") ? 
                                            "https://" + window.location.hostname + ":8443/GateGuard-0.0.1-SNAPSHOT/edit-pass" :
                                            "http://" + window.location.hostname + ":8080/edit-pass";
      await axios.post(url, passData)
      .then((result) => {
        if (result.status == 200) {
          props.passID = passData.passID;
          props.firstName = passData.firstName;
          props.lastName = passData.lastName;
          props.usageBased = passData.usageBased;
          props.expirationDate = passData.expirationDate;
          props.usesTotal = passData.usesTotal;
          props.usesLeft = passData.usesLeft;
          props.email = passData.email;
        }
      });
    } catch (e: any) {
      console.log(e);
    }
    setNewPassModalIsOpen(false);
    return {};
    }


  const saveChanges = () => {
    
  }

  useEffect(() => {
    //const temp = Cookies.get('auth')
    
  }, []);
  
  return (
    <>
      <hr className="dottedhr"/>
      <div className="passComponentDiv">
            <h2>{props.firstName + " " + props.lastName}</h2>
            <div className="expLine">
                <FontAwesomeIcon icon={faPencilAlt} onClick={() => setNewPassModalIsOpen(true)}/>
                <FontAwesomeIcon icon={faTrashCan} onClick={() => setRevokePassModalIsOpen(true)}/> 
                {props.usageBased ? 
                     <h5>Uses Left: <span className="expDateOrUses">{props.usesLeft} / {props.usesTotal}</span> </h5>: 
                     <h5>Exp: <span className="expDateOrUses">{new Date(props.expirationDate!).toLocaleDateString("en-US")}</span></h5>}
            </div>
      </div>
      <Modal
        show={newPassModalIsOpen}
        onHide={() => setNewPassModalIsOpen(false)}
        className="newPassModal"
      >
        <Form id="newPassForm" onSubmit={handleSubmit(editPass)}>
        <Modal.Header closeButton>
          <Modal.Title>Edit pass</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form.Label>Name</Form.Label>
          <Form.Control className="newPassInput" type="text" defaultValue={props.firstName} {...register("firstName")}></Form.Control>
          <Form.Control className="newPassInput" type="text" defaultValue={props.lastName} {...register("lastName")}></Form.Control>

          <Form.Label>Email</Form.Label>
          <Form.Control className="newPassInput" type="text" defaultValue={props.email} {...register("email")}></Form.Control>
          
          <Form.Check label="Usage-based" type="switch" checked={usesPass} {...register("usageBased")} onChange={() => setUsesPass(!usesPass)}></Form.Check>
          {usesPass ? 
            <>
              <Form.Label>Total uses</Form.Label>
              <Form.Control className="newPassInput" type="text" defaultValue={String(props.usesTotal)} {...register("usesTotal")}></Form.Control>
            </> :
            <>
              <Form.Label>Expiration</Form.Label>
              <Form.Control className="newPassInput" type="text" defaultValue={new Date(props.expirationDate!).toLocaleDateString("en-US")} {...register("expirationDate")}></Form.Control>
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
            Update
          </Button>
        </Modal.Footer>
        </Form>
      </Modal>



      <Modal
        show={revokePassModalIsOpen}
        onHide={() => setRevokePassModalIsOpen(false)}
        className="newPassModal"
      >
        
        <Modal.Header closeButton>
          <Modal.Title>New pass</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Are you sure you would like to revoke {props.firstName} {props.lastName}'s pass
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setRevokePassModalIsOpen(false)}>
            Cancel
          </Button>
          <Button variant="primary" onClick={() => {props.revokePassFunc(props.passID!)}}>
            Revoke
          </Button>
        </Modal.Footer>
        
      </Modal>


      {/* {showModal && <Modal.Dialog>
        {/* <Modal.Header closeButton>
            <Modal.Title>Modal title</Modal.Title>
        </Modal.Header> */}

        {/* <Modal.Body className="passEditModal">
            <Form className="passEditForm">
                <Form.Label>Name</Form.Label>
                <Form.Control type="text"></Form.Control>

                <Form.Label>Phone Number</Form.Label>
                <Form.Control type="text"></Form.Control>

                <Form.Check label="Usage-based" type="switch" checked={usesPass} onChange={() => setUsesPass(!usesPass)}></Form.Check>

                {
                  usesPass ? 
                  <>
                    <Form.Label>Total uses</Form.Label>
                    <Form.Control type="text"></Form.Control>
                  </> :
                  <>
                    <Form.Label>Expiration date</Form.Label>
                    <Form.Control type="text"></Form.Control>
                  </> 
                }
            </Form>
        </Modal.Body>

        <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowModal(false)}>Cancel</Button>
            <Button variant="light" onClick={saveChanges}>Save changes</Button>
        </Modal.Footer>
      </Modal.Dialog>} */}
      
    </>
  );
}

export default PassComponent;