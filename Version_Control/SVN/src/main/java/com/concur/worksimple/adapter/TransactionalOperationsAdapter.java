package com.concur.worksimple.adapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNException;

import com.concur.worksimple.adaptee.Adaptee;
import com.concur.worksimple.adaptee.SVNOperations;
import com.concur.worksimple.exception.CustomException;
import com.concur.worksimple.util.VersionControlOperations;

@Service
public class TransactionalOperationsAdapter implements VersionControlOperations{
	private Adaptee adaptee;
	
	public TransactionalOperationsAdapter() {
	}
	
	public TransactionalOperationsAdapter(String versionControl, String root, String name, String password,String wc) {
		if(versionControl.equalsIgnoreCase("svn")) {
			this.adaptee = new SVNOperations(root,name,password,wc);
		}
	}

	@Override
	public String update(String path,String name,long version) {
		try {
			this.adaptee.update(path,name,version);
			throw new CustomException(200,false,"Successfully updated");
		} catch (SVNException e) {
			throw new CustomException(e.getErrorMessage().getErrorCode().getCode(),true,e.getMessage());
		} catch (Exception e) {
			//how to get status code of error
			throw new CustomException(0,true,e.getMessage());
		}
	}

	@Override
	public String commit(String path, File input) {
		try {
			this.adaptee.save(path,input);
			return "Success";
		} catch (SVNException | IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Error";
	}

	@Override
	public String delete(String path,String name) {
		try {
			this.adaptee.delete(path, name);
			return "Success";
		} catch (SVNException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Error";
	}

	@Override
	public List<String> listAllFiles(String path) {
		try {
			return this.adaptee.listAllFilesInDir(path);
		} catch (SVNException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public long getLatestVersion(String path,String name) {
		try {
			return this.adaptee.getLatestVersion(path,name);
		} catch (SVNException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@SuppressWarnings("resource")
	@Override
	public String getFileContents(String path, String name, Long version) {
		try {
			File output = this.adaptee.getFile(path,name,version);
			BufferedReader in = new BufferedReader(new FileReader(output));
			String line = null;
			StringBuilder stringBuilder = new StringBuilder();

			while((line = in.readLine()) != null) {
	            stringBuilder.append(line);
	        }

			return stringBuilder.toString();
		} catch (SVNException e) {
			if(e.getMessage().contains("Attempted to lock an already-locked dir"))
				return "File locked by another user";
			else
				e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<String> getRepository() {
		try {
			return this.adaptee.getRepository();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String createDirectory(String name) {
		try {
			this.adaptee.createDirectory(name);
			return "Success";
			}
			//String errorcode,boolean error,String message
			catch (SVNException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
		}
			return "Error";
	}
	

	@Override
	public String create(String path, File input) {
		try {
			this.adaptee.createFile(path,input);
			return "Success";
		} catch (SVNException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Error";
	}

	@Override
	public byte[] downloadFile(String path, String name, Long version) {
		try {
			//System.out.println("In downloadFile");
			File output = this.adaptee.getFile(path,name,version);
			InputStream inputStream =  new FileInputStream(output);
			
			return IOUtils.toByteArray(inputStream);
		} catch (SVNException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void checkout() {
		this.adaptee.doCheckOut();
	}
	
	/*	@Override
	public String rollBack(String name, float versionNum) {
		// TODO Auto-generated method stub
		return null;
	}*/
}
