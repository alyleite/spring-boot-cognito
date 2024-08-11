package br.com.cognito_teste.util;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private boolean status;
    private Object data;
    private String message;
    private int statusCode;
}
