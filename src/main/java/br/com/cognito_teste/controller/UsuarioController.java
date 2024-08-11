package br.com.cognito_teste.controller;

import br.com.cognito_teste.dto.CadastroDto;
import br.com.cognito_teste.dto.LoginDto;
import br.com.cognito_teste.services.UsuarioService;
import br.com.cognito_teste.util.ApiResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UsuarioController {

    private final UsuarioService userService;

    public UsuarioController(UsuarioService userService) {
        this.userService = userService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse> signUp(@RequestBody CadastroDto cadastroDto) {
        ApiResponse apiResponse = userService.cadastrar(cadastroDto);
        return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(apiResponse.getStatusCode()));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse> signIn(@RequestBody LoginDto loginDto) {
        ApiResponse apiResponse = userService.login(loginDto);
        return new ResponseEntity<>(apiResponse, HttpStatusCode.valueOf(apiResponse.getStatusCode()));
    }
}
