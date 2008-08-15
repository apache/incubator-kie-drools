package org.drools.dataloaders.jaxb;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;

import com.sun.tools.xjc.Language;
import com.sun.tools.xjc.Options;

public class DroolsJaxbTest extends TestCase {

    public void testModelLoad() throws Exception {
        Options xjcOpts = new Options();
        xjcOpts.setSchemaLanguage( Language.XMLSCHEMA );
        PackageBuilder pkgBuilder = new PackageBuilder();

        InputStream stream = getClass().getResourceAsStream( "test.xsd" );
        String[] classNames = DroolsJaxbHelper.addModel( new InputStreamReader( stream ),
                                                         pkgBuilder,
                                                         xjcOpts,
                                                         "xsd" );

        assertFalse( pkgBuilder.hasErrors() );

        RuleBase rb = RuleBaseFactory.newRuleBase();
        rb.addPackage( pkgBuilder.getPackage() );

        JAXBContext jaxbCtx = DroolsJaxbHelper.newInstance( classNames,
                                                            rb );
        Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
        JAXBElement elm = ( JAXBElement ) unmarshaller.unmarshal( getClass().getResourceAsStream( "data.xml" ) );
       
        assertEquals( "com.oracle.sample3.USAddress",
                      elm.getValue().getClass().getName() );        
    }

    public void testDirectRoot() throws Exception {
        Options xjcOpts = new Options();
        xjcOpts.setSchemaLanguage( Language.XMLSCHEMA );
        PackageBuilder pkgBuilder = new PackageBuilder();

        InputStream stream = getClass().getResourceAsStream( "order.xsd" );
        String[] classNames = DroolsJaxbHelper.addModel( new InputStreamReader( stream ),
                                                         pkgBuilder,
                                                         xjcOpts,
                                                         "xsd" );

        assertFalse( pkgBuilder.hasErrors() );
        
        pkgBuilder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Jaxb.drl" ) ) );

        RuleBase rb = RuleBaseFactory.newRuleBase();
        rb.addPackages( pkgBuilder.getPackages() );

        JAXBContext jaxbCtx = DroolsJaxbHelper.newInstance( classNames,
                                                            rb );
        //        
        Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();

        StatefulSession session = rb.newStatefulSession();
        List list1 = new ArrayList();
        session.setGlobal( "list1", list1 );        
                
        DroolsJaxbStatefulSession dataLoader = new DroolsJaxbStatefulSession( session,
                                                                              unmarshaller );       
        
        Map<FactHandle, Object> handles = dataLoader.insertUnmarshalled( new InputStreamReader( getClass().getResourceAsStream( "order.xml" ) ) );

        session.fireAllRules();

        assertEquals( 1,
                      handles.size() );
        assertEquals( 1,
                      list1.size() );

        assertEquals( "org.drools.model.order.Order",
                      list1.get( 0 ).getClass().getName() );
    }

    
    public void testNestedIterable() throws Exception {
        Options xjcOpts = new Options();
        xjcOpts.setSchemaLanguage( Language.XMLSCHEMA );
        PackageBuilder pkgBuilder = new PackageBuilder();

        InputStream stream = getClass().getResourceAsStream( "order.xsd" );
        String[] classNames = DroolsJaxbHelper.addModel( new InputStreamReader( stream ),
                                                         pkgBuilder,
                                                         xjcOpts,
                                                         "xsd" );

        assertFalse( pkgBuilder.hasErrors() );
        
        pkgBuilder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_Jaxb.drl" ) ) );

        RuleBase rb = RuleBaseFactory.newRuleBase();
        rb.addPackages( pkgBuilder.getPackages() );

        JAXBContext jaxbCtx = DroolsJaxbHelper.newInstance( classNames,
                                                            rb );
        //        
        Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();

        StatefulSession session = rb.newStatefulSession();
        List list1 = new ArrayList();
        List list2 = new ArrayList();
        session.setGlobal( "list1", list1 );
        session.setGlobal( "list2", list2 );
        
        DroolsJaxbConfiguration configuration = new DroolsJaxbConfiguration();
        configuration.setIterableGetter( "this.orderItem" );
        
        DroolsJaxbStatefulSession dataLoader = new DroolsJaxbStatefulSession( session,
                                                                              unmarshaller,
                                                                              configuration );
                     
        Map<FactHandle, Object> handles = dataLoader.insertUnmarshalled( new InputStreamReader( getClass().getResourceAsStream( "order.xml" ) ) );

        session.fireAllRules();

        assertEquals( 2,
                      handles.size() );
        assertEquals( 1,
                      list1.size() );
        assertEquals( 1,
                      list2.size() );        

        assertEquals( "org.drools.model.order.Order$OrderItem",
                      list1.get( 0 ).getClass().getName() );
        
        assertEquals( "org.drools.model.order.Order$OrderItem",
                      list2.get( 0 ).getClass().getName() );
        
        assertNotSame( list1.get(0), list2.get(0) );
    }    
}
