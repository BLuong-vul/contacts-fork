import homepagestyles from './social-media-homepage.module.css'; // adjust the path as necessary
import styles from '../styles/app.layout.css';
import Image from "next/image";
import Navbar from "../../components/Navbar";
import Link from 'next/link';

export default function Projects() {
	return (
		<><header className={styles.header}><Navbar /></header>
		<main className={styles.mainContainer}>
			<div className={homepagestyles.homeContainer}>
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
