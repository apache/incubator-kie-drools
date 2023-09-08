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
package org.drools.mvel.integrationtests;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;

import org.drools.base.common.DroolsObjectOutputStream;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

public class SerializedPackageMergeTwoSteps1Test {

    // kpackage serialization is not supported. But leave it for standard-drl.

	public static final String[] BINPKG = { System.getProperty( "java.io.tmpdir" ) + File.separator + "SerializedPackageMergeTwoSteps_1.bin", 
			System.getProperty( "java.io.tmpdir" ) + File.separator + "SerializedPackageMergeTwoSteps_2.bin" };

	@Test @Ignore("DROOLS-5620 - test failed randomly and it doesn't reproduce the original issue (DROOLS-2224) scenario on CI."+
			      "It can be tested manually")
	public void testBuildAndSerializePackagesInTwoSteps1() {
		String str1 =
				"package com.sample.packageA\n" +
						"import org.drools.mvel.compiler.Person\n" +
						"global java.util.List list\n" +
						"rule R1 when\n" +
						"  $p : Person( name == \"John\" )\n" +
						"then\n" +
						"  list.add($p);" +
						"end\n";

		String str2 =
				"package com.sample.packageB\n" +
						"import org.drools.mvel.compiler.Person\n" +
						"global java.util.List list\n" +
						"rule R2 when\n" +
						"  $p : Person()\n" +
						"then\n" +
						"  list.add($p);" +
						"end\n";

		// Create 2 knowledgePackages separately
		KnowledgeBuilder builder1 = KnowledgeBuilderFactory.newKnowledgeBuilder();
		builder1.add( ResourceFactory.newByteArrayResource( str1.getBytes() ), ResourceType.DRL );
		Collection<KiePackage> knowledgePackages1 = builder1.getKnowledgePackages();

		// serialize the first package to a file
		writeKnowledgePackage(knowledgePackages1, BINPKG[0]);

		KnowledgeBuilder builder2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
		builder2.add( ResourceFactory.newByteArrayResource( str2.getBytes() ), ResourceType.DRL );
		Collection<KiePackage> knowledgePackages2 = builder2.getKnowledgePackages();

		// serialize the second package to a file
		writeKnowledgePackage(knowledgePackages2, BINPKG[1]);               
	}   

	public void writeKnowledgePackage(Collection<KiePackage> pkgs, String filePath) 
	{
		FileOutputStream  fileOutStream = null;
		DroolsObjectOutputStream out = null;
		try {
			fileOutStream = new FileOutputStream(filePath);
			out = new DroolsObjectOutputStream(fileOutStream);
			out.writeObject(pkgs);
			out.flush();
		} catch(Exception ex){
			// TODO
		}finally {
			try{
				if(out != null)
					out.close();
				if(fileOutStream != null)
					fileOutStream.close();
			}catch(Exception e){}
		}
	}     
}
