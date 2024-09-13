import Image from "next/image";
import styles from '../styles/app.layout.css';
import Navbar from "../../components/Navbar";

export default function Projects() {
	return (
		<><header className={"header"}><Navbar/></header>
		<main className={"main-container"}>
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
