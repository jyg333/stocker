import React, {useEffect, useState} from 'react';
import axiosInstance from '../utils/axiosInstance';

interface UpdateStatusPopupProps {
    symbol: string; // 현재 symbol
    onConfirm: () => void; // 확인 클릭 시 실행될 함수
    onCancel: () => void; // 취소 클릭 시 실행될 함수
}

const UpdateStatusPopup: React.FC<UpdateStatusPopupProps> = ({ symbol, onConfirm, onCancel }) => {
    const [formData, setFormData] = useState({
        initAmount: 0,
        upperLimit: 0,
        lowerLimit: 0,
        // startAt: "",
        endAt: "",
        alType:""
    });

    const [alTypes, setAlTypes] = useState<string[]>([]); // 드롭다운에 표시할 데이터
    // 드롭다운 데이터 가져오기
    useEffect(() => {
        const fetchAlTypes = async () => {
            try {
                const response = await axiosInstance.get("/api/al-trade/al-types");
                setAlTypes(response.data); // 데이터 설정
            } catch (error) {
                console.error("Failed to fetch algorithm types:", error);
                alert("알고리즘 유형 데이터를 가져오는 데 실패했습니다.");
            }
        };

        fetchAlTypes();
    }, []);

// 유효성 검사 메서드
    const validateForm = (): boolean => {
        const { initAmount, upperLimit, lowerLimit, endAt,alType } = formData;
        console.log(initAmount, upperLimit, lowerLimit, endAt, alType)
        // 숫자형 값 검사
        if (isNaN(initAmount) || initAmount <= 0) {
            alert("초기 금액은 숫자여야 하며 0보다 커야 합니다.");
            return false;
        }
        if (isNaN(upperLimit) || upperLimit <= 0) {
            alert("상한선은 숫자여야 하며 0보다 커야 합니다.");
            return false;
        }
        if (isNaN(lowerLimit) || lowerLimit < 0) {
            alert("하한선은 숫자여야 하며 0 이상이어야 합니다.");
            return false;
        }
        // 오늘 이후의 날짜인지 확인
        const today = new Date();
        today.setHours(0, 0, 0, 0); // 시간을 0으로 설정하여 날짜만 비교
        const endDate = new Date(endAt);
        if (endDate <= today) {
            alert("종료 시간은 오늘 이후여야 합니다.");
            return false;
        }
        // 종료 시간 검사
        if (endAt && isNaN(new Date(endAt).getTime())) {
            alert("유효한 종료 시간을 입력하세요.");
            return false;
        }

        // 추가 논리 검사: 하한선이 상한선보다 크지 않은지 확인
        if (Number(lowerLimit) > Number(upperLimit)) {
            alert("하한선은 상한선보다 클 수 없습니다.");
            return false;
        }
        // 알고리즘 유형 선택 검사
        if (!alType) {
            alert("알고리즘 유형을 선택하세요.");
            return false;
        }

        return true; // 모든 조건이 통과되면 true 반환
    };

    // 입력값 변경 핸들러
    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    // 확인 버튼 클릭 핸들러
    const handleConfirm = async () => {
        if (!validateForm()) {
            return; // 유효성 검사 실패 시 요청 전송하지 않음
        }

        try {
            // PUT 요청으로 데이터 전송
            await axiosInstance.post(`/api/al-trade/update-settings`, {
                symbol: symbol,
                initAmount: formData.initAmount,
                upperLimit: formData.upperLimit,
                lowerLimit: formData.lowerLimit,
                endAt: formData.endAt,
                alType: formData.alType, // 선택된 알고리즘 유형 추가
            });
            alert(`${symbol}의 설정이 업데이트되었습니다.`);
            onConfirm(); // 확인 콜백 실행
        } catch (error) {
            console.error("Failed to update settings:", error);
            alert("설정 업데이트에 실패했습니다.");
        }
    };

    return (
        <div className="fixed inset-0 z-50 bg-black bg-opacity-50 flex justify-center items-center">
            <div className="bg-white rounded-lg shadow-lg p-6 w-96">
                <h2 className="text-xl font-bold text-gray-800 mb-4">알고리즘 매매 등록 : {symbol}</h2>
                <div className="space-y-4 text-black">
                    {/* 초기 금액 */}
                    <div>
                        <label className="block text-gray-700">초기 금액($)</label>
                        <input
                            type="number"
                            name="initAmount"
                            value={formData.initAmount}
                            onChange={handleChange}
                            className="w-full p-2 border rounded"
                        />
                    </div>
                    {/* 상한선 */}
                    <div>
                        <label className="block text-gray-700">상한선($)</label>
                        <input
                            type="number"
                            name="upperLimit"
                            value={formData.upperLimit}
                            onChange={handleChange}
                            className="w-full p-2 border rounded"
                        />
                    </div>
                    {/* 하한선 */}
                    <div>
                        <label className="block text-gray-700">하한선($)</label>
                        <input
                            type="number"
                            name="lowerLimit"
                            value={formData.lowerLimit}
                            onChange={handleChange}
                            className="w-full p-2 border rounded"
                        />
                    </div>

                    <div>
                        <label className="block text-gray-700">종료 시간</label>
                        <input
                            type="datetime-local"
                            name="endAt"
                            value={formData.endAt}
                            onChange={handleChange}
                            className="w-full p-2 border rounded"
                        />
                    </div>
                    {/* 알고리즘 유형 드롭다운 */}
                    <div>
                        <label className="block text-gray-700">알고리즘 유형</label>
                        <select
                            name="alType"
                            value={formData.alType}
                            onChange={handleChange}
                            className="w-full p-2 border rounded"
                        >
                            <option value="">선택하세요</option>
                            {alTypes.map((type) => (
                                <option key={type} value={type}>
                                    {type}
                                </option>
                            ))}
                        </select>
                    </div>
                </div>
                <div className="flex justify-between mt-6">
                    <button
                        onClick={onCancel}
                        className="px-4 py-2 bg-gray-200 text-gray-800 rounded hover:bg-gray-300"
                    >
                        취소
                    </button>
                    <button
                        onClick={handleConfirm}
                        className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                    >
                        저장
                    </button>
                </div>
            </div>
        </div>
    );
};

export default UpdateStatusPopup;
