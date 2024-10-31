"use client";

import { useState } from 'react';
import styles from '../styles/Home.module.css';
import Link from 'next/link';
import * as Fetch from '../../components/Functions';

const LoginForm = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        // Clear any previous error
        setError('');

        if (await Fetch.login(username, password)){
            window.location.href = '/social-media-app';
        } else {
            setError('Invalid username or password');
        }
        
    };

    return (
        <div>
            <div className={styles.container}>
                <aside className="bg-white w-full max-w-md rounded-xl bg-opacity-20 shadow-lg shadow-black">
                    <h1 className="text-center text-black font-light text-4xl bg-navy rounded-t-xl m-0 py-4">Sign In</h1>
                    <form className="p-6" onSubmit={handleSubmit}>
                        <input
                            type="text"
                            placeholder="Username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            className="py-2 px-3 w-full text-black text-lg font-light outlined-none"
                        />
                        <input
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
                        />
                        {error && <p className="text-red-500 mt-3">{error}</p>}
                        <div className="flex mt-5 justify-between items-center">
                            <Link href="/sign-up" className="text-white cursor-pointer transition hover:text-black">Not Yet Registered?</Link>
                            <button type="submit" className="bg-black text-white font-medium py-2 px-8 transition hover:text-white">Sign In</button>
                        </div>
                    </form>
                </aside>
            </div>
        </div>
    );
};

export default LoginForm;
