/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.xml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DrlParser;
import org.drools.mvel.DrlDumper;
import org.jbpm.compiler.xml.compiler.SemanticKnowledgeBuilderConfigurationImpl;
import org.jbpm.compiler.xml.compiler.XmlDumper;
import org.jbpm.compiler.xml.compiler.XmlPackageReader;
import org.kie.internal.builder.conf.LanguageLevelOption;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Helper Class for both xml and drl Dump Tests
 */
public class DumperTestHelper {

    public static void XmlFile(String filename) throws Exception {
        SemanticKnowledgeBuilderConfigurationImpl conf = new SemanticKnowledgeBuilderConfigurationImpl();

        XmlPackageReader xmlPackageReader = new XmlPackageReader(conf.getSemanticModules());
        xmlPackageReader.getParser().setClassLoader(DumperTestHelper.class.getClassLoader());
        xmlPackageReader.read(new InputStreamReader(DumperTestHelper.class.getResourceAsStream(filename)));
        final PackageDescr pkgOriginal = xmlPackageReader.getPackageDescr();

        final XmlDumper dumper = new XmlDumper();
        final String result = dumper.dump(pkgOriginal);

        String buffer = readFile(filename);

        System.out.println(buffer);
        System.out.println(result);

        assertThat(buffer).isEqualToIgnoringWhitespace(result);
        assertThat(result).isNotNull();
    }

    public static void DrlFile(String filename) throws Exception {

        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        final PackageDescr pkgOriginal = parser.parse(new InputStreamReader(DumperTestHelper.class.getResourceAsStream(filename)));
        final DrlDumper dumper = new DrlDumper();
        String result1 = dumper.dump(pkgOriginal);
        final PackageDescr pkgDerivated = parser.parse(new StringReader(result1));
        String result2 = dumper.dump(pkgDerivated);
        System.out.println(result1);

        assertThat(result1).isEqualToIgnoringWhitespace(result2);
    }

    public static String dump(String filename) throws Exception {
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL6);
        final PackageDescr pkgOriginal = parser.parse(new InputStreamReader(DumperTestHelper.class.getResourceAsStream(filename)));
        final DrlDumper dumper = new DrlDumper();
        return dumper.dump(pkgOriginal);
    }

    private static String readFile(final String file) throws IOException {
        final InputStreamReader reader = new InputStreamReader(DumperTestHelper.class.getResourceAsStream(file));
        final StringBuilder text = new StringBuilder();
        final char[] buf = new char[1024];
        int len = 0;

        while ((len = reader.read(buf)) >= 0) {
            text.append(buf,
                    0,
                    len);
        }
        return text.toString();
    }
}
