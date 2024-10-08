package com.spot.good2travel.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.common.exception.ExceptionMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class MemberAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        log.info("[ClientAccessDeniedException] 접근 권한이 없습니다.");

        CommonResponse commonResponse = new CommonResponse(0, ExceptionMessage.TOKEN_NOT_AUTHORIZED.getMessage(), null);


        response.setStatus(403);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(commonResponse));

    }
}
