import { useEffect, useState } from "react";  // useState 추가
import "../Css/Profile.css";
import { UserprofileApi } from "../api/ApiService";

export default function Profile({ uuid }) {
  const [user, setUser] = useState(null); // 프로필 상태 저장

  useEffect(() => {
    if (!uuid) return;

    UserprofileApi(uuid)
      .then((res) => {
        setUser(res.data); // 백엔드에서 내려준 ProfileDTO 저장
      })
      .catch((err) => {
        console.error("프로필 불러오기 실패:", err);
      });
  }, [uuid]); // uuid가 바뀔 때마다 실행

  return (
    <div className="profilesidebar">
      {/* 프로필 사진 */}
      <div className="profile-photo">
        <img
          src={user?.photoURL || "/default-profile.png"}
          alt={user?.name || "프로필"}
          style={{
            width: "100%",
            height: "100%",
            borderRadius: "50%",
            objectFit: "cover"
          }}
        />
      </div>

      {/* 프로필 정보 컨테이너 */}
      <div className="profile-container">
        {/* 사용자 이름 */}
        <div className="profile-container-name">
          {user?.name || "사용자"}
        </div>

        {/* 게시물 수 */}
        <div className="profile-container-post">
          게시물 {user?.postCount ?? 0}
        </div>

        {/* 자기소개 텍스트 */}
        <div className="profile-container-introduction">
          {user?.introduce || "소개글이 없습니다."}
        </div>
      </div>
    </div>
  );
}
