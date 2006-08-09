package org.drools.runtime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SVNClient
    implements
    RepositoryClient {


    private static final String DROOLS_STATUS_KEY = "drools:status";
    private final SVNRepository repository;
    private final String status;
    
    
    /**
     * @param repoUrl URL to the actual repository host (HTTP or HTTPS only).
     * @param user User name
     * @param password
     * @param status The status to look for to get the latest resource.
     */
    public SVNClient(String repoUrl, String user, String password, String status) {
        
        DAVRepositoryFactory.setup();
        try { 
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(repoUrl));
            ISVNAuthenticationManager authManager = 
                         SVNWCUtil.createDefaultAuthenticationManager(user, password);
            repository.setAuthenticationManager(authManager);
            this.status = status;
        } catch (SVNException e) {
            throw new RuntimeException("Unable to connect to subversion rule repository.");
        }
        
        
    }
    
    public ResourceVersion getResource(String resourceURI) {
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            //load up the revisions, work out latest, then work backwards to find the appropriate version
            long revision = getLatestRevisionNumberForResource( resourceURI );

            //get latest...
            Map props = new HashMap();
            repository.getFile( resourceURI, -1, props, out );
            
            //now walk back until 
            while (!isCorrectStatus( props )) {
                props = new HashMap();
                out = new ByteArrayOutputStream();
                revision--;
                repository.getFile( resourceURI, revision, props, out );
            }       
            return new ResourceVersion(revision, out.toByteArray());
            
        } catch (SVNException e) {
            throw new RuntimeException("SVNException: " + e.getMessage());
        } finally {
            try {
                out.close();
            } catch ( IOException e ) {}
        }
        
        
    }

    /**
     * check to see if the status of the resource is correct.
     */
    private boolean isCorrectStatus(Map props) {
        return (props.containsKey( DROOLS_STATUS_KEY ) && props.get( DROOLS_STATUS_KEY ).equals( status ));
    }

    private long getLatestRevisionNumberForResource(String resourceURI) throws SVNException {
        Collection revs = repository.getFileRevisions( resourceURI, null, 1, repository.getLatestRevision() );
        SVNFileRevision[] fileRevisions = new SVNFileRevision[revs.size()];//(revs.toArray( new SVNFileRevision[] {} );
        revs.toArray( fileRevisions );
        Arrays.sort( fileRevisions );
        
        return  fileRevisions[fileRevisions.length - 1].getRevision();
    }

    public boolean hasResourceChanged(long latestVersionNumber,
                                      String resourceUri) {
        try {
            return getLatestRevisionNumberForResource( resourceUri ) != latestVersionNumber;
        } catch ( SVNException e ) {
            throw new RuntimeException(e.getMessage());
        }
        
    }

}
