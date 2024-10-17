

// Checks for token and if it is valid
export async function validateToken(token){
	if (!token){
		console.error('User not logged in. Redirecting to login page.');
		window.location.href = '/login'; 
		return false;
	}

	try {
		const response = await fetch('http://localhost:8080/auth/validate', {
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