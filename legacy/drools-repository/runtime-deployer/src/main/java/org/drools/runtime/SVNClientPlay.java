package org.drools.runtime;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.SVNException;

public class SVNClientPlay {

    public SVNClientPlay() {
        
        //Set up connection protocols support:
        //for DAV (over http and https)
        DAVRepositoryFactory.setup();
        String url="http://localhost:8080/svn/myrepos";
        String name="xxx";
        String password="xxx";
        SVNRepository repository = null;
        DAVRepositoryFactory.setup();
        try { 
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
            ISVNAuthenticationManager authManager = 
                         SVNWCUtil.createDefaultAuthenticationManager(name, password);
            repository.setAuthenticationManager(authManager);
            
            
            Map props = new HashMap();
            OutputStream out = new ByteArrayOutputStream();
            
            String resource = "drools/rules/michael.drl";
            
            
            

            //load up the revisions, work out latest, then work backwards to find the appropriate version
            Collection revs = repository.getFileRevisions( resource, null, 1, repository.getLatestRevision() );
            SVNFileRevision[] fileRevisions = new SVNFileRevision[revs.size()];//(revs.toArray( new SVNFileRevision[] {} );
            revs.toArray( fileRevisions );
            Arrays.sort( fileRevisions );
            
            long startRevision = fileRevisions[fileRevisions.length - 1].getRevision();
            
            repository.getFile( resource, -1, props, out );
            while (!(props.containsKey( "drools:status" ) && props.get( "drools:status" ).equals( "production" ))) {
                System.out.println("going back a version...");
                out = new ByteArrayOutputStream();
                startRevision--;
                repository.getFile( resource, startRevision, props, out );
            }
            
            System.out.println("we have prod !");
            //System.out.println(out.toString());
            
            
            



            
        
            
        } catch (SVNException e){
            e.printStackTrace();
            System.exit(1);
        }        
    }
    
    public static void main(String[] args) {
        SVNClientPlay client = new SVNClientPlay();
        
    }
    
}
