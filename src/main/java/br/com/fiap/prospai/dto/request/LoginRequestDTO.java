package br.com.fiap.prospai.dto.request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

    @NotBlank(message = "O campo de nome de usuário não pode estar vazio.")
    private String username;

    @NotBlank(message = "O campo de senha não pode estar vazio.")
    private String password;

    // Getters e Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
