package com.altankoc.beuverse_backend.post.repository;

import com.altankoc.beuverse_backend.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    void deleteByPostId(Long postId);
}
