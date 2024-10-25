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
    			destination: 'http://four800-webapp.onrender.com/:path*'
    		},
    	];
    },
};

export default nextConfig;
