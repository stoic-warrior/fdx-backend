package com.fdx.backend.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 관련 비즈니스 로직 Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 평문 비밀번호 → 해시
    private final JwtTokenProvider jwtTokenProvider; // JWT 생성/검증 담당 (직접 구현한 클래스)
    private final AuthenticationManager authenticationManager; // Spring Security의 핵심, 아이디/비밀번호가 맞는지 실제 검증하는 놈

    /**
     * 회원가입
     */
    @Transactional
    public UserResponse signup(SignupRequest request) {
        log.info("회원가입 시도: email={}", request.getEmail());

        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + request.getEmail());
        }

        // 비밀번호 암호화 후 저장
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // 비밀번호 + salt ㅡ> 해싱
                .name(request.getName())
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("회원가입 완료: id={}, email={}", savedUser.getId(), savedUser.getEmail());

        return UserResponse.from(savedUser);
    }

    /**
     * 로그인
     */
    public TokenResponse login(LoginRequest request) {
        log.info("로그인 시도: email={}", request.getEmail());

        // 인증 수행
        authenticationManager.authenticate( // 검증 공장. Spring Security가 인증 요청서를 가지고 검증, 틀리면 예외던짐
                new UsernamePasswordAuthenticationToken( // 인증 요청서
                        request.getEmail(), request.getPassword())
        );

        // 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // JWT 토큰 생성, '로그인 성공 증명서 발급'
        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());

        log.info("로그인 성공: email={}", request.getEmail());

        return TokenResponse.of(token, user.getId(), user.getEmail(), user.getName());
    }


    /**
     * 현재 로그인한 사용자 조회
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // 인증정보 꺼내기

        if (authentication == null || !authentication.isAuthenticated()) { // 인증 객체가 없거나, 인증되지 않은 상태
            throw new IllegalStateException("인증 정보가 없습니다");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));
    }

    /**
     * 현재 로그인한 사용자 정보 조회
     */
    public UserResponse getCurrentUserInfo() {
        User user = getCurrentUser();
        return UserResponse.from(user);
    }

}
