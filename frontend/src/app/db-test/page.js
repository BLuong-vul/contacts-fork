"use client"; 

import { useState } from 'react';
import styles from '../styles/demo.module.css';

export default function Home() {
	const [textBoxData, setTextBoxData] = useState({
		fullName: '',
		username: '',
		password: '',
		email: '',
		phoneNumber: '',
		address: '',
		city: '',
		state: '',
		zipCode: '',
		country: '',
		followerCount: '',
	});

	const [inputId, setInputId] = useState('');

	const fetchDataFromBackend = async () => {
	const mockData = {
		fullName: 'John',
		username: 'Doe',
		password: 'john.doe@example.com',
		email: '123-456-7890',
		phoneNumber: '1234 Elm St',
		address: 'Metropolis',
		city: 'NY',
		state: '10001',
		zipCode: 'USA',
		country: 'Software Engineer',
		followerCount: 'Tech Co',
	};

		setTextBoxData(mockData);
	};

	return (
		<div>
			<h1>Database Fetch Demo</h1>

			<div style={{ display: 'flex', alignItems: 'center', gap: '10px'}}>
				<button
					onClick={fetchDataFromBackend}
					style={{
						backgroundColor: 'white',
						color: 'black',
						border: '1px solid black',
						padding: '10px 20px',
						cursor: 'pointer',
					}}
					onMouseOver={(e) => (e.target.style.backgroundColor = 'lightgrey')}
					onMouseOut={(e) => (e.target.style.backgroundColor = 'white')}
				>
					Fetch User Data
				</button>
				<input
					type="text"
					placeholder="Enter User ID"
					value={inputId}
					onChange={(e) => setInputId(e.target.value)}
					style={{ padding: '5px', borderRadius: '4px', border: '1px solid #ccc', color: 'black'}}
				/>
				</div>

				<div className={styles.formContainer}>
				{Object.entries(textBoxData).map(([key, value]) => (
					<div className={styles.formRow} key={key}>
						<label className={styles.label}>{key.charAt(0).toUpperCase() + key.slice(1).replace(/([A-Z])/g, ' $1')}:</label>
						<input className={styles.input} type="text" value={value} readOnly />
					</div>
				))}
			</div>
		</div>
	);
}
