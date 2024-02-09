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
/**
 * Process Fluent API allows programmer to build an in memory representation of a bpmn file.<br>
 * This information can later be used to build a KIE resource and execute the process .
 * Typical use of fluent API will be:
 * <li> Build a process definition that prints action string in console using <code>ProcessBuilder</code> several methods</li>
 * <pre>
        // Obtain default ProcessBuilderFactory
        ProcessBuilderFactory factory = ProcessBuilderFactories.get();
                ProcessBuilderFactory factory = ProcessBuilderFactories.get();
        Process process =
                // Create process builder
                factory.processBuilder(processId)
                       // package and name 
                       .packageName("org.jbpm")
                       .name("My process")
                       // start node
                       .startNode(1).name("Start").done()
                       // Add variable of type string
                       .variable(var("pepe", String.class))
                       // Add exception handler
                       .exceptionHandler(IllegalArgumentException.class, Dialect.JAVA, "System.out.println(\"Exception\");")
                       // script node in Java language that prints "action"
                       .actionNode(2).name("Action")
                       .action(Dialect.JAVA,
                               "System.out.println(\"Action\");").done()
                       // end node
                       .endNode(3).name("End").done()
                       // connections
                       .connection(1,
                                   2)
                       .connection(2,
                                   3)
                       .build();
   </pre>
 * <li> Create a resource from the process definition (process needs to be converted to byte[] using <code>ProcessBuilderFactory</code>)</li>
 * <pre>
        // Build resource from ProcessBuilder
        KieResources resources = ServiceRegistry.getInstance().get(KieResources.class);
        Resource res = resources
                                .newByteArrayResource(factory.toBytes(process))
                                .setSourcePath("/tmp/processFactory.bpmn2"); // source path or target path must be set to be added into kbase
  </pre>  
 * <li> Build kie base from this resource using KIE API</li>
 * <pre>
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(res);
        KieBuilder kb = ks.newKieBuilder(kfs);
        kb.buildAll(); // kieModule is automatically deployed to KieRepository if successfully built.
        KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());
        KieBase kbase = kContainer.getKieBase();
   </pre>
 * <li> Create kie session using this kie base and execute the process</li>
 * <pre>
        // Create kie session
        KieSessionConfiguration conf = ...;
        Environment env = ....; 
        KieSession ksession = kbase.newKieSession(conf,env);
        // execute process using same process id that was used to obtain <code>ProcessBuilder</code> instance
        ksession.startProcess(processId); 
 *</pre>
 */
package org.kie.api.fluent;
