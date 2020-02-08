package com.advancedit.ppms.configs;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.advancedit.ppms.controllers.beans.ApiError;
import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.utils.LoggedUserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtTokenFilter extends OncePerRequestFilter {

	private JwtTokenProvider jwtTokenProvider;
	
    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            String token = jwtTokenProvider.resolveToken(request);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                LoggedUserInfo loggedUserInfo = jwtTokenProvider.getUserInfoFromToken(token);
                if (loggedUserInfo.getTenantId() == 0 && isNotAllowedUriWithoutTenant(request)){
                    throw new PPMSException(ErrorCode.ORGANISATION_SELECTION_REQUIRED, "Organisation selection is mandatory");
                }
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(loggedUserInfo, "", Collections.emptyList()));
            }


        filterChain.doFilter(request, response);
        }catch (PPMSException ex){
//custom error response class used across my project
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(convertObjectToJson(new ApiError(ex.getCode().getCode(), ex.getMessage())));
        }
    }

    public String convertObjectToJson(ApiError object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    private boolean isNotAllowedUriWithoutTenant(HttpServletRequest req){
        String uri = (req).getRequestURI();
        return !uri.contains("auth/") && !uri.contains("error")  ;
    }
}
