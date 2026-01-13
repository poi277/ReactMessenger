import { useEffect, useState, useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Header from "../UI/Header";
import { MyinfoApi, UpdateMyinfoApi, MyProfilePhotoApi } from "../api/ApiService";
import FriendSidebar from "../UI/FriendSidebar";
import "../Css/UserMyInformation.css"; 

export default function UserMyInfomation() {
  const navigate = useNavigate();
  const { uuid } = useParams();
  const [user, setUser] = useState(null);
  const [name, setName] = useState("");
  const [introduce, setIntroduce] = useState(""); // 소개
  const [photoURL, setPhotoURL] = useState("");
  const [selectedFile, setSelectedFile] = useState(null);

  // 유저 정보 불러오기
  useEffect(() => {
    MyinfoApi(uuid)
      .then((res) => {
        setUser(res.data);
        setName(res.data.name);
        setIntroduce(res.data.introduce || "");
        setPhotoURL(res.data.photoURL);
      })
      .catch((err) => console.error("유저 정보 불러오기 실패:", err));
  }, [uuid]);

  // 파일 선택 시 미리보기
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    setSelectedFile(file);

    if (file) {
      const previewURL = URL.createObjectURL(file);
      setPhotoURL(previewURL);
    }
  };

  const handleCopyUUID = () => {
    navigator.clipboard.writeText(uuid);
  };

  // 수정 여부 체크
  const isChanged = useMemo(() => {
    if (!user) return false;
    return (
      name !== user.name ||
      introduce !== (user.introduce || "") ||
      selectedFile !== null
    );
  }, [name, introduce, selectedFile, user]);

  // 저장 버튼 클릭
  const handleSave = async () => {
    if (!isChanged) return; // 변경 없으면 동작 안 함
    try {
      let finalPhotoURL = photoURL;

      if (selectedFile) {
        const formData = new FormData();
        formData.append("file", selectedFile);
        finalPhotoURL = await MyProfilePhotoApi(uuid, formData);
      }

      const updatedUser = { ...user, name, introduce, photoURL: finalPhotoURL };
      const res = await UpdateMyinfoApi(uuid, updatedUser);
      setUser(res.data);
      setPhotoURL(res.data.photoURL);
      setSelectedFile(null);
      alert("정보가 수정되었습니다!");
      navigate(`/messenger/${uuid}`);
    } catch (err) {
      console.error("정보 수정 실패:", err);
      alert("수정 중 오류 발생");
    }
  };

  if (!user) return <div>Loading...</div>;

  return (  
    <div className="user-feed-background">
      <Header />
      <div className="user-feed-wrapper">
        <div className="profile-card">
          <div className="profile-font">프로필 편집</div>
          <div className="profile-incontainer">
            
            {/* 프로필 사진 */}
            <div className="profile-photo-wrapper">
              <img
                className="profile-photo-img"
                src={photoURL || "/default-avatar.png"}
                alt="프로필"
              />
              <div className="profile-photo-text">
                {user.id || "Null"}
              </div>
                <label className="photo-change-btn" htmlFor="fileInput">
                  사진 변경
                </label>
                <input
                  id="fileInput"
                  type="file"
                  accept="image/*"
                  onChange={handleFileChange}
                  style={{ display: "none" }}
                />
            </div>

            {/* UUID */}
            <div className="uuid-container">
              내 UUID {uuid}
              <div className="copy-icon" onClick={handleCopyUUID}>
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                  <path
                    d="M6 11C6 8.172 6 6.757 6.879 5.879C7.757 5 9.172 5 12 5H15C17.828 5 19.243 5 20.121 5.879C21 6.757 21 8.172 21 11V16C21 18.828 21 20.243 20.121 21.121C19.243 22 17.828 22 15 22H12C9.172 22 7.757 22 6.879 21.121C6 20.243 6 18.828 6 16V11Z"
                    stroke="#000"
                    strokeWidth="1"
                  />
                  <path
                    d="M6 19C5.20435 19 4.44129 18.6839 3.87868 18.1213C3.31607 17.5587 3 16.7956 3 16V10C3 6.229 3 4.343 4.172 3.172C5.344 2.001 7.229 2 11 2H15C15.7956 2 16.5587 2.31607 17.1213 2.87868C17.6839 3.44129 18 4.20435 18 5"
                    stroke="#000"
                    strokeWidth="1"
                  />
                </svg>
              </div>
            </div>

            <div className="profile-incontainer2">
              {/* 이름 */}
              <div className="input-group">
                <label className="input-group-text">이름</label>
                <div className="input-container">
                  <div className="input-incontainer">
                    <input
                      type="text"
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      className="input-field"
                      maxLength={8}
                    />
                    <span className="char-count">{name.length} / 8</span>
                  </div>
                  <div className="warning-message"></div>
                </div>
              </div>

              {/* 소개 */}
              <div className="input-group">
                <label className="input-group-text">소개</label>
                <div className="input-container">
                  <div className="input-incontainer">
                    <input
                      value={introduce}
                      onChange={(e) => setIntroduce(e.target.value)}
                      className="input-field"
                      maxLength={30}
                    />
                    <span className="char-count">{introduce.length} / 30</span>
                  </div>
                  <div className="warning-message"></div>
                </div>
              </div>

              {/* 저장 버튼 */}
              {isChanged ? (
                <button className="save-btn" onClick={handleSave}>
                  저장
                </button>
              ) : (
                <button disabled className="save-btn-before">
                  저장
                </button>
              )}
            </div>
          </div>
        </div>
        <FriendSidebar />
      </div>
    </div>
  );
}
