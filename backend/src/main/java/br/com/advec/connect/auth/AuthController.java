package br.com.advec.connect.auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;
    private final SecurityContextRepository securityContextRepository =
        new HttpSessionSecurityContextRepository();

    public AuthController(AuthenticationManager authenticationManager,
                          LoginAttemptService loginAttemptService) {
        this.authenticationManager = authenticationManager;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest body,
                                                     HttpServletRequest request,
                                                     HttpServletResponse response) {
        String clientKey = clientKey(request);
        if (loginAttemptService.isBlocked(clientKey)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("message", "Muitas tentativas. Tente novamente em alguns minutos."));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated("admin", body.password()));

            // Previne session fixation: troca o ID da sessao apos autenticar
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                request.changeSessionId();
            }

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            loginAttemptService.recordSuccess(clientKey);
            return ResponseEntity.ok(Map.of("authenticated", true));
        } catch (BadCredentialsException e) {
            loginAttemptService.recordFailure(clientKey);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Senha incorreta."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        boolean authenticated = authentication != null
            && authentication.isAuthenticated()
            && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken);
        return Map.of("authenticated", authenticated);
    }

    /** Endpoint para o front obter o cookie XSRF-TOKEN antes do primeiro POST. */
    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        return Map.of("headerName", token.getHeaderName());
    }

    private String clientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
