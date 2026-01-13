import axios from 'axios';
import { GuestApiClient,UserApiClient} from './ApiClient'
const BASE_URL = process.env.REACT_APP_BASE_URL;
// REACT_APP_BASE_URL=http://localhost:5000
//Messenger-env.eba-bedupuxw.ap-southeast-2.elasticbeanstalk.com 

export const RegistersubmitApi = (email) =>GuestApiClient.post('/register/send-verification-code', { email });
  export const RegisterGetemailsubmitApi = (email, code)=> GuestApiClient.post('/register/verify-code', { email, code });
  export const UserRegisterApi = (formData, email) =>
    GuestApiClient.post('/register', {
      ...formData,
      email,  // email은 별도로 꺼내서 DTO에 포함
    });
export const DuplicateCheckIdApi = (id) =>  GuestApiClient.post('/register/id',{id})
export const PaswordConfirmApi = (password,confirmPassword) =>  GuestApiClient.post('/register/password', {password,confirmPassword});


    export const SendMailCodeApi = (email) =>  GuestApiClient.post('/find-id/send-code', { email });
    export const GetMailCodeApi= (email, code) => GuestApiClient.post('/find-id/verify-code', { email, code });

// 인증 코드 요청 (아이디, 이메일)
export const sendPasswordResetCodeApi = (id, email) => GuestApiClient.post('/password/code', {id,email});
// 인증 코드 검증
export const verifyPasswordResetCodeApi = (id, email, code) => GuestApiClient.post('/password/verify', {id,email,code});
// 비밀번호 재설정
export const resetPasswordApi = (id, newPassword,confirmPassword) =>  GuestApiClient.post('/password/change', {id,newPassword,confirmPassword});


// 로그인 요청
export const LoginApi = async (id, password) => {
  const response = await GuestApiClient.post('/userlogin', { id, password });
  return response.data;
};
// 로그인 사용자 세션체크
export const SessioncheckApi = () => {
  return GuestApiClient.get('/sessioncheck');
};
// 로그아웃 요청
export const LogoutApi = async () => {
  const response = await UserApiClient.post('/userlogout');
  return response.data;
};

export const searchUsersApi = async (name) => {
  const res = await GuestApiClient.get(`/searchName?name=${encodeURIComponent(name)}`);
  return res.data; // UserNameDTO 리스트
};

export const UserprofileApi = (uuid) => {
  return GuestApiClient.get(`/profile/${uuid}`);
};



export const MyinfoApi = (uuid) => {
  return UserApiClient.get(`/myinfo/${uuid}`);
};
export const UpdateMyinfoApi = (uuid, userInfoDTO) => {
  return UserApiClient.put(`/myinfo/${uuid}`, userInfoDTO);
};

export const MyProfilePhotoApi = async (uuid,formData) => {
  const response = await UserApiClient.post(`/photo/myinfo/${uuid}`, formData, {
    headers: { "Content-Type": "multipart/form-data" }
  });

  return response.data; // 업로드된 사진 URL
};

export const writePostApi = async ({ title, context, userUuid,postRange }) => {
  const response = await UserApiClient.post('/post/write', { title, context, userUuid,postRange }, {
    headers: { "Content-Type": "application/json" }
  });
  return response.data;
};

export const PosteditApi = async ({ postid, title, context,postRange }) => {
  const response = await UserApiClient.put(`/post/${postid}`, 
    { title, context,postRange }, 
    { headers: { "Content-Type": "application/json" } }
  );
  return response.data;
};
export const PhotoPostApi = async (postid, formData) => {
  const response = await UserApiClient.post(`/photo/${postid}`, formData, {
    headers: { "Content-Type": "multipart/form-data" }
  });
  return response.data;
};
export const DeletePhotoApi = async (postId, photoId) => {
  const response = await UserApiClient.delete(`/photo/${postId}/${photoId}`);
  return response.data;
};



export const PostDeleteApi = (postid) => {
  return UserApiClient.delete(`/post/${postid}`);
};

export const AllPostListApi = () => {
  return GuestApiClient.get('/post/allpostlist');
};
export const UserPostListApi = (uuid) => {
  return GuestApiClient.get(`/post/userpostlist/${uuid}`);
};

export const MyphotoApi = (uuid) => {
  return GuestApiClient.get(`/post/userphotolist/${uuid}`);
};
export const MylikepostApi = (uuid) => {
  return GuestApiClient.get(`/post/userlikepost/${uuid}`);
};


export const UserPostApi = (postid) => {
  return GuestApiClient.get(`/post/${postid}`);
};


// 특정 게시글의 댓글 목록 가져오기
export const GetCommentsByPostApi = (postid) => {
  return GuestApiClient.get(`/comment/${postid}`);
};
// 댓글 작성 (content, parentId 포함)
export const WriteCommentApi = (postid, content, parentId = null) => {
  return UserApiClient.post(`/comment/${postid}`, {
    postId: postid,    // ← 반드시 추가
    content,
    parentId,
  });
};
// 댓글 수정
export const UpdateCommentApi = (commentId, content) => {
  return UserApiClient.put(`/comment/${commentId}`, {
    content
  });
};
// 댓글 삭제 (content를 null로 변경)
export const DeleteCommentApi = (commentId) => {
  return UserApiClient.delete(`/comment/${commentId}`);
};
export const toggleLikeApi = (postid) => {
  return GuestApiClient.post(`/post/${postid}/like`);
}

export const friendListApi = () => {
  return UserApiClient.get("/friend/list");
};
export const friendrequestListApi = () => {
  return UserApiClient.get("/friend/requestslist");
};
export const acceptFriendRequestApi = (requestId) => {  
  return UserApiClient.post(`/friend/accept/${requestId}`);
};
export const rejectFriendRequestApi = (requestId) => {
  return UserApiClient.post(`/friend/reject/${requestId}`);
};
export const deleteFriendApi = (friendUuid) => {
  return UserApiClient.delete(`/friend/remove/${friendUuid}`);
};

export const sendFriendRequestApi = async (receiveruuid) => {
  const response = await UserApiClient.post(`/friend/sendrequest/${receiveruuid}`, null, {
    headers: { "Content-Type": "application/json" }
  });
  return response.data;
};


//채팅


export const GetOnetoOneChatApi = async (senderuuid, receiveruuid) => {
  const response = await UserApiClient.get(`/chat/one-to-one`, {
    params: { senderuuid, receiveruuid },
    headers: { "Content-Type": "application/json" }
  });
  return response.data;
};


//
export const PostOnetoOneChatApi = async (senderuuid, receiveruuid,content) => {
  const response = await UserApiClient.post(`/chat/one-to-one`, {senderuuid, receiveruuid,content},
    {
      headers: { "Content-Type": "application/json" }
    } );
  return response.data;
};

export const GetReceiverApi = (receiveruuid) => {
  return UserApiClient.get(`chat/info/${receiveruuid}`) 
  };