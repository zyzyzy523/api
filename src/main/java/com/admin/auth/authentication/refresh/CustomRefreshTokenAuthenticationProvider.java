package com.admin.auth.authentication.refresh;


import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationToken;



/**
 * <p>
 *  {@link org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationProvider}
 * </p>
 *
 * @author bin.xie
 * @since 2023/10/25
 */

public final class CustomRefreshTokenAuthenticationProvider implements AuthenticationProvider {

    private final RefreshTokenProvider provider;

    public CustomRefreshTokenAuthenticationProvider(RefreshTokenProvider provider) {
        this.provider = provider;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2RefreshTokenAuthenticationToken refreshTokenAuthentication =
                (OAuth2RefreshTokenAuthenticationToken) authentication;
        return provider.authenticate(refreshTokenAuthentication, refreshTokenAuthentication.getRefreshToken());
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2RefreshTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }

}