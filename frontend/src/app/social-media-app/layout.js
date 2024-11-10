'use client';
import React, { useEffect } from 'react';
import Image from "next/image";
import Navbar from "../../components/Navbar";
import homestyles from './social-media-homepage.module.css';
import styles from '../styles/app.layout.css';
import Link from 'next/link';

export default function RootLayout({ children }) {
  useEffect(() => {
    const container = document.createElement('div');
    container.className = 'shooting-stars-container';
    document.body.appendChild(container);

    // Create and append each shooting star
    for (let i = 1; i <= 10; i++) {
      const star = document.createElement('div');
      star.className = 'shooting-star';
      
      // Randomize position within the viewport
      const randomTop = 'vh';
      const randomRight = Math.random() * 100 + 'vw';
      star.style.top = randomTop;
      star.style.right = randomRight;

      // Add random animation delay and duration
      star.style.animationDelay = `${Math.random() * 3}s`;
      star.style.animationDuration = `${1 + Math.random() * 2}s`;
      
      container.appendChild(star);
    }
    // Cleanup on unmount
    return () => {
      container.remove();
    };
  }, []);
  return (
    <>
      <Navbar/>
      <div
      	className="min-h-screen flex flex-col"
        style={{
          backgroundImage: 'url(/background.png)',        // Path to your background image in the public folder
          backgroundSize: 'cover',                         // Ensures the background image covers the entire screen
          backgroundPosition: 'center',                    // Centers the background image
          backgroundAttachment: 'fixed',                   // Keeps the background fixed while scrolling
        }}
      >
      	
        <div className="ps-10 flex-grow">{children}</div>
      </div>
    </>
  );
}
