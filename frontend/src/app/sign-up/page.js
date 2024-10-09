import styles from '../styles/Home.module.css';
import Link from 'next/link';

const SignUp = () => {
    return(
        <main>
        <div className={styles.container}>
            <aside className="bg-white w-full max-w-md rounded-xl  bg-opacity-20 shadow-lg shadow-black">  
                <h1 className="text-center text-black font-light text-4xl bg-navy rounded-t-xl m-0 py-4">Sign Up</h1>
                    <form className="p-6">
                        <input type="text" 
                        name="" 
                        placeholder="Full Name" 
                        className="py-2 px-3 w-full text-black text-lg font-light outlined-none"
                        />
                        <input type="text" 
                        name="" 
                        placeholder="Email" 
                        className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
                        />
                        <input type="text" 
                        name="" 
                        placeholder="Password"
                        className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
                        />
                        <input type="text" 
                        name="" 
                        placeholder="Confirm Password" 
                        className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
                        />
                    <div className="flex mt-5 justify-between items-center">
                        <Link href="/login" className="text-white cursor-pointer transition hover:text-black">Already Registered?</Link>
                    </div>
                </form>
            </aside>     
        </div>
    </main>

    );
}

export default SignUp