import React, { useState } from 'react';
import axios from 'axios';
import {SendMailCodeApi,GetMailCodeApi} from "../api/ApiService"
import '../Css/FindIdPage.css';

export default function FindIdPage() {
  const [email, setEmail] = useState('');
  const [code, setCode] = useState('');
  const [message, setMessage] = useState('');
  const [codeSent, setCodeSent] = useState(false);
  const [foundId, setFoundId] = useState('');
  const [emailSuccess, setEmailSuccess] = useState(''); // 성공 메시지용 상태 추가

  const [errors, setErrors] = useState({
    email: '',
    code: ''
  });

  // 인증 코드 요청
  const requestVerificationCode = async () => {
    // 에러 및 성공 메시지 초기화
    setErrors(prev => ({ ...prev, email: '' }));
    setEmailSuccess('');
    
    if (!email.trim()) {
      setErrors(prev => ({ ...prev, email: '이메일을 입력해주세요.' }));
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      setErrors(prev => ({ ...prev, email: '올바른 이메일 형식을 입력해주세요.' }));
      return;
    }

    try {
      const res = await SendMailCodeApi(email);
      setEmailSuccess(res.data || '인증코드가 발송되었습니다.'); // 성공 메시지 설정
      setMessage(''); // 기존 message는 클리어
      setCodeSent(true);
    } catch (err) {
      setErrors(prev => ({ 
        ...prev, 
        email: err.response?.data || '코드 전송 실패' 
      }));
    }
  };

  // 아이디 찾기 제출
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // 에러 초기화
    setErrors({ email: '', code: '' });
    
    if (!email.trim()) {
      setErrors(prev => ({ ...prev, email: '이메일을 입력해주세요.' }));
      return;
    }
    
    if (!code.trim()) {
      setErrors(prev => ({ ...prev, code: '인증 코드를 입력해주세요.' }));
      return;
    }

    if (!codeSent) {
      setErrors(prev => ({ ...prev, email: '인증 코드를 먼저 발송해주세요.' }));
      return;
    }

    try {
      const res = await GetMailCodeApi(email, code);
      setFoundId(res.data);
      setMessage('');
      setErrors({ email: '', code: '' });
    } catch (err) {
      setErrors(prev => ({ ...prev, code: err.response?.data || '인증코드가 다릅니다.' }));
    }
  };

  const handleEmailChange = (e) => {
    setEmail(e.target.value);
    if (errors.email) {
      setErrors(prev => ({ ...prev, email: '' }));
    }
    if (emailSuccess) {
      setEmailSuccess(''); // 성공 메시지 클리어
    }
    // 이메일이 변경되면 코드 발송 상태 초기화
    if (codeSent) {
      setCodeSent(false);
    }
  };

  const handleCodeChange = (e) => {
    setCode(e.target.value);
    if (errors.code) {
      setErrors(prev => ({ ...prev, code: '' }));
    }
  };

  return (
    <div className="find-id-wrapper">
      <div className="find-id-container">
        <div className="find-id-centercontainer">
          <h1 className="find-id-title">아이디 찾기</h1>
          
          <div className="find-id-input-wrapper">
            {!foundId ? (
              <form onSubmit={handleSubmit}>
                <div className="find-id-input-container">
                  
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
                            onChange={handleEmailChange}
                            required
                            className="input-label"
                          />
                        </div>
                        <button 
                          type="button" 
                          onClick={requestVerificationCode} 
                          className="find-id-middle-button"
                        >
                          인증코드 발송
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
                          onChange={handleCodeChange}
                          required
                          className="input-label"
                        />
                      </div>
                      
                      <div className="warningMesseage">
                        <div className="warningMesseageFont">
                          {errors.code}
                        </div>
                      </div>
                    </div>
                  </div>
                {/* 제출 버튼 */}
                <button type="submit" className="find-id-btn">
                  아이디 찾기
                </button>
              </div>
              </form>
            ) : (
              <div className="result-container">
                <div className="found-id-box">  
                  <p className="result-text">당신의 아이디는</p>
                  <p className="found-id"><strong>{foundId}</strong></p>
                  <p className="result-text">입니다.</p>
                </div>
              </div>
            )}

            {message && (
              <div className={`find-id-message ${message.includes('발송') ? 'success' : 'error'}`}>
                {message}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}