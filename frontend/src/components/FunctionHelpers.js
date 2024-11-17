// FunctionHelpers.js

/* ===== FETCH HELPERS ===== */

// Use for fetches that do not require authentication
// Only returns the response. Remember to do .json() or .text() or whatever on the return value
export async function fetchFromApi(endpoint, methodArg='GET', body=null){
	console.log(endpoint);
	const response = await fetch(endpoint, {
		method: methodArg,
		headers: {
		    'Content-Type': 'application/json',
		},
		body: body ? JSON.stringify(body) : null,
	});
	if (!response.ok) throw new Error(`Failed to fetch: ${response.statusText}`);
	return response;
}

// Use for fetches that require authentication
// Only returns the response. Remember to do .json() or .text() or whatever on the return value
export async function authFetchFromApi(endpoint, methodArg='GET', body=null) {
    const token = localStorage.getItem('token');
    const response = await fetch(endpoint, {
    	method: methodArg,
    	headers: {
    	    'Authorization': `Bearer ${token}`,
    	    'Content-Type': 'application/json',
    	},
    	body: body ? JSON.stringify(body) : null,
    });
    if (!response.ok) throw new Error(`Failed authenticated fetch: ${response.statusText}`);
    return response;
}
