import {Poppins} from "next/font/google";
import "../styles/Home.module.css";

const poppins = Poppins({ weight: ["300", "400", "400"], subsets: ["latin"] });

export const metadata = {
    title: "Contacts",
    description: "",
};

export default function RootLayout({ children }) {
    return (
    <html lang="en">
        <body className={poppins.className}>{children}</body>
    </html>
    );
}
