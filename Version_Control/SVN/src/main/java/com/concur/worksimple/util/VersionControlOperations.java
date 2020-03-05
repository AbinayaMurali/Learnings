package com.concur.worksimple.util;

import java.io.File;
import java.util.List;

public interface VersionControlOperations {
	//public String rollBack(String name,float versionNum);
	
	public String update(String path,String name,long version);
	public String commit(String path,File input);
	public String delete(String path,String name);
	public List<String> listAllFiles(String path);
	public long getLatestVersion(String path,String name);
	public String getFileContents(String path, String name, Long version);
	public List<String> getRepository();
	public String createDirectory(String name);
	public String create(String path, File input);
	public byte[] downloadFile(String path, String name, Long version);
}
