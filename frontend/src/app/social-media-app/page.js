'use client';

import styles from '../styles/Home.module.css';
import { useState } from 'react';

export default function Projects() {
	const [inputValue, setInputValue] = useState('');
	const [result, setResult] = useState('');

	const handleButtonClick = async () => {
		if (inputValue) {
			try {
				const trimmedInput = inputValue.trim();
				const response = await(fetch(`http://localhost:8080/exampledata/${trimmedInput}`));
				const data = await response.text();
				setResult(data);
				console.log("Data successfully retrieved.");
			} catch (error) {
				setResult('Error while fetching data');
				console.error(error);
			}
		}
	};


	return (
		<div className={styles.container}>
			<header className={styles.banner}></header>

			<input
			  type="text"
			  value={inputValue}
			  onChange={(e) => setInputValue(e.target.value)}
			  placeholder="Enter some text"
			  className="text-black" 
			/>

			<button onClick={handleButtonClick} className={styles.button}>
			  Fetch Data
			</button>

			<p className={styles.resultLabel}>{result}</p>

		</div>
	);
}
