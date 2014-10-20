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
package org.drools.compiler.kie.util;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.FunctionDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.io.impl.ByteArrayResource;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.ChangeType;
import org.kie.internal.builder.ResourceChange;
import org.kie.internal.builder.ResourceChangeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static org.drools.core.util.StringUtils.isEmpty;

public class ChangeSetBuilder {
    
    private final Logger logger = LoggerFactory.getLogger( ChangeSetBuilder.class );

    private String defaultPackageName;

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
        if( ResourceType.DRL.equals( type ) || ResourceType.GDRL.equals( type ) || ResourceType.RDRL.equals( type ) || ResourceType.TDRL.equals( type )) {
            try {
                PackageDescr opkg = new DrlParser().parse( new ByteArrayResource( ob ) );
                PackageDescr cpkg = new DrlParser().parse( new ByteArrayResource( cb ) );
                String pkgName = isEmpty(cpkg.getName()) ? getDefaultPackageName() : cpkg.getName();

                for( RuleDescr crd : cpkg.getRules() ) {
                    pkgcs.getLoadOrder().add(new ResourceChangeSet.RuleLoadOrder(pkgName, crd.getName(), crd.getLoadOrder()));
                }

                List<RuleDescr> orules = new ArrayList<RuleDescr>( opkg.getRules() ); // needs to be cloned
                diffDescrs(ob, cb, pkgcs, orules, cpkg.getRules(), ResourceChange.Type.RULE, RULE_CONVERTER);

                List<FunctionDescr> ofuncs = new ArrayList<FunctionDescr>( opkg.getFunctions() ); // needs to be cloned
                diffDescrs(ob, cb, pkgcs, ofuncs, cpkg.getFunctions(), ResourceChange.Type.FUNCTION, FUNC_CONVERTER);
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

    private interface DescrNameConverter<T extends BaseDescr> {
        String getName(T descr);
    }

    private static final RuleDescrNameConverter RULE_CONVERTER = new RuleDescrNameConverter();
    private static class RuleDescrNameConverter implements DescrNameConverter<RuleDescr> {
        @Override
        public String getName(RuleDescr descr) {
            return descr.getName();
        }
    }

    private static final FuncDescrNameConverter FUNC_CONVERTER = new FuncDescrNameConverter();
    private static class FuncDescrNameConverter implements DescrNameConverter<FunctionDescr> {
        @Override
        public String getName(FunctionDescr descr) {
            return descr.getName();
        }
    }

    private <T extends BaseDescr> void diffDescrs(byte[] ob, byte[] cb,
                                                  ResourceChangeSet pkgcs,
                                                  List<T> odescrs, List<T> cdescrs,
                                                  ResourceChange.Type type, DescrNameConverter<T> descrNameConverter) {
        for( T crd : cdescrs ) {
            String cName = descrNameConverter.getName(crd);

            // unfortunately have to iterate search for a rule with the same name
            boolean found = false;
            for( Iterator<T> it = odescrs.iterator(); it.hasNext(); ) {
                T ord = it.next();
                if( descrNameConverter.getName(ord).equals( cName ) ) {
                    found = true;
                    it.remove();

                    // using byte[] comparison because using the descriptor equals() method
                    // is brittle and heavier than iterating an array
                    if( !segmentEquals(ob, ord.getStartCharacter(), ord.getEndCharacter(),
                            cb, crd.getStartCharacter(), crd.getEndCharacter() ) ) {
                        pkgcs.getChanges().add( new ResourceChange( ChangeType.UPDATED,
                                                                    type,
                                                                    cName ) );
                    }
                    break;
                }
            }
            if( !found ) {
                pkgcs.getChanges().add( new ResourceChange( ChangeType.ADDED,
                                                            type,
                                                            cName ) );
            }
        }

        for( T ord : odescrs ) {
            pkgcs.getChanges().add( new ResourceChange( ChangeType.REMOVED,
                                                        type,
                                                        descrNameConverter.getName(ord) ) );
        }
    }

    private boolean segmentEquals( byte[] a1, int s1, int e1, 
                                     byte[] a2, int s2, int e2) {
        int length = e1 - s1;
        if( length <= 0 || length != e2-s2 || s1+length > a1.length || s2+length > a2.length ) {
            return false;
        }
        for( int i = 0; i < length; i++ ) {
            if( a1[s1+i] != a2[s2+i] ) {
                return false;
            }
        }
        return true;
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

    private String getDefaultPackageName() {
        if (defaultPackageName == null) {
            defaultPackageName = new KnowledgeBuilderConfigurationImpl().getDefaultPackageName();
        }
        return defaultPackageName;
    }
}
