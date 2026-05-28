package com.isptec.economiahistoriaapi.repository;

import com.isptec.economiahistoriaapi.model.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, String> {

    @Query("SELECT pl FROM PostLike pl WHERE pl.post.postId = :postId AND pl.user.userId = :userId")
    Optional<PostLike> findByPostIdAndUserId(@Param("postId") String postId, @Param("userId") String userId);

    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.postId = :postId")
    Long countByPostId(@Param("postId") String postId);

    @Query("SELECT CASE WHEN COUNT(pl) > 0 THEN true ELSE false END FROM PostLike pl WHERE pl.post.postId = :postId AND pl.user.userId = :userId")
    boolean existsByPostIdAndUserId(@Param("postId") String postId, @Param("userId") String userId);
}
