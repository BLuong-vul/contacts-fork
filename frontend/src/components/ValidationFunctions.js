// ValidationFunctions.js
// This is used for unit testing purposes


import { authFetchFromApi } from './FunctionHelpers';

const baseURL = process.env.BASE_API_URL || 'http://localhost:8080';

async function validationHelper(redirect=false){
	const token = localStorage.getItem('token');
	if (!token){
		console.log('User not logged in.');
		if (redirect) window.location.href = '/login';
		return false;
	}

	try {
		await authFetchFromApi(`${baseURL}/auth/validate`);
		return true;
	} catch (error){
		console.log('Validation error: ', error);
		localStorage.removeItem('token');
		if (redirect) window.location.href = '/login';
		return false;
	}
}

export async function validateToken(){
	return validationHelper();
}

export async function validateTokenWithRedirect(){
	return validationHelper(true);
}