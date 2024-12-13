"use client";

import styles from '../styles/Home.module.css';
import Link from 'next/link';
import { useState } from 'react';
import * as Fetch from '../../components/Functions';

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

  const [errors, setErrors] = useState({
    username: '',
    password: '',
    confirmPassword: '',
    email: '',
    phoneNumber: '',
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setUserData((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  const validateForm = () => {
    const newErrors = {};
    let isValid = true;

    // Username validation
    if (!userData.username) {
      newErrors.username = 'Username is required.';
      isValid = false;
    } else if (userData.username.length > 20) {
      newErrors.username = 'Username cannot exceed 20 characters.';
      isValid = false;
    }

    // Password validation
    if (!userData.password) {
      newErrors.password = 'Password is required.';
      isValid = false;
    }

    // Confirm Password validation
    if (!userData.confirmPassword) {
      newErrors.confirmPassword = 'Please confirm your password.';
      isValid = false;
    } else if (userData.password !== userData.confirmPassword) {
      newErrors.confirmPassword = 'Passwords do not match.';
      isValid = false;
    }

    // Email validation
    if (!userData.email) {
      newErrors.email = 'Email is required.';
      isValid = false;
    } else if (!/\S+@\S+\.\S+/.test(userData.email)) {
      newErrors.email = 'Email address is invalid.';
      isValid = false;
    }

    // Phone Number validation
    if (userData.phoneNumber && userData.phoneNumber.length > 15) {
      newErrors.phoneNumber = 'Phone number cannot exceed 15 characters.';
      isValid = false;
    }

    setErrors(newErrors);
    return isValid;
  };

  const createUser = async () => {
    if (validateForm()) {
      // Call the function to create the account
      Fetch.createAccount(userData);
    }
  };

  return (
    <main>
      <div className={styles.container}>
        <aside className="bg-white w-full max-w-md rounded-xl bg-opacity-20 shadow-lg shadow-black">
          <h1 className="text-center text-black font-light text-4xl bg-navy rounded-t-xl m-0 py-4">Sign Up</h1>
          <form className="p-6" onSubmit={(e) => e.preventDefault()}>
            <input
              type="text"
              name="username"
              placeholder="Username"
              className="py-2 px-3 w-full text-black text-lg font-light outlined-none"
              value={userData.username}
              onChange={handleInputChange}
              onBlur={validateForm}
            />
            {errors.username && <p className="text-red-500 text-sm">{errors.username}</p>}
            
            <input
              type="password"
              name="password"
              placeholder="Password"
              className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
              value={userData.password}
              onChange={handleInputChange}
              onBlur={validateForm}
            />
            {errors.password && <p className="text-red-500 text-sm">{errors.password}</p>}

            <input
              type="password"
              name="confirmPassword"
              placeholder="Confirm Password"
              className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
              value={userData.confirmPassword}
              onChange={handleInputChange}
              onBlur={validateForm}
            />
            {errors.confirmPassword && <p className="text-red-500 text-sm">{errors.confirmPassword}</p>}

            <input
              type="email"
              name="email"
              placeholder="Email"
              className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
              value={userData.email}
              onChange={handleInputChange}
              onBlur={validateForm}
            />
            {errors.email && <p className="text-red-500 text-sm">{errors.email}</p>}

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
              onBlur={validateForm}
            />
            {errors.phoneNumber && <p className="text-red-500 text-sm">{errors.phoneNumber}</p>}

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
              <button
                type="button"
                onClick={createUser}
                className={`bg-navy text-white px-4 py-2 rounded transition hover:text-black ${Object.values(errors).some((error) => error) ? 'cursor-not-allowed opacity-50' : ''}`}
                disabled={Object.values(errors).some((error) => error)}
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
