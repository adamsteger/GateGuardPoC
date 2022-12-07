import React from 'react';
import {useState, useEffect} from 'react';
import GuardNavBar from '../components/GuardNavbar';
import { users } from "./Users";
import "./Search.css";
import Table from "./Table";
import Cookies from "js-cookie";
import axios from "axios";



interface LoadMembersRequest {
    sessionKey: string;
}

interface LoadMembersResponse {
    memberList: Member[];
}

interface Member {
    id?: string;
    first_name?: string;
    last_name?: string;
    email?: string;
}

const FindFirstOrLast: React.FC = () => {

    const [listOfMembers, setListOfMembers] = useState<Member[]>([]);
    const [query, setQuery] = useState("");
    const search = (data : any) => {
        return data.filter((item : any)=>item.first_name.toLowerCase().includes(query) || item.last_name.toLowerCase().includes(query) || item.email.toLowerCase().includes(query));
    }

    const onSubmit = async (data: LoadMembersRequest) => {
        try {
          let url = (window.location.protocol == "https:") ? 
                                            "https://" + window.location.hostname + ":8443/GateGuard-0.0.1-SNAPSHOT/load-members" :
                                            "http://" + window.location.hostname + ":8080/load-members";
          await axios.post(url, data)
          .then((result) => {
            if (result.status == 200) {
                setListOfMembers(result.data.memberList);
            }
          });
        } catch (e: any) {
          if (e.message.includes("401")) {
            //setWrongLogin(true); TODO: Write message not admin.
          } else {
            console.log(e);
          }
        }
      }

    useEffect(() => {
        let sessionKey = Cookies.get("auth");
        onSubmit({sessionKey: sessionKey!});
    },[]);
    
    return (
        <div className="SearchButton">
            <input type="text" 
            placeholder="Search..." 
            className="search" 
            onChange={(e) => setQuery(e.target.value)} 
            />
            <div className="tableData"><Table data={search(listOfMembers)}/></div>
        </div>
    );
};

export default FindFirstOrLast;