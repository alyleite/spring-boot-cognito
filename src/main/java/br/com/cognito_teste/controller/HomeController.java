package br.com.cognito_teste.controller;

import br.com.cognito_teste.services.HomeService;
import br.com.cognito_teste.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getData(HttpServletRequest request) {
        ApiResponse data = homeService.getData(request);
        return new ResponseEntity<>(data, HttpStatusCode.valueOf(data.getStatusCode()));
    }
}
