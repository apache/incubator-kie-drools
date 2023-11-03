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
package org.drools.compiler.builder.impl.processors;

import java.util.Collection;

import org.drools.drl.ast.descr.AnnotatedBaseDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ConditionalElementDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.PatternDestinationDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.kie.internal.builder.KnowledgeBuilderResult;

public class RuleAnnotationNormalizer implements CompilationPhase {
    private final AnnotationNormalizer annotationNormalizer;
    private final PackageDescr packageDescr;

    public RuleAnnotationNormalizer(AnnotationNormalizer annotationNormalizer, PackageDescr packageDescr) {
        this.annotationNormalizer = annotationNormalizer;
        this.packageDescr = packageDescr;
    }

    public void process() {
        for (RuleDescr ruleDescr : packageDescr.getRules()) {
            annotationNormalizer.normalize(ruleDescr);
            traverseAnnotations(ruleDescr.getLhs());
        }
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return annotationNormalizer.getResults();
    }

    private void traverseAnnotations(BaseDescr descr) {
        if (descr instanceof AnnotatedBaseDescr) {
            annotationNormalizer.normalize((AnnotatedBaseDescr) descr);
        }
        if (descr instanceof ConditionalElementDescr) {
            for (BaseDescr baseDescr : ((ConditionalElementDescr) descr).getDescrs()) {
                traverseAnnotations(baseDescr);
            }
        }
        if (descr instanceof PatternDescr && ((PatternDescr) descr).getSource() != null) {
            traverseAnnotations(((PatternDescr) descr).getSource());
        }
        if (descr instanceof PatternDestinationDescr) {
            traverseAnnotations(((PatternDestinationDescr) descr).getInputPattern());
        }
    }
}
