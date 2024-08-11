package br.com.cognito_teste.services;

import br.com.cognito_teste.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class HomeServiceImpl implements HomeService {
    @Override
    public ApiResponse getData(HttpServletRequest request) {
        return ApiResponse
                .builder()
                .data("Olá")
                .message("Tudo certo")
                .statusCode(HttpStatus.OK.value())
                .status(false)
                .build();
    }
}
