import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Header from "../UI/Header";
import Profile  from "../UI/Profile";
import FriendSidebar from "../UI/FriendSidebar";
import { UserPostListApi, MyphotoApi, MylikepostApi, toggleLikeApi } from "../api/ApiService";
import { FaHeart, FaRegHeart } from "react-icons/fa";
import { useUserAuth } from "../api/AuthProvider";
import "../Css/UserMyMessenger.css";

export default function UserMyMessenger() {
  const { uuid } = useParams();
  const navigate = useNavigate();
  const [posts, setPosts] = useState([]);
  const [filter, setFilter] = useState("myPosts");
  const { isLogin } = useUserAuth();

  useEffect(() => {
    if (!uuid) return;
    const fetchData = async () => {
      try {
        let res;
        if (filter === "myPosts") res = await UserPostListApi(uuid);
        else if (filter === "myPhotos") res = await MyphotoApi(uuid);
        else if (filter === "likedPosts") res = await MylikepostApi(uuid);

        const postsWithLiked = res.data.map(post => ({
          ...post,
          liked: post.likes,
          currentPhotoIndex: 0
        }));
        setPosts(postsWithLiked);
      } catch (err) {
        console.error("데이터 불러오기 실패:", err);
      }
    };
    fetchData();
  }, [uuid, filter]);

    const changePhotoIndex = (postId, index) => {
    setPosts(posts =>
      posts.map(post =>
        post.id === postId
          ? { 
              ...post, 
              currentPhotoIndex: (post.currentPhotoIndex + index + post.photos.length) % post.photos.length 
            }
          : post
      )
    );
  };


  const toggleLike = async (postId) => {
    if (!isLogin) return alert("로그인 후 이용 가능합니다.");
    setPosts(prev =>
      prev.map(p =>
        p.id === postId
          ? { ...p, liked: !p.liked, likeCount: p.liked ? p.likeCount - 1 : p.likeCount + 1 }
          : p
      )
    );
    try {
      await toggleLikeApi(postId);
    } catch (err) {
      console.error("좋아요 토글 실패:", err);
      setPosts(prev =>
        prev.map(p =>
          p.id === postId
            ? { ...p, liked: !p.liked, likeCount: p.liked ? p.likeCount - 1 : p.likeCount + 1 }
            : p
        )
      );
    }
  };

  const handlePostClick = (postId) => {
    navigate(`/messenger/${uuid}/${postId}`);
  };

  return (
    <div>
      <Header />
      <div className="feed-wrapper">
             <Profile uuid={uuid} />
        <div>
          <div className="filter-buttons">
            <button 
              className={`filter-buttonselect ${filter === "myPosts" ? "active" : ""}`} 
              onClick={() => setFilter("myPosts")}
            >
              내 게시물
            </button>
            <div className="filter-buttongap">|</div>
            <button 
              className={`filter-buttonselect ${filter === "myPhotos" ? "active" : ""}`} 
              onClick={() => setFilter("myPhotos")}
            >
              내 사진
            </button>
            <div className="filter-buttongap">|</div>
            <button 
              className={`filter-buttonselect ${filter === "likedPosts" ? "active" : ""}`} 
              onClick={() => setFilter("likedPosts")}
            >
              좋아요한 게시물
            </button>
          </div>
        <div className="feed-container">
          {/* 필터 버튼 */}
            {filter === "myPhotos" ? (
              <div className="photo-grid">
                {posts.length === 0 ? (
                  <p style={{ gridColumn: "1 / -1", textAlign: "center", color: "#8e8e8e", padding: "40px 0" }}>
                    사진이 없습니다.
                  </p>
                ) : (
                  posts.map(photo => (
                    <img
                      key={photo.id}
                      src={photo.photoUrl}
                      alt={`photo-${photo.id}`}
                      className="photo-item"
                      onClick={() => handlePostClick(photo.postId)}
                    />
                  ))
                )}
              </div>
            ) : (
              posts.length === 0 ? (
                <p style={{ textAlign: "center", color: "#8e8e8e", padding: "40px 0" }}>게시물이 없습니다.</p>
              ) : (
                posts.map(post => (
                  <div key={post.id} className="post-card" onClick={() => handlePostClick(post.id)}>
                    {/* 헤더 */}
                    <div className="post-header">
                      <div className="profile-pic">
                        <img
                          src={post.profilePhotoUrl || "/default-profile.png"}
                          alt={post.name}
                          style={{ width: 32, height: 32, borderRadius: "50%" }}
                        />
                      </div>
                      <div className="post-user-info">
                        <span className="username">{post.name}</span>
                        <span className="post-time">{new Date(post.createdDate).toLocaleString()}</span>
                      </div>
                    </div>

                    {/* 본문 */}
                    <div className="post-content">{post.context}</div>
                      {post.photos?.length > 0 && (
                        <div className="post-image-wrapper">
                          <img src={post.photos[post.currentPhotoIndex].photoUrl} alt=""
                            className="post-image-single"
                          />
                          {post.photos.length > 1 && (
                            <>
                              <button
                                className="photo-nav-btn left"
                                onClick={e => { e.stopPropagation(); changePhotoIndex(post.id, -1); }}
                              >
                                ◀
                              </button>
                              <button
                                className="photo-nav-btn right"
                                onClick={e => { e.stopPropagation(); changePhotoIndex(post.id, 1); }}
                              >
                                ▶
                              </button>
                              <div className="photo-indicators">
                                {post.photos.map((_, idx) => (
                                  <span
                                    key={idx}
                                    className={`indicator-dot ${idx === post.currentPhotoIndex ? "active" : ""}`}
                                  ></span>
                                ))}
                              </div>
                            </>
                          )}
                        </div>
                      )}


                    {/* 좋아요 + 댓글 */}
                    <div className="post-actions">
                      <div className="post-actions-left">
                        <button
                          className="action-btn"
                          onClick={e => { e.stopPropagation(); toggleLike(post.id); }}
                        >
                          {post.liked ? <FaHeart color="red" /> : <FaRegHeart color="gray" />}
                        </button>
                        <span className="likes-count">좋아요 {post.likeCount}개</span>
                      </div>
                      <div className="post-actions-right">
                        <span className="comment-count">댓글 {post.commentCount ?? 0}개</span>
                      </div>
                    </div>
                  </div>
                ))
              )
            )}
        </div>
      </div>
          <FriendSidebar />
      </div>
    </div>
  );
}
