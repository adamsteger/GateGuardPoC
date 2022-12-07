import React from 'react';
import GuardNavbar from "../components/GuardNavbar";
import { useForm } from "react-hook-form";
import "../styles/MyPasses.scss";

interface NewNotification {
  message?: string;
  time?: String;
  expDate?: string;
}


const Notification = (id: any, message: String, time: String, expDate: String) => { 
  return { 
    id: id, 
    message: message,
    time: time,
    expDate: expDate
     }};



//for loop that grabs existing passes from database
//where we go when the user wants to add a notification
//Don't necessarily need this bit, just wanted to add it in to see how everything works
const testNotification = Notification(1,"Erin used their pass","1 min ago", "Expires in 22 hours");

const initialNotificationList = [
  {
    id: testNotification.id,
    message: testNotification.message,
    time: testNotification.time,
    expDate: testNotification.expDate
  },
  {
    id: 2,
    message: 'UPS used their pass',
    time: '30 min ago',
    expDate: 'Expires in 3 days' //ideally want this to appear in red
  },
  {
    id: 3,
    message: 'Henry used their pass',
    time: '50 min ago',
    expDate: 'Expires in 2 days'
  }
];


const Notifications: React.FC = () => {
  const { register, handleSubmit, watch, formState: { errors } } = useForm();
  const [notificationList, setNotificationList] = React.useState(initialNotificationList);
  const [newNotification, setNewNotification] = React.useState<typeof Notification>();
  const [newPassModalIsOpen, setNewPassModalIsOpen] = React.useState(false);


  
  const CreateNewNotification =  async (notificationData: NewNotification) => {
    if(notificationData.expDate && notificationData.time && notificationData.message){
      //TODO Fix ID
      const tempCard = Notification(7, notificationData.message, notificationData.time, notificationData.expDate);
      initialNotificationList.push(tempCard);
      setNotificationList(initialNotificationList);
      setNewPassModalIsOpen(false);
    }
    
    
    
  }

  

  return (
    <>
      <GuardNavbar/>
      <div>
        <h2 className="pageHeader">Notifications</h2>
        <hr></hr>

      </div>
      <div>

      </div>
      <div className="notificationList-container">
        <ul className="notificationList">
          {notificationList.map(notification=>(
            <li className="notificationList-item" key={notification.id}>
              <h3>{notification.message}</h3>
              <p>{notification.expDate}</p>
              <hr></hr>
            </li>
          ))}
        </ul>
      </div>
      
   
    </>
  );
}

export default Notifications;