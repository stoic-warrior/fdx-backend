package com.fdx.backend.dto;

import com.fdx.backend.domain.GoalDirection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lead Measure 생성/수정 요청 DTO
 */
@Data // @Getter @Setter
@NoArgsConstructor // JSON 역직렬화에 필요. HTTP로 전달받은 json을 자바 인스턴스의 필드로 주입. 잭슨이 리플렉션을 활용
@AllArgsConstructor // 테스트용
@Builder
public class LeadMeasureRequest {

    @NotBlank(message = "선행지표 이름은 필수입니다")
    private String name;

    @NotNull(message = "일일 목표는 필수입니다")
    @Positive(message = "일일 목표는 0보다 커야 합니다")
    private Double dailyTarget;

    @NotNull(message = "주간 목표는 필수입니다")
    @Positive(message = "주간 목표는 0보다 커야 합니다")
    private Double weeklyTarget;

    @NotBlank(message = "단위는 필수입니다")
    private String unit;

    /**
     * 목표 방향 (기본값: MAXIMIZE)
     * MAXIMIZE: 높을수록 좋음
     * MINIMIZE: 낮을수록 좋음
     */
    private GoalDirection goalDirection = GoalDirection.MAXIMIZE;

    @NotNull(message = "WIG ID는 필수입니다")
    private Long wigId;
}
