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

import bpsim.BPSimDataType;
import bpsim.BpsimFactory;
import bpsim.BpsimPackage;
import bpsim.DocumentRoot;
import bpsim.ElementParameters;
import bpsim.Parameter;
import bpsim.Scenario;
import bpsim.ScenarioParameters;
import bpsim.TimeParameters;
import bpsim.TimeUnit;
import bpsim.UniformDistributionType;
import bpsim.util.BpsimResourceFactoryImpl;

import junit.framework.TestCase;

public class BpsimTest extends TestCase {
private ResourceSet resourceSet;
    
    @Override
    protected void setUp() throws Exception {
        resourceSet = new ResourceSetImpl();
        
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put
            (Resource.Factory.Registry.DEFAULT_EXTENSION, 
             new BpsimResourceFactoryImpl());
        resourceSet.getPackageRegistry().put
            (BpsimPackage.eNS_URI, 
            		BpsimPackage.eINSTANCE);
    }
    
    @Override
    protected void tearDown() throws Exception {
    }
    
    @SuppressWarnings("unchecked")
    public void testBpsimData() throws Exception {
    	//write
    	XMLResource inResource = (XMLResource) resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));
        inResource.getDefaultLoadOptions().put(XMLResource.OPTION_ENCODING, "UTF-8");
        inResource.setEncoding("UTF-8");
        
        DocumentRoot documentRoot = BpsimFactory.eINSTANCE.createDocumentRoot();
        
        BPSimDataType bpsimData = BpsimFactory.eINSTANCE.createBPSimDataType();
        Scenario defaultScenario = BpsimFactory.eINSTANCE.createScenario();
        defaultScenario.setId("default");
        defaultScenario.setName("Scenario");
        ScenarioParameters scenarioParams = BpsimFactory.eINSTANCE.createScenarioParameters();
        scenarioParams.setBaseTimeUnit(TimeUnit.S);
        defaultScenario.setScenarioParameters(scenarioParams);
        ElementParameters elementParams = BpsimFactory.eINSTANCE.createElementParameters();
        TimeParameters elementTimeParams = BpsimFactory.eINSTANCE.createTimeParameters();
        
        Parameter processingTimeParameter = BpsimFactory.eINSTANCE.createParameter();
        UniformDistributionType uniformDistrobutionType = BpsimFactory.eINSTANCE.createUniformDistributionType();
        uniformDistrobutionType.setMin(180.0);
        uniformDistrobutionType.setMax(600.0);
        processingTimeParameter.getParameterValue().add(uniformDistrobutionType);
        elementTimeParams.setProcessingTime(processingTimeParameter);
        elementParams.setTimeParameters(elementTimeParams);
        defaultScenario.getElementParameters().add(elementParams);
        bpsimData.getScenario().add(defaultScenario);
        
        documentRoot.setBPSimData(bpsimData);
        inResource.getContents().add(documentRoot);
        StringWriter stringWriter = new StringWriter();
        inResource.save(stringWriter, null);
        assertNotNull(stringWriter.getBuffer().toString());
        if(stringWriter.getBuffer().toString().length() < 1) {
            fail("generated xml is empty");
        }
        
    	
    	//read
        XMLResource outResource = (XMLResource) resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));
        outResource.getDefaultLoadOptions().put(XMLResource.OPTION_ENCODING, "UTF-8");
        outResource.setEncoding("UTF-8");
        Map<String, Object> options = new HashMap<String, Object>();
        options.put( XMLResource.OPTION_ENCODING, "UTF-8" );
        InputStream is = new ByteArrayInputStream(stringWriter.getBuffer().toString().getBytes("UTF-8"));
        outResource.load(is, options);
        
        DocumentRoot outRoot = (DocumentRoot) outResource.getContents().get(0);
        assertNotNull(outRoot.getBPSimData());
        
        BPSimDataType outAnalysisData = outRoot.getBPSimData();
        assertEquals(outAnalysisData.getScenario().size(), 1);
        Scenario outScenario = outAnalysisData.getScenario().get(0);
        assertEquals(outScenario.getElementParameters().size(), 1);
        assertEquals(outScenario.getId(), "default");
        assertEquals(outScenario.getName(), "Scenario");
        assertNotNull(outScenario.getScenarioParameters());
        assertNotNull(outScenario.getElementParameters());
        assertEquals(outScenario.getElementParameters().size(), 1);
        ElementParameters outElementParamType = outScenario.getElementParameters().get(0);
        assertNotNull(outElementParamType.getTimeParameters());
        TimeParameters outTimeParams = outElementParamType.getTimeParameters();
        assertNotNull(outTimeParams.getProcessingTime());
        assertEquals(outTimeParams.getProcessingTime().getParameterValue().size(), 1);
        UniformDistributionType outDistributionType = (UniformDistributionType) outTimeParams.getProcessingTime().getParameterValue().get(0);
        assertEquals(outDistributionType.getMax(), 600.0);
        assertEquals(outDistributionType.getMin(), 180.0);
    }
}
