package com.concur.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.concur.services.ProcessVaultDataService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v1Api")
@Api(description = "This allows to communicate between api signatures", produces = "application/json")
public class ProcessVaultDataController {
	
	@Value("${application.vault_token}")
	private String vault_token;

	@Autowired
	private ProcessVaultDataService processVaultDataService;
	
	@RequestMapping(value = "/healthCheck", method = RequestMethod.GET)
	public String index() {

		return "Greetings from Vault Reaper!!";
	}

	@GetMapping
	@RequestMapping(value = "/logRandom", method = RequestMethod.GET)
	public ResponseEntity<?> loggingUserInputToKibana(@RequestParam final String initialUserInput) throws IOException {
		if (!initialUserInput.isEmpty()) {
			ProcessVaultDataService processVaultDataService = new ProcessVaultDataService();
			String returnJson = processVaultDataService.logActivityFromController(initialUserInput);
			return new ResponseEntity<String>(returnJson, HttpStatus.OK);
		} else {
			String returnJson = "Api for the same is not yet exposed ";
			return new ResponseEntity<String>(returnJson, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getVaultToken", method = RequestMethod.GET)
	public ResponseEntity<?> getVaultTokenFromApplicationProperties() {
		return new ResponseEntity<String>(vault_token, HttpStatus.OK);
	}

	@GetMapping("/getSecret")
	public ResponseEntity<?> getSecret(@RequestParam String path,@RequestParam String key) {
		try {
			return new ResponseEntity<>(this.processVaultDataService.readSecrets(path, key),
					HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/getAllSecrets")
	public ResponseEntity<?> getAllSecrets(@RequestParam String path) {
		try {
			return new ResponseEntity<>(this.processVaultDataService.getAllSecrets(path),
					HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/postSecret")
	public ResponseEntity<?> postSecret(@RequestParam String key, @RequestParam String secret,
			@RequestParam String path) {
		try {
			return new ResponseEntity<>(this.processVaultDataService.writeSecrets(key, secret, path),
					HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/getFolderList")
	public ResponseEntity<?> getFolderList(@RequestParam String path) {
		try {
			return new ResponseEntity<List<String>>(this.processVaultDataService.getFolderList(path), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/getKeyList")
	public ResponseEntity<?> getKeyList(@RequestParam String path) {
		try {
			return new ResponseEntity<>(this.processVaultDataService.getKeyList(path), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/getSecretUsingValueAnnotation")
	public ResponseEntity<?> getSecretUsingValueAnnotation() throws NoSuchFieldException, SecurityException {
		try {
			return new ResponseEntity<>("test", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/deleteFolder")
	public ResponseEntity<?> deleteFolder(@RequestParam String path) throws NoSuchFieldException, SecurityException {
		try {
			return new ResponseEntity<>("", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping("/test")
	public ResponseEntity<?> encodeAndDecode(){
		try {
			return new ResponseEntity<>(this.processVaultDataService.encodeAndDecode(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	private static final String versionCheck = "1.71";

}
