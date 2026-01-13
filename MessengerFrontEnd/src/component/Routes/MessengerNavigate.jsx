import { BrowserRouter, Route, Routes } from 'react-router-dom';
import LoginPage from '../Guest/LoginPage';
import AuthProvider from '../api/AuthProvider';
import UserHomePage from "../User/UserHomePage"
import UserMyMessenger from "../User/UserMyMessenger"
import UserMyInfomation from "../User/UserMyInfomation"
import UserMessageWrite from "../User/UserMessageWrite"
import UserMyMessengerDetail from "../User/UserMyMessengerDetail"
import UserMyMessengerEdit from "../User/UserMyMessengerEdit"
import UserRegister from "../User/UserRegister"
import UserChating from "../User/UserChating"

import FindIdPage from "../FindAccountPage/FindIdPage"
import PasswordResetWithEmailCode from "../FindAccountPage/PasswordResetWithEmailCode"


function Navigate() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/" element={<UserHomePage />} />
          <Route path="/messenger/:uuid/info" element={<UserMyInfomation />} />
          <Route path="/messenger/:uuid" element={<UserMyMessenger />} />
          <Route path="/messenger/:uuid/write" element={<UserMessageWrite />} />
          <Route path="/messenger/:uuid/:postid" element={<UserMyMessengerDetail />} />
          <Route path="/messenger/:uuid/edit/:postid" element={<UserMyMessengerEdit />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/login/register" element={<UserRegister />} />
          <Route path="/find/id" element={<FindIdPage />} />
          <Route path="/find/password" element={<PasswordResetWithEmailCode />} />

          <Route path="/chating/:uuid/:frienduuid" element={<UserChating />} />

        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}


export default Navigate;
