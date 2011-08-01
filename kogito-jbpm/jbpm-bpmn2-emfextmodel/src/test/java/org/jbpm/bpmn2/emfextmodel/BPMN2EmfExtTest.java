/**
 * Copyright 2010 JBoss Inc
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
import org.jbpm.bpmn2.emfextmodel.util.EmfextmodelResourceFactoryImpl;

import junit.framework.TestCase;

public class BPMN2EmfExtTest extends TestCase {
    private ResourceSet resourceSet;
    
    @Override
    protected void setUp() throws Exception {
        resourceSet = new ResourceSetImpl();
        
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put
            (Resource.Factory.Registry.DEFAULT_EXTENSION, 
             new EmfextmodelResourceFactoryImpl());
        resourceSet.getPackageRegistry().put
            (EmfextmodelPackage.eNS_URI, 
             EmfextmodelPackage.eINSTANCE);
    }
    
    @Override
    protected void tearDown() throws Exception {
    }
    
    public void testOnEntryScriptElement() throws Exception {
        // write
        XMLResource inResource = (XMLResource) resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));
        inResource.getDefaultLoadOptions().put(XMLResource.OPTION_ENCODING, "UTF-8");
        inResource.setEncoding("UTF-8");
        DocumentRoot documentRoot = EmfextmodelFactory.eINSTANCE.createDocumentRoot();
        OnEntryScriptType root = EmfextmodelFactory.eINSTANCE.createOnEntryScriptType();
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
        DocumentRoot documentRoot = EmfextmodelFactory.eINSTANCE.createDocumentRoot();
        OnExitScriptType root = EmfextmodelFactory.eINSTANCE.createOnExitScriptType();
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
}
