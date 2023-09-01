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
package org.drools.model.codegen.execmodel.benchmark;

import java.util.concurrent.TimeUnit;

import org.drools.compiler.kie.builder.impl.ZipKieModule;
import org.drools.modelcompiler.CanonicalKieModule;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@Fork(1)
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BuildFromKJarBenchmark {

    public enum BenchmarkType {

        DRL(false, false), MODEL(true, false), MODEL_WITH_EXPR_ID(true, true);

        public final boolean useRuleModel;
        public final boolean generateExprId;

        BenchmarkType( boolean useRuleModel, boolean generateExprId ) {
            this.useRuleModel = useRuleModel;
            this.generateExprId = generateExprId;
        }
    }

    @Param({"10000"})
    private int numberOfRules;

    @Param("50")
    private int numberOfRulesPerFile;

//    @Param({"DRL", "MODEL", "MODEL_WITH_EXPR_ID"})
    @Param({"DRL"})
    private BenchmarkType type;

    public BuildFromKJarBenchmark() { }

    public BuildFromKJarBenchmark( int numberOfRules, int numberOfRulesPerFile, BenchmarkType type ) {
        this.numberOfRules = numberOfRules;
        this.numberOfRulesPerFile = numberOfRulesPerFile;
        this.type = type;
    }

    private KieServices kieServices;
    private KieRepository kieRepository;
    private ReleaseId releaseId;
    private KJarWithKnowledgeFiles kjarFiles;
    private KieModuleModel kieModuleModel;

    @Setup(Level.Trial)
    public void setUpKJar() {
        kieServices = KieServices.get();
        kieRepository = kieServices.getRepository();
        releaseId = kieServices.newReleaseId("org.kie", "kjar-test", "1.0");
        kjarFiles = BenchmarkUtil.createJarFile( kieServices, releaseId, numberOfRules, numberOfRulesPerFile, type );
        kieModuleModel = BenchmarkUtil.getDefaultKieModuleModel( kieServices );
    }

    @Setup(Level.Invocation)
    public void cleanUpRepo() {
        kieRepository.removeKieModule(releaseId);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        System.gc();
    }

    @Benchmark
    public KieBase buildKnowledge(final Blackhole eater) {
        final KieModule zipKieModule = type.useRuleModel ?
                                       new CanonicalKieModule( releaseId, kieModuleModel, kjarFiles.getJarFile(), kjarFiles.getKnowledgeFiles()) :
                                       new ZipKieModule( releaseId, kieModuleModel, kjarFiles.getJarFile());

        kieRepository.addKieModule(zipKieModule);
        if (eater != null) {
            eater.consume( zipKieModule );
        }

        return kieServices.newKieContainer(releaseId).getKieBase();
    }
}
