package com.lambdaschool.shoppingcart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

//Allows us to determine who gets access to what endpoint
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    //Create the resource server, can have multiple if you'd like
    //Each resource must have a unique name
    private static final String RESOURCE_ID = "resource_id";


    //Configure the resource server
    //stateless = false, so that we can work with testing
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(RESOURCE_ID).stateless(false);
    }

    //By default everyone has access to everything
    //our antMatchers control which roles of users have access to which endpoints
    //we must order our antmatchers from most restrictive to least restrictive.
    //So restrict at method level before restricting at endpoint level.
    //Set up our security for who has access to what endpoints
    //Use antmatchers to list what we want to grant access to
    //hasAnyRole states which role can access that specific endpoint
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/",
                        "/h2-console/**",
                        "/swagger-resources/**",
                        "/swagger-resource/**",
                        "/swagger-ui.html",
                        "/v2/api-docs",
                        "/webjars/").permitAll()
                .antMatchers(HttpMethod.POST, "/users/user").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/users/user/**").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/users/user/**").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/users/user/**").hasAnyRole("ADMIN")
                .antMatchers("/roles/**", "/products/**").hasAnyRole("ADMIN")
                .antMatchers("/users/myinfo", "/oauth/revoke-token", "/logout").authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(new OAuth2AccessDeniedHandler());

        //Cross Site Resource Tokens
        http.csrf().disable();

        //Get H2 Console working. Disable frames for security.
        http.headers().frameOptions().disable();

        //Disable Spring Boot default logout functionality
        http.logout().disable();
    }
}
