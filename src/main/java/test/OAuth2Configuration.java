package test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
public class OAuth2Configuration {

    @EnableResourceServer
    @Configuration
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http // TODO create different versions of the confirm_access controller for json/html invocations
                 //      this will require supporting cookie and auth header authentication with JWT
                .requestMatchers()
                    .antMatchers("/api/**")
                    .and()
                .authorizeRequests()
                    .anyRequest()
                    .authenticated();
            }

    }

    @EnableAuthorizationServer
    @Configuration
    protected static class AuthServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        @Qualifier("authenticationManagerBean")
        private AuthenticationManager authenticationManager;

        @Autowired
        ClientDetailsService clientDetailsService;

        @Bean
        public JwtAccessTokenConverter accessTokenConverter() {
            JwtAccessTokenConverter conv = new JwtAccessTokenConverter();
            conv.setSigningKey("key");
            return conv;
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            oauthServer.realm("test");
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.inMemory().withClient("my-client")
                    .authorizedGrantTypes("refresh_token", "implicit")
                    .authorities("ROLE_CLIENT")
                    .scopes("read", "write")
                    .autoApprove(true) // don't make people click approve for this client
                    .accessTokenValiditySeconds(300);
        }

        private UserApprovalHandler userApprovalhandler() {
            //use this not for token store approval, but for client details auto-approval
            TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
            handler.setTokenStore(new JwtTokenStore(accessTokenConverter()));
            handler.setClientDetailsService(clientDetailsService);
            return handler;
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints
                //don't really need this now though... as there isn't any extenral auth grant client
                //.authorizationCodeServices( TODO JwtAuthorizationCodeServices or Hibernate, so you get caching )
                .tokenStore(new JwtTokenStore(accessTokenConverter()))
                .userApprovalHandler(userApprovalhandler())
                .authenticationManager(authenticationManager) // TODO remove this... use the implicit flow with normal jwt token login
                .accessTokenConverter(accessTokenConverter());
        }
    }
}
