package com.fdx.backend.domain.user;

import com.fdx.backend.domain.wig.Wig;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User (사용자) 엔티티
 * 회원 정보 및 인증 관리
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 이메일 (로그인 ID로 사용)
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * 비밀번호 (BCrypt 암호화 저장)
     */
    @Column(nullable = false)
    private String password;

    /**
     * 사용자 이름
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 사용자 역할 (USER, ADMIN)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    /**
     * 사용자의 WIG 목록 (1:N)
     * 한 사용자는 최대 2개의 WIG를 가질 수 있음
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Wig> wigs = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 편의 메서드: WIG 추가
     */
    public void addWig(Wig wig) {
        wigs.add(wig);
        wig.setUser(this);
    }

    /**
     * 편의 메서드: WIG 제거
     */
    public void removeWig(Wig wig) {
        wigs.remove(wig);
        wig.setUser(null);
    }

}
