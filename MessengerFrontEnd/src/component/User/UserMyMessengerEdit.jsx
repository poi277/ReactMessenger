import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Header from "../UI/Header";
import { useUserAuth } from "../api/AuthProvider";
import { UserPostApi, PosteditApi, PhotoPostApi, DeletePhotoApi } from "../api/ApiService";
import '../Css/UserMyMessengerEdit.css';

export default function UserMyMessengerEdit() {
  const { postid } = useParams();
  const navigate = useNavigate();
  const { user } = useUserAuth();

  const [post, setPost] = useState(null);
  const [title, setTitle] = useState("");
  const [context, setContext] = useState("");
  const [postRange, setPostRange] = useState("privatepost");
  const [existingPhotos, setExistingPhotos] = useState([]);
  const [newFiles, setNewFiles] = useState([]);
  const [deletedPhotoIds, setDeletedPhotoIds] = useState([]);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!postid) return;
    UserPostApi(postid)
      .then((res) => {
        setPost(res.data);
        setTitle(res.data.title);
        setContext(res.data.context);
        setPostRange(res.data.postRange || "privatepost");
        setExistingPhotos(res.data.photos || []);
      })
      .catch((err) => {
        console.error("게시글 불러오기 실패:", err);
        setPost(null);
      });
  }, [postid]);

  if (!post) return <div><Header /><p>게시글이 없습니다.</p></div>;

  const isOwner = user && post.userUuid === user.uuid;
  if (!isOwner) return <div><Header /><p>이 게시글을 수정할 권한이 없습니다.</p></div>;

  const handleDeleteExistingPhoto = (id) => {
    if (!window.confirm("이 사진을 삭제하시겠습니까?")) return;
    setExistingPhotos(prev => prev.filter(p => p.id !== id));
    setDeletedPhotoIds(prev => [...prev, id]);
  };

  const handleNewFileChange = (e) => {
    const files = Array.from(e.target.files);
    setNewFiles(prev => [...prev, ...files]);
  };

  const removeNewFile = (idx) => {
    setNewFiles(prev => prev.filter((_, i) => i !== idx));
  };

  const handleSave = async () => {
    if (!window.confirm("변경 사항을 저장하시겠습니까?")) return;
    setSaving(true);

    try {
      await PosteditApi({ postid, title, context, postRange });

      // 기존 사진 삭제
      for (const id of deletedPhotoIds) await DeletePhotoApi(postid, id);

      // 새 사진 업로드
      for (const file of newFiles) {
        const formData = new FormData();
        formData.append("file", file);
        await PhotoPostApi(postid, formData);
      }

      alert("게시글이 수정되었습니다.");
      navigate(`/messenger/${user.uuid}/${postid}`);
    } catch (err) {
      console.error(err);
      alert("수정 중 오류가 발생했습니다.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div>
      <Header />
      <div className="write-wrapper">
        <div className="write-container">
          <div className="write-card">
            <h2 className="write-cardTitle">게시글 수정</h2>

            {/* 제목 */}
            <div className="write-formGroup">
              <label className="write-label">제목</label>
              <div className="write-inputBox">
                <input
                  type="text"
                  className="write-input"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="제목을 입력하세요"
                />
              </div>
            </div>

            {/* 내용 */}
            <div className="write-formGroup">
              <label className="write-label">내용</label>
              <div className="write-inputBox">
                <textarea
                  className="write-textarea"
                  value={context}
                  onChange={(e) => setContext(e.target.value)}
                  placeholder="내용을 입력하세요"
                />
              </div>
            </div>

            {/* 공개 범위 */}
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
            <div className="edit-photocontainer">
            {/* 새 사진 업로드 */}
            <div className="write-formGroup">
              <label className="write-label">새 사진 추가</label>
              <div className="write-fileUpload">
                <label className="write-fileLabel">
                  <span>사진 선택</span>
                  <input
                    type="file"
                    accept="image/*"
                    multiple
                    onChange={handleNewFileChange}
                    style={{ display: "none" }}
                  />
                </label>

                {newFiles.length > 0 && (
                  <div className="write-filePreviewGrid">
                    {newFiles.map((file, idx) => (
                      <div key={idx} className="write-filePreview">
                        <div className="write-filePreviewImgWrapper">
                          <img src={URL.createObjectURL(file)} alt={`new-${idx}`} />
                          <button
                            type="button"
                            className="write-filePreviewBtn"
                            onClick={() => removeNewFile(idx)}
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
            
            {/* 기존 사진 */}
            {existingPhotos.length > 0 && (
              <div className="write-formGroup">
                <div className="write-filePreviewGrid">
                  {existingPhotos.map(photo => (
                    <div key={photo.id} className="write-filePreview">
                      <div className="write-filePreviewImgWrapper">
                        <img src={photo.photoUrl} alt="existing" />
                        <button
                          type="button"
                          className="write-filePreviewBtn"
                          onClick={() => handleDeleteExistingPhoto(photo.id)}
                        >
                          ×
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
            </div>
            {/* 저장 버튼 */}
            <button
              onClick={handleSave}
              className={`write-submitBtn ${saving || !title.trim() || !context.trim() ? 'write-submitBtnDisabled' : ''}`}
              disabled={saving || !title.trim() || !context.trim()}
            >
              {saving ? "저장 중..." : "변경사항 저장"}
            </button>

          </div>
        </div>
      </div>
    </div>
  );
}
