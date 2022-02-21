package com.study.post.repository;

import java.util.List;
import java.util.Optional;

import com.study.post.domain.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>{

    @Query("select p from Post p join p.user on p.user.id = :userId")
    List<Post> getPostsByUser(@Param("userId") Long userId);

    @Query("select p from Post p join p.user on p.user.id = :userId and p.id = :postId")
    Optional<Post> getPostByUser(@Param("userId") Long userId, @Param("postId")Long postId);
}
