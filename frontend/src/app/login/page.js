"use client";

import { useState } from 'react';
import styles from '../styles/Home.module.css';
import Link from 'next/link';

const LoginForm = () => {


    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        // Clear any previous error
        setError('');

        const loginData = { username, password };

        try {
            const response = await fetch('https://contacts-5min.onrender.com/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(loginData),
            });

            if (!response.ok) {
                throw new Error('Invalid username or password');
            }

            const result = await response.json();

            // Store JWT in localStorage
            localStorage.setItem('token', result.jwt);
            console.log(result);

            window.location.href = '/social-media-app';
        } catch (error) {
            console.error('Login error:', error);
            setError('Invalid username or password');
        }
    };

    return (
        <main>
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
        </main>
    );
};

export default LoginForm;
