import Image from "next/image";
import Navbar from "../../components/Navbar";
import styles from '../styles/app.layout.css';
import Link from 'next/link';


export default function RootLayout({ children }) {
  return (
    <><div className="flex h-screen flex-col md:flex-row md:overflow-hidden">
      <div className="w-full flex-none md:w-60">
        <aside id="sidebar">
          <header className={"header"}>
            <Image
              src="/app_name1.png"
              width={200}
              height={200}
              alt="" />

          </header>
          <br></br>
          <p>
            &emsp;Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec odio.
            Praesent libero. Sed cursus ante dapibus diam. Sed nisi. Nulla quis sem at
            nibh elementum imperdiet. Duis sagittis ipsum. Praesent mauris. Fusce nec
            tellus sed augue semper porta. Mauris massa. Vestibulum lacinia arcu eget nulla.
          </p>
        </aside>
      </div>
      <div className="flex-grow p-0 md:overflow-y-auto md:p-0">{children}</div>
    </div></>
  );
}