package com.admin.auth.config;



import com.admin.auth.authentication.ClientAuthenticationConverter;
import com.admin.auth.authentication.authorization.CuxAuthenticationFailureHandler;
import com.admin.auth.authentication.authorization.CuxAuthorizationCodeAuthenticationConverter;
import com.admin.auth.authentication.authorization.CuxAuthorizationCodeRequestAuthenticationConverter;
import com.admin.auth.authentication.authorization.CuxAuthorizationCodeRequestAuthenticationValidator;
import com.admin.auth.authentication.clients.CuxClientCredentialsAuthenticationConverter;
import com.admin.auth.authentication.password.PasswordGrantAuthenticationConverter;
import com.admin.auth.authentication.password.PasswordGrantAuthenticationProvider;
import com.admin.auth.authentication.refresh.CustomRefreshTokenAuthenticationProvider;
import com.admin.auth.authentication.refresh.CuxOAuth2RefreshTokenAuthenticationConverter;
import com.admin.auth.authentication.refresh.RefreshTokenProvider;
import com.admin.auth.service.BaseAuthorizationService;
import com.admin.auth.service.BaseClientDetailService;
import com.admin.auth.service.CheckTokenService;
import com.admin.auth.service.SecurityUtil;
import com.admin.auth.user.PrincipalLite;
import com.admin.common.util.RedisHelper;
import com.admin.config.AppProperties;
import com.admin.exception.BizException;
import com.admin.exception.Msg;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;



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
@Slf4j
public class AuthServerConfiguration {

    private static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";
    private PasswordGrantAuthenticationProvider provider;
    private final AppProperties appProperties;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
    public RefreshTokenProvider refreshTokenProvider(OAuth2AuthorizationService authorizationService,
                                                     OAuth2TokenGenerator<?> tokenGenerator) {
        return new RefreshTokenProvider(authorizationService, tokenGenerator);
    }
    /**
     * Spring Authorization Server 相关配置
     * 此处方法与下面defaultSecurityFilterChain都是SecurityFilterChain配置，配置的内容有点区别，
     * 因为Spring Authorization Server是建立在Spring Security 基础上的，defaultSecurityFilterChain方法主要
     * 配置Spring Security相关的东西，而此处authorizationServerSecurityFilterChain方法主要配置OAuth 2.1和OpenID Connect 1.0相关的东西
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      RefreshTokenProvider refreshTokenProvider,
                                                                      OAuth2AuthorizationService authorizationService,
                                                                      OAuth2TokenGenerator<?> tokenGenerator,
                                                                      BaseClientDetailService clientDetailService,
                                                                      PasswordEncoder passwordEncoder,
                                                                      UserDetailsService userDetailsService,
                                                                      AuthenticationSuccessHandler authenticationSuccessHandler)
            throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        CustomRefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider =
                new CustomRefreshTokenAuthenticationProvider(refreshTokenProvider);
        provider = new PasswordGrantAuthenticationProvider(
                authorizationService, tokenGenerator, clientDetailService, passwordEncoder, userDetailsService);
        http.authenticationProvider(refreshTokenAuthenticationProvider)
                .getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .deviceAuthorizationEndpoint(deviceAuthorizationEndpoint ->
                        // 设置用户码校验地址
                        deviceAuthorizationEndpoint.verificationUri("/activate")
                )
                .deviceVerificationEndpoint(deviceVerificationEndpoint ->
                        // 设置授权页地址
                        deviceVerificationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI)
                )

                .clientAuthentication(item -> item.authenticationConverter(new ClientAuthenticationConverter()))
                .authorizationEndpoint(authorizationEndpoint ->
                        authorizationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI)
                                .authenticationProviders(providers -> {
                                    for (AuthenticationProvider authenticationProvider : providers) {
                                        if (authenticationProvider instanceof OAuth2AuthorizationCodeRequestAuthenticationProvider t) {
                                            t.setAuthenticationValidator(new CuxAuthorizationCodeRequestAuthenticationValidator());
                                        }
                                    }
                                })
                                .authorizationRequestConverter(new CuxAuthorizationCodeRequestAuthenticationConverter())
                                .errorResponseHandler(new CuxAuthenticationFailureHandler(appProperties)))
                // 设置自定义密码模式
                .tokenEndpoint(tokenEndpoint ->
                        tokenEndpoint
                                .accessTokenResponseHandler(authenticationSuccessHandler)
                                .errorResponseHandler(SecurityUtil::exceptionHandler)
                                .accessTokenRequestConverter(
                                        new PasswordGrantAuthenticationConverter())
                                .accessTokenRequestConverter(new CuxAuthorizationCodeAuthenticationConverter())
                                .accessTokenRequestConverter(new CuxClientCredentialsAuthenticationConverter())
                                .accessTokenRequestConverter(new CuxOAuth2RefreshTokenAuthenticationConverter())
                                .authenticationProvider(
                                        provider))
                // 开启OpenID Connect 1.0（其中oidc为OpenID Connect的缩写）。
                .oidc(Customizer.withDefaults());
        // 设置登录地址，需要进行认证的请求被重定向到该地址
        http
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer.opaqueToken(Customizer.withDefaults())
                                .authenticationEntryPoint(SecurityUtil::exceptionHandler)
                                .accessDeniedHandler(SecurityUtil::exceptionHandler)
                );
        //http.securityContext(context -> context.securityContextRepository(redisSecurityContextRepository));

        return  http.build();
    }


    public PasswordGrantAuthenticationProvider getProvider() {
        return this.provider;
    }


    @Bean
    @SneakyThrows
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public OpaqueTokenIntrospector opaqueTokenIntrospector(OAuth2AuthorizationService authorizationService) {
        return new CheckTokenService(authorizationService);
    }



    /**
     * 授权信息
     * 对应表：oauth2_authorization
     */
    @Bean
    public BaseAuthorizationService authorizationService(JdbcTemplate jdbcTemplate,
                                                         RegisteredClientRepository registeredClientRepository,
                                                         RedisHelper redisHelper) {

        return new BaseAuthorizationService(jdbcTemplate, registeredClientRepository, redisHelper);
    }

    /**
     * 授权确认
     *对应表：oauth2_authorization_consent
     */
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }




    /**
     * 配置认证服务器请求地址
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        //什么都不配置，则使用默认地址
        return AuthorizationServerSettings.builder()
                .tokenEndpoint("/login")
                .build();
    }

    /**
     *配置token生成器
     */
    @Bean
    OAuth2TokenGenerator<?> tokenGenerator() {
        // JwtGenerator jwtGenerator = new JwtGenerator(new NimbusJwtEncoder(jwkSource));
        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
        accessTokenGenerator.setAccessTokenCustomizer(v -> {
            Authentication principal = v.getPrincipal();
            RegisteredClient registeredClient = v.getRegisteredClient();
            if (principal instanceof UsernamePasswordAuthenticationToken o) {
                Object userObj = o.getPrincipal();
                if (userObj instanceof PrincipalLite user) {
                    user.setPassword(null);
                    user.setClientId(registeredClient.getClientId());
                    // 这里校验角色
                    // 查询用户有没有角色
                    checkRole(user);
                }
            }
        });
        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
        return new DelegatingOAuth2TokenGenerator(accessTokenGenerator, refreshTokenGenerator);
    }


    private void checkRole(PrincipalLite user) {
        String clientId = user.getClientId();
        String username = user.getUsername();
        Long userId = user.getId();
        // 校验授权时间
        // 校验角色权限
        // authUserService.checkRoles(userId, clientId, username);

        if (!user.getActivated()) {
            throw new BizException(Msg.USER_NOT_ACTIVATED);
        }
    }

}
