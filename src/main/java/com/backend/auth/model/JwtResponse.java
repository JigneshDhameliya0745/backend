package com.backend.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class JwtResponse {

    private String jwttoken;
    private String userName;
    private String userEmail;
    private Integer userId;
    private String roleName;
}
