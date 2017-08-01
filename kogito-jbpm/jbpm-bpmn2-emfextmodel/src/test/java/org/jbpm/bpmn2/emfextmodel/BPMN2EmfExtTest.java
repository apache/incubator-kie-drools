/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.bpmn2.emfextmodel;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.jboss.drools.DocumentRoot;
import org.jboss.drools.DroolsFactory;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.GlobalType;
import org.jboss.drools.ImportType;
import org.jboss.drools.MetaDataType;
import org.jboss.drools.OnEntryScriptType;
import org.jboss.drools.OnExitScriptType;
import org.jboss.drools.util.DroolsResourceFactoryImpl;

import junit.framework.TestCase;

public class BPMN2EmfExtTest extends TestCase {
    private ResourceSet resourceSet;
    
    @Override
    protected void setUp() throws Exception {
        resourceSet = new ResourceSetImpl();
        
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put
            (Resource.Factory.Registry.DEFAULT_EXTENSION, 
             new DroolsResourceFactoryImpl());
        resourceSet.getPackageRegistry().put
            (DroolsPackage.eNS_URI, 
            		DroolsPackage.eINSTANCE);
    }
    
    @Override
    protected void tearDown() throws Exception {
    }
    
    @SuppressWarnings("unchecked")
	public void testMetadataElement() throws Exception {
    	// write
    	XMLResource inResource = (XMLResource) resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));
        inResource.getDefaultLoadOptions().put(XMLResource.OPTION_ENCODING, "UTF-8");
        inResource.setEncoding("UTF-8");
        DocumentRoot documentRoot = DroolsFactory.eINSTANCE.createDocumentRoot();
        
        MetaDataType metadataType =  DroolsFactory.eINSTANCE.createMetaDataType();
        metadataType.setName("testvalue");
        metadataType.setMetaValue("testentry"); 
        
        documentRoot.setMetaData(metadataType);
        inResource.getContents().add(documentRoot);
        
        StringWriter stringWriter = new StringWriter();
        inResource.save(stringWriter, null);
        assertNotNull(stringWriter.getBuffer().toString());
        if(stringWriter.getBuffer().toString().length() < 1) {
            fail("generated xml is empty");
        }
    	
        // read
        XMLResource outResource = (XMLResource) resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));
        outResource.getDefaultLoadOptions().put(XMLResource.OPTION_ENCODING, "UTF-8");
        outResource.setEncoding("UTF-8");
        Map<String, Object> options = new HashMap<String, Object>();
        options.put( XMLResource.OPTION_ENCODING, "UTF-8" );
        InputStream is = new ByteArrayInputStream(stringWriter.getBuffer().toString().getBytes("UTF-8"));
        outResource.load(is, options);
        
        DocumentRoot outRoot = (DocumentRoot) outResource.getContents().get(0);
        assertNotNull(outRoot.getMetaData());
        MetaDataType outMetadataType =  outRoot.getMetaData();
        assertEquals(outMetadataType.getName(), "testvalue");
        assertEquals(outMetadataType.getMetaValue(), "testentry");
        
    }
    
    public void testOnEntryScriptElement() throws Exception {
        // write
        XMLResource inResource = (XMLResource) resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));
        inResource.getDefaultLoadOptions().put(XMLResource.OPTION_ENCODING, "UTF-8");
        inResource.setEncoding("UTF-8");
        DocumentRoot documentRoot = DroolsFactory.eINSTANCE.createDocumentRoot();
        OnEntryScriptType root = DroolsFactory.eINSTANCE.createOnEntryScriptType();
        root.setScript("script");
        root.setScriptFormat("format");
        documentRoot.setOnEntryScript(root);
        inResource.getContents().add(documentRoot);
        
        StringWriter stringWriter = new StringWriter();
        inResource.save(stringWriter, null);
        assertNotNull(stringWriter.getBuffer().toString());
        if(stringWriter.getBuffer().toString().length() < 1) {
            fail("generated xml is empty");
        }
        
        // read
        XMLResource outResource = (XMLResource) resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));
        outResource.getDefaultLoadOptions().put(XMLResource.OPTION_ENCODING, "UTF-8");
        outResource.setEncoding("UTF-8");
        Map<String, Object> options = new HashMap<String, Object>();
        options.put( XMLResource.OPTION_ENCODING, "UTF-8" );
        InputStream is = new ByteArrayInputStream(stringWriter.getBuffer().toString().getBytes("UTF-8"));
        outResource.load(is, options);
        
        DocumentRoot outRoot = (DocumentRoot) outResource.getContents().get(0);
        assertNotNull(outRoot.getOnEntryScript());
        OnEntryScriptType scriptType = outRoot.getOnEntryScript();
        assertEquals("script", scriptType.getScript());
        assertEquals("format", scriptType.getScriptFormat());
    }
    
    public void testOnExitScriptElement() throws Exception {
        // write
        XMLResource inResource = (XMLResource) resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));
        inResource.getDefaultLoadOptions().put(XMLResource.OPTION_ENCODING, "UTF-8");
        inResource.setEncoding("UTF-8");
        DocumentRoot documentRoot = DroolsFactory.eINSTANCE.createDocumentRoot();
        OnExitScriptType root = DroolsFactory.eINSTANCE.createOnExitScriptType();
        root.setScript("script");
        root.setScriptFormat("format");
        documentRoot.setOnExitScript(root);
        inResource.getContents().add(documentRoot);
        
        StringWriter stringWriter = new StringWriter();
        inResource.save(stringWriter, null);
        assertNotNull(stringWriter.getBuffer().toString());
        if(stringWriter.getBuffer().toString().length() < 1) {
            fail("generated xml is empty");
        }
        
        // read
        XMLResource outResource = (XMLResource) resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));
        outResource.getDefaultLoadOptions().put(XMLResource.OPTION_ENCODING, "UTF-8");
        outResource.setEncoding("UTF-8");
        Map<String, Object> options = new HashMap<String, Object>();
        options.put( XMLResource.OPTION_ENCODING, "UTF-8" );
        InputStream is = new ByteArrayInputStream(stringWriter.getBuffer().toString().getBytes("UTF-8"));
        outResource.load(is, options);
        
        DocumentRoot outRoot = (DocumentRoot) outResource.getContents().get(0);
        assertNotNull(outRoot.getOnExitScript());
        OnExitScriptType scriptType = outRoot.getOnExitScript();
        assertEquals("script", scriptType.getScript());
        assertEquals("format", scriptType.getScriptFormat());
    }
    
    public void testImportElement() throws Exception {
        // write
        XMLResource inResource = (XMLResource) resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));
        inResource.getDefaultLoadOptions().put(XMLResource.OPTION_ENCODING, "UTF-8");
        inResource.setEncoding("UTF-8");
        DocumentRoot documentRoot = DroolsFactory.eINSTANCE.createDocumentRoot();
        ImportType root = DroolsFactory.eINSTANCE.createImportType();
        root.setName("import");
        documentRoot.setImport(root);
        inResource.getContents().add(documentRoot);
        
        StringWriter stringWriter = new StringWriter();
        inResource.save(stringWriter, null);
        assertNotNull(stringWriter.getBuffer().toString());
        if(stringWriter.getBuffer().toString().length() < 1) {
            fail("generated xml is empty");
        }
        
        // read
        XMLResource outResource = (XMLResource) resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));
        outResource.getDefaultLoadOptions().put(XMLResource.OPTION_ENCODING, "UTF-8");
        outResource.setEncoding("UTF-8");
        Map<String, Object> options = new HashMap<String, Object>();
        options.put( XMLResource.OPTION_ENCODING, "UTF-8" );
        InputStream is = new ByteArrayInputStream(stringWriter.getBuffer().toString().getBytes("UTF-8"));
        outResource.load(is, options);
        
        DocumentRoot outRoot = (DocumentRoot) outResource.getContents().get(0);
        assertNotNull(outRoot.getImport());
        ImportType importType = outRoot.getImport();
        assertEquals("import", importType.getName());
    }
    
    public void testGlobalElement() throws Exception {
        // write
        XMLResource inResource = (XMLResource) resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));
        inResource.getDefaultLoadOptions().put(XMLResource.OPTION_ENCODING, "UTF-8");
        inResource.setEncoding("UTF-8");
        DocumentRoot documentRoot = DroolsFactory.eINSTANCE.createDocumentRoot();
        GlobalType root = DroolsFactory.eINSTANCE.createGlobalType();
        root.setIdentifier("identifier");
        root.setType("type");
        documentRoot.setGlobal(root);
        inResource.getContents().add(documentRoot);
        
        StringWriter stringWriter = new StringWriter();
        inResource.save(stringWriter, null);
        assertNotNull(stringWriter.getBuffer().toString());
        if(stringWriter.getBuffer().toString().length() < 1) {
            fail("generated xml is empty");
        }
        
        // read
        XMLResource outResource = (XMLResource) resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));
        outResource.getDefaultLoadOptions().put(XMLResource.OPTION_ENCODING, "UTF-8");
        outResource.setEncoding("UTF-8");
        Map<String, Object> options = new HashMap<String, Object>();
        options.put( XMLResource.OPTION_ENCODING, "UTF-8" );
        InputStream is = new ByteArrayInputStream(stringWriter.getBuffer().toString().getBytes("UTF-8"));
        outResource.load(is, options);
        
        DocumentRoot outRoot = (DocumentRoot) outResource.getContents().get(0);
        assertNotNull(outRoot.getGlobal());
        GlobalType globalType = outRoot.getGlobal();
        assertEquals("identifier", globalType.getIdentifier());
        assertEquals("type", globalType.getType());
    }
    
}