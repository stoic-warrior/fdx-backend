package com.fdx.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Milestone 생성/수정 요청 DTO
 */
@Data // 엔티티랑 다르게 DTO는 단방향이라 @Data써도 됨
@NoArgsConstructor // json 역직렬화용. 리플렉션 활용
@AllArgsConstructor // 테스트용
@Builder // 테스트용
public class MilestoneRequest {

    @NotBlank(message = "마일스톤 이름은 필수입니다") // notblank = string에서 null말고 ""같은 공백까지 불허용할때 사용
    private String name;

    private Boolean completed;

    @NotNull(message = "순서는 필수입니다")
    @Positive(message = "순서는 1이상이어야 합니다")
    private Integer orderIndex;

    @NotNull(message = "WIG ID는 필수입니다") // wig ID로 레포에서 wig엔티티꺼내서 마일스톤에 연결
    private Long wigId;
}
