import Image from "next/image";
import styles from '../styles/app.layout.css';
import Navbar from "../../components/Navbar";
import Link from 'next/link';

export default function Projects() {
	return (
		<><header className={"header"}><Navbar/></header>
		<main className={"main-container"}>
			<div>
          		<Link href="/social-media-app/friends-list" className="text-blue-500 underline">
            		Friends List
          		</Link>
			</div>
        	<Image
				src="/vision_text.png"
				width={200}
				height={200} />
			<Image
				src="/logo.png"
				width={200}
				height={200} />
			</main></>
	);
}
