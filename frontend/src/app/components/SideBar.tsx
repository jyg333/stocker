import React, { useState } from 'react';

const Sidebar = ({
                     items,
                     onSymbolSelect,
                     onDelete,
                 }: {
    items: string[];
    onSymbolSelect: (symbol: string) => void;
    onDelete: (symbol: string) => void; // 삭제를 위한 콜백 추가
}) => {
    const [selectedSymbol, setSelectedSymbol] = useState<string | null>(null); // 선택된 Symbol 상태

    const handleSelect = (symbol: string) => {
        setSelectedSymbol(symbol); // 선택된 Symbol 업데이트
        onSymbolSelect(symbol); // 상위 콜백 호출
    };

    const handleDelete = () => {
        if (selectedSymbol) {
            onDelete(selectedSymbol); // 상위 콜백 호출로 Symbol 삭제
            setSelectedSymbol(null); // 선택된 Symbol 초기화
        } else {
            alert('삭제할 Symbol을 선택하세요.');
        }
    };

    return (
        <div className="sticky top-4 h-[66vh] bg-sky-300 text-white p-4 shadow-lg overflow-y-auto rounded-lg ml-4 mt-4 w-60">
            <h2 className="text-xl font-bold mb-4">Stock List</h2>
            <ul className="space-y-2">
                {items.map((item, index) => (
                    <li
                        key={index}
                        className={`p-2 rounded cursor-pointer ${
                            selectedSymbol === item ? 'bg-sky-500 font-bold' : 'hover:bg-sky-400'
                        }`}
                        onClick={() => handleSelect(item)} // Symbol 선택 핸들링
                    >
                        {item}
                    </li>
                ))}
            </ul>
            <button
                className="mt-4 bg-red-300 hover:bg-red-600 text-white p-2 rounded"
                onClick={handleDelete} // 삭제 핸들러
            >
                Delete
            </button>
        </div>
    );
};

export default Sidebar;
