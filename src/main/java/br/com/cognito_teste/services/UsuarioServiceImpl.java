package br.com.cognito_teste.services;

import br.com.cognito_teste.dto.CadastroDto;
import br.com.cognito_teste.dto.CadastroResponseDto;
import br.com.cognito_teste.dto.LoginDto;
import br.com.cognito_teste.util.ApiResponse;
import br.com.cognito_teste.util.Mensagem;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UsuarioServiceImpl implements UsuarioService {
    private final AWSCognitoIdentityProvider provider;

    @Value("${aws.cognito.user-pool-id}")
    private String userPoolId;
    @Value("${aws.cognito.user-pool-name}")
    private String userPoolName;
    @Value("${aws.cognito.client-id}")
    private String clientId;

    public UsuarioServiceImpl(AWSCognitoIdentityProvider provider) {
        this.provider = provider;
    }

    @Override
    public ApiResponse cadastrar(CadastroDto cadastro) {
        ApiResponse apiResponse;

        try {
            // Create User with Temporary Password
            AdminCreateUserRequest userRequest = new AdminCreateUserRequest()
                    .withUserPoolId(userPoolId)
                    .withUsername(cadastro.getEmail())
                    .withUserAttributes(
                            new AttributeType().withName("email").withValue(cadastro.getEmail()),
                            new AttributeType().withName("name").withValue(cadastro.getNome()),
                            new AttributeType().withName("phone_number").withValue(cadastro.getCelular())
                    )
                    .withTemporaryPassword(cadastro.getSenha())
                    .withDesiredDeliveryMediums("EMAIL");

            AdminCreateUserResult createUserResult = provider.adminCreateUser(userRequest);

            if (createUserResult.getSdkHttpMetadata().getHttpStatusCode() == HttpStatus.OK.value()) {
                provider.adminAddUserToGroup(new AdminAddUserToGroupRequest()
                        .withGroupName(userPoolName)
                        .withUserPoolId(userPoolId) // use environment variable
                        .withUsername(userRequest.getUsername()));


                AdminSetUserPasswordRequest userPasswordRequest = new AdminSetUserPasswordRequest()
                        .withUserPoolId(userPoolId)
                        .withUsername(cadastro.getEmail())
                        .withPassword(cadastro.getSenha()).withPermanent(true);

                provider.adminSetUserPassword(userPasswordRequest);

                apiResponse = ApiResponse
                        .builder()
                        .status(true)
                        .data("")
                        .message(Mensagem.SIGN_UP)
                        .statusCode(HttpStatus.CREATED.value())
                        .build();
            } else {
                apiResponse = ApiResponse
                        .builder()
                        .status(false)
                        .data(null)
                        .message(Mensagem.INTERNAL_SERVER_ERROR + "no registro")
                        .statusCode(createUserResult.getSdkHttpMetadata().getHttpStatusCode())
                        .build();
            }
        } catch (Exception e) {
            log.error("Exception {}", e.getMessage());
            apiResponse = ApiResponse
                    .builder()
                    .status(false)
                    .data(null)
                    .message(Mensagem.EXCEPTION_INTERNAL_SERVER_ERROR)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }

        return apiResponse;
    }

    @Override
    public ApiResponse login(LoginDto signInDto) {
        ApiResponse apiResponse;

        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("USERNAME", signInDto.getEmail());
        //userDetails.put("PASSWORD", signInDto.getSenha());

        AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
                .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .withUserPoolId(userPoolId)
                .withClientId(clientId)
                .withAuthParameters(userDetails);

        try {
            var authResult = provider.adminInitiateAuth(authRequest);

            AuthenticationResultType authenticationResultType;

            if (authResult.getSdkHttpMetadata().getHttpStatusCode() == HttpStatus.OK.value()) {

                if (ChallengeNameType.PASSWORD_VERIFIER.toString().equals(authResult.getChallengeName())) {
                    authenticationResultType = authResult.getAuthenticationResult();

                    var cadastroResponse = getCadastroResponse(authenticationResultType);

                    apiResponse = ApiResponse
                            .builder()
                            .status(false)
                            .data(cadastroResponse)
                            .message(Mensagem.SIGN_IN)
                            .statusCode(HttpStatus.OK.value())
                            .build();
                } else {
                    apiResponse = ApiResponse
                            .builder()
                            .status(false)
                            .data(null)
                            .message(Mensagem.RETRY)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build();

                }
            } else {
                apiResponse = ApiResponse
                        .builder()
                        .status(false)
                        .data(null)
                        .message(Mensagem.INTERNAL_SERVER_ERROR + "doing sign in")
                        .statusCode(authResult.getSdkHttpMetadata().getHttpStatusCode())
                        .build();
            }
        } catch (Exception e) {
            log.error("Exception {}", e.getMessage());
            apiResponse = ApiResponse
                    .builder()
                    .status(false)
                    .data(null)
                    .message(Mensagem.EXCEPTION_INTERNAL_SERVER_ERROR)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }

        return apiResponse;
    }

    private CadastroResponseDto getCadastroResponse(AuthenticationResultType result) {
        return CadastroResponseDto.builder()
                .accessToken(result.getAccessToken())
                .idToken(result.getIdToken())
                .refreshToken(result.getRefreshToken())
                .expirationTime(result.getExpiresIn())
                .tokenType(result.getTokenType())
                .build();
    }
}
