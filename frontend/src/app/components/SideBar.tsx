import React from 'react';

const Sidebar = ({ items }: { items: string[] }) => {
    return (
        // <div className="sticky top-4 h-[calc(100vh-2rem)] bg-sky-300 text-white p-4 shadow-lg overflow-y-auto rounded-lg ml-2 ">
        <div className="sticky top-4 h-[66vh] bg-sky-300 text-white p-4 shadow-lg overflow-y-auto rounded-lg ml-4 mt-4 w-60">

            <h2 className="text-xl font-bold mb-4">Stock List</h2>
            <ul className="space-y-2">
                {items.map((item, index) => (
                    <li
                        key={index}
                        className="hover:bg-sky-400 p-2 rounded cursor-pointer"
                    >
                        {item}
                    </li>
                ))}
            </ul>
            <button className="mt-4 bg-red-300 hover:bg-red-600 text-white p-2 rounded">
                Delete
            </button>
        </div>
    );
};

export default Sidebar;
