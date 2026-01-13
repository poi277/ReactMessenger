import React, { useState } from 'react';
import { useNavigate } from "react-router-dom";
import { RegistersubmitApi, RegisterGetemailsubmitApi, UserRegisterApi,PaswordConfirmApi,DuplicateCheckIdApi } from "../api/ApiService";
import '../Css/UserRegister.css';
import Header from "../UI/Header";

export default function UserRegister() {
  const [email, setEmail] = useState('');
  const [code, setCode] = useState('');
  const [emailVerified, setEmailVerified] = useState(false);
  const [codeSent, setCodeSent] = useState(false);
  const [message, setMessage] = useState('');
  const navigate = useNavigate();
  const [isIdChecked, setIsIdChecked] = useState(false); // ✅ 아이디 중복 확인 여부
  const [isPasswordChecked, setIsPasswordChecked] = useState(false); // ✅ 비밀번호 확인 여부

  const [formData, setFormData] = useState({
    id: '',
    name: '',
    password: '',
    confirmPassword: ''
  });

  const [errors, setErrors] = useState({
    id: '',
    password: '',
    confirmPassword: '',
    name: '',
    email: '',
    code: ''
  });

  // 성공 메시지 상태 추가 (errors와 동일한 구조)
  const [successMessages, setSuccessMessages] = useState({
    id: '',
    password: '',
    confirmPassword: '',
    name: '',
    email: '',
    code: ''
  });

  const handleDuplicateCheckId = async () => {
    if (!formData.id.trim()) {
      setErrors(prev => ({
        ...prev,
        id: '아이디를 입력해주세요.'
      }));
      return;
    }

    try {
      const response = await DuplicateCheckIdApi(formData.id);
      if (response.status === 202) { // ✅ HttpStatus.ACCEPTED
        // alert(response.data); // alert 제거
        setErrors(prev => ({ ...prev, id: '' }));
        setSuccessMessages(prev => ({ ...prev, id: response.data })); // 성공 메시지 표시
        setIsIdChecked(true); // 성공 시 true
      }
    } catch (err) {
      setIsIdChecked(false); // 실패하면 false
      setSuccessMessages(prev => ({ ...prev, id: '' })); // 성공 메시지 초기화
      setErrors(prev => ({
        ...prev,
        id: err.response?.data || '아이디가 중복됩니다.'
      }));
    }
  };

  const handlePasswordCheck = async () => {
    if (!formData.password || !formData.confirmPassword) {
      setErrors(prev => ({
        ...prev,
        confirmPassword: '비밀번호와 비밀번호 확인을 입력해주세요.'
      }));
      return;
    }

    try {
      // 서버에 비밀번호 확인 요청
      const response = await PaswordConfirmApi(formData.password, formData.confirmPassword);
      if (response.status === 202) { // ✅ HttpStatus.ACCEPTED
        // alert(response.data); // alert 제거
        setErrors(prev => ({ ...prev, confirmPassword: '' }));
        setSuccessMessages(prev => ({ ...prev, confirmPassword: response.data })); // 성공 메시지 표시
        setIsPasswordChecked(true); // 성공 시 true
      }
    } catch (err) {
      setIsPasswordChecked(false); // 실패 시 false
      setSuccessMessages(prev => ({ ...prev, confirmPassword: '' })); // 성공 메시지 초기화
      // 서버에서 비밀번호 불일치 반환 시
      setErrors(prev => ({
        ...prev,
        confirmPassword: err.response?.data || '비밀번호가 일치하지 않습니다.'
      }));
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));

    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }

    // 입력값이 변경되면 해당 성공 메시지와 확인 상태 초기화
    if (name === 'id') {
      setSuccessMessages(prev => ({ ...prev, id: '' }));
      setIsIdChecked(false);
    }
    if (name === 'password' || name === 'confirmPassword') {
      setSuccessMessages(prev => ({ ...prev, confirmPassword: '' }));
      setIsPasswordChecked(false);
    }
  };

  // 인증 코드 전송
  const sendCode = async () => {
    if (!email) {
      setErrors(prev => ({ ...prev, email: '이메일을 입력해주세요.' }));
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      setErrors(prev => ({ ...prev, email: '올바른 이메일 형식을 입력해주세요.' }));
      return;
    }

    try {
      await RegistersubmitApi(email); // 실제 API 호출
      setErrors(prev => ({ ...prev, email: '' }));
      setCodeSent(true);
      setMessage('인증 코드가 이메일로 전송되었습니다.');
    } catch (err) {
      setErrors(prev => ({ ...prev, email: err.response?.data || '인증 코드 전송 실패' }));
    }
  };

  // 인증 코드 확인
  const verifyCode = async () => {
    if (!code) {
      setErrors(prev => ({ ...prev, code: '인증 코드를 입력해주세요.' }));
      return;
    }

    try {
      await RegisterGetemailsubmitApi(email, code); // 실제 API 호출
      setErrors(prev => ({ ...prev, code: '' }));
      setEmailVerified(true);
      setMessage('이메일 인증 성공! 회원가입을 진행해주세요.');
    } catch (err) {
      setErrors(prev => ({ ...prev, code: err.response?.data || '인증 코드가 올바르지 않습니다.' }));
    }
  };

  // 회원가입
  const handleSubmit = async (e) => {
    e.preventDefault();

    setErrors({ id: '', password: '', confirmPassword: '', name: '', email: '', code: '' });

    let hasError = false;

    // 기본 값 체크
    if (!formData.id.trim()) { setErrors(prev => ({ ...prev, id: '아이디를 입력해주세요.' })); hasError = true; }
    if (!formData.password.trim()) { setErrors(prev => ({ ...prev, password: '비밀번호를 입력해주세요.' })); hasError = true; }
    if (!formData.confirmPassword.trim()) { setErrors(prev => ({ ...prev, confirmPassword: '비밀번호 확인을 입력해주세요.' })); hasError = true; }
    else if (formData.password !== formData.confirmPassword) { setErrors(prev => ({ ...prev, confirmPassword: '비밀번호가 일치하지 않습니다.' })); hasError = true; }
    if (!formData.name.trim()) { setErrors(prev => ({ ...prev, name: '닉네임을 입력해주세요.' })); hasError = true; }
    if (!emailVerified) { setErrors(prev => ({ ...prev, email: '이메일 인증을 먼저 완료해주세요.' })); hasError = true; }

    // ✅ 추가 체크 (중복 확인, 비밀번호 확인)
    if (!isIdChecked) {
      setErrors(prev => ({ ...prev, id: '아이디 중복 확인을 해주세요.' }));
      hasError = true;
    }
    if (!isPasswordChecked) {
      setErrors(prev => ({ ...prev, confirmPassword: '비밀번호 확인을 해주세요.' }));
      hasError = true;
    }

    if (hasError) return;

    try {
      await UserRegisterApi(formData, email); 
      alert('회원가입 성공! 로그인 페이지로 이동합니다.');
      navigate('/');
    } catch (err) {
      setMessage(err.response?.data || '회원가입에 실패했습니다.');
    }
  };

  return (
    <div>
    <div className="register-wrapper">
      <Header />
    <div className="register-container">
      <h1 className="register-title">Messenger</h1>

      <div className="register-input-wrapper">
        <div className="register-input-container">

        {/* 아이디 */}
        <div className="register-form-row-first"> 
          <div className="register-form-field">아이디</div>
          <div className="register-row-input-container-buttonon">
            <div className="input-buttoncontainer">
              <div className="input-field-buttonon">
                <input
                  name="id"
                  type="text"
                  placeholder="아이디"
                  value={formData.id}
                  onChange={handleChange}
                  required
                  className={"input-label"}
                />
              </div>
              <button
                type="button"
                onClick={handleDuplicateCheckId}
                className="register-middle-button"
              >
                중복 확인
              </button>
            </div>

          <div className={"warningMesseage"}>
            <div className={errors.id ? "warningMesseageFont" : "successMesseageFont"}>
              {errors.id || successMessages.id}
            </div>
          </div>
          </div>
        </div>

        {/* 비밀번호 */}
        <div className="register-form-row-other">
            <div className="register-form-field">
                비밀번호
            </div>
             <div className="register-row-input-container-buttonoff">
              <div className="register-input-field-buttonoff">
              <input
                name="password"
                type="password"
                placeholder="비밀번호"
                value={formData.password}
                onChange={handleChange}
                required
                className={"input-label"}
              />
              </div>
               <div className={"warningMesseage"}>
              <div className={"warningMesseageFont"}>
              {errors.password}
              </div>
            </div>
        </div>
       </div>
        {/* 비밀번호 확인 */}
      <div className="register-form-row-other">
        <div className="register-form-field">비밀번호 확인</div>
        <div className="register-row-input-container-buttonon">
          <div className="input-buttoncontainer">
            <div className="input-field-buttonon">
              <input
                name="confirmPassword"
                type="password"
                placeholder="비밀번호 확인"
                value={formData.confirmPassword}
                onChange={handleChange}
                required
                className={"input-label"}
              />
            </div>
            <button
              type="button"
              className="register-middle-button"
              onClick={handlePasswordCheck}
            >
              비밀번호 확인
            </button>
          </div>

          <div className={"warningMesseage"}>
            <div className={errors.confirmPassword ? "warningMesseageFont" : "successMesseageFont"}>
              {errors.confirmPassword || successMessages.confirmPassword}
            </div>
          </div>
        </div>
      </div>


        {/* 닉네임 */}
        <div className="register-form-row-other">
          <div className="register-form-field">닉네임</div>
          <div className="register-row-input-container-buttonoff">
            <div className="register-input-field-buttonoff">
              <input
                name="name"
                type="text"
                placeholder="닉네임"
                value={formData.name}
                onChange={handleChange}
                required
                className={"input-label"}
              />
            </div>
            <div className={"warningMesseage"}>
              <div className={"warningMesseageFont"}>
                {errors.name}
              </div>
            </div>
          </div>
        </div>


        {/* 이메일 */}
      <div className="register-form-row-other">
        <div className="register-form-field">이메일</div>
        <div className="register-row-input-container-buttonon">
          <div className="input-buttoncontainer">
            <div className="input-field-buttonon">
              <input
                type="email"
                name="email"
                placeholder="이메일"
                value={email}
                onChange={(e) => {
                  setEmail(e.target.value);
                  if (emailVerified) setEmailVerified(false);
                  if (errors.email) setErrors(prev => ({ ...prev, email: '' }));
                }}
                required
                disabled={emailVerified}
                className={"input-label"}
              />
            </div>
            <button
              type="button"
              onClick={sendCode}
              disabled={emailVerified}
              className="register-middle-button"
            >
              인증코드 발송
            </button>
          </div>

          <div className={"warningMesseage"}>
            <div className={"warningMesseageFont"}>
              {errors.email}
            </div>
          </div>
        </div>
      </div>

        {/* 인증 코드 */}
          <div className="register-form-row-other">
            <div className="register-form-field">인증코드</div>
            <div className="register-row-input-container-buttonon">
              <div className="input-buttoncontainer">
                <div className="input-field-buttonon">
                <input
                  type="text"
                  name="code"
                  placeholder="인증코드"
                  value={code}
                  onChange={(e) => {
                    setCode(e.target.value);
                    if (errors.code) setErrors(prev => ({ ...prev, code: '' }));
                  }}
                  required
                  className={"input-label"}
                  />
                 </div>
            <button type="button" onClick={verifyCode} className="register-middle-button">
                    인증
            </button> 
              </div>
              
              <div className={"warningMesseage"}>
                <div className={"warningMesseageFont"}>
                  {errors.code}
                </div>
              </div>
            </div>
          </div>
                  
        {emailVerified && (
          <p className="success-text">이메일 인증이 완료되었습니다.</p>
        )}
        </div>
          <button type="submit" onClick={handleSubmit} className="register-btn">
            회원가입
          </button>
      </div>
      {message && (
        <div className={`register-message ${message.includes('성공') ? 'success' : 'error'}`}>
          {message}
        </div>
      )}
    </div>
    </div>
    </div>
  );
}