package com.concur.springtutorial.interservicecommunication.configuration;

import static org.springframework.security.extensions.saml2.config.SAMLConfigurer.saml;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {  
	@Value("${security.saml2.metadata-url}")
    String metadata;
	
	@Value("${server.ssl.key-alias}")
    String keyAlias;
    
    @Value("${server.ssl.key-store-password}")
    String password;

    @Value("${server.ssl.key-store}")
    String keyStoreFilePath;
    
    @Value("${server.port}")
    String port;
    
    @Override
    protected void configure(final HttpSecurity http) throws Exception {  	
		http
            .authorizeRequests()
                .antMatchers("/saml*").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
            .apply(saml())
                .serviceProvider()
                    .keyStore()
                        .storeFilePath(keyStoreFilePath)
                        .password(this.password)
                        .keyname(this.keyAlias)
                        .keyPassword(this.password)
                        .and()
                    .protocol("https")
                    .hostname(String.format("%s:%s", "localhost",this.port))
                    .basePath("/")
                    .and()
                .identityProvider()
                .metadataFilePath(this.metadata);
    }
}