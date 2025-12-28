package com.fdx.backend.dto;

import com.fdx.backend.domain.commitment.Commitment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Commitment 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommitmentResponse {

    private Long id;
    private String text;
    private String week;
    private Boolean completed;
    private Long wigId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity를 DTO로 변환하는 정적 팩토리 메서드
     */
    public static CommitmentResponse from(Commitment commitment) {
        return CommitmentResponse.builder()
                .id(commitment.getId())
                .text(commitment.getText())
                .week(commitment.getWeek())
                .completed(commitment.getCompleted())
                .wigId(commitment.getWig().getId())
                .createdAt(commitment.getCreatedAt())
                .updatedAt(commitment.getUpdatedAt())
                .build();
    }
}
