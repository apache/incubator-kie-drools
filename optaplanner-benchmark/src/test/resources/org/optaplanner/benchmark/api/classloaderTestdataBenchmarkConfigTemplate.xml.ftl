<?xml version="1.0" encoding="UTF-8"?>
<plannerBenchmark>
  <benchmarkDirectory>target/benchmarkTest/output</benchmarkDirectory>
  <warmUpSecondsSpentLimit>0</warmUpSecondsSpentLimit>
<#list ['FIRST_FIT', 'CHEAPEST_INSERTION'] as constructionHeuristicType>
  <solverBenchmark>
    <problemBenchmarks>
      <solutionFileIOClass>org.optaplanner.persistence.common.api.domain.solution.RigidTestdataSolutionFileIO</solutionFileIOClass>
      <inputSolutionFile>target/test/benchmarkTest/input.xml</inputSolutionFile>
    </problemBenchmarks>
    <solver>
      <!-- Using these classnames doesn't work because the className still differs from class.getName()-->
      <!--<solutionClass>divertThroughClassLoader.org.optaplanner.core.impl.testdata.domain.TestdataSolution</solutionClass>-->
      <!--<entityClass>divertThroughClassLoader.org.optaplanner.core.impl.testdata.domain.TestdataEntity</entityClass>-->
      <solutionClass>org.optaplanner.core.impl.testdata.domain.TestdataSolution</solutionClass>
      <entityClass>org.optaplanner.core.impl.testdata.domain.TestdataEntity</entityClass>

      <!-- Score configuration -->
      <scoreDirectorFactory>
        <!-- Using these classnames doesn't work because the className still differs from class.getName()-->
        <!-- <constraintProviderClass>divertThroughClassLoader.org.optaplanner.core.impl.testdata.domain.TestdataConstraintProvider</constraintProviderClass>-->
        <constraintProviderClass>org.optaplanner.core.impl.testdata.domain.TestdataConstraintProvider</constraintProviderClass>
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
