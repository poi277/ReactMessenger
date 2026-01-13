import React, { useState } from 'react';
import { sendPasswordResetCodeApi, verifyPasswordResetCodeApi, resetPasswordApi } from "../api/ApiService";
import { useNavigate } from "react-router-dom"
import '../Css/PasswordResetWithEmailCode.css';

export default function PasswordResetWithEmailCode() {
  const [Id, setId] = useState('');
  const [email, setEmail] = useState('');
  const [code, setCode] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [step, setStep] = useState(1); // 1: 입력, 2: 코드 인증, 3: 비밀번호 재설정
  const [message, setMessage] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [codeSent, setCodeSent] = useState(false);
  const [emailSuccess, setEmailSuccess] = useState(''); // 성공 메시지용 상태 추가
  const [codeSuccess, setCodeSuccess] = useState(''); // 인증 성공 메시지용 상태 추가
  const navigate = useNavigate();

  const [errors, setErrors] = useState({
    id: '',
    email: '',
    code: '',
    newPassword: '',
    confirmPassword: ''
  });

  const handleSendCode = async () => {
    setErrors(prev => ({ ...prev, id: '', email: '' }));
    setEmailSuccess(''); // 성공 메시지 초기화
    
    if (!Id || !email) {
      if (!Id) setErrors(prev => ({ ...prev, id: '아이디를 입력해주세요.' }));
      if (!email) setErrors(prev => ({ ...prev, email: '이메일을 입력해주세요.' }));
      return;
    }

    try {
      await sendPasswordResetCodeApi(Id, email);
      setCodeSent(true);
      setEmailSuccess("이메일로 인증 코드를 보냈습니다."); // 성공 메시지를 emailSuccess에 설정
      setMessage(''); // 기존 message 클리어
    } catch (err) {
      setErrors(prev => ({ ...prev, email: err.response?.data || "코드 전송 실패" }));
    }
  };

  const handleVerifyCode = async () => {
    setErrors(prev => ({ ...prev, code: '' }));
    setCodeSuccess(''); // 인증 성공 메시지 초기화
    
    if (!code) {
      setErrors(prev => ({ ...prev, code: '인증 코드를 입력해주세요.' }));
      return;
    }

    try {
      await verifyPasswordResetCodeApi(Id, email, code);
      setStep(3);
      setCodeSuccess("인증 성공! 비밀번호를 재설정하세요."); // 인증 성공 메시지
      setMessage(''); // 기존 message 클리어
    } catch (err) {
      setErrors(prev => ({ ...prev, code: err.response?.data || "인증 코드가 일치하지 않습니다." }));
    }
  };

  const handleResetPassword = async () => {
    setErrors(prev => ({ ...prev, newPassword: '', confirmPassword: '' }));
    
    if (!newPassword) {
      setErrors(prev => ({ ...prev, newPassword: '새 비밀번호를 입력해주세요.' }));
      return;
    }
    
    if (!confirmPassword) {
      setErrors(prev => ({ ...prev, confirmPassword: '비밀번호 확인을 입력해주세요.' }));
      return;
    }
    
    if (newPassword !== confirmPassword) {
      setErrors(prev => ({ ...prev, confirmPassword: '비밀번호가 일치하지 않습니다.' }));
      return;
    }

    try {
      await resetPasswordApi(Id, newPassword, confirmPassword);
      alert("비밀번호가 성공적으로 변경되었습니다.");
      window.close(); // 팝업 닫기
    } catch (err) {
      setErrors(prev => ({ ...prev, confirmPassword: err.response?.data || "비밀번호 변경 실패" }));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (step === 1) {
      // 아직 구현하지 않음 (단계별 진행)
    } else if (step === 2) {
      handleVerifyCode();
    } else if (step === 3) {
      handleResetPassword();
    }
  };

  return (
    <div className="find-id-wrapper">
      <div className="find-id-container">
        <div className="find-id-centercontainer">
          <h1 className="find-id-title">비밀번호 찾기</h1>
          
          <div className="find-id-input-wrapper">
            {step === 1 && (
              <form onSubmit={handleSubmit}>
                <div className="find-id-input-container">
                  
                  {/* 아이디 입력 */}
                  <div className="form-row-other">
                    <div className="find-id-form-field">아이디</div>
                    <div className="row-input-container-buttonoff">
                      <div className="input-field-buttonoff">
                        <input
                          type="text"
                          placeholder="아이디 입력"
                          value={Id}
                          onChange={(e) => {
                            setId(e.target.value);
                            if (errors.id) setErrors(prev => ({ ...prev, id: '' }));
                          }}
                          required
                          className="input-label"
                        />
                      </div>
                      
                      <div className="warningMesseage">
                        <div className="warningMesseageFont">
                          {errors.id}
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* 이메일 입력 */}
                  <div className="form-row-other">
                    <div className="find-id-form-field">이메일</div>
                    <div className="find-id-row-input-container-buttonon">
                      <div className="input-buttoncontainer">
                        <div className="input-field-buttonon">
                          <input
                            type="email"
                            placeholder="이메일 입력"
                            value={email}
                            onChange={(e) => {
                              setEmail(e.target.value);
                              if (errors.email) setErrors(prev => ({ ...prev, email: '' }));
                              if (emailSuccess) setEmailSuccess(''); // 성공 메시지 클리어
                              if (codeSent) setCodeSent(false);
                            }}
                            required
                            className="input-label"
                          />
                        </div>
                        <button
                          type="button"
                          onClick={handleSendCode}
                          className="find-id-middle-button"
                        >
                          코드 발송
                        </button>
                      </div>
                      
                      <div className="warningMesseage">
                        <div className={emailSuccess ? 'successMesseageFont' : 'warningMesseageFont'}>
                          {emailSuccess || errors.email}
                        </div>
                      </div>
                    </div>
                  </div>
                   
                  {/* 인증코드 입력 */}
                  <div className="form-row-other">
                    <div className="find-id-form-field">인증코드</div>
                    <div className="row-input-container-buttonoff">
                      <div className="input-field-buttonoff">
                        <input
                          type="text"
                          placeholder="인증코드 입력"
                          value={code}
                          onChange={(e) => {
                            setCode(e.target.value);
                            if (errors.code) setErrors(prev => ({ ...prev, code: '' }));
                            if (codeSuccess) setCodeSuccess(''); // 성공 메시지 클리어
                          }}
                          required
                          className="input-label"
                        />
                      </div>
                      
                      <div className="warningMesseage">
                        <div className={codeSuccess ? 'successMesseageFont' : 'warningMesseageFont'}>
                          {codeSuccess || errors.code}
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* 제출 버튼 */}
                  <button type="button" onClick={handleVerifyCode} className="find-id-btn">
                    인증 하기
                  </button>
                </div>
              </form>
            )}

            {step === 3 && (
              <form onSubmit={handleSubmit}>
                <div className="find-id-input-container">
                  
                  {/* 새 비밀번호 */}
                  <div className="form-row-other">
                    <div className="find-id-form-field">새 비밀번호</div>
                    <div className="row-input-container-buttonoff">
                      <div className="input-field-buttonoff">
                        <input
                          type="password"
                          placeholder="새 비밀번호 설정"
                          value={newPassword}
                          onChange={(e) => {
                            setNewPassword(e.target.value);
                            if (errors.newPassword) setErrors(prev => ({ ...prev, newPassword: '' }));
                          }}
                          required
                          className="input-label"
                        />
                      </div>
                      
                      <div className="warningMesseage">
                        <div className="warningMesseageFont">
                          {errors.newPassword}
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* 비밀번호 확인 */}
                  <div className="form-row-other">
                    <div className="find-id-form-field">비밀번호 확인</div>
                    <div className="row-input-container-buttonoff">
                      <div className="input-field-buttonoff">
                        <input
                          type="password"
                          placeholder="비밀번호 확인"
                          value={confirmPassword}
                          onChange={(e) => {
                            setConfirmPassword(e.target.value);
                            if (errors.confirmPassword) setErrors(prev => ({ ...prev, confirmPassword: '' }));
                          }}
                          required
                          className="input-label"
                        />
                      </div>
                      
                      <div className="warningMesseage">
                        <div className="warningMesseageFont">
                          {errors.confirmPassword}
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* 제출 버튼 */}
                  <button type="button" onClick={handleResetPassword} className="find-id-btn">
                    제출 하기
                  </button>
                </div>
              </form>
            )}

            {message && (
              <div className={`find-id-message ${message.includes('발송') || message.includes('성공') ? 'success' : 'error'}`}>
                {message}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}