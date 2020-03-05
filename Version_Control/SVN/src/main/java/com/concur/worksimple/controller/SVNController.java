package com.concur.worksimple.controller;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.tmatesoft.svn.core.SVNException;

import com.concur.worksimple.adapter.TransactionalOperationsAdapter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value="SVN",description="API for SVN integration",tags="svn")
@ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 401, message = "You are not authorized access the resource"),
        @ApiResponse(code = 404, message = "The resource not found")
})
public class SVNController {
	
	@Value("${versioncontrol.type}")
    private String type;
	
	@Value("${svn.root}")
	private String root;
	
	@Value("${svn.username}")
	private String name;
	
	@Value("${svn.password}")
	private String password;
	
	@Value("${svn.wc}")
	private String wc;
	
	private TransactionalOperationsAdapter transactionalOperationsAdapter;
	
	@Autowired
	private void transactionalOperationsAdapter() {
		this.transactionalOperationsAdapter = new TransactionalOperationsAdapter(type,root,name,password,wc);	
	};
	
	@PostConstruct
	public void init() {
		this.transactionalOperationsAdapter.checkout();
	}
	
	@RequestMapping(value = "/createDir", method = RequestMethod.POST)
	@ApiOperation(value="Creating directory in SVN Repository")
	public String createDirectory(@RequestParam String name) throws Exception
    {	
			return this.transactionalOperationsAdapter.createDirectory(name);
    }
	
	//Takes care of adding new files and commiting the changes of the existing file
		@SuppressWarnings("resource")
		@RequestMapping(value = "/commitFile", method = RequestMethod.POST)
		@ApiOperation(value="Creating file in a directory in SVN Repository")
		public String commitFile(@RequestParam String path,@RequestBody MultipartFile input) throws SVNException
	    {	
			try {
				InputStream inputStream =  new BufferedInputStream(input.getInputStream());
				byte[] buffer = new byte[inputStream.available()];
				inputStream.read(buffer);
				
				File inputFile = new File(input.getOriginalFilename());
				OutputStream outStream = new FileOutputStream(inputFile);
				outStream.write(buffer);
				
				return this.transactionalOperationsAdapter.commit(path,inputFile);
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
			
			return null;
	    }
		
		@RequestMapping(value = "/listFiles", method = RequestMethod.GET)
		@ApiOperation(value="Retrieving list of file names in a directory from SVN Repository")
		public List<String> listFilesFromDir(@RequestParam String path) throws SVNException
	    {	
				return this.transactionalOperationsAdapter.listAllFiles(path);
	    }
		
		@RequestMapping(value = "/latestVersion", method = RequestMethod.GET)
		@ApiOperation(value="Retrieving latest version of SVN Repository")
		public long latestVersionNumber(@RequestParam String path,@RequestParam String name) throws SVNException
	    {	
				return this.transactionalOperationsAdapter.getLatestVersion(path,name);
	    }
		
		@RequestMapping(value = "/get", method = RequestMethod.GET)
		@ApiOperation(value="Retrieving contents of a file in a directory in SVN Repository")
		public String getFileContents(@RequestParam String path,@RequestParam String name,@RequestParam(required=false) Long version) throws SVNException
	    {	
				return this.transactionalOperationsAdapter.getFileContents(path,name,version);
	    }
		
		@RequestMapping(value = "/download", method = RequestMethod.GET)
		@ApiOperation(value="Downloading a file in a directory in SVN Repository")
		public StreamingResponseBody downloadFile(@RequestParam String path,@RequestParam String name,@RequestParam(required=false) Long version) throws SVNException
	    {	
				byte[] wb = this.transactionalOperationsAdapter.downloadFile(path,name,version);
				ByteArrayInputStream bis = new ByteArrayInputStream(wb);
				return outputStream -> {
		            int nRead;
		            byte[] data = wb;
		            while ((nRead = bis.read(data, 0, data.length)) != -1) {
		                outputStream.write(data, 0, nRead);
		            }
		        };
	    }
		
		@RequestMapping(value = "/update", method = RequestMethod.POST)
		@ApiOperation(value="Updating a file to previous versions in a directory in SVN Repository")
		public String update(@RequestParam String path,@RequestParam String name,@RequestParam long version) throws SVNException
	    {	
				return this.transactionalOperationsAdapter.update(path,name,version);
	    }
		
		@RequestMapping(value = "/delete", method = RequestMethod.POST)
		@ApiOperation(value="Deleting a file from directory in SVN Repository")
		public String delete(@RequestParam String path,@RequestParam String name) throws SVNException
	    {	
				return this.transactionalOperationsAdapter.delete(path,name);
	    }
	
/*	@RequestMapping(value = "/createFile/{path}", method = RequestMethod.POST)
	public String createFile(@PathVariable String path,@RequestBody MultipartFile input) throws SVNException
    {	
		try {
			InputStream inputStream =  new BufferedInputStream(input.getInputStream());
			byte[] buffer = new byte[inputStream.available()];
			inputStream.read(buffer);
			
			File inputFile = new File(input.getOriginalFilename());
			OutputStream outStream = new FileOutputStream(inputFile);
			outStream.write(buffer);
			
			this.transactionalOperationsAdapter = new TransactionalOperationsAdapter(type);
			return this.transactionalOperationsAdapter.create(path,inputFile);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		
		return null;
    }
    
    	@SuppressWarnings("resource")
		@RequestMapping(value = "/createFile", method = RequestMethod.POST)
		public String createFile(@RequestParam String path,@RequestBody MultipartFile input) throws SVNException
	    {	
			try {
				InputStream inputStream =  new BufferedInputStream(input.getInputStream());
				byte[] buffer = new byte[inputStream.available()];
				inputStream.read(buffer);
				
				File inputFile = new File(input.getOriginalFilename());
				OutputStream outStream = new FileOutputStream(inputFile);
				outStream.write(buffer);
				
				return this.transactionalOperationsAdapter.create(path,inputFile);
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
			
			return null;
	    }
    
   @RequestMapping(value = "/getRepo", method = RequestMethod.GET)
	public List<String> getRepository() throws SVNException
    {	
			return this.transactionalOperationsAdapter.getRepository();
    }
    *
    */
	
}
