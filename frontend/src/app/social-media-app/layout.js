'use client';
import React, { useEffect } from 'react';
import anime from 'animejs/lib/anime.es.js';
import Image from "next/image";
import Navbar from "../../components/Navbar";
import homestyles from './social-media-homepage.module.css';
import styles from '../styles/app.layout.css';
import style from '../styles/globals.css';
import Link from 'next/link';

export default function RootLayout({ children }) {
  useEffect(() => {
  		// Function to create a shooting star
  		const createShootingStar = () => {
    	const star = document.createElement('div');
    	star.className = 'shooting-star';
    	document.body.appendChild(star);

    	// Starting position (upper left)
    	const startX = -50;
    	const startY = Math.random() * (window.innerHeight / 2); // Randomly between 0 and half the height

    	// Midpoint for a smooth curve, calculated based on the window dimensions
    	const midX = window.innerWidth * (0.4 + Math.random() * 0.2); // Midpoint with slight variation
    	const midY = window.innerHeight * 0.5; // Smooth curve downward

    	// End position towards the bottom right
    	const endX = window.innerWidth * Math.random();
    	const endY = window.innerHeight + Math.random() * 100; // Random variation for natural feel

   		anime({
      		targets: star,
      		keyframes: [
        	{ translateX: startX, translateY: startY, opacity: 1, duration: 0 },
        	{ translateX: endX, translateY: endY, opacity: 0, duration: 2000, easing: 'cubicBezier(0.25, 0.25, 0.75, 1)' }
      	],
      	complete: () => {
        	star.remove();
      		}
    	});
  	};

  	// Interval to create multiple stars at different times
  	const interval = setInterval(() => {
    	for (let i = 0; i < 3; i++) { // Number of stars to appear at the same time
    	  setTimeout(createShootingStar, Math.random() * 1000); // Random delay for natural effect
    	}
  	}, 2000); // Adjust interval for more or fewer stars

  	return () => clearInterval(interval); // Cleanup interval on unmount
	}, []);
  
  return (
    <>
      <Navbar/>
      <div>
        <div className="ps-10 flex-grow">{children}</div>
      </div>
    </>
  );
}
