const Layout = ({ children }) => {
    return (
        <div className="min-h-screen flex flex-col">
            <header className="bg-transparent-500 text-white py-4 text-center border-2 border-white font-bold">
                <h1>Direct Messages</h1>
            </header>
            <main className="flex-grow">{children}</main>
        </div>
    );
};

export default Layout;
