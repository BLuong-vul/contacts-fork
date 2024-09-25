'use client';
import { useState } from 'react';
import homepagestyles from './social-media-homepage.module.css'; // adjust the path as necessary
import styles from '../styles/app.layout.css';
import Image from "next/image";
import Navbar from "../../components/Navbar";
import Link from 'next/link';

export default function Projects() {
	const [sidebarWidth, setSidebarWidth] = useState(250); //initial width of the sidebar
	const minWidth = 5; //minimum width for sidebar
	const maxWidth = 600; //maximum width
	
	const handleMouseDown = (e) => {
		const startX = e.clientX; //get starting X position

		const onMouseMove = (moveEvent) => {
			
			const newWidth = window.innerWidth - moveEvent.clientX;
			const dragSpeedMultiplier = 2.0;
			if (newWidth > minWidth && newWidth < maxWidth) {
				setSidebarWidth(newWidth * dragSpeedMultiplier);
			}
		};
		
		const onMouseUp = () => {
			window.removeEventListener('mousemove', onMouseMove);
			window.removeEventListener('mouseup', onMouseUp);
		};
		window.addEventListener('mousemove', onMouseMove);
		window.addEventListener('mouseup', onMouseUp);
	};
	
	return (
		<>
		<header className={styles.header}>
		<Navbar />
		</header>
		<main className={styles.mainContainer}>
			{/* sidebar section start */}
			<div className={homepagestyles.sidebar} style={{ width: sidebarWidth}}>
				<div className={homepagestyles.dragHandle} onMouseDown={handleMouseDown}>
					<div className={homepagestyles.indicator}>&lt;&gt;</div>
				</div>
					
			{ /* sidebar content can go here */}
			<p>Sidebar Content</p>
			</div>
			{/* sidebar section ends */}
			
			<div className={homepagestyles.contentContainer} style={{ marginRight: setSidebarWidth}}>
          		<nav className={homepagestyles.navContainer}>
          			<Link href="/social-media-app" className={homepagestyles.linkBox}>
            			Home
          			</Link>
                	<Link href="/social-media-app/friends-list" className={homepagestyles.linkBox}>
            			Friends List
          			</Link>
          		</nav>
			</div>
			<div>
        	<Image
				src="/vision_text.png"
				width={200}
				height={200} />
			<Image
				src="/logo.png"
				width={200}
				height={200} />
			</div>
			</main></>
	);
}
