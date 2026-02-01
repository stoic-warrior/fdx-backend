package com.fdx.backend.domain.wig;

import com.fdx.backend.dto.WigRequest;
import com.fdx.backend.dto.WigResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WIG REST API Controller
 *
 * API 엔드포인트:
 * GET    /api/wigs           - 모든 WIG 조회
 * GET    /api/wigs/count     - WIG 개수 조회
 * POST   /api/wigs           - WIG 생성 (최대 2개)
 * PUT    /api/wigs/{id}      - WIG 수정
 * DELETE /api/wigs/{id}      - WIG 삭제
 */
@RestController // @RestController = @Controller + @ResponseBody
@RequestMapping("/api/wigs") // @RequestMapping: 모든 메서드의 기본 경로를 /api/wigs로 설정
@RequiredArgsConstructor // final 필드 생성자 주입 (서비스 필드)
@Slf4j // 로깅 객체(log)를 자동 생성 ㅡ> log.info 사용가능
@Tag(name = "WIG", description = "Wildly Important Goals (가장 중요한 목표) 관리 API")
public class WigController {

    private final WigService wigService;

    @Operation(
            summary = "모든 WIG 조회",
            description = "현재 등록된 모든 WIG를 조회합니다. (최대 2개)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = WigResponse.class))
    )
    @GetMapping
    public ResponseEntity<List<WigResponse>> getAllWigs() {
        log.info("GET /api/wigs - 모든 WIG 조회 요청");
        List<WigResponse> wigs = wigService.getAllWigs();
        return ResponseEntity.ok(wigs);
    }

    @Operation(
            summary = "WIG 개수 조회",
            description = "현재 등록된 WIG 개수를 확인합니다. 추가 가능 여부도 함께 반환됩니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "조회 성공"
    )
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getWigCount() {
        log.info("GET /api/wigs/count - WIG 개수 조회 요청");
        long count = wigService.countWigs(); // jpa에서 count는 long타입 반환

        Map<String,Object> response = new HashMap<>();
        response.put("count",count);
        response.put("maxCount",2);
        response.put("canAddMore",count<2);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "WIG 생성",
            description = """
                    새로운 WIG를 생성합니다.
                    
                    **제약사항:**
                    - 최대 2개까지만 생성 가능 (4DX 원칙)
                    - NUMERIC 타입의 경우 unit 필수
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = WigResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (유효성 검증 실패)"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "WIG 개수 초과 (최대 2개)"
            )
    })
    @PostMapping
    public ResponseEntity<WigResponse> createWig(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "생성할 WIG 정보",
                    required = true
            )
            WigRequest request) {
        log.info("POST /api/wigs - WIG 생성 요청: {}", request.getTitle());
        WigResponse createdWig = wigService.createWig(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWig);
    }

    @Operation(
            summary = "WIG 수정",
            description = "기존 WIG의 정보를 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = WigResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "WIG를 찾을 수 없음"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<WigResponse> updateWig(
            @Parameter(description = "수정할 WIG ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody WigRequest request) {

        log.info("PUT /api/wigs/{} - WIG 수정 요청",id);
        WigResponse updatedWig = wigService.updateWig(id,request);
        return ResponseEntity.ok(updatedWig);
    }

    @Operation(
            summary = "WIG 삭제",
            description = "WIG와 관련된 모든 데이터(Lead Measures, Milestones 등)를 함께 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "WIG를 찾을 수 없음"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWig(
            @Parameter(description = "삭제할 WIG ID", required = true)
            @PathVariable Long id) {
        log.info("DELETE /api/wigs/{} - WIG 삭제 요청",id);
        wigService.deleteWig(id);
        return ResponseEntity.noContent().build();
    }


    /**
     * 예외 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("요청 처리 중 오류 발생: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException e) {
        log.error("상태 오류: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
