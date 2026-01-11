package com.user.inside_user.security.jwtAuth;



import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
/**
 * The JwtAuthEntryPoint class implements the AuthenticationEntryPoint interface from Spring Security.
 * It is used to handle the case where an unauthenticated user tries to access a resource that requires authentication.
 * The commence method is overridden to send a custom response in case of authentication failure.
 * The response includes the status, error message, exception message, and the path of the requested resource.
 */
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    public JwtAuthEntryPoint() {
    }

    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(401);
        Map<String, Object> body = new HashMap();
        body.put("status", 401);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());
        PrintWriter writer = response.getWriter();
        writer.println("{");
        writer.println("\"status\": " + body.get("status") + ",");
        writer.println("\"error\": \"" + body.get("error") + "\",");
        writer.println("\"message\": \"" + body.get("message") + "\",");
        writer.println("\"path\": \"" + body.get("path") + "\"");
        writer.println("}");
        writer.flush();
    }
}

