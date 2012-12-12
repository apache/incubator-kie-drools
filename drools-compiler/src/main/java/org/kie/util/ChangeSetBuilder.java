/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.drools.compiler.DrlParser;
import org.drools.io.impl.ByteArrayResource;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.kie.builder.impl.InternalKieModule;
import org.kie.io.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeSetBuilder {
    
    private final Logger logger = LoggerFactory.getLogger( ChangeSetBuilder.class );

    public KieJarChangeSet build( InternalKieModule original, InternalKieModule currentJar ) {
        KieJarChangeSet result = new KieJarChangeSet();
        
        Collection<String> originalFiles = original.getFileNames();
        Collection<String> currentFiles = currentJar.getFileNames();
        
        ArrayList<String> removedFiles = new ArrayList<String>( originalFiles );
        removedFiles.removeAll( currentFiles );
        if( ! removedFiles.isEmpty() ) {
            for( String file : removedFiles ) {
                // there should be a way to get the JAR name/url to produce a proper URL for the file in it
                result.getChanges().put( file, new ResourceChangeSet( file, ChangeType.REMOVED ) );
            }
        }

        for( String file : currentFiles ) {
            if( originalFiles.contains( file ) ) {
                // check for modification
                byte[] ob = original.getBytes( file );
                byte[] cb = currentJar.getBytes( file );
                if( ! Arrays.equals( ob, cb ) ) {
                    // parse the file to figure out the difference
                    result.getChanges().put( file, diffResource( file, ob, cb ) );
                }
            } else {
                // file was added
                result.getChanges().put( file, new ResourceChangeSet( file, ChangeType.ADDED ) );
            }
        }
        
        return result;
    }


    public ResourceChangeSet diffResource(String file,
                                          byte[] ob,
                                          byte[] cb) {
        ResourceChangeSet pkgcs = new ResourceChangeSet( file, ChangeType.UPDATED );
        ResourceType type = ResourceType.determineResourceType( file );
        if( ResourceType.DRL.equals( type ) ) {
            try {
                PackageDescr opkg = new DrlParser().parse( new ByteArrayResource( ob ) );
                PackageDescr cpkg = new DrlParser().parse( new ByteArrayResource( cb ) );
                
                List<RuleDescr> orules = new ArrayList<RuleDescr>( opkg.getRules() ); // needs to be cloned
                List<RuleDescr> crules = cpkg.getRules();
                
                for( RuleDescr crd : crules ) {
                    // unfortunately have to iterate search for a rule with the same name
                    boolean found = false;
                    for( Iterator<RuleDescr> it = orules.iterator(); it.hasNext(); ) {
                        RuleDescr ord = it.next();
                        if( ord.getName().equals( crd ) ) {
                            found = true;
                            it.remove();
                            if( !ord.equals( crd ) ) {
                                pkgcs.getChanges().add( new ResourceChange( ChangeType.UPDATED, 
                                                                            ResourceChange.Type.RULE, 
                                                                            crd.getName() ) );
                            }
                            break;
                        }
                    }
                    if( !found ) {
                        pkgcs.getChanges().add( new ResourceChange( ChangeType.ADDED, 
                                                                    ResourceChange.Type.RULE, 
                                                                    crd.getName() ) );
                    }
                }
                
                for( RuleDescr ord : orules ) {
                    pkgcs.getChanges().add( new ResourceChange( ChangeType.REMOVED, 
                                                                ResourceChange.Type.RULE, 
                                                                ord.getName() ) );
                }
            } catch ( Exception e ) {
                logger.error( "Error analyzing the contents of "+file+". Skipping.", e );
            }
        }
        Collections.sort( pkgcs.getChanges(), new Comparator<ResourceChange>() {
            public int compare(ResourceChange o1,
                               ResourceChange o2) {
                return o1.getChangeType().ordinal() - o2.getChangeType().ordinal();
            }
        } );
        return pkgcs;
    }
    
    public String toProperties( KieJarChangeSet kcs ) {
        StringBuilder builder = new StringBuilder();
        builder.append( "kiejar.changeset.version=1.0\n" );
        int i = 0;
        for( ResourceChangeSet rcs : kcs.getChanges().values() ) {
            String prefix = "kiejar.changeset."+rcs.getChangeType()+".r"+(i++);
            builder.append( prefix )
                   .append( "=" )
                   .append( rcs.getResourceName() )
                   .append( "\n" );
            int j = 0;
            for( ResourceChange change : rcs.getChanges() ) {
                builder.append( prefix )
                       .append( "." )
                       .append( change.getChangeType() )
                       .append( "." )
                       .append( change.getType() )
                       .append( j++ )
                       .append( "=" )
                       .append( change.getName() )
                       .append( "\n" );
            }
        }
        
        return builder.toString();
    }
    
}
