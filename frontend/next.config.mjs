// next.config.mjs
/** @type {import('next').NextConfig} */
const nextConfig = {
    output: "standalone",
    images: {
        remotePatterns: [
            {
                protocol: "https",
                hostname: "images.pexels.com",
            },
        ],
    },
    async rewrites() {
    	return [
    		{
    			source: '/api/:path*',
    			destination: 'http://localhost:8080/:path*'
    		},
    	];
    },
};

export default nextConfig;
