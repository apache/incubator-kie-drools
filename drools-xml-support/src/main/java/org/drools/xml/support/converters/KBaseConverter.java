/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.xml.support.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.compiler.kproject.KieModuleException;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.kproject.models.KieSessionModelImpl;
import org.drools.compiler.kproject.models.RuleTemplateModelImpl;
import org.drools.util.StringUtils;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.RuleTemplateModel;
import org.kie.api.conf.BetaRangeIndexOption;
import org.kie.api.conf.DeclarativeAgendaOption;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.KieBaseMutabilityOption;
import org.kie.api.conf.PrototypesOption;
import org.kie.api.conf.SequentialOption;
import org.kie.api.conf.SessionsPoolOption;

import static org.kie.api.conf.SequentialOption.YES;

public class KBaseConverter extends AbstractXStreamConverter {

    public KBaseConverter() {
        super( KieBaseModelImpl.class );
    }

    public void marshal(Object value,
                        HierarchicalStreamWriter writer,
                        MarshallingContext context) {
        KieBaseModelImpl kBase = (KieBaseModelImpl) value;
        writer.addAttribute( "name", kBase.getName() );
        writer.addAttribute( "default", Boolean.toString(kBase.isDefault()) );
        if ( kBase.getEventProcessingMode() != null ) {
            writer.addAttribute( "eventProcessingMode", kBase.getEventProcessingMode().getMode() );
        }
        if ( kBase.getPrototypes() != null ) {
            writer.addAttribute( "prototypes", kBase.getPrototypes().toString().toLowerCase() );
        }
        if ( kBase.getEqualsBehavior() != null ) {
            writer.addAttribute( "equalsBehavior", kBase.getEqualsBehavior().toString().toLowerCase() );
        }
        if ( kBase.getMutability() != null ) {
            writer.addAttribute( "mutability", kBase.getMutability().toString().toLowerCase() );
        }
        if ( kBase.getDeclarativeAgenda() != null ) {
            writer.addAttribute( "declarativeAgenda", kBase.getDeclarativeAgenda().toString().toLowerCase() );
        }
        if ( kBase.getSequential() != null ) {
            writer.addAttribute( "sequential", kBase.getSequential() == YES ? "true" : "false" );
        }
        if ( kBase.getSessionsPool() != null ) {
            writer.addAttribute( "sessionsPool", "" + kBase.getSessionsPool().getSize() );
        }
        if ( kBase.getBetaRangeIndexOption() != null ) {
            writer.addAttribute( "betaRangeIndex", kBase.getBetaRangeIndexOption().toString().toLowerCase() );
        }

        if ( kBase.getScope() != null ) {
            writer.addAttribute( "scope", kBase.getScope() );
        }

        if ( ! kBase.getPackages().isEmpty() ) {
            StringBuilder buf = new StringBuilder();
            boolean first = true;
            for( String pkg : kBase.getPackages() ) {
                if( first ) {
                    first = false;
                } else {
                    buf.append( ", " );
                }
                buf.append( pkg );
            }
            writer.addAttribute( "packages", buf.toString() );
        }
        if ( !kBase.getIncludes().isEmpty() ) {
            StringBuilder sb = new StringBuilder();
            boolean insertComma = false;
            for ( String include : kBase.getIncludes() ) {
                if ( insertComma ) {
                    sb.append( ", " );
                }
                sb.append( include );
                if ( !insertComma ) {
                    insertComma = true;
                }
            }
            writer.addAttribute( "includes", sb.toString() );
        }

        for ( RuleTemplateModel ruleTemplateModel : kBase.getRuleTemplates()) {
            writeObject( writer, context, "ruleTemplate", ruleTemplateModel);
        }

        for ( KieSessionModel kSessionModel : kBase.getKieSessionModels().values()) {
            writeObject( writer, context, "ksession", kSessionModel);
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader,
                            final UnmarshallingContext context) {
        final KieBaseModelImpl kBase = new KieBaseModelImpl();

        String kbaseName = reader.getAttribute( "name" );
        if (kbaseName == null) {
            kbaseName = StringUtils.uuid();
        } else if (kbaseName.isEmpty()) {
            throw new KieModuleException("kbase name is empty in kmodule.xml");
        }
        kBase.setNameForUnmarshalling( kbaseName );

        kBase.setDefault( "true".equals(reader.getAttribute( "default" )) );

        String eventMode = reader.getAttribute( "eventProcessingMode" );
        if ( eventMode != null ) {
            kBase.setEventProcessingMode( EventProcessingOption.determineEventProcessingMode( eventMode ) );
        }

        String prototypes = reader.getAttribute( "prototypes" );
        if ( prototypes != null ) {
            kBase.setPrototypes( PrototypesOption.determinePrototypesOption(prototypes) );
        }

        String equalsBehavior = reader.getAttribute( "equalsBehavior" );
        if ( equalsBehavior != null ) {
            kBase.setEqualsBehavior( EqualityBehaviorOption.determineEqualityBehavior( equalsBehavior ) );
        }

        String mutability = reader.getAttribute( "mutability" );
        if ( mutability != null ) {
            kBase.setMutability( KieBaseMutabilityOption.determineMutability( mutability ) );
        }

        String declarativeAgenda = reader.getAttribute( "declarativeAgenda" );
        if ( declarativeAgenda != null ) {
            kBase.setDeclarativeAgenda( DeclarativeAgendaOption.determineDeclarativeAgenda( declarativeAgenda ) );
        }

        String sequential = reader.getAttribute( "sequential" );
        if ( sequential != null ) {
            kBase.setSequential( SequentialOption.determineSequential( sequential ) );
        }

        String sessionsPool = reader.getAttribute( "sessionsPool" );
        if ( sessionsPool != null ) {
            kBase.setSessionsPool( SessionsPoolOption.get( Integer.parseInt( sessionsPool ) ) );
        }

        String betaRangeIndex = reader.getAttribute( "betaRangeIndex" );
        if ( betaRangeIndex != null ) {
            kBase.setBetaRangeIndexOption( BetaRangeIndexOption.determineBetaRangeIndex( betaRangeIndex ) );
        }

        String scope = reader.getAttribute( "scope" );
        if ( scope != null ) {
            kBase.setScope( scope.trim() );
        }

        String pkgs = reader.getAttribute( "packages" );
        if( pkgs != null ) {
            for( String pkg : pkgs.split( "," ) ) {
                kBase.addPackage( pkg.trim() );
            }
        }

        String includes = reader.getAttribute( "includes" );
        if( includes != null ) {
            for( String include : includes.split( "," ) ) {
                kBase.addInclude( include.trim() );
            }
        }

        readNodes( reader, new AbstractXStreamConverter.NodeReader() {
            public void onNode(HierarchicalStreamReader reader,
                               String name,
                               String value) {
                if ( "ksession".equals( name ) ) {
                    KieSessionModelImpl kSession = readObject( reader, context, KieSessionModelImpl.class );
                    kBase.getRawKieSessionModels().put( kSession.getName(), kSession );
                    kSession.setKBase(kBase);
                } else if ( "ruleTemplate".equals( name ) ) {
                    RuleTemplateModelImpl ruleTemplate = readObject( reader, context, RuleTemplateModelImpl.class );
                    kBase.getRawRuleTemplates().add( ruleTemplate );
                    ruleTemplate.setKBase( kBase );
                }

                // @TODO we don't use support nested includes
//                    if ( "includes".equals( name ) ) {
//                        for ( String include : readList( reader ) ) {
//                            kBase.addInclude( include );
//                        }
//                    }
            }
        } );
        return kBase;
    }
}