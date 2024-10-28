const Layout = ({ children }) => {
    return (
        <div className="min-h-screen flex flex-col">
            <header className="bg-blue-500 text-white py-4 text-center">
                <h1>Direct Messages</h1>
            </header>
            <main className="flex-grow">{children}</main>
        </div>
    );
};

export default Layout;
