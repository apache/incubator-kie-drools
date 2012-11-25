<?xml version="1.0" encoding="UTF-8"?>
<plannerBenchmark>
  <benchmarkDirectory>local/data/cloudbalancing/template</benchmarkDirectory>
  <parallelBenchmarkCount>AUTO</parallelBenchmarkCount>
  <warmUpSecondsSpend>30</warmUpSecondsSpend>

  <inheritedSolverBenchmark>
    <problemBenchmarks>
      <xstreamAnnotatedClass>org.drools.planner.examples.cloudbalancing.domain.CloudBalance</xstreamAnnotatedClass>
      <!--<inputSolutionFile>data/cloudbalancing/unsolved/cb-0002comp-0006proc.xml</inputSolutionFile>-->
      <!--<inputSolutionFile>data/cloudbalancing/unsolved/cb-0003comp-0009proc.xml</inputSolutionFile>-->
      <!--<inputSolutionFile>data/cloudbalancing/unsolved/cb-0004comp-0012proc.xml</inputSolutionFile>-->
      <inputSolutionFile>data/cloudbalancing/unsolved/cb-0100comp-0300proc.xml</inputSolutionFile>
      <inputSolutionFile>data/cloudbalancing/unsolved/cb-0200comp-0600proc.xml</inputSolutionFile>
      <inputSolutionFile>data/cloudbalancing/unsolved/cb-0400comp-1200proc.xml</inputSolutionFile>
      <inputSolutionFile>data/cloudbalancing/unsolved/cb-0800comp-2400proc.xml</inputSolutionFile>
      <problemStatisticType>BEST_SOLUTION_CHANGED</problemStatisticType>
    </problemBenchmarks>

    <solver>
      <solutionClass>org.drools.planner.examples.cloudbalancing.domain.CloudBalance</solutionClass>
      <planningEntityClass>org.drools.planner.examples.cloudbalancing.domain.CloudProcess</planningEntityClass>
      <scoreDirectorFactory>
        <scoreDefinitionType>HARD_AND_SOFT</scoreDefinitionType>
        <scoreDrl>/org/drools/planner/examples/cloudbalancing/solver/cloudBalancingScoreRules.drl</scoreDrl>
      </scoreDirectorFactory>
      <termination>
        <maximumMinutesSpend>5</maximumMinutesSpend>
      </termination>
      <constructionHeuristic>
        <constructionHeuristicType>FIRST_FIT_DECREASING</constructionHeuristicType>
        <constructionHeuristicPickEarlyType>FIRST_LAST_STEP_SCORE_EQUAL_OR_IMPROVING</constructionHeuristicPickEarlyType>
      </constructionHeuristic>
    </solver>
  </inheritedSolverBenchmark>

<#list [5, 7, 11, 13] as planningEntityTabuSize>
<#list [500, 1000, 2000] as minimalAcceptedSelection>
  <solverBenchmark>
    <name>entityTabu ${planningEntityTabuSize} acceptedSelection ${minimalAcceptedSelection}</name>
    <solver>
      <localSearch>
        <unionMoveSelector>
          <changeMoveSelector/>
          <swapMoveSelector/>
        </unionMoveSelector>
        <acceptor>
          <planningEntityTabuSize>${planningEntityTabuSize}</planningEntityTabuSize>
        </acceptor>
        <forager>
          <minimalAcceptedSelection>${minimalAcceptedSelection}</minimalAcceptedSelection>
        </forager>
      </localSearch>
    </solver>
  </solverBenchmark>
</#list>
</#list>
</plannerBenchmark>
