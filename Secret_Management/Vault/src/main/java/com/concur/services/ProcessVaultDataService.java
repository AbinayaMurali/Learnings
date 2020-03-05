package com.concur.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultTemplate;

@Service
public class ProcessVaultDataService {

	@Autowired
	private VaultOperations operations;

	@Autowired
	private VaultTemplate vaultTemplate;

	private static final Logger logger = LoggerFactory.getLogger(ProcessVaultDataService.class);

	public String logActivityFromController(final String initialUserInput) {
		long TIME_STARTED = System.currentTimeMillis();
		logger.info("Logging the log sentby user" + " " + initialUserInput + "started at :" + " " + TIME_STARTED);
		long TIME_ENDED = System.currentTimeMillis();
		logger.info("Logging ended" + " " + "ended at : " + " " + TIME_ENDED);
		logger.info("Time took for the function to execute " + " " + (TIME_ENDED - TIME_STARTED) + " milliseconds");
		return new String(
				"DevTest Container Initialized" + " " + "User Defined Input:" + " " + initialUserInput);
	}

	public Map<String, Object> writeSecrets(String key, String secret, String path) throws Exception {
		logger.info("In ProcessVaultDataService, writeSecrets method");
		Map<String, Object> lhashmap;
		if (operations.read(path) != null) {
			lhashmap = operations.read(path).getData();
		} else {
			lhashmap = new HashMap<String, Object> ();
		}
		lhashmap.put(key, secret);
		
		vaultTemplate.write(path, lhashmap);
		return vaultTemplate.read(path).getData();
	}

	public Object readSecrets(String path,String key) throws Exception {
		Base64 base64 = new Base64();
		Object secret = vaultTemplate.read(path).getData().get(key);
		logger.info("In ProcessVaultDataService, readSecrets method");
		if(operations.read(path) == null)
			throw new Exception("Invalid path");
		if(operations.read(path).getData() == null)
			throw new Exception("No data found in path");
		if(key.equalsIgnoreCase("file_content")) {
			return new String(base64.decode(((String) secret).getBytes()));
		}
		
		return secret;
	}

	public List<String> getFolderList(String path) {
		logger.info("In ProcessVaultDataService, getFolderList method");
		return vaultTemplate.list(path);
	}

	public Set<String> getKeyList(String path) throws Exception {
		logger.info("In ProcessVaultDataService, getKeyList method");
		if(operations.read(path) == null)
			throw new Exception("Invalid path");
		if(operations.read(path).getData() == null)
			throw new Exception("No data found in path");
		return vaultTemplate.read(path).getData().keySet();
	}

	public Map<String, Object> getAllSecrets(String path) throws Exception {
		logger.info("In ProcessVaultDataService, getAllSecrets method");
		if(operations.read(path) == null)
			throw new Exception("Invalid path");
		return vaultTemplate.read(path).getData();
	}

	public void deleteFolder(String path) throws Exception {
		if(operations.read(path) == null)
			throw new Exception("Invalid path");
		vaultTemplate.delete(path);
	}

	public Object encodeAndDecode() throws IOException {
		InputStream in = new FileInputStream("C:\\Users\\i863571\\Documents\\GTS\\Vault Exploration\\domainname.pfx");
		//	File file = new File("C:\\Users\\i863571\\Documents\\GTS\\Vault Exploration\\domainname.pfx");
		Base64 base64 = new Base64();
		String file_content=new String(base64.encode(IOUtils.toByteArray(in)));
		
		byte[] decodedBytes = base64.decode(file_content);
		FileUtils.writeByteArrayToFile(new File("C:\\Users\\i863571\\Documents\\GTS\\Vault Exploration\\domainname_output.pfx"), decodedBytes);
		return null;
	}
}
