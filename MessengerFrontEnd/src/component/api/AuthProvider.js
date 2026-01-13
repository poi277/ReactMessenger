import React, { createContext, useContext, useState, useEffect } from "react";
import { LogoutApi, SessioncheckApi, LoginApi } from "../api/ApiService";
import { useNavigate } from "react-router-dom";

const UserAuthContext = createContext();
export const useUserAuth  = () => useContext(UserAuthContext);

export function AuthProvider({ children }) {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isLogin, setIsLogin] = useState(false);

   useEffect(() => {
      if (!isLogin) return; // 로그인 안 되어있으면 실행 안 함

      const interval = setInterval(() => {
        SessioncheckApi()
          .then(res => {
              setUser(res.data); // 이거 추가
            })
          .catch(err => {
            if (err.response?.status === 403) {
              alert("로그인 세션이 만료되었습니다. 다시 로그인해주세요.");
              logout()
              // 또는 navigate("/login");
            }
          });
      }, 5000); // 5초마다 체크

      return () => clearInterval(interval); // 컴포넌트 언마운트 시 정리
    }, [isLogin]);




  // 로그인
  const login = async (e, id, password) => {
    e.preventDefault();
    try {
      const response = await LoginApi(id, password);
      setIsLogin(true);
      console.log("로그인 성공:", response);
      await SessonCheck();
      navigate("/");
    } catch (error) {
      console.error("로그인 실패:", error);
    }
  };


    //  const logout  = () => {
    //   // 클라이언트 상태 초기화
    //   setUser(null);
    //   setIsLogin(false);
    //   navigate("/");
    //   // 서버 로그아웃 + SPA 홈으로 리다이렉트
    //   window.location.href = "http://localhost:5000/userlogout";
    // };

    const logout = async () => {
      try {
        const res = await LogoutApi();
        console.log("로그아웃 성공", res);
        setUser(null);
        setIsLogin(false);
        navigate("/");
        window.location.reload(); 
      } catch (error) {
        console.error("로그아웃 실패", error);
      }
    };
    

  // 프로필 불러오기
  const SessonCheck = async () => {
    try {
      const response = await SessioncheckApi();
      setUser(response.data);
      setIsLogin(true);
    } catch (err) {
      setError("비 로그인 상태");
      setIsLogin(false);
    } finally {
      setLoading(false);
    }
  };

  // 페이지 로드 시 자동 로그인 체크
  useEffect(() => {
    SessonCheck();
  }, []);

  return (
    <UserAuthContext.Provider value={{ login, user, loading, error, logout, isLogin }}>
      {children}
    </UserAuthContext.Provider>
  );
}

export default AuthProvider;
