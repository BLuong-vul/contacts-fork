import Image from "next/image";
import styles from './styles/Home.module.css';
import Link from 'next/link';

export default function Home() {
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
      <main className={styles.mainContent}>
      	<header className="text-center">
        	<h1 className="font-bold">Steven Diep | Bryan Luong | Kyung Ho Min | Phi Nguyen | Garrett Rogers</h1>
      	</header>
      	<br></br>
        <p>
          &emsp;Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec odio.
          Praesent libero. Sed cursus ante dapibus diam. Sed nisi. Nulla quis sem at
          nibh elementum imperdiet. Duis sagittis ipsum. Praesent mauris. Fusce nec
          tellus sed augue semper porta. Mauris massa. Vestibulum lacinia arcu eget nulla.
        </p>
        <p>
          &emsp;Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos
          himenaeos. Curabitur sodales ligula in libero. Sed dignissim lacinia nunc. Curabitur
          tortor. Pellentesque nibh. Aenean quam. In scelerisque sem at dolor. Maecenas mattis.
          Sed convallis tristique sem. Proin ut ligula vel nunc egestas porttitor. Morbi lectus
          risus, iaculis vel, suscipit quis, luctus non, massa.
        </p>
        <p>
          &emsp;Fusce ac turpis quis ligula lacinia aliquet. Mauris ipsum. Nulla metus metus,
          ullamcorper vel, tincidunt sed, euismod in, nibh. Quisque volutpat condimentum velit.
          Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos
          himenaeos. Nam nec ante. Sed lacinia, urna non tincidunt mattis, tortor neque
          adipiscing diam, a cursus ipsum ante quis turpis. Nulla facilisi.
        </p>
        <br></br>
        <header className="text-center">
	        <Link href="/projects" className="text-blue-500 underline">
	        	Project List
	        </Link>
        </header>
      </main>
    </div>
  );
}
