package com.concur.worksimple.adaptee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SVNOperations extends Adaptee{
	
	private String root;
	private String username;
	private String password;
	private SVNClientManager ourClientManager;
	private SVNRepository repository = null;
	private SVNWCClient svnWCClient= null;
	private ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
	private SVNCommitClient svnCommitClient = null;
	private SVNUpdateClient svnUpdateClient = null;
	private File wcDir = null;
	private String myWorkingCopyPath = null;
	
	@SuppressWarnings("deprecation")
	public SVNOperations(String root,String un,String pass,String wc) {
		this.root = root;
		this.username=un;
		this.password=pass;
		this.myWorkingCopyPath = wc;
		
		this.ourClientManager = SVNClientManager.newInstance(options, SVNWCUtil.createDefaultAuthenticationManager( username , password ));
		this.svnCommitClient = ourClientManager.getCommitClient();
		this.svnWCClient= ourClientManager.getWCClient();
		this.svnUpdateClient = ourClientManager.getUpdateClient( );
		this.wcDir = new File( myWorkingCopyPath );		
		
		configRepository();		
	}
	
	@Override
	public List<String> getRepository() throws SVNException {
		List<String> entryNameList = new ArrayList<String>();
		return listEntries(entryNameList, repository,"");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void createDirectory(String path) throws SVNException{
		SVNURL svnurl = SVNURL.parseURIDecoded( root+path );
		this.makeDirectory(svnurl, "Creating folder");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public SVNCommitInfo save(String path, File input) throws SVNException, IOException {
		//Checkout
		doCheckOut();
		
		File existingFile = new File(wcDir+"//"+path+"//"+input.getName());
		
		File[] files = new File[] {existingFile};
		InputStream inputStream =  new FileInputStream(input); 
		
		OutputStream outputStream = new FileOutputStream(existingFile);
		IOUtils.copy(inputStream,outputStream);
		
		//Unlock the file
		if(this.listAllFilesInDir(path).contains(existingFile.getName()))
			this.svnWCClient.doUnlock(files, true);
		
		//Add		
		this.svnWCClient.doAdd(existingFile, true, false, false, false);
		
		// Commit
		return this.commit(existingFile, false, "Commiting File");
	}
	
	@SuppressWarnings("static-access")
	@Override
	public List<String> listAllFilesInDir(String path) throws SVNException {
		List<String> entryNameList = new ArrayList<String>();
		return this.listEntries(entryNameList, repository, path);
	}
	
	@Override
	public long getLatestVersion(String path, String name) throws SVNException {
		File existingFile = new File(wcDir+"//"+path+"//"+name);
		return this.svnWCClient.doInfo(existingFile, SVNRevision.HEAD).getRevision().getNumber();
	}
	
	@Override
	public File getFile(String path, String name, Long Lversion) throws SVNException, FileNotFoundException {
		//Checkout
		//doCheckOut();
		
		//Return file contents
		long version = (( Lversion == null ? SVNRevision.HEAD.getNumber(): Lversion.longValue()) ); 
		File existingFile = new File(wcDir+"//"+path+"//"+name);
		File newFile = new File(name);
		boolean lockExists = false;
		SVNLock lock = this.getStatus(existingFile,version).getLock();
		if(lock != null) {
			lockExists = true;
		}
		
		//Check if file is locked
		if(!lockExists) {						
			SVNRevision rvn = SVNRevision.create(version );
			OutputStream dst = new FileOutputStream(newFile);	
			
			this.svnWCClient.doGetFileContents(existingFile, SVNRevision.HEAD, rvn, false, dst);
			
			//Lock file
			//this.lock(existingFile, true, "Working on it");
		}else {
			//Throw exception telling its locked
			throw new SVNException(SVNErrorMessage.create(SVNErrorCode.WC_LOCKED));
		}		
		return newFile;
	}
	
	@Override
	public long update(String path, String name, long version) throws SVNException {
		SVNRevision rvn = SVNRevision.create(version);
		File existingFile = new File(wcDir+"//"+path+"//"+name);
		File[] files = new File[] {existingFile};
		//Unlock the file
		if(this.listAllFilesInDir(path).contains(existingFile.getName()))
			this.svnWCClient.doUnlock(files, true);
		
		this.update(existingFile, rvn, false);
		return this.commit(existingFile, false, "Updating to another version").getNewRevision();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void delete(String path, String name) throws SVNException {
/*		File existingFile = new File(wcDir+"//"+path+"//"+name);
		File[] files = new File[] {existingFile};
		//Unlock the file
		if(this.listAllFilesInDir(path).contains(existingFile.getName()))
			this.svnWCClient.doUnlock(files, true);*/

		SVNURL svnurl = SVNURL.parseURIDecoded( root+path+"/"+name );
		this.delete(svnurl, "Deleting file");
		this.svnWCClient.doCleanup(wcDir,false);
	}
	
	@Override
	public void createFile(String path, File input) throws SVNException {
		File existingFile = new File(wcDir+"//"+path+"//"+input.getName());
		this.addEntry(existingFile);
		this.commit(existingFile, false, "Adding new File");
	}
	
	@SuppressWarnings("rawtypes")
	private static List<String> listEntries( List<String> entryNameList,SVNRepository repository, String path ) throws SVNException {
		   Collection entries = repository.getDir( path, -1 , null , (Collection) null );
		   Iterator iterator = entries.iterator( );
			   while ( iterator.hasNext( ) ) {
				   SVNDirEntry entry = ( SVNDirEntry ) iterator.next( );
				   entryNameList.add(entry.getName( ));
					   if ( entry.getKind() == SVNNodeKind.DIR ) {
						   listEntries(entryNameList, repository, ( path.equals( "" ) ) ? entry.getName( ) : path + "/" + entry.getName( ) );
			     }
		  }
	   return entryNameList;
	}

	private SVNCommitInfo makeDirectory( SVNURL url , String commitMessage ) throws SVNException {
		SVNCommitInfo svmcomm = svnCommitClient.doMkDir( new SVNURL[] { url } , commitMessage );
		return svmcomm;
	}
	
	@SuppressWarnings({ "deprecation", "unused" })
	private SVNCommitInfo addNewEntry( String path, File wcPath ) throws SVNException {
		SVNURL svnurl = SVNURL.parseURIDecoded( root+path+"/"+wcPath.getName() );
		return svnCommitClient.doImport(wcPath, svnurl, "Adding files", true);
	}
	
	@SuppressWarnings("deprecation")
	private long checkout( SVNURL url , SVNRevision revision , File destPath , boolean isRecursive ) throws SVNException {
        this.svnUpdateClient.setIgnoreExternals( false );
        return this.svnUpdateClient.doCheckout( url , destPath , revision , revision , isRecursive );
    }
	
	@SuppressWarnings("deprecation")
	private void configRepository() {
		try {
			this.repository = SVNRepositoryFactory.create( SVNURL.parseURIDecoded( root ) );
			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager( username , password );
			repository.setAuthenticationManager( authManager );		
		} catch (SVNException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public void doCheckOut() {
		try {
			this.checkout(SVNURL.parseURIDecoded(root), SVNRevision.HEAD, wcDir, true);
		} catch (SVNException e1) {
			e1.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	private SVNCommitInfo commit( File wcPath , boolean keepLocks , String commitMessage ) throws SVNException {
        return this.svnCommitClient.doCommit( new File[] { wcPath } , keepLocks , commitMessage , false , true );
    }
	
    @SuppressWarnings("deprecation")
	private long update( File wcPath , SVNRevision updateToRevision , boolean isRecursive ) throws SVNException {
        this.svnUpdateClient.setIgnoreExternals( false );
        return this.svnUpdateClient.doUpdate( wcPath , updateToRevision , isRecursive,true );
    }
    
    private SVNInfo getStatus(File existingFile, long version) throws SVNException {
    	SVNRevision rvn = SVNRevision.create(version);
    	return this.svnWCClient.doInfo(existingFile, rvn);
    }
    
    private SVNCommitInfo delete( SVNURL url,String msg) throws SVNException {
    	return this.svnCommitClient.doDelete(new SVNURL[] { url }, msg);
    }
    
    @SuppressWarnings({ "unused", "deprecation" })
	private void addEntry( File wcPath ) throws SVNException {
    	this.svnWCClient.doAdd( wcPath , false , false , false , true );
    }

}
