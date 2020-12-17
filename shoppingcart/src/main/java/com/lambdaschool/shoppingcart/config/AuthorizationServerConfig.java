package com.lambdaschool.shoppingcart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;


//Allows client to access our server
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    //Get the ENV variables from the System
    //CLIENT_ID AND CLIENT_SECRET OATH = username and pass for the application
    static final String CLIENT_ID = System.getenv("OAUTHCLIENTID");
    static final String CLIENT_SECRET = System.getenv("OAUTHCLIENTSECRET");

    //Can also hardcode if no ENV setup. Not good practice as it'll be publicly available on Github if uploaded.
    //static final String CLIENT_ID = "lambda-client";
    //static final String CLIENT_SECRET = "lambda-secret";

    //Users can auth in many ways. In this version we'll use username/password.
    static final String GRANT_TYPE_PASSWORD = "password";

    //Client can also send an api key authorization code base64 combination of clien_tid/client_secret
    static final String AUTHORIZATION_CODE = "authorization_code";

    //OATH2 has scopes to further refine how user roles are defined.
    //Used to limit what a user can do with the application as a whole
    static final String SCOPE_READ = "read";
    static final String SCOPE_WRITE = "write";
    static final String TRUST = "trust";

    //Set the expiration time of a user authentication token which identifies users
    //-1 is always valid. 10 minutes would be 600 (10 min x 60s)
    static final int ACCESS_TOKEN_VALIDITY_SECONDS = -1;

    //Bring in Token Store. Configured in SecurityConfig, but managed in AuthorizationServerConfig
    @Autowired
    private TokenStore tokenStore;

    //Bring in Authentication Manager - authenticates a user and gives ability to assign acess token
    //Managed by AuthorizationServer
    @Autowired
    private AuthenticationManager authenticationManager;

    //Bring in password encoder. AuthorizationServer needs to encrypt client secret so we tell it
    //what encoder to use
    @Autowired
    private PasswordEncoder encoder;

    //Connect up all of our configurations for Client Details Service for our application
    //authorization in SecurityConfig
    @Override
    public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
        configurer.inMemory()
                .withClient(CLIENT_ID)
                .secret(encoder.encode(CLIENT_SECRET))
                .authorizedGrantTypes(GRANT_TYPE_PASSWORD, AUTHORIZATION_CODE)
                .scopes(SCOPE_READ, SCOPE_WRITE, TRUST)
                .accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS);
    }


    //Configure which endpoints are handled through Spring Security
    //Connects our endpoints to our custom authentication server and token store.
    //Use our token store and manager
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(tokenStore)
                .authenticationManager(authenticationManager);

        // localhost:2019/oauth/token <- typical endpoint
        // localhost:2019/login <- custom login endpoint
        // localhost:2019/logout <- custom logout endpoint
        // Map custom login endpoint
        endpoints.pathMapping("/oauth/token", "/login");
        endpoints.pathMapping("/oauth/revoke-token", "/logout");
    }
}
