<?xml version="1.0" encoding="UTF-8"?>
<plannerBenchmark>
  <benchmarkDirectory>local/data/curriculumcourse/template</benchmarkDirectory>
  <parallelBenchmarkCount>AUTO</parallelBenchmarkCount>

  <inheritedSolverBenchmark>
    <problemBenchmarks>
      <xStreamAnnotatedClass>org.optaplanner.examples.curriculumcourse.domain.CourseSchedule</xStreamAnnotatedClass>
      <inputSolutionFile>data/curriculumcourse/unsolved/comp01.xml</inputSolutionFile>
      <inputSolutionFile>data/curriculumcourse/unsolved/comp02.xml</inputSolutionFile>
      <inputSolutionFile>data/curriculumcourse/unsolved/comp03.xml</inputSolutionFile>
      <inputSolutionFile>data/curriculumcourse/unsolved/comp04.xml</inputSolutionFile>
      <inputSolutionFile>data/curriculumcourse/unsolved/comp05.xml</inputSolutionFile>
      <inputSolutionFile>data/curriculumcourse/unsolved/comp06.xml</inputSolutionFile>
      <inputSolutionFile>data/curriculumcourse/unsolved/comp07.xml</inputSolutionFile>
      <inputSolutionFile>data/curriculumcourse/unsolved/comp08.xml</inputSolutionFile>
      <inputSolutionFile>data/curriculumcourse/unsolved/comp09.xml</inputSolutionFile>
      <inputSolutionFile>data/curriculumcourse/unsolved/comp10.xml</inputSolutionFile>
      <inputSolutionFile>data/curriculumcourse/unsolved/comp11.xml</inputSolutionFile>
      <inputSolutionFile>data/curriculumcourse/unsolved/comp12.xml</inputSolutionFile>
      <inputSolutionFile>data/curriculumcourse/unsolved/comp13.xml</inputSolutionFile>
      <inputSolutionFile>data/curriculumcourse/unsolved/comp14.xml</inputSolutionFile>
    </problemBenchmarks>

    <solver>
      <solutionClass>org.optaplanner.examples.curriculumcourse.domain.CourseSchedule</solutionClass>
      <entityClass>org.optaplanner.examples.curriculumcourse.domain.Lecture</entityClass>
      <scoreDirectorFactory>
        <scoreDrl>org/optaplanner/examples/curriculumcourse/solver/curriculumCourseScoreRules.drl</scoreDrl>
      </scoreDirectorFactory>
      <termination>
          <minutesSpentLimit>5</minutesSpentLimit>
      </termination>
      <constructionHeuristic>
        <constructionHeuristicType>FIRST_FIT_DECREASING</constructionHeuristicType>
      </constructionHeuristic>
    </solver>
  </inheritedSolverBenchmark>

  <#list [9] as entityTabuSize>
    <#list [900] as acceptedCountLimit>
      <solverBenchmark>
        <name>Entity Tabu ${entityTabuSize} (acceptedCount ${acceptedCountLimit})</name>
        <solver>
          <localSearch>
            <unionMoveSelector>
              <changeMoveSelector/>
              <swapMoveSelector>
                <filterClass>org.optaplanner.examples.curriculumcourse.solver.move.DifferentCourseSwapMoveFilter</filterClass>
              </swapMoveSelector>
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
  <#list [600] as lateAcceptanceSize>
    <#list [4] as acceptedCountLimit>
      <solverBenchmark>
        <name>Late Acceptance ${lateAcceptanceSize} (acceptedCount ${acceptedCountLimit})</name>
        <solver>
          <localSearch>
            <unionMoveSelector>
              <changeMoveSelector/>
              <swapMoveSelector>
                <filterClass>org.optaplanner.examples.curriculumcourse.solver.move.DifferentCourseSwapMoveFilter</filterClass>
              </swapMoveSelector>
            </unionMoveSelector>
            <acceptor>
              <lateAcceptanceSize>${lateAcceptanceSize}</lateAcceptanceSize>
            </acceptor>
            <forager>
              <acceptedCountLimit>${acceptedCountLimit}</acceptedCountLimit>
            </forager>
          </localSearch>
        </solver>
      </solverBenchmark>
    </#list>
  </#list>
  <#list [200] as stepCountingHillClimbingSize>
    <#list [1] as acceptedCountLimit>
      <solverBenchmark>
        <name>Step Counting Hill Climbing ${stepCountingHillClimbingSize} (acceptedCount ${acceptedCountLimit})</name>
        <solver>
          <localSearch>
            <unionMoveSelector>
              <changeMoveSelector/>
              <swapMoveSelector>
                <filterClass>org.optaplanner.examples.curriculumcourse.solver.move.DifferentCourseSwapMoveFilter</filterClass>
              </swapMoveSelector>
            </unionMoveSelector>
            <acceptor>
              <stepCountingHillClimbingSize>${stepCountingHillClimbingSize}</stepCountingHillClimbingSize>
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
