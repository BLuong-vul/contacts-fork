import styles from '../styles/Home.module.css';
import Link from 'next/link';

const LoginForm = () => {
    return(
        <main>
            <div className={styles.container}>
                <aside className="bg-white w-full max-w-md rounded-xl  bg-opacity-20 shadow-lg shadow-black">  
                    <h1 className="text-center text-black font-light text-4xl bg-navy rounded-t-xl m-0 py-4">Sign In</h1>
                        <form className="p-6">
                            <input type="text" 
                            name="" 
                            placeholder="Email"
                            className="py-2 px-3 w-full text-black text-lg font-light outlined-none"/>
                            <input type="text" 
                            name="" 
                            placeholder="Password"
                            className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"/>
                        <div className="flex mt-5 justify-between items-center">
                            <Link href="/sign-up" className="text-white cursor-pointer transition hover:text-black">Not Yet Registered?</Link>
                            <Link href="/social-media-app" className="bg-black text-white font-medium py-2 px-8 transition hover:text-white">Sign In</Link>
                        </div>
                    </form>
                </aside>     
            </div>
        </main>
    )
}

export default LoginForm