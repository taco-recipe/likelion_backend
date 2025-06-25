package org.example.backendproject.Auth.dto;


import lombok.*;
import org.example.backendproject.Auth.entity.Auth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginReponseDTO {

    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private String userid;

    @Builder
    public LoginReponseDTO(Auth auth) {
        this.tokenType = auth.getTokenType();
        this.accessToken = auth.getAccessToken();
        this.refreshToken = auth.getRefreshToken();
        this.userid = auth.getUser().getUserid();
    }
}
