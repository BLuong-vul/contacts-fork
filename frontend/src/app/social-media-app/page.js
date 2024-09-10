import Image from "next/image";
import styles from '../styles/app.layout.css';

export default function Projects() {
  return (
	<><header className={"header"}>
	</header>
	<main className={"main-container"}>
    <Image
		src="/vision_text.png"
		width={200}
		height={200}
		alt="Vision text"
	/>
	<Image
		src="/logo.png"
		width={200}
		height={200} 
		alt="Vision logo"
	/>
	</main></>
  );
}
