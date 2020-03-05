package com.concur.springtutorial.interservicecommunication.controller;

import java.util.Date;

import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Controller
public class IndexController {
	@RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserNameSimple() throws MessageEncodingException {
		String jwtToken = "";
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	SAMLCredential credential = (SAMLCredential) authentication.getCredentials();
    	String name = credential.getAttributeAsString("FirstName")+" "+credential.getAttributeAsString("LastName");
    	String email = credential.getAttributeAsString("Email");

    	jwtToken = Jwts.builder().setHeaderParam("name", name).setHeaderParam("email", email).claim("roles", "user").setIssuedAt(new Date())
				.signWith(SignatureAlgorithm.HS256, "secretkey").compact();
    	
    	return jwtToken;
    }
}