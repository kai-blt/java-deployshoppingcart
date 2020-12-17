package com.lambdaschool.shoppingcart.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

//removes token from token store to allow user to log out
@RestController
public class LogoutController {

    @Autowired
    private TokenStore tokenStore;

    @GetMapping(value = {"oauth/revoke-token", "/logout"}, produces = "application/json")
    public ResponseEntity<?> logoutSelf(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        //If there is an access token, delete from Token Store
        //Replace the Bearer part of the header with empty string
        //Trim off any whitespace
        if (authHeader != null) {
            String tokenValue = authHeader.replace("Bearer", "").trim();
            OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
            tokenStore.removeAccessToken(accessToken);
        }
        //If null, no access token sent with this request so there's no user to log out
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
