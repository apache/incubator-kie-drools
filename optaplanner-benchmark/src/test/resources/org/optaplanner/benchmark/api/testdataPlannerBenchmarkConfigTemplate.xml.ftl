<?xml version="1.0" encoding="UTF-8"?>
<plannerBenchmark>
  <benchmarkDirectory>target/benchmarkTest/output</benchmarkDirectory>
<#list ['FIRST_FIT', 'CHEAPEST_INSERTION'] as constructionHeuristicType>
  <solverBenchmark>
    <problemBenchmarks>
      <solutionFileIOClass>org.optaplanner.persistence.common.api.domain.solution.TestdataSolutionFileIO</solutionFileIOClass>
      <inputSolutionFile>target/benchmarkTest/input.xml</inputSolutionFile>
    </problemBenchmarks>
    <solver>
      <solutionClass>org.optaplanner.core.impl.testdata.domain.TestdataSolution</solutionClass>
      <entityClass>org.optaplanner.core.impl.testdata.domain.TestdataEntity</entityClass>
      <scoreDirectorFactory>
        <scoreDefinitionType>SIMPLE</scoreDefinitionType>
        <scoreDrl>org/optaplanner/core/api/solver/testdataScoreRules.drl</scoreDrl>
      </scoreDirectorFactory>
      <termination>
        <secondsSpentLimit>0</secondsSpentLimit>
      </termination>
      <constructionHeuristic>
        <constructionHeuristicType>${constructionHeuristicType}</constructionHeuristicType>
      </constructionHeuristic>
    </solver>
  </solverBenchmark>
</#list>
</plannerBenchmark>
