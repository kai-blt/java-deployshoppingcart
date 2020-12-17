package com.lambdaschool.shoppingcart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

//Handles encryption for passwords, where are tokens stored, tie to Spring Security with our own User Model
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //Enable Authentication Server (Bean method)
    //Named authenticationManagerBean to not conflict with Spring Security
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }

    //Tie user model to Spring Security, tell which to use using a Service (SecurityUserServiceImpl)
    @Autowired
    private UserDetailsService securityUserService;

    //Tie in authentication manager to Spring Security and our User Model + our password encoder
    //public as other parts of app need this.
    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(securityUserService).passwordEncoder(encoder());
    }

    //Tells up which password encoder we are using
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    //Create Token Store that holds our access tokens in memory
    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

}
