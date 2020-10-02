/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.mvel.integrationtests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.DroolsObjectOutputStream;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class SerializedPackageMergeTwoSteps2Test {

    @Test @Ignore("DROOLS-5620 - test failed randomly and it doesn't reproduce the original issue (DROOLS-2224) scenario on CI."+
                  "It can be tested manually")
    public void testBuildAndSerializePackagesInTwoSteps2() throws IOException, ClassNotFoundException    {        

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        // Read the two serialized knowledgePackages
        for(String fileName : SerializedPackageMergeTwoSteps1Test.BINPKG) {
            Collection<KiePackage> kpkgs=null;
            byte[] data=null;
            try {
            	data=Files.readAllBytes(Paths.get(fileName));
            } catch(java.nio.file.NoSuchFileException ex) {
                // bin file does not exist, finish test
                return;
            }
        	kpkgs=_deserializeFromBytes(data);  
        	if(kpkgs!=null)
        		kbase.addPackages(kpkgs); 
        }    

        Collection<KiePackage> knowledgePackagesCombined = kbase.getKiePackages();

        // serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new DroolsObjectOutputStream( baos );
        out.writeObject( knowledgePackagesCombined );
        out.flush();
        out.close();

        // deserialize
        ObjectInputStream in = new DroolsObjectInputStream( new ByteArrayInputStream( baos.toByteArray() ) );
        Collection<KiePackage> deserializedPackages = (Collection<KiePackage>) in.readObject();

        // Use the deserialized knowledgePackages
        InternalKnowledgeBase kbase2 = KnowledgeBaseFactory.newKnowledgeBase();
        kbase2.addPackages(deserializedPackages);

        KieSession ksession = kbase2.newKieSession();
        try {
            List<String> list = new ArrayList<String>();
            ksession.setGlobal( "list", list );
            ksession.insert(new org.drools.mvel.compiler.Person("John"));
            ksession.fireAllRules();

            assertEquals(2, list.size());
        } finally {
            ksession.dispose();
        }
    }
    
  	private Collection<KiePackage>  _deserializeFromBytes(byte [] byteCode){
		Collection<KiePackage> ret = null;
		ByteArrayInputStream bis = null;
		ObjectInput in = null;
		try {
			bis = new ByteArrayInputStream(byteCode);
			in = new DroolsObjectInputStream(bis);
			ret =(Collection<KiePackage>)in.readObject();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
					if(bis != null)
						bis.close();
					if(in != null)
						in.close();
				} catch (IOException e) {
				}
		}
		return ret;
	}        
}
