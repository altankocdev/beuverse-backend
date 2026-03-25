package com.altankoc.beuverse_backend.core.security;

import com.altankoc.beuverse_backend.core.exception.UnauthorizedException;
import com.altankoc.beuverse_backend.student.entity.Student;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {}

    public static Student getCurrentStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Giriş yapmanız gerekmektedir!");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof Student student)) {
            throw new UnauthorizedException("Giriş yapmanız gerekmektedir!");
        }

        return student;
    }

    public static Long getCurrentStudentId() {
        return getCurrentStudent().getId();
    }
}