package com.pracainzynierska.PiotrPecuch.payload.Responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JwtResponse {
    private String accessToken;
    private String type = "Bearer";
    private String refreshToken;
    private Long id;
    private String email;
    private List<String> roles;

    public JwtResponse(String token, String refreshToken, Long id, String email, List<String> roles) {
        this.accessToken = token;
        this.refreshToken = refreshToken;
        this.id = id;
        this.email = email;
        this.roles = roles;
    }
}
