import React, { useState } from "react";
import Header from '../UI/Header';
import { useNavigate, useParams } from "react-router-dom";
import { writePostApi, PhotoPostApi } from '../api/ApiService';
import '../Css/UserMessageWrite.css';
export default function UserMessageWrite() {
  const { uuid } = useParams();
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [context, setContext] = useState("");
  const [postRange, setPostRange] = useState("publicpost"); 
  const [selectedFiles, setSelectedFiles] = useState([]);  
  const [photoUrls, setPhotoUrls] = useState([]);  
  const [uploading, setUploading] = useState(false);

  const handleFileChange = (e) => {
    const files = Array.from(e.target.files);
    setSelectedFiles(prev => [...prev, ...files]);
  };

  const removeFile = (indexToRemove) => {
    setSelectedFiles(prev => prev.filter((_, index) => index !== indexToRemove));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (uploading) return;
    setUploading(true);

    try {
      const postId = await writePostApi({
        userUuid: uuid,
        title,
        context,
        postRange
      });

      const uploadedUrls = [];
      for (const file of selectedFiles) {
        const formData = new FormData();
        formData.append("file", file);

        const uploadedUrl = await PhotoPostApi(postId, formData);
        uploadedUrls.push(uploadedUrl);
      }

      setPhotoUrls(uploadedUrls);
      alert("글과 사진이 성공적으로 등록되었습니다!");
      navigate(`/messenger/${uuid}`);
    } catch (error) {
      console.error(error);
      alert("등록 중 오류가 발생했습니다.");
    } finally {
      setUploading(false);
    }
  };

  return (
    <div>
      <Header />
      <div className="write-wrapper">
        <div className="write-container">
          <div className="write-card">
            <h2 className="write-cardTitle">새 게시물 작성</h2>
              <div className="write-formGroup">
                <label className="write-label">제목</label>
                <div className="write-inputBox">
                  <input
                    type="text"
                    className="write-input"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    required
                    placeholder="제목을 입력하세요"
                  />
                </div>
              </div>

              <div className="write-formGroup">
                <label className="write-label">내용</label>
                <div className="write-inputBox">
                  <textarea
                    className="write-textarea"
                    value={context}
                    onChange={(e) => setContext(e.target.value)}
                    required
                    placeholder="내용을 입력하세요"
                  />
                </div>
              </div>

              <div className="write-formGroup">
                <label className="write-label">공개 범위</label>
                <div className="write-inputBox">
                  <select
                    className="write-selectBox"
                    value={postRange}
                    onChange={(e) => setPostRange(e.target.value)}
                  >
                    <option value="publicpost">공개</option>
                    <option value="friendpost">친구만</option>
                    <option value="privatepost">비공개</option>
                  </select>
                </div>
              </div>

              <div className="write-formGroup">
                <label className="write-label">사진 업로드</label>
                <div className="write-fileUpload">
                  <label className="write-fileLabel">
                    <span>사진 선택</span>
                    <input
                      type="file"
                      accept="image/*"
                      multiple
                      onChange={handleFileChange}
                      disabled={uploading}
                      style={{ display: 'none' }}
                    />
                  </label>

                  {selectedFiles.length > 0 && (
                    <div className="write-filePreviewGrid">
                      {selectedFiles.map((file, idx) => (
                        <div key={idx} className="write-filePreview">
                          <div className="write-filePreviewImgWrapper">
                            <img src={URL.createObjectURL(file)} alt={`preview-${idx}`} />
                            <button
                              type="button"
                              onClick={() => removeFile(idx)}
                              className="write-filePreviewBtn"
                            >
                              ×
                            </button>
                          </div>
                          <p className="write-filePreviewText">{file.name}</p>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>

              {uploading && (
                <div className="write-uploading">
                  <p>등록 중...</p>
                </div>
              )}

              <button
              onClick={handleSubmit}
                type="submit"
                className={`write-submitBtn ${uploading || !title.trim() || !context.trim() ? 'write-submitBtnDisabled' : ''}`}
                disabled={uploading || !title.trim() || !context.trim()}
              >
                {uploading ? '등록 중...' : '게시물 등록'}
              </button>
          </div>
        </div>
      </div>
    </div>
  );
}
