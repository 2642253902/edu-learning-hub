package com.exampe.auth.service;

import com.exampe.auth.entity.user.AccountUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface AuthorizeService extends UserDetailsService {

    String sendValidateEmail(String email, String sessionId, boolean hasAccount);

    String validateAndRegister(String username, String password, String email, String code, String sessionId);

    String validateOnly(String email, String code, String sessionId);

    boolean resetPassword(String email, String newPassword);

    List<AccountUser> getTeachers();

}
