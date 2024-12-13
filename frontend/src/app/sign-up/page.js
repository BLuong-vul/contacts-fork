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
    country: ''
  });

  const [errors, setErrors] = useState({
    username: '',
    password: '',
    confirmPassword: '',
    email: '',
    phoneNumber: '',
    address: '',
    city: '',
    state: '',
    zipCode: '',
    country: ''
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
    } else if (!/^[a-zA-Z0-9_]{3,20}$/.test(userData.username)) {
      newErrors.username = 'Username must be 3-20 characters and contain only letters, numbers, and underscores.';
      isValid = false;
    }

    // Password validation
    if (!userData.password) {
      newErrors.password = 'Password is required.';
      isValid = false;
    } else if (userData.password.length < 8) {
      newErrors.password = 'Password must be at least 8 characters.';
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
      newErrors.email = 'Invalid email format.';
      isValid = false;
    }

    // Name validation
    if (!userData.fullName) {
      newErrors.fullName = 'Full name is required.';
      isValid = false;
    }

    // Phone Number validation
    if (!userData.phoneNumber) {
      newErrors.phoneNumber = 'Phone number is required.';
      isValid = false;
    } else if (!/^\d{10}$/.test(userData.phoneNumber)) {
      newErrors.phoneNumber = 'Phone number must be 10 digits.';
      isValid = false;
    }

    // Address validation
    if (!userData.address) {
      newErrors.address = 'Address is required.';
      isValid = false;
    }

    // City validation
    if (!userData.city) {
      newErrors.city = 'City is required.';
      isValid = false;
    }

    // State validation
    if (!userData.state) {
      newErrors.state = 'State is required.';
      isValid = false;
    }

    // Zip code validation
    if (!userData.zipCode) {
      newErrors.zipCode = 'Zip code is required.';
      isValid = false;
    } else if (!/^\d{5}$/.test(userData.zipCode)) {
      newErrors.zipCode = 'Zip code must be 5 digits.';
      isValid = false;
    }

    // Country validation
    if (!userData.country) {
      newErrors.country = 'Country is required.';
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
              onBlur={validateForm}
            />
            {errors.fullName && <p className="text-red-500 text-sm">{errors.fullName}</p>}

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
              onBlur={validateForm}
            />
            {errors.address && <p className="text-red-500 text-sm">{errors.address}</p>}

            <input
              type="text"
              name="city"
              placeholder="City"
              className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
              value={userData.city}
              onChange={handleInputChange}
              onBlur={validateForm}
            />
            {errors.city && <p className="text-red-500 text-sm">{errors.city}</p>}

            <input
              type="text"
              name="state"
              placeholder="State"
              className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
              value={userData.state}
              onChange={handleInputChange}
              onBlur={validateForm}
            />
            {errors.state && <p className="text-red-500 text-sm">{errors.state}</p>}

            <input
              type="text"
              name="zipCode"
              placeholder="Zip Code"
              className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
              value={userData.zipCode}
              onChange={handleInputChange}
              onBlur={validateForm}
            />
            {errors.zipCode && <p className="text-red-500 text-sm">{errors.zipCode}</p>}

            <input
              type="text"
              name="country"
              placeholder="Country"
              className="py-2 px-3 w-full text-black text-lg font-light outlined-none mt-3"
              value={userData.country}
              onChange={handleInputChange}
              onBlur={validateForm}
            />
            {errors.country && <p className="text-red-500 text-sm">{errors.country}</p>}

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
