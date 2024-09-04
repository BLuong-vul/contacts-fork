import Image from "next/image";
import styles from '../styles/Home.module.css';
import Link from 'next/link';

export default function Projects() {
  return (
    <div className={styles.container}>
      <header className={styles.banner}>
      	<Image
      		src="/logo.png"
      		width={200}
      		height={200}
      	/>
      	<Image
      		src="/vision_text.png"
      		width={200}
      		height={200}
      	/>
      </header>
    </div>
  );
}
