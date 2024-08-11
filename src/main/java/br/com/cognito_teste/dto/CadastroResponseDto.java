package br.com.cognito_teste.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CadastroResponseDto {
    private String accessToken;
    private String idToken;
    private String refreshToken;
    private String tokenType;
    private int expirationTime;
}
