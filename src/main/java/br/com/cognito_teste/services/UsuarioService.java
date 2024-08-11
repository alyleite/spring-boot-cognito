package br.com.cognito_teste.services;

import br.com.cognito_teste.dto.CadastroDto;
import br.com.cognito_teste.dto.LoginDto;
import br.com.cognito_teste.util.ApiResponse;

public interface UsuarioService {
    ApiResponse cadastrar(CadastroDto cadastro);

    ApiResponse login(LoginDto signInDto);



}
