

// Checks for token and if it is valid
export async function validateToken(token){
	if (!token){
		console.error('User not logged in. Redirecting to login page.');
		window.location.href = '/login'; 
		return;
	}

	try {
		const response = await fetch('/api/validate', {
			method: 'GET',
			headers: {
				'Authorization': `Bearer ${token}`,
			},
		});

		if (!response.ok){
			throw new Error('Invalid token');
		}
		
	} catch (error){
		console.error('Login expired. Redirecting to login page.');
		localStorage.removeItem('token');
		window.location.href = '/login';
	}
}