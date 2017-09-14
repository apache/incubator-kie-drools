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

package org.drools.modelcompiler.drlx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.PackageDeclaration;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;

import static org.drools.javaparser.printer.PrintUtil.toDrl;
import static org.drools.javaparser.printer.PrintUtil.toJava;
import static org.drools.drlx.DrlxUtils.hasRules;

public class DrlxCompiler {

    public static CompiledUnit compileFolders(String... folders) {
        return compileFolders(  KieServices.get().getRepository().getDefaultReleaseId(), folders );
    }

    public static CompiledUnit compileFolders(ReleaseId releaseId, String... folders) {
        KieServices ks = KieServices.get();
        KieFileSystem kfs = createKieFileSystem( ks, releaseId );

        List<String> units = new ArrayList<>();
        for (String folder : folders) {
            File file = new File( folder );
            if ( !file.exists() ) {
                throw new RuntimeException( "File not found: " + file.getAbsolutePath() );
            }
            addToFileSystem( kfs, file, units );
        }

        KieContainer kieContainer = createKieContainer( ks, kfs, releaseId );
        return new CompiledUnit(kieContainer, units);
    }

    private static void addToFileSystem( KieFileSystem kfs, File file, List<String> units ) {
        if (file.isDirectory()) {
            for (File subfile : file.listFiles()) {
                addToFileSystem( kfs, subfile, units );
            }
        } else {
            CompilationUnit compilationUnit;
            try {
                compilationUnit = JavaParser.parse( new FileReader( file ) );
            } catch (FileNotFoundException e) {
                throw new RuntimeException( e );
            }

            ClassOrInterfaceDeclaration unitClass = (ClassOrInterfaceDeclaration) compilationUnit.getType( 0 );
            String pkg = compilationUnit.getPackageDeclaration().map( PackageDeclaration::getNameAsString ).orElse( "defaultpkg" );
            String unit = unitClass.getNameAsString();
            String unitPath = pkg.replace( ".", "/" ) + "/" + unit;

            kfs.write("src/main/java/" + unitPath + ".java", toJava( compilationUnit ));
            if (hasRules(compilationUnit)) {
                kfs.write("src/main/resources/" + unitPath + ".drl", toDrl( compilationUnit ));
            }

            if ( unitClass.getImplementedTypes().stream().anyMatch( type -> type.getNameAsString().equals( "RuleUnit" ) ) ) {
                units.add(pkg + "." + unit);
            }
        }
    }

    public static CompiledUnit compileSingleSource( InputStream source ) {
        return compileSingleSource( new InputStreamReader( source ) );
    }

    public static CompiledUnit compileSingleSource( Reader source ) {
        CompilationUnit compilationUnit = JavaParser.parse( source );

        ClassOrInterfaceDeclaration unitClass = (ClassOrInterfaceDeclaration) compilationUnit.getType( 0 );
        String pkg = compilationUnit.getPackageDeclaration().map( PackageDeclaration::getNameAsString ).orElse( "defaultpkg" );
        String unit = unitClass.getNameAsString();

        KieServices ks = KieServices.get();
        ReleaseId releaseId = ks.newReleaseId( pkg, unit, "1.0" );
        KieFileSystem kfs = createKieFileSystem( ks, releaseId );

        String unitPath = pkg.replace( ".", "/" ) + "/" + unit;
        String javaPath = "src/main/java/" + unitPath + ".java";
        String drlPath = "src/main/resources/" + unitPath + ".drl";

        kfs.write(drlPath, toDrl( compilationUnit ))
           .write(javaPath, toJava( compilationUnit ));

        KieContainer kieContainer = createKieContainer( ks, kfs, releaseId );
        return new CompiledUnit(kieContainer, pkg + "." + unit);
    }

    private static KieContainer createKieContainer( KieServices ks, KieFileSystem kfs, ReleaseId releaseId ) {
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        Results results = kieBuilder.getResults();
        if (results.hasMessages( Message.Level.ERROR )) {
            throw new RuntimeException(results.getMessages().toString());
        }
        return ks.newKieContainer( releaseId );
    }

    private static KieFileSystem createKieFileSystem( KieServices ks, ReleaseId releaseId ) {
        KieModuleModel kproj = ks.newKieModuleModel();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        kfs.writePomXML(getPom(releaseId));
        return kfs;
    }

    private static String getPom(ReleaseId releaseId) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
               "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
               "  <modelVersion>4.0.0</modelVersion>\n" +
               "\n" +
               "  <groupId>" + releaseId.getGroupId() + "</groupId>\n" +
               "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" +
               "  <version>" + releaseId.getVersion() + "</version>\n" +
               "</project>\n";
    }
}
