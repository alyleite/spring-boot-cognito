package br.com.cognito_teste.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CadastroDto {
    private String nome;
    private String email;
    private String celular;
    private String senha;
}
