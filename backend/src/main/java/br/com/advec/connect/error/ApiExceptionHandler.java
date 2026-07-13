package br.com.advec.connect.error;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.advec.connect.scoreboard.ScoreboardService.GroupNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(GroupNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getDefaultMessage())
            .findFirst()
            .orElse("Dados invalidos.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", message));
    }
}
