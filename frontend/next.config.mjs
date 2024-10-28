// next.config.mjs
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
    env: {
        BASE_API_URL: process.env.NODE_ENV === 'production'
            ? 'https://four800-webapp.onrender.com'
            : 'http://localhost:8080',
    },
};

export default nextConfig;
