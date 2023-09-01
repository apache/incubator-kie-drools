package org.drools.model.codegen.execmodel.benchmark;

import org.drools.model.codegen.execmodel.benchmark.BuildFromKJarBenchmark.BenchmarkType;
import org.kie.api.KieBase;

public class BenchmarkMain {

    public static void main( String[] args ) {
        BuildFromKJarBenchmark benchmark = new BuildFromKJarBenchmark( 4, 2, BenchmarkType.MODEL );
        benchmark.setUpKJar();
        KieBase kieBase = benchmark.buildKnowledge( null );
    }
}
