package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.dto.CourseRequest;
import com.spot.good2travel.dto.CourseResponse;
import com.spot.good2travel.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/v1/item/{itemid}/course")
    @Operation(
            summary = "공식 굳이의 코스 가져오기",
            description = "공식 굳이의 코스를 가져옵니다. <br><br> - request: <br> pathvariable Long itemid : 공식 굳이의 pk <br><br> - response: CourseDetailResponse"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =  "공식 굳이의 계획 가져오기", content = @Content(schema = @Schema(implementation = CourseResponse.CourseDetailResponse.class)))
    })
    public CommonResponse<?> getGoodeCourse(@PathVariable Long itemid){
        return CommonResponse.success("공식 굳이의 계획 가져오기 성공", courseService.getGoodeCourse(itemid));
    }

    @PostMapping("/v1/item/{itemid}/course")
    @Operation(
            summary = "공식 굳이에 코스 만들기",
            description = "공식 굳이에 코스를 만들어줄거에요 <br><br> - request: <br> pathvariable Long itemid : 공식 굳이의 pk <br> CourseCreateRequest <br><br> - response: Long courseId"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공식 굳이 코스 만들기", content = @Content(schema = @Schema(implementation = Long.class)))
    })
    public ResponseEntity<CommonResponse<?>> createCourse(@PathVariable Long itemid, @RequestBody @Valid CourseRequest.CourseCreateUpdateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("공식 굳이 만들기 성공", courseService.createCourse(itemid, request)));
    }

    @PutMapping("/v1/course/{courseid}")
    @Operation(
            summary = "공식 굳이의 코스 수정하기 <br><br> - request: <br> pathvariable Long courseid : 공식 코스의 pk <br> CourseUpdateRequest <br><br> - response: Long courseId",
            description = "공식 굳이의 코스를 수정할겁니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공식 굳이 수정 완료", content = @Content(schema = @Schema(implementation = Long.class)))
    })
    public CommonResponse<?> updateCourse(@PathVariable Long courseid, @RequestBody CourseRequest.CourseCreateUpdateRequest request){
        return CommonResponse.success("공식 굳이의 코스를 수정 완료", courseService.updateCourse(courseid, request));
    }

    @DeleteMapping("/v1/course/{courseid}")
    @Operation(
            summary = "공식 굳이 삭제 <br><br> - request: <br> pathvariable Long courseid : 공식 코스의 pk <br> CourseUpdateRequest <br><br> - response: Long courseId",
            description = "공식 굳이의 코스를 수정할겁니다"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공식 굳이 코스 삭제 완료", content = @Content(schema = @Schema(implementation = Long.class)))
    })
    public CommonResponse<?> updateCourse(@PathVariable Long courseid){
        courseService.deleteCourse(courseid);
        return CommonResponse.success("공식 굳이의 코스를 삭제 완료", null);
    }
}
