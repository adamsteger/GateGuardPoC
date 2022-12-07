import React from 'react';
import '../styles/Homepage.scss';
import SampleComponent from "../components/SampleComponent";
import GuardNavbar from "../components/GuardNavbar";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBars } from '@fortawesome/free-solid-svg-icons';
import {Link} from "react-router-dom"

const Homepage: React.FC = (): JSX.Element => {

  return (
    <>
      <GuardNavbar/>
      <div className="contentContainer">
        <h4>Hello User</h4>
        <ol>
          <li>
            <p>Please start by viewing this webpage on a phone, or a desktop with the dimensions of a phone (see <a className="link" href='https://themeisle.com/blog/view-mobile-version-of-website/'>here</a>)</p>
          </li>
          <li>
            <p>Then login by clicking "<FontAwesomeIcon className="navBar-icon" icon={faBars}/>" in the upper right corner, then click "Log in"</p>
            {/* <p>Alternatively, click <Link to="/login" className="link">here</Link></p> */}
          </li>
          <li>
            <p>Here's a list of our POC issues and their corresponding links:</p>
            <ul>
              
              <li>
                <a className="link" href='https://github.com/SCCapstone/LifesABit/issues/13'>
                  #13: Members can login
                </a>
              </li>
              <li>
                <a className="link" href='https://github.com/SCCapstone/LifesABit/issues/14'>
                  #14: Members can create create an account
                </a>
              </li>
              <li>
                <a className="link" href='https://github.com/SCCapstone/LifesABit/issues/15'>
                  #15: Members can create a new pass
                </a>
              </li>
              <li>
                <a className="link" href='https://github.com/SCCapstone/LifesABit/issues/16'>
                  #16: Members can view their current passes
                </a>
              </li>
              <li>
                <a className="link" href='https://github.com/SCCapstone/LifesABit/issues/17'>
                  #17: Members can edit their current passes
                </a>
              </li>
              <li>
                <a className="link" href='https://github.com/SCCapstone/LifesABit/issues/18'>
                  #18: Admins can revoke passes
                </a>
              </li>
              <li>
                <a className="link" href='https://github.com/SCCapstone/LifesABit/issues/19'>
                  #19: System can verify if a pass is valid
                </a>
              </li>
              
              <li>
                <a className="link" href='https://github.com/SCCapstone/LifesABit/issues/20'>
                  #20: System will have 2 user types: Admin and Member
                </a>
              </li>
              <li>
                <a className="link" href='https://github.com/SCCapstone/LifesABit/issues/24'>
                  #24: Deploy to Internet
                </a>
              </li>
              
            </ul>
          </li>
            
        </ol>
        
      </div>
      <h3>
        Welcome to the homepage. Please log in to continue, or visit the MyPasses page.
      </h3>
    </>
  );
}

export default Homepage;
