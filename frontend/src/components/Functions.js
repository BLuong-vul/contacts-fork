

// Checks for token and if it is valid
// Deletes token if not valid
// Returns true if valid, false if not
export async function validateToken(token){
	if (!token){
		console.log('User not logged in.');
		return false;
	}

	try {
		const response = await fetch('/api/auth/validate', {
			method: 'GET',
			headers: {
				'Authorization': `Bearer ${token}`,
			},
		});

		if (!response.ok){
			throw new Error('Invalid token');
		}
		return true;
	} catch (error){
		console.log('Login expired.');
		localStorage.removeItem('token');
		return false;
	}
}

// Checks for token and if it is valid
// Deletes token if not valid
// Redirects if not valid
export async function validateTokenWithRedirect(token){
	if (!token){
		console.log('User not logged in. Redirecting to login page.');
		window.location.href = '/login'; 
		return false;
	}

	try {
		const response = await fetch('/api/auth/validate', {
			method: 'GET',
			headers: {
				'Authorization': `Bearer ${token}`,
			},
		});

		if (!response.ok){
			throw new Error('Invalid token');
		}
		return true;
	} catch (error){
		console.error('Login expired. Redirecting to login page.');
		localStorage.removeItem('token');
		window.location.href = '/login';
		return false;
	}
}