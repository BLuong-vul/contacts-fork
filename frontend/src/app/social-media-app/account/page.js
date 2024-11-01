'use client';
import React, { useEffect, useState } from 'react';
// import homepagestyles from './social-media-homepage.module.css'; 
// import styles from '../styles/app.layout.css';

// import Navbar from "../../components/Navbar";
import * as Fetch from '../../../components/Functions';



export default function Account() {
	const handleLogout = () => {
        Fetch.logout();
        window.location.href = '/social-media-app';
    };
	
	return (
		<div>
            <button onClick={handleLogout}>Log Out</button>
        </div>
	);
}
