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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.compiler.PackageBuilder;

import com.sun.tools.xjc.Language;
import com.sun.tools.xjc.Options;

public class DroolsJaxbTest extends TestCase {

    public void test1() throws Exception {
        Options xjcOpts = new Options();
        xjcOpts.setSchemaLanguage( Language.XMLSCHEMA );
        PackageBuilder pkgBuilder = new PackageBuilder();
        
        InputStream stream = getClass().getResourceAsStream( "test.xsd" );
        String[] classNames = DroolsJaxbHelper.addModel( new InputStreamReader( stream ), pkgBuilder, xjcOpts, "xsd" );        
        
        assertFalse( pkgBuilder.hasErrors() );
        
        RuleBase rb = RuleBaseFactory.newRuleBase();
        rb.addPackage( pkgBuilder.getPackage() );
        
        JAXBContext jaxbCtx  = DroolsJaxbHelper.newInstance( classNames, rb );
        
        Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
        JAXBElement elm = (JAXBElement) unmarshaller.unmarshal( getClass().getResourceAsStream( "data.xml" ) );
        
        assertEquals( "com.oracle.sample3.USAddress", elm.getValue().getClass().getName() );
    }        
    
}
