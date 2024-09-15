import Image from "next/image";
import styles from "../styles/Home.module.css"
import Link from 'next/link';
import Navbar from "../../components/Navbar";

export default function Projects() {
  return (
    <div className={styles.container}>
      <header className={styles.banner}>
      	<Image
      		src="/logo.png"
      		width={200}
      		height={200}
          alt="Vision logo"
      	/>
      	<Image
      		src="/vision_text.png"
      		width={200}
      		height={200}
          alt="Vision text"
      	/>
      </header>
      <main className={styles.mainContent}>
      	<header className="text-center">
        	<h1 className="font-bold">Projects</h1>
      	</header>

				<div className="flex justify-center min-h-screen">
					<div className="flex p-4 shadow-md">
						<Link href="/social-media-app" className="text-blue-500 underline">
							<div className="w-64 h-64 flex-shrink-0 bg-white border border-gray-300"></div>
						</Link>
						<div className="ml-4 text-white-300">
							<header className="text-center">
								<h1 className="font-bold">Project 1</h1>
							</header>
							<p>
								Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec odio.
								Praesent libero. Sed cursus ante dapibus diam. Sed nisi. Nulla quis sem at
								nibh elementum imperdiet. Duis sagittis ipsum. Praesent mauris. Fusce nec
								tellus sed augue semper porta. Mauris massa. Vestibulum lacinia arcu eget nulla.
							</p>
						</div>
					</div>
				</div>

			</main>
		</div></>
	);
}
