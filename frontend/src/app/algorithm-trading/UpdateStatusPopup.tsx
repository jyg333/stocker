import React from 'react';
import axiosInstance from '../utils/axiosInstance';

interface UpdateStatusPopupProps {
    symbol: string; // 현재 symbol
    onConfirm: () => void; // 확인 클릭 시 실행될 함수
    onCancel: () => void; // 취소 클릭 시 실행될 함수
}

const UpdateStatusPopup: React.FC<UpdateStatusPopupProps> = ({ symbol, onConfirm, onCancel }) => {
    const handleConfirm = async () => {
        try {
            // PUT 요청 보내기
            await axiosInstance.put('/api/al-trade/set-status', {
                symbol: symbol,
                al_status: true,
            });
            alert(`${symbol}의 상태가 활성화되었습니다.`);
            onConfirm(); // 확인 콜백 실행
        } catch (error) {
            console.error('Failed to update status:', error);
            alert('상태 변경에 실패했습니다.');
        }
    };

    return (
        <div className="fixed inset-0 z-50 bg-black bg-opacity-50 flex justify-center items-center">
            <div className="bg-white rounded-lg shadow-lg p-6 w-80">
                <h2 className="text-xl font-bold text-gray-800 mb-4">Activate Symbol</h2>
                <p className="text-gray-600 mb-6">
                    <strong>{symbol}</strong>의 상태를 활성화하시겠습니까?
                </p>
                <div className="flex justify-between">
                    <button
                        onClick={onCancel}
                        className="px-4 py-2 bg-gray-200 text-gray-800 rounded hover:bg-gray-300"
                    >
                        취소
                    </button>
                    <button
                        onClick={handleConfirm}
                        className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
                    >
                        확인
                    </button>
                </div>
            </div>
        </div>
    );
};

export default UpdateStatusPopup;
