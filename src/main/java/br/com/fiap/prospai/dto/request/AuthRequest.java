package br.com.fiap.prospai.dto.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;

    // Getters e Setters
}