package com.plumstep.controller;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.swagger.annotations.ApiOperation;

@RestController
public class ClientController {
	
	@Autowired
    private RestTemplate restTemplate;
	
	@Value("${app.server.url}")
	private String serverUrl;
    
	@ApiOperation(value = "Forward GET call with MTLS")
    @GetMapping
    ResponseEntity<?> get() {
		return restTemplate.getForEntity(serverUrl, String.class);
    }
	
	@ApiOperation(value = "Forward POST call with MTLS")
    @PostMapping
    ResponseEntity<?> post(HttpServletRequest request, @RequestBody String body) {
		HttpEntity<String> entity = new HttpEntity<>(body, getHeaders(request));
		return restTemplate.postForEntity(serverUrl, entity, String.class);
    }
	
	private MultiValueMap<String, String> getHeaders(HttpServletRequest request) {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		for (Enumeration<?> e = request.getHeaderNames(); e.hasMoreElements();) {
		    String headerName = (String) e.nextElement();
		    String headerValue = request.getHeader(headerName);
		    headers.add(headerName, headerValue);
		}
		return headers;
	}
}
