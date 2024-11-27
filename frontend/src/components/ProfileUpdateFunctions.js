import { fetchFromApi, authFetchFromApi } from './FunctionHelpers';
import { validateToken, validateTokenWithRedirect } from './ValidationFunctions';

const baseURL = process.env.BASE_API_URL || 'http://localhost:8080';

export async function updateProfileInfo(attribute, value){
	if (!(await validateTokenWithRedirect())) return false;
	try {
		const res = await authFetchFromApi(`${baseURL}/user/account/${attribute}?${attribute}=${value}`, 'POST');
		return true;
	} catch (error){
		console.error('Error during profile update:', error);
		return false;
	}
}