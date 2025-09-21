package com.keserugr.transaction.web;

import com.keserugr.transaction.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthResource {
    private final TokenService tokenService;

    @GetMapping("/token")
    public ResponseEntity<String> getToken() {
        return ResponseEntity.ok(tokenService.getValidToken());
    }
}
