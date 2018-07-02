<?xml version="1.0" encoding="UTF-8"?>
<plannerBenchmark>
  <benchmarkDirectory>local/data/projectjobscheduling/template</benchmarkDirectory>
  <parallelBenchmarkCount>AUTO</parallelBenchmarkCount>

  <inheritedSolverBenchmark>
    <problemBenchmarks>
      <xStreamAnnotatedClass>org.optaplanner.examples.projectjobscheduling.domain.Schedule</xStreamAnnotatedClass>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-1.xml</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-2.xml</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-3.xml</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-4.xml</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-5.xml</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-6.xml</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-7.xml</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-8.xml</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-9.xml</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-10.xml</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-1.xml</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-2.xml</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-3.xml</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-4.xml</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-5.xml</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-6.xml</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-7.xml</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-8.xml</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-9.xml</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-10.xml</inputSolutionFile>
    </problemBenchmarks>

    <solver>
      <solutionClass>org.optaplanner.examples.projectjobscheduling.domain.Schedule</solutionClass>
      <entityClass>org.optaplanner.examples.projectjobscheduling.domain.Allocation</entityClass>

      <scoreDirectorFactory>
        <incrementalScoreCalculatorClass>org.optaplanner.examples.projectjobscheduling.solver.score.ProjectJobSchedulingIncrementalScoreCalculator</incrementalScoreCalculatorClass>
      </scoreDirectorFactory>
      <termination>
        <minutesSpentLimit>5</minutesSpentLimit>
      </termination>
    </solver>
  </inheritedSolverBenchmark>

<#list [500, 1000, 2000] as lateAcceptanceSize>
  <solverBenchmark>
    <name>LA ${lateAcceptanceSize}</name>
    <solver>
      <constructionHeuristic>
        <constructionHeuristicType>FIRST_FIT</constructionHeuristicType>
      </constructionHeuristic>
      <localSearch>
        <unionMoveSelector>
          <changeMoveSelector>
            <valueSelector variableName="executionMode"/>
          </changeMoveSelector>
          <changeMoveSelector>
            <valueSelector variableName="delay"/>
          </changeMoveSelector>
        </unionMoveSelector>
        <acceptor>
          <entityTabuRatio>0.2</entityTabuRatio>
          <lateAcceptanceSize>${lateAcceptanceSize}</lateAcceptanceSize>
        </acceptor>
        <forager>
          <acceptedCountLimit>4</acceptedCountLimit>
        </forager>
      </localSearch>
    </solver>
  </solverBenchmark>
</#list>
</plannerBenchmark>
