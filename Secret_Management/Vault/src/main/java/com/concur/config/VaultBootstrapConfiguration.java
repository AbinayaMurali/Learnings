package com.concur.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.vault.config.VaultProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.vault.config.AbstractVaultConfiguration;
import org.springframework.vault.config.ClientHttpRequestFactoryFactory;
import org.springframework.vault.core.lease.LeaseEndpoints;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.support.ClientOptions;
import org.springframework.vault.support.SslConfiguration;

import org.springframework.cloud.vault.config.VaultProperties.Ssl;
import org.springframework.util.StringUtils;
import org.springframework.vault.support.SslConfiguration.KeyStoreConfiguration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

@Configuration
@EnableConfigurationProperties(VaultProperties.class)
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class VaultBootstrapConfiguration {

	private static final String X_VAULT_NAMESPACE = "X-Vault-Namespace";
	private final VaultProperties vaultProperties;

	@Value("${spring.cloud.vault.namespace}")
	private String namespace;

	@Autowired
	private SecretLeaseContainer secretLeaseContainer;

	public VaultBootstrapConfiguration(VaultProperties vaultProperties) {
		this.vaultProperties = vaultProperties;
	}

	@Bean
	public AbstractVaultConfiguration.ClientFactoryWrapper clientHttpRequestFactoryWrapper() {
		SslConfiguration sslConfiguration = createSslConfiguration(this.vaultProperties.getSsl());
		return new AbstractVaultConfiguration.ClientFactoryWrapper(getClientHttpRequestFactory(sslConfiguration));
	}

	private InterceptingClientHttpRequestFactory getClientHttpRequestFactory(SslConfiguration sslConfiguration) {
		System.out.println("@56 ::::::: namespace : "+namespace);
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add((request, body, execution) -> {
			request.getHeaders().add(X_VAULT_NAMESPACE, namespace);
			return execution.execute(request, body);
		});
		return new InterceptingClientHttpRequestFactory(clientHttpRequestFactoryFactory(sslConfiguration),
			interceptors);
	}

	private ClientHttpRequestFactory clientHttpRequestFactoryFactory(SslConfiguration sslConfiguration) {
		ClientOptions clientOptions =
			new ClientOptions(Duration.ofMillis((long)vaultProperties.getConnectionTimeout()),
				Duration.ofMillis((long)vaultProperties.getReadTimeout()));

		return ClientHttpRequestFactoryFactory.create(clientOptions, sslConfiguration);
	}

	static private SslConfiguration createSslConfiguration(Ssl ssl) {

		if (ssl == null) {
			return SslConfiguration.unconfigured();
		}

		KeyStoreConfiguration keyStore = KeyStoreConfiguration.unconfigured();
		KeyStoreConfiguration trustStore = KeyStoreConfiguration.unconfigured();

		if (ssl.getKeyStore() != null) {
			keyStore = StringUtils.hasText(ssl.getKeyStorePassword()) ? KeyStoreConfiguration.of(ssl.getKeyStore(),
				ssl.getKeyStorePassword().toCharArray()) : KeyStoreConfiguration.of(ssl.getKeyStore());
		}

		if (ssl.getTrustStore() != null) {

			trustStore = StringUtils.hasText(ssl.getTrustStorePassword()) ? KeyStoreConfiguration.of(ssl.getTrustStore(),
				ssl.getTrustStorePassword().toCharArray()) : KeyStoreConfiguration.of(ssl.getTrustStore());
		}

		return new SslConfiguration(keyStore, trustStore);
	}

	@PostConstruct
	public void modifyLeaseEndpoints() {
		secretLeaseContainer.setLeaseEndpoints(LeaseEndpoints.SysLeases);
	}
}
