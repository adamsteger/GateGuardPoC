import React from 'react';
import GuardNavbar from '../components/GuardNavbar';
import '../styles/Homepage.scss';

const NoPage: React.FC = () => {
  return (
    <>
      <GuardNavbar/>
      Page not found.
    </>
  );
}

export default NoPage;
