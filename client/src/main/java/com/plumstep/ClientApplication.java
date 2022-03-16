package com.plumstep;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class ClientApplication {
	@Value("${server.ssl.trust-store-password}")
	private String trustStorePassword;
	@Value("${server.ssl.trust-store:}")
	private String trustResource;
	@Value("${server.ssl.key-store-password}")
	private String keyStorePassword;
	@Value("${server.ssl.key-password}")
	private String keyPassword;
	@Value("${server.ssl.key-store:}")
	private String keyStore;

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public RestTemplate restTemplate() throws Exception {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        restTemplate.setErrorHandler(
                new DefaultResponseErrorHandler() {
                    @Override
                    protected boolean hasError(HttpStatus statusCode) {
                        return false;
                    }
                });

        return restTemplate;
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() throws Exception {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    private HttpClient httpClient() throws Exception {
		// Load our keystore and truststore containing certificates that we trust.
		SSLContextBuilder builder = SSLContexts.custom();
		if(isExternalResource(trustResource)) {
			builder.loadTrustMaterial(Paths.get(trustResource).toFile(), trustStorePassword.toCharArray());
		} else {
			builder.loadTrustMaterial(getInternalResourceUrl(trustResource), trustStorePassword.toCharArray());
		}
		
		if(isExternalResource(keyStore)) {
			builder.loadKeyMaterial(Paths.get(keyStore).toFile(), keyStorePassword.toCharArray(), keyPassword.toCharArray()).build();
		} else {
			builder.loadKeyMaterial(getInternalResourceUrl(keyStore), keyStorePassword.toCharArray(), keyPassword.toCharArray()).build();
		}
		
		SSLContext sslcontext = builder.build();
		SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslcontext,
				new NoopHostnameVerifier());
		return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
	}
	
	private boolean isExternalResource(String path) {
		return !path.contains("classpath:");
	}
	
	private URL getInternalResourceUrl(String path) throws IOException {
		return new ClassPathResource(trustResource.replaceAll("classpath:", "")).getURL();
	}
}
