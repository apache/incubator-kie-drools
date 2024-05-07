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
package org.kie.dmn.core.compiler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.util.Either;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.v1_1.TImport;

import static org.assertj.core.api.Assertions.assertThat;

class ImportDMNResolverUtilTest {

    @Test
    void nSonly() {
        final Import i = makeImport("ns1", null, null);
        final List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                                    new QName("ns2", "m2"),
                                                    new QName("ns3", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("ns1", "m1"));
    }

    @Test
    void nSandModelName() {
        final Import i = makeImport("ns1", null, "m1");
        final List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                                    new QName("ns2", "m2"),
                                                    new QName("ns3", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("ns1", "m1"));
    }

    @Test
    void nSandModelNameWithAlias() {
        final Import i = makeImport("ns1", "aliased", "m1");
        final List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                                    new QName("ns2", "m2"),
                                                    new QName("ns3", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("ns1", "m1"));
    }

    @Test
    void nSnoModelNameWithAlias() {
        final Import i = makeImport("ns1", "mymodel", null);
        final List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                                    new QName("ns2", "m2"),
                                                    new QName("ns3", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("ns1", "m1"));
    }

    @Test
    void nSandUnexistentModelName() {
        final Import i = makeImport("ns1", null, "boh");
        final List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                                    new QName("ns2", "m2"),
                                                    new QName("ns3", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isLeft()).isTrue();
    }

    @Test
    void nSnoModelNameDefaultWithAlias2() {
        final Import i = makeImport("ns1", "boh", null);
        final List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                                    new QName("ns2", "m2"),
                                                    new QName("ns3", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("ns1", "m1"));
    }

    @Test
    void locateInNS() {
        final Import i = makeImport("nsA", null, "m1");
        final List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                                    new QName("nsA", "m2"),
                                                    new QName("nsB", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("nsA", "m1"));
    }

    @Test
    void locateInNSnoModelNameWithAlias() {
        final Import i = makeImport("nsA", "m1", null);
        final List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                                    new QName("nsA", "m2"),
                                                    new QName("nsB", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isLeft()).isTrue();
    }

    @Test
    void locateInNSAliased() {
        final Import i = makeImport("nsA", "aliased", "m1");
        final List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                                    new QName("nsA", "m2"),
                                                    new QName("nsB", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("nsA", "m1"));
    }

    @Test
    void locateInNSunexistent() {
        final Import i = makeImport("nsA", null, "boh");
        final List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                                    new QName("nsA", "m2"),
                                                    new QName("nsB", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isLeft()).isTrue();
    }

    @Test
    void locateInNSnoModelNameWithAlias2() {
        final Import i = makeImport("nsA", "boh", null);
        final List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                                    new QName("nsA", "m2"),
                                                    new QName("nsB", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isLeft()).isTrue();
    }

    @Test
    void locateInNSAliasedBadScenario() {
        // this is a BAD scenario are in namespace `nsA` there are 2 models with the same name.
        final Import i = makeImport("nsA", "aliased", "mA");
        final List<QName> available = Arrays.asList(new QName("nsA", "mA"),
                                                    new QName("nsA", "mA"),
                                                    new QName("nsB", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isLeft()).isTrue();
    }

    private Import makeImport(final String namespace, final String name, final String modelName) {
        final Import i = new TImport();
        i.setNamespace(namespace);
        final Map<QName, String> addAttributes = new HashMap<>();
        if (name != null) {
            addAttributes.put(TImport.NAME_QNAME, name);
        }
        if (modelName != null) {
            addAttributes.put(TImport.MODELNAME_QNAME, modelName);
        }
        i.setAdditionalAttributes(addAttributes);
        return i;
    }

}
