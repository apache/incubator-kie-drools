///*
// * Copyright 2012 JBoss by Red Hat.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.jbpm.form.builder.services;
//
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.shrinkwrap.api.Archive;
//import org.jboss.shrinkwrap.api.ArchivePaths;
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.spec.JavaArchive;
//import org.junit.runner.RunWith;
//
///**
// *
// *
// */
//@RunWith(Arquillian.class)
//public class FileServiceTest extends FileServiceBaseTest {
//    
//
//    @Deployment()
//    public static Archive<?> createDeployment() {
//        return ShrinkWrap.create(JavaArchive.class, "jbpm-form-builder-services-cdi.jar")
//                .addPackage("org.jboss.seam.persistence") //seam-persistence
//                .addPackage("org.jboss.seam.transaction") //seam-persistence
//                .addPackage("org.jbpm.form.builder.services.api") 
//                .addPackage("org.jbpm.form.builder.services.encoders")
//                .addPackage("org.jbpm.form.builder.services.impl.base")
//                .addPackage("org.jbpm.form.builder.services.impl.db")
//                .addPackage("org.jbpm.form.builder.services.impl.fs")
//                .addPackage("org.jbpm.form.builder.services.internal")
//                .addPackage("org.jbpm.form.builder.services.tasks")
//                .addPackage("org.jbpm.form.builder.services.annotations")
//                
//                //.addPackage("org.jbpm.services.task.commands") // This should not be required here 
//                .addAsManifestResource("persistence.xml", ArchivePaths.create("persistence.xml"))
//                .addAsManifestResource("META-INF/Settingsorm.xml", ArchivePaths.create("Settingsorm.xml"))
//                .addAsManifestResource("beans-default.xml", ArchivePaths.create("beans.xml"));
//
//    }
//    
//  
//    
//   
//}
