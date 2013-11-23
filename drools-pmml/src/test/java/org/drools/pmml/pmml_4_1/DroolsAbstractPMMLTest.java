/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.pmml.pmml_4_1;


import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EventFactHandle;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.Variable;
import org.kie.internal.io.ResourceFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public abstract class DroolsAbstractPMMLTest {


    public static final String PMML = PMML4Compiler.PMML;
    public static final String BASE_PACK = DroolsAbstractPMMLTest.class.getPackage().getName();

    public static final String RESOURCE_PATH = "src/main/resources/" + BASE_PACK.replace('.','/') + "/";
    public static final String DEFAULT_KIEBASE = "PMML_Test";

    private KieSession kSession;
    private KieBase kbase;

    public DroolsAbstractPMMLTest() {
        super();
    }

    protected KieSession getModelSession( String pmmlSource ) {
        return getModelSession( new String[] {pmmlSource}, false );
    }

    protected KieSession getModelSession(String pmmlSource, boolean verbose) {
        return getModelSession(new String[] {pmmlSource}, verbose);
    }

    protected KieSession getModelSession( String[] pmmlSources, boolean verbose ) {
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieFileSystem kfs = ks.newKieFileSystem();

        for ( int j = 0; j < pmmlSources.length; j++ ) {
            Resource res = ResourceFactory.newClassPathResource( pmmlSources[ j ] ).setResourceType( ResourceType.PMML );
            kfs.write( "src/main/resources/" + pmmlSources[ j ].replace( ".xml", ".pmml" ) , res );
        }

        KieModuleModel model = ks.newKieModuleModel();
        KieBaseModel kbModel = model.newKieBaseModel( DEFAULT_KIEBASE )
                .setDefault( true )
                .addPackage( BASE_PACK )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        kfs.writeKModuleXML( model.toXML() );

        KieBuilder kb = ks.newKieBuilder( kfs );

        kb.buildAll();
        if ( kb.getResults().hasMessages( Message.Level.ERROR ) ) {
            throw new RuntimeException( "Build Errors:\n" + kb.getResults().toString() );
        }

        KieContainer kContainer = ks.newKieContainer( kr.getDefaultReleaseId() );

        KieBase kieBase = kContainer.getKieBase();
        return kieBase.newKieSession();
    }










    protected KieSession getSession(String theory) {
        KieBase kbase = readKnowledgeBase( new ByteArrayInputStream( theory.getBytes() ) );
        return kbase != null ? kbase.newKieSession() : null;
    }

    protected void refreshKSession() {
        if ( getKSession() != null ) {
            getKSession().dispose();
        }
        setKSession( getKbase().newKieSession());
    }








    private static KieBase readKnowledgeBase( InputStream theory ) {
        return readKnowledgeBase( Arrays.asList( theory ) );
    }

    private static KieBase readKnowledgeBase( List<InputStream> theory ) {
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieFileSystem kfs = ks.newKieFileSystem();

        for ( int j = 0; j < theory.size(); j++ ) {
            Resource res = ks.getResources().newInputStreamResource( theory.get( j ) );
            kfs.write( RESOURCE_PATH + "source_" + j + ".drl", res );
        }

        KieModuleModel model = ks.newKieModuleModel();
        KieBaseModel kbModel = model.newKieBaseModel( DEFAULT_KIEBASE )
                .setDefault( true )
                .addPackage( BASE_PACK )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        kfs.writeKModuleXML( model.toXML() );

        KieBuilder kb = ks.newKieBuilder( kfs );

        kb.buildAll();
        if ( kb.getResults().hasMessages( Message.Level.ERROR ) ) {
            throw new RuntimeException( "Build Errors:\n" + kb.getResults().toString() );
        }

        KieContainer kContainer = ks.newKieContainer( kr.getDefaultReleaseId() );

        return kContainer.getKieBase();
    }



    public String reportWMObjects(KieSession session) {
        PriorityQueue<String> queue = new PriorityQueue<String>();
        for (FactHandle fh : session.getFactHandles()) {
            Object o;
            if (fh instanceof EventFactHandle ) {
                EventFactHandle efh = (EventFactHandle) fh;
                queue.add("\t " + efh.getStartTimestamp() + "\t" + efh.getObject().toString() + "\n");
            } else {
                o = ((DefaultFactHandle) fh).getObject();
                queue.add("\t " + o.toString() + "\n");
            }

        }
        String ans = " ---------------- WM " + session.getObjects().size() + " --------------\n";
        while (! queue.isEmpty())
            ans += queue.poll();
        ans += " ---------------- END WM -----------\n";
        return ans;
    }


    private void dump(String s, OutputStream ostream) {
        // write to outstream
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(ostream, "UTF-8");
            writer.write(s);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (writer != null) {
                    writer.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    public KieSession getKSession() {
        return kSession;
    }

    public void setKSession( KieSession kSession ) {
        this.kSession = kSession;
    }

    public KieBase getKbase() {
        return kbase;
    }

    public void setKbase( KieBase kbase ) {
        this.kbase = kbase;
    }






    protected void checkFirstDataFieldOfTypeStatus(FactType type, boolean valid, boolean missing, String ctx, Object... target) {
        Class<?> klass = type.getFactClass();
        Iterator iter = getKSession().getObjects( new ClassObjectFilter( klass ) ).iterator();
        assertTrue( iter.hasNext() );
        Object obj = iter.next();
        if (ctx == null) {
            while ( type.get( obj, "context" ) != null && iter.hasNext() )
                obj = iter.next();
        } else {
            while ( ( ! ctx.equals( type.get( obj, "context" ) ) ) && iter.hasNext() )
                obj = iter.next();
        }
        Object tgt = type.get( obj, "value" );
        if ( tgt instanceof Double ) {
            assert( target[0] instanceof Double );
            assertEquals( (Double) target[0], (Double) tgt, 1e-6 );
        } else {
            assertEquals( target[0], tgt );
        }
        assertEquals( valid, type.get( obj, "valid" ) );
        assertEquals( missing, type.get( obj, "missing" ) );

    }


    protected double queryDoubleField( String target, String modelName ) {
        QueryResults results = getKSession().getQueryResults( target, modelName, Variable.v );
        assertEquals( 1, results.size() );

        return (Double) results.iterator().next().get( "$result" );
    }


    protected double queryIntegerField( String target, String modelName ) {
        QueryResults results = getKSession().getQueryResults( target, modelName, Variable.v );
        assertEquals( 1, results.size() );

        return (Integer) results.iterator().next().get( "$result" );
    }


    protected String queryStringField( String target, String modelName ) {
        QueryResults results = getKSession().getQueryResults( target, modelName, Variable.v );
        assertEquals( 1, results.size() );

        return (String) results.iterator().next().get( "$result" );
    }


    public Double getDoubleFieldValue( FactType type ) {
        Class<?> klass = type.getFactClass();
        Iterator iter = getKSession().getObjects( new ClassObjectFilter( klass ) ).iterator();
        Object obj = iter.next();
        return (Double) type.get( obj, "value" );
    }

    public Object getFieldValue( FactType type ) {
        Class<?> klass = type.getFactClass();
        Iterator iter = getKSession().getObjects( new ClassObjectFilter( klass ) ).iterator();
        Object obj = iter.next();
        return type.get( obj, "value" );
    }



}
