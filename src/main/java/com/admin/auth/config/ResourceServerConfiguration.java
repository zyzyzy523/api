package com.admin.auth.config;


import com.admin.auth.service.LogoutSuccessHandler;
import com.admin.auth.service.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2020/4/20
 */
@RequiredArgsConstructor
@Configuration
public class ResourceServerConfiguration  {

    private final LogoutSuccessHandler logoutSuccessHandler;


    @Bean
    @Order(2)
    public SecurityFilterChain baseSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .authorizeHttpRequests(authorize ->
                        authorize
                                //.requestMatchers("/api/**").authenticated()
                                .requestMatchers("/**", "/webjars/**", "/login").permitAll()

                )
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .opaqueToken(Customizer.withDefaults())
                        .authenticationEntryPoint(SecurityUtil::exceptionHandler)
                )
                .logout(v -> v.logoutSuccessHandler(logoutSuccessHandler).logoutUrl("/api/logout"));
        ;
        return http.build();
    }

}
