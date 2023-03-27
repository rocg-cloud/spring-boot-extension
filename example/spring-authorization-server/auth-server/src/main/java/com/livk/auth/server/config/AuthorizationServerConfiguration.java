package com.livk.auth.server.config;

import com.livk.auth.server.common.constant.SecurityConstants;
import com.livk.auth.server.common.converter.OAuth2PasswordAuthenticationConverter;
import com.livk.auth.server.common.converter.OAuth2SmsAuthenticationConverter;
import com.livk.auth.server.common.core.FormIdentityLoginConfigurer;
import com.livk.auth.server.common.core.UserDetailsAuthenticationProvider;
import com.livk.auth.server.common.core.customizer.OAuth2JwtTokenCustomizer;
import com.livk.auth.server.common.handler.AuthenticationFailureEventHandler;
import com.livk.auth.server.common.handler.AuthenticationSuccessEventHandler;
import com.livk.auth.server.common.provider.OAuth2PasswordAuthenticationProvider;
import com.livk.auth.server.common.provider.OAuth2SmsAuthenticationProvider;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

/**
 * <p>
 * AuthorizationServerConfiguration
 * </p>
 *
 * @author livk
 */
@Configuration
public class AuthorizationServerConfiguration {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      OAuth2AuthorizationService authorizationService,
                                                                      OAuth2TokenGenerator<OAuth2Token> oAuth2TokenGenerator,
                                                                      UserDetailsAuthenticationProvider userDetailsAuthenticationProvider) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        OAuth2AuthorizationServerConfigurer configurer = authorizationServerConfigurer.tokenEndpoint(
                        (tokenEndpoint) -> {
                            tokenEndpoint.accessTokenRequestConverter(accessTokenRequestConverter())
                                    .accessTokenResponseHandler(new AuthenticationSuccessEventHandler())
                                    .errorResponseHandler(new AuthenticationFailureEventHandler());
                        }
                )
                .authorizationEndpoint(authorizationEndpoint ->
                        authorizationEndpoint.consentPage(SecurityConstants.CUSTOM_CONSENT_PAGE_URI));

        http.apply(configurer);

        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
        HttpSessionSecurityContextRepository httpSessionSecurityContextRepository = new HttpSessionSecurityContextRepository();

        DefaultSecurityFilterChain securityFilterChain = http.securityMatcher(endpointsMatcher)
                .securityContext().securityContextRepository(httpSessionSecurityContextRepository).and()
                .authorizeHttpRequests().requestMatchers("/auth/**", "/actuator/**", "/css/**", "/error").permitAll().and()
                .authorizeHttpRequests().anyRequest().authenticated().and()
                .csrf().ignoringRequestMatchers(endpointsMatcher).and()
                .apply(configurer.authorizationService(authorizationService)).and()
                .apply(new FormIdentityLoginConfigurer()).and()
                .build();

        // 注入自定义授权模式实现
        addCustomOAuth2GrantAuthenticationProvider(http, oAuth2TokenGenerator, userDetailsAuthenticationProvider);
        return securityFilterChain;
    }

    private void addCustomOAuth2GrantAuthenticationProvider(HttpSecurity http,
                                                            OAuth2TokenGenerator<OAuth2Token> oAuth2TokenGenerator,
                                                            UserDetailsAuthenticationProvider userDetailsAuthenticationProvider) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        OAuth2AuthorizationService authorizationService = http.getSharedObject(OAuth2AuthorizationService.class);

        OAuth2PasswordAuthenticationProvider passwordAuthenticationProvider = new OAuth2PasswordAuthenticationProvider(
                authenticationManager, authorizationService, oAuth2TokenGenerator);

        OAuth2SmsAuthenticationProvider smsAuthenticationProvider = new OAuth2SmsAuthenticationProvider(
                authenticationManager, authorizationService, oAuth2TokenGenerator);
        http.authenticationProvider(userDetailsAuthenticationProvider);
        http.authenticationProvider(passwordAuthenticationProvider);
        http.authenticationProvider(smsAuthenticationProvider);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder,
                                                               UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public OAuth2TokenGenerator<OAuth2Token> oAuth2TokenGenerator(JWKSource<SecurityContext> jwkSource) {
        JwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource);
        JwtGenerator generator = new JwtGenerator(jwtEncoder);
        generator.setJwtCustomizer(new OAuth2JwtTokenCustomizer());
        return new DelegatingOAuth2TokenGenerator(generator, new OAuth2RefreshTokenGenerator());
    }

    private AuthenticationConverter accessTokenRequestConverter() {
        return new DelegatingAuthenticationConverter(List.of(
                new OAuth2AuthorizationCodeAuthenticationConverter(),
                new OAuth2ClientCredentialsAuthenticationConverter(),
                new OAuth2RefreshTokenAuthenticationConverter(),
                new OAuth2PasswordAuthenticationConverter(),
                new OAuth2SmsAuthenticationConverter()));
    }
}
