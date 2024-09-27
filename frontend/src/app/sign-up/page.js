"use client";

import styles from '../styles/Home.module.css';
import Link from 'next/link';
import { useState } from 'react';

export default function SignUp() {
	const [userData, setUserData] = useState({
		username: '',
		password: '',
		confirmPassword: '',
		email: '',
		fullName: '',
		phoneNumber: '',
		address: '',
		city: '',
		state: '',
		zipCode: '',
	});

	const handleInputChange = (e) => {
	const { name, value } = e.target;
		setUserData((prevState) => ({
			...prevState,
			[name]: value,
		}));
	};

	const createUser = async () => {
	    const { confirmPassword, ...userPayload } = userData;

	    // Check if password and confirm password match
	    if (userData.password !== confirmPassword) {
	      alert('Passwords do not match');
	      return;
	    }

	    try {							//CHANGE URL BEFORE DEPLOYING
	      const response = await fetch('http://localhost:8080/auth/register', {
	        method: 'POST',
	        headers: {
	          'Content-Type': 'application/json',
	        },
	        body: JSON.stringify(userPayload),
	      });

	      if (!response.ok) {
	        throw new Error(`Error: ${response.status}`);
	      }

	      const result = await response.json();
	      console.log('User created successfully:', result);
	      alert('Account creation successful!')

	      window.location.href = '/login';
	    } catch (error) {
	      console.error('Error:', error);
	      alert('Account creation error')
	    }
	};

    return(
        <main>
          <div className={styles.container}>
            <aside className="bg-white w-full max-w-md rounded-xl  bg-opacity-20 shadow-lg shadow-black">
              <h1 className="text-center text-black font-light text-4xl bg-navy rounded-t-xl m-0 py-4">Sign Up</h1>
              <form className="p-6" onSubmit={(e) => e.preventDefault()}>
                <input
                  type="text"
                  name="username"
                  placeholder="Username"
                  className="py-2 px-3 w-full text-black text-lg font-light outlined-none"
                  value={userData.username}
                  onChange={handleInputChange}
                />
                <input
                  type="password"
                  name="password"
                  placeholder="Password"
                  className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
                  value={userData.password}
                  onChange={handleInputChange}
                />
                <input
                  type="password"
                  name="confirmPassword"
                  placeholder="Confirm Password"
                  className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
                  value={userData.confirmPassword}
                  onChange={handleInputChange}
                />
                <input
                  type="email"
                  name="email"
                  placeholder="Email"
                  className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
                  value={userData.email}
                  onChange={handleInputChange}
                />
                <input
                  type="text"
                  name="fullName"
                  placeholder="Full Name"
                  className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
                  value={userData.fullName}
                  onChange={handleInputChange}
                />
                <input
                  type="text"
                  name="phoneNumber"
                  placeholder="Phone Number"
                  className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
                  value={userData.phoneNumber}
                  onChange={handleInputChange}
                />
                <input
                  type="text"
                  name="address"
                  placeholder="Street Address"
                  className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
                  value={userData.address}
                  onChange={handleInputChange}
                />
                <input
                  type="text"
                  name="city"
                  placeholder="City"
                  className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
                  value={userData.city}
                  onChange={handleInputChange}
                />
                <input
                  type="text"
                  name="state"
                  placeholder="State"
                  className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
                  value={userData.state}
                  onChange={handleInputChange}
                />
                <input
                  type="text"
                  name="zipCode"
                  placeholder="Zip Code"
                  className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
                  value={userData.zipCode}
                  onChange={handleInputChange}
                />

                <div className="flex mt-5 justify-between items-center">
                  <Link href="/login" className="text-white cursor-pointer transition hover:text-black">
                    Already Registered?
                  </Link>
                  {/* Add the button here */}
                  <button
                    type="button"
                    onClick={createUser}
                    className="bg-navy text-white px-4 py-2 rounded hover:bg-opacity-80"
                  >
                    Sign Up
                  </button>
                </div>
              </form>
            </aside>
          </div>
        </main>

    );
}