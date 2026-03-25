package com.altankoc.beuverse_backend.student.repository;

import com.altankoc.beuverse_backend.student.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);

    Optional<Student> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<Student> findByEmailVerificationToken(String token);

    List<Student> findAllByDeletedTrue();

    Page<Student> findAllByDeletedFalse(Pageable pageable);

    Page<Student> findByUsernameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String username, String firstName, String lastName, Pageable pageable);

    void deleteByDeletedTrueAndUpdatedAtBefore(LocalDateTime threshold);
    void deleteByEmailVerifiedFalseAndCreatedAtBefore(LocalDateTime threshold);
}