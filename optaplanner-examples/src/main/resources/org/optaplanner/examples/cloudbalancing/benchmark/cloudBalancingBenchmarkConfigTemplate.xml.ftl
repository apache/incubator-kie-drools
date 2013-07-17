<?xml version="1.0" encoding="UTF-8"?>
<plannerBenchmark>
  <benchmarkDirectory>local/data/cloudbalancing/template</benchmarkDirectory>
  <parallelBenchmarkCount>AUTO</parallelBenchmarkCount>
  <warmUpSecondsSpend>30</warmUpSecondsSpend>

  <inheritedSolverBenchmark>
    <problemBenchmarks>
      <xstreamAnnotatedClass>org.optaplanner.examples.cloudbalancing.domain.CloudBalance</xstreamAnnotatedClass>
      <!--<inputSolutionFile>data/cloudbalancing/unsolved/cb-0002comp-0006proc.xml</inputSolutionFile>-->
      <!--<inputSolutionFile>data/cloudbalancing/unsolved/cb-0003comp-0009proc.xml</inputSolutionFile>-->
      <!--<inputSolutionFile>data/cloudbalancing/unsolved/cb-0004comp-0012proc.xml</inputSolutionFile>-->
      <inputSolutionFile>data/cloudbalancing/unsolved/cb-0100comp-0300proc.xml</inputSolutionFile>
      <inputSolutionFile>data/cloudbalancing/unsolved/cb-0200comp-0600proc.xml</inputSolutionFile>
      <inputSolutionFile>data/cloudbalancing/unsolved/cb-0400comp-1200proc.xml</inputSolutionFile>
      <inputSolutionFile>data/cloudbalancing/unsolved/cb-0800comp-2400proc.xml</inputSolutionFile>
      <problemStatisticType>BEST_SCORE</problemStatisticType>
    </problemBenchmarks>

    <solver>
      <solutionClass>org.optaplanner.examples.cloudbalancing.domain.CloudBalance</solutionClass>
      <planningEntityClass>org.optaplanner.examples.cloudbalancing.domain.CloudProcess</planningEntityClass>
      <scoreDirectorFactory>
        <scoreDefinitionType>HARD_SOFT</scoreDefinitionType>
        <scoreDrl>/org/optaplanner/examples/cloudbalancing/solver/cloudBalancingScoreRules.drl</scoreDrl>
      </scoreDirectorFactory>
      <termination>
        <maximumMinutesSpend>5</maximumMinutesSpend>
      </termination>
      <constructionHeuristic>
        <constructionHeuristicType>FIRST_FIT_DECREASING</constructionHeuristicType>
        <pickEarlyType>FIRST_NON_DETERIORATING_SCORE</pickEarlyType>
      </constructionHeuristic>
    </solver>
  </inheritedSolverBenchmark>

<#list [5, 7, 11, 13] as entityTabuSize>
<#list [500, 1000, 2000] as acceptedCountLimit>
  <solverBenchmark>
    <name>entityTabuSize ${entityTabuSize} acceptedCountLimit ${acceptedCountLimit}</name>
    <solver>
      <localSearch>
        <unionMoveSelector>
          <changeMoveSelector/>
          <swapMoveSelector/>
        </unionMoveSelector>
        <acceptor>
          <entityTabuSize>${entityTabuSize}</entityTabuSize>
        </acceptor>
        <forager>
          <acceptedCountLimit>${acceptedCountLimit}</acceptedCountLimit>
        </forager>
      </localSearch>
    </solver>
  </solverBenchmark>
</#list>
</#list>
</plannerBenchmark>
