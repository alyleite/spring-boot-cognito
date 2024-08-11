package br.com.cognito_teste.services;

import br.com.cognito_teste.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
public interface HomeService {
    ApiResponse getData(HttpServletRequest request);
}
