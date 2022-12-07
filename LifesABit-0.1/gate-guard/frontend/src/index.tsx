import React from 'react';
import ReactDOM from 'react-dom/client';
import './styles/index.scss';
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Homepage from './pages/Homepage';
import NoPage from './pages/NoPage';
import Updates from './pages/Updates';
import MyPasses from './pages/MyPasses';
import Notifications from './pages/Notifications';
import LogIn from "./pages/LogIn";
import Settings from "./pages/Settings";
import Search from "./pages/Search";
import Table from "./pages/Table";



import CreateAccount from "./pages/CreateAccount";
import UsePass from "./pages/UsePass";
import "./styles/main.scss";
import { ToastContainer } from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

root.render(
  <React.StrictMode>
    <ToastContainer
        position="top-right"
        autoClose={5000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="dark"
        />
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Homepage/>}/>
        <Route path="updates" element={<Updates/>}/>
        <Route path="*" element={<NoPage/>}/>
        <Route path="/login" element={<LogIn/>}/>
        <Route path="/settings" element={<Settings/>}/>
        <Route path="/search" element={<Search/>}/>
        <Route path="/mypasses" element={<MyPasses/>}/>
        <Route path="/notifications" element={<Notifications/>}/>
        <Route path="/create-account" element={<CreateAccount/>}/>
        <Route path="/use-pass" element={<UsePass/>}/>
      </Routes>
    </BrowserRouter>
  </React.StrictMode>
);

