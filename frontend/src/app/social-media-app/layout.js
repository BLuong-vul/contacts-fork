import Image from "next/image";
import Navbar from "../../components/Navbar";
import homestyles from './social-media-homepage.module.css';
import styles from '../styles/app.layout.css';
import Link from 'next/link';



export default function RootLayout({ children }) {
  return (
    <>
      <Navbar/>
      <div>
        <div className="ps-10 flex-grow">{children}</div>
      </div>
    </>
  );
}