package com.vision.middleware.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.vision.middleware.service.UserService;
import com.vision.middleware.utils.RSAKeyProperties;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for setting up Spring Security.
 */
@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private final RSAKeyProperties keys;

    @Autowired
    private final UserService userService;

    @Autowired
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    /**
     * Bean definition for a password encoder.
     *
     * @return PasswordEncoder instance using BCrypt.
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Sets the security context holder strategy to use thread-local storage.
     */
    @PostConstruct
    public void setSecurityContextHolderStrategy() {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL);
    }

    /**
     * Bean definition for an authentication manager.
     *
     * @param authenticationConfiguration the Spring Security authentication configuration
     * @return AuthenticationManager instance
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Bean definition for a DAO authentication provider.
     *
     * @return DaoAuthenticationProvider instance
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userService);
        daoProvider.setPasswordEncoder(passwordEncoder());
        return daoProvider;
    }

    /**
     * Bean definition for the security filter chain.
     *
     * @param http the HttpSecurity object to configure
     * @return SecurityFilterChain configured with the provided HttpSecurity object
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configure(http))
            .authorizeHttpRequests(auth -> {
                // by default, spring boot will lock down everything.
                // We can change what endpoints are available to what users here.
                auth.requestMatchers("/auth/validate").hasAnyRole("ADMIN", "USER"); //this endpoint is used for lazy validation
                auth.requestMatchers("/auth/**").permitAll(); //allow any user to access /auth/** to be able to sign up.
                auth.requestMatchers("/exampledata/**").permitAll(); //allow any user to access /auth/** to be able to sign up.
                auth.requestMatchers("/admin/**").hasRole("ADMIN");
                auth.requestMatchers("/user/id/**").permitAll();
                auth.requestMatchers("/user/public-info").permitAll();
                auth.requestMatchers("/user/following/list").hasAnyRole("ADMIN", "USER"); //probably very unnecessary
                auth.requestMatchers("/user/account/**").hasAnyRole("ADMIN", "USER");
                auth.requestMatchers("/user/search").permitAll(); // allow searching by anyone
                auth.requestMatchers("/user/**").hasAnyRole("ADMIN", "USER");
                auth.requestMatchers("/chat/**").hasAnyRole("ADMIN", "USER"); // is this being used?
                auth.requestMatchers("/replies/post/**").permitAll();
                auth.requestMatchers("/post/new").hasAnyRole("ADMIN", "USER");
                auth.requestMatchers("/post/all").permitAll();
                auth.requestMatchers("/post/search**").permitAll(); // anyone is allowed to search i guess
                auth.requestMatchers("/ws/notifications/testCreate").denyAll();
                auth.requestMatchers("/ws/**", "/ws/notifications/**").permitAll(); // websocket

                auth.requestMatchers("/media/upload").hasAnyRole("ADMIN", "USER");
                auth.requestMatchers("/media/**").permitAll();

                // todo: remove this after testing
                auth.requestMatchers("/notifications/**").permitAll(); // websocket

                auth.anyRequest().permitAll(); // some changes for the demo

                // auth.anyRequest().authenticated(); // authentication required for all queries
            })
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            ) // tell jwt to use oauth2 resource server and use our jwtAuthenticationConverter defined in this class
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // stateless because we are using JWT
            .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    /**
     * Bean definition for a JWT decoder.
     *
     * @return JwtDecoder instance using the public key from RSAKeyProperties
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(keys.getPublicKey()).build();
    }

    /**
     * Bean definition for a JWT encoder.
     *
     * @return JwtEncoder instance using the public and private keys from RSAKeyProperties
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(keys.getPublicKey()).privateKey(keys.getPrivateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    /**
     * Bean definition for a JWT authentication converter.
     *
     * @return JwtAuthenticationConverter instance with a custom JwtGrantedAuthoritiesConverter
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        // convert roles from form USER -> ROLE_USER for Spring Security matching users against roles from converted JWT tokens.
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles"); // as defined in the TokenService
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtConverter;
    }
}
