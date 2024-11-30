import React from 'react';

interface SavePopupProps {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: () => void;
    message?: string; // 팝업 메시지 커스터마이징
}

const SavePopup: React.FC<SavePopupProps> = ({ isOpen, onClose, onConfirm, message }) => {
    if (!isOpen) return null; // 팝업이 닫힌 상태에서는 렌더링하지 않음

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
            <div className="bg-white rounded-lg shadow-lg p-6 w-80">
                <h2 className="text-xl font-bold text-gray-800 mb-4">주식이름 *수정</h2>
                <p className="text-gray-600 mb-6">{message || "검색 결과를 저장하시겠습니까?"}</p>
                <div className="flex justify-between">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 bg-gray-200 text-gray-800 rounded hover:bg-gray-300"
                    >
                        No
                    </button>
                    <button
                        onClick={onConfirm}
                        className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                    >
                        Yes
                    </button>
                </div>
            </div>
        </div>
    );
};

export default SavePopup;
