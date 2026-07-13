package br.com.advec.connect.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank(message = "Informe a senha.")
    @Size(max = 128, message = "Senha muito longa.")
    String password
) {}
