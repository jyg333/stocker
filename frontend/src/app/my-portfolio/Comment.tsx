import React, { useEffect, useState } from "react";
import axiosInstance from "../utils/axiosInstance";

interface Comment {
    updatedAt: string;
    content: string;
    reference: string;
}

const Comment = ({ symbol }: { symbol: string }) => {
    const [comments, setComments] = useState<Comment[]>([]);
    const [newComment, setNewComment] = useState<string>("");
    const [newReference, setNewReference] = useState<string>("");
    const [currentPage, setCurrentPage] = useState<number>(0);
    const [totalPages, setTotalPages] = useState<number>(1);

    const [selectedComment, setSelectedComment] = useState<Comment | null>(null);
    const [editComment, setEditComment] = useState<string>("");
    const [editReference, setEditReference] = useState<string>("");

    // 댓글 데이터 로드
    const fetchComments = async (page = 0) => {
        try {
            const response = await axiosInstance.get(`/api/comments`, {
                params: { symbol, page, size: 5 }, // 페이지와 크기를 쿼리로 전달
            });
            const { content, totalPages: total } = response.data;
            const mappedComments = content.map((item: any) => ({
                updatedAt: item.updatedAt.replace("T"," "), // 서버의 updatedAt을 datetime으로 매핑
                content: item.comment,
                reference: item.ref,
            }));
            setComments((prev) => (page === 0 ? mappedComments : [...prev, ...mappedComments])); // 페이지 0일 때 초기화
            setTotalPages(total);
        } catch (error) {
            console.error("Failed to fetch comments:", error);
            alert("댓글 데이터를 가져오는 데 실패했습니다.");
        }
    };

    // Symbol 변경 시 데이터 초기화 및 로드
    useEffect(() => {
        if (symbol) {
            setComments([]); // 기존 댓글 초기화
            setCurrentPage(0); // 페이지 초기화
            fetchComments(0); // 첫 페이지 로드
        }
    }, [symbol]);

    // 새로운 댓글 추가
    const handleAddComment = async () => {
        if (!newComment.trim()) {
            alert("댓글 내용을 입력하세요.");
            return;
        }

        const currentDatetime = new Date().toISOString(); // 현재 시간
        const newEntry = {
            updatedAt: currentDatetime,
            content: newComment,
            reference: newReference || null,
        };

        try {
            await axiosInstance.post("/api/comments", {
                symbol,
                comment: newComment,
                ref: newReference,
            });
            setComments((prev) => [newEntry, ...prev]); // 최신 댓글을 상단에 추가
            setNewComment(""); // 입력 초기화
            setNewReference("");
        } catch (error) {
            console.error("Failed to add comment:", error);
            alert("댓글 추가에 실패했습니다.");
        }
    };

    // 선택된 댓글 처리
    const handleRowClick = (comment: Comment) => {
        if (selectedComment?.updatedAt === comment.updatedAt) {
            // 동일한 항목 클릭 시 선택 해제
            setSelectedComment(null);
        } else {
            // 새로운 항목 선택
            setSelectedComment(comment);
            setEditComment(comment.content);
            setEditReference(comment.reference || "");
        }
    };

    // 댓글 수정
    const handleUpdateComment = async () => {
        if (!selectedComment) return alert("수정할 댓글을 선택하세요.");
        try {
            const { updatedAt } = selectedComment;
            await axiosInstance.put(`/api/comments`, {
                symbol,
                updatedAt: updatedAt.replace(" ","T"),
                newComment: editComment,
                newReference: editReference || null,
            });
            alert("댓글이 수정되었습니다.");
            // 데이터 로드 또는 상태 업데이트
            await fetchComments(0);
            setSelectedComment(null); // 선택 해제
        } catch (error) {
            console.error("Failed to update comment:", error);
            alert("댓글 수정에 실패했습니다.");
        }
    };
    // 댓글 삭제
    const handleDeleteComment = async () => {
        if (!selectedComment) return alert("삭제할 댓글을 선택하세요.");
        try {
            const { updatedAt } = selectedComment;
            await axiosInstance.delete(`/api/comments`, {
                params: { symbol, updatedAt: updatedAt.replace(" ","T") },
            });
            alert("댓글이 삭제되었습니다.");
            // 데이터 로드 또는 상태 업데이트
            await fetchComments(0);
            setSelectedComment(null); // 선택 해제
        } catch (error) {
            console.error("Failed to delete comment:", error);
            alert("댓글 삭제에 실패했습니다.");
        }
    };

    // 더 보기 기능
    const handleLoadMore = () => {
        if (currentPage + 1 < totalPages) {
            fetchComments(currentPage + 1);
            setCurrentPage((prev) => prev + 1);
        }
    };

    return (
        <div className="mt-4">
            <h2 className="text-2xl font-bold mb-4 ml-2">Comment for {symbol}</h2>
            <div className="bg-gray-100 p-4 rounded-md shadow-md">
                <table className="w-full table-auto border-collapse border border-gray-300">
                    <thead>
                    <tr>
                        <th className="border border-gray-300 px-4 py-2 w-1/6">DATETIME</th>
                        <th className="border border-gray-300 px-4 py-2 w-3/6">CONTENT</th>
                        <th className="border border-gray-300 px-4 py-2 w-2/6">REFERENCE</th>
                    </tr>
                    </thead>
                    <tbody>
                    {comments.map((comment, index) => (
                        <tr
                            key={index}
                            onClick={() => handleRowClick(comment)}
                            className={`cursor-pointer ${
                                selectedComment?.updatedAt === comment.updatedAt
                                    ? "bg-blue-200"
                                    : "hover:bg-gray-200"
                            }`}
                        >
                            <td className="border border-gray-300 px-4 py-2">
                                {comment.updatedAt}
                            </td>
                            <td className="border border-gray-300 px-4 py-2">
                                {comment.content}
                            </td>
                            <td className="border border-gray-300 px-4 py-2">
                                {comment.reference ? (
                                    <a
                                        href={comment.reference}
                                        target="_blank"
                                        rel="noopener noreferrer"
                                        className="text-blue-500"
                                    >
                                        {comment.reference}
                                    </a>
                                ) : (
                                    "N/A"
                                )}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
                {currentPage + 1 < totalPages && (
                    <div className="mt-4">
                        <button
                            onClick={handleLoadMore}
                            className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
                        >
                            더 보기
                        </button>
                    </div>
                )}
                {selectedComment && (
                    <div className="mt-4 p-4 bg-white border border-gray-300 rounded-md">
                        <h3 className="text-lg font-bold mb-2">Edit Selected Comment</h3>
                        <textarea
                            value={editComment}
                            onChange={(e) => setEditComment(e.target.value)}
                            className="w-full p-2 border border-gray-300 rounded-md mb-2"
                        />
                        <input
                            type="text"
                            value={editReference}
                            onChange={(e) => setEditReference(e.target.value)}
                            placeholder="Reference URL (Optional)"
                            className="w-full p-2 border border-gray-300 rounded-md mb-2"
                        />
                        <div className="flex space-x-4">
                            <button
                                onClick={handleUpdateComment}
                                className="px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600"
                            >
                                Update
                            </button>
                            <button
                                onClick={handleDeleteComment}
                                className="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600"
                            >
                                Delete
                            </button>
                        </div>
                    </div>
                )}
                {!selectedComment && <div className="mt-4 flex space-x-4">
                    <input
                        type="text"
                        value={newComment}
                        onChange={(e) => setNewComment(e.target.value)}
                        placeholder="댓글 입력"
                        className="flex-1 px-4 py-2 border border-gray-300 rounded-md"
                    />
                    <input
                        type="text"
                        value={newReference}
                        onChange={(e) => setNewReference(e.target.value)}
                        placeholder="참고 URL (선택)"
                        className="flex-1 px-4 py-2 border border-gray-300 rounded-md"
                    />
                    <button
                        onClick={handleAddComment}
                        className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
                    >
                        추가
                    </button>
                </div>}
            </div>
        </div>
    );
};

export default Comment;
