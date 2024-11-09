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
    let interval;

    const createShootingStar = () => {
        const star = document.createElement('div');
        star.className = 'shooting-star';
        document.body.appendChild(star);

        // Randomize the starting position from the left half of the screen
        const startX = -50;
        const startY = Math.random() * (window.innerHeight / 2);

        // Calculate midpoints for the curve
        const midX = window.innerWidth * (0.4 + Math.random() * 0.2);
        const midY = window.innerHeight * 0.5;

        // Randomize the end position toward the bottom right
        const endX = window.innerWidth * Math.random();
        const endY = window.innerHeight + Math.random() * 100;

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

    const startInterval = () => {
        interval = setInterval(() => {
            for (let i = 0; i < 3; i++) { // Controls the number of stars created per cycle
                setTimeout(createShootingStar, Math.random() * 1000); // Random delay for each star
            }
        }, 2000); // Controls the frequency of star bursts
    };

    const stopInterval = () => {
        clearInterval(interval);
    };

    document.addEventListener('visibilitychange', () => {
        if (document.hidden) {
            stopInterval();
        } else {
            startInterval();
        }
    });

    startInterval(); // Start the animation loop initially

    return () => {
        stopInterval();
        document.removeEventListener('visibilitychange', stopInterval);
    };
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
