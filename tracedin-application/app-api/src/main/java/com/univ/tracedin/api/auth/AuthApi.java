package com.univ.tracedin.api.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.api.auth.dto.LoginRequest;
import com.univ.tracedin.api.auth.dto.SignUpRequest;
import com.univ.tracedin.api.global.dto.Response;
import com.univ.tracedin.api.global.util.TokenUtils;
import com.univ.tracedin.api.user.dto.UserResponse;
import com.univ.tracedin.domain.auth.AuthService;
import com.univ.tracedin.domain.auth.Tokens;
import com.univ.tracedin.domain.user.User;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class AuthApi implements AuthApiDocs {

    private final AuthService authService;

    @Override
    @PostMapping("/sign-up")
    public Response<UserResponse> signup(@RequestBody SignUpRequest request) {
        final User signUpUser = authService.signUp(request.toUserProfile(), request.password());
        return Response.success(UserResponse.from(signUpUser));
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest request) {
        final Tokens tokens = authService.login(request.toLoginInfo());
        final HttpHeaders tokenHeaders = TokenUtils.createTokenHeaders(tokens);
        return ResponseEntity.ok().headers(tokenHeaders).build();
    }
}
