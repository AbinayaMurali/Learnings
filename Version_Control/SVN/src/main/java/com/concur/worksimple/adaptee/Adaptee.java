package com.concur.worksimple.adaptee;

import java.io.File;
import java.util.List;

public class Adaptee {

	public long update(String path, String name, long version) throws Exception {
		return 0;
	}

	public Object save(String path, File input) throws Exception {
		return null;
	}

	public void delete(String path, String name) throws Exception {
	}

	public List<String> listAllFilesInDir(String path) throws Exception {
		return null;
	}

	public long getLatestVersion(String path, String name) throws Exception {
		return 0;
	}

	public File getFile(String path, String name, Long version) throws Exception {
		return null;
	}

	public List<String> getRepository() throws Exception {
		return null;
	}

	public void createDirectory(String name) throws Exception {		
	}

	public void createFile(String path, File input) throws Exception {
	}

	public void doCheckOut() {
	}

}
