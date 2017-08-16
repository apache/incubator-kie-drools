<?xml version="1.0" encoding="UTF-8"?>
<plannerBenchmark>
  <benchmarkDirectory>local/data/vehiclerouting/template</benchmarkDirectory>
  <parallelBenchmarkCount>AUTO</parallelBenchmarkCount>

  <inheritedSolverBenchmark>
    <problemBenchmarks>
      <solutionFileIOClass>org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingFileIO</solutionFileIOClass>
      <!-- Belgium datasets -->
      <inputSolutionFile>data/vehiclerouting/import/belgium/basic/air/belgium-n50-k10.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/belgium/basic/air/belgium-n100-k10.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/belgium/basic/air/belgium-n500-k20.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/belgium/basic/air/belgium-n1000-k20.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/belgium/basic/air/belgium-n2750-k55.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/belgium/basic/road-km/belgium-road-km-n50-k10.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/belgium/basic/road-km/belgium-road-km-n100-k10.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/belgium/basic/road-time/belgium-road-time-n50-k10.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/belgium/basic/road-time/belgium-road-time-n100-k10.vrp</inputSolutionFile>
      <!-- USA datasets -->
      <inputSolutionFile>data/vehiclerouting/import/usa/basic/air/usa-n100-k10.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/usa/basic/air/usa-n500-k20.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/usa/basic/air/usa-n1000-k20.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/usa/basic/air/usa-n5000-k100.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/usa/basic/air/usa-n10000-k100.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/usa/basic/air/usa-n50000-k500.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/usa/basic/air/usa-n100000-k500.vrp</inputSolutionFile>
      <!-- VRP web datasets -->
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n32-k5.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n33-k5.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n33-k6.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n34-k5.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n36-k5.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n37-k5.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n37-k6.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n38-k5.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n39-k5.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n39-k6.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n44-k7.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n45-k6.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n45-k7.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n46-k7.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n48-k7.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n53-k7.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n54-k7.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n55-k9.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n60-k9.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n61-k9.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n62-k8.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n63-k10.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n63-k9.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n64-k9.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n65-k9.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n69-k9.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/A-n80-k10.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/F-n135-k7.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/F-n45-k4.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/basic/air/F-n72-k4.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Solomon_025_C101.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Solomon_025_C201.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Solomon_025_R101.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Solomon_025_R201.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Solomon_025_RC101.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Solomon_025_RC201.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Solomon_100_C101.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Solomon_100_C201.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Solomon_100_R101.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Solomon_100_R201.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Solomon_100_RC101.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Solomon_100_RC201.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0200_C1_2_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0200_C2_2_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0200_R1_2_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0200_R2_2_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0200_RC1_2_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0200_RC2_2_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0400_C1_4_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0400_C2_4_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0400_R1_4_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0400_R2_4_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0400_RC1_4_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0400_RC2_4_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0600_C1_6_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0600_C2_6_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0600_R1_6_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0600_R2_6_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0600_RC2_6_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0600_RC1_6_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0800_C1_8_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0800_C2_8_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0800_R1_8_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0800_R2_8_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0800_RC1_8_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_0800_RC2_8_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_1000_C110_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_1000_C210_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_1000_R110_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_1000_R210_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_1000_RC110_1.vrp</inputSolutionFile>
      <inputSolutionFile>data/vehiclerouting/import/vrpweb/timewindowed/air/Homberger_1000_RC210_1.vrp</inputSolutionFile>
    </problemBenchmarks>

    <solver>
      <!--<environmentMode>FAST_ASSERT</environmentMode>-->
      <solutionClass>org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution</solutionClass>
      <entityClass>org.optaplanner.examples.vehiclerouting.domain.Standstill</entityClass>
      <entityClass>org.optaplanner.examples.vehiclerouting.domain.Customer</entityClass>
      <entityClass>org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer</entityClass>

      <scoreDirectorFactory>
        <incrementalScoreCalculatorClass>org.optaplanner.examples.vehiclerouting.solver.score.VehicleRoutingIncrementalScoreCalculator</incrementalScoreCalculatorClass>
        <!--<scoreDrl>org/optaplanner/examples/vehiclerouting/solver/vehicleRoutingScoreRules.drl</scoreDrl>-->
      </scoreDirectorFactory>

      <termination>
        <minutesSpentLimit>5</minutesSpentLimit>
      </termination>
    </solver>
  </inheritedSolverBenchmark>

<#list [7, 9] as entityTabuSize>
  <solverBenchmark>
    <name>Tabu Search ${entityTabuSize} Nearby</name>
    <solver>
      <constructionHeuristic>
        <constructionHeuristicType>FIRST_FIT_DECREASING</constructionHeuristicType>
      </constructionHeuristic>
      <localSearch>
        <unionMoveSelector>
          <changeMoveSelector>
            <entitySelector id="entitySelector1"/>
            <valueSelector>
              <nearbySelection>
                <originEntitySelector mimicSelectorRef="entitySelector1"/>
                <nearbyDistanceMeterClass>org.optaplanner.examples.vehiclerouting.domain.solver.nearby.CustomerNearbyDistanceMeter</nearbyDistanceMeterClass>
                <parabolicDistributionSizeMaximum>40</parabolicDistributionSizeMaximum>
              </nearbySelection>
            </valueSelector>
          </changeMoveSelector>
          <swapMoveSelector>
            <entitySelector id="entitySelector2"/>
            <secondaryEntitySelector>
              <nearbySelection>
                <originEntitySelector mimicSelectorRef="entitySelector2"/>
                <nearbyDistanceMeterClass>org.optaplanner.examples.vehiclerouting.domain.solver.nearby.CustomerNearbyDistanceMeter</nearbyDistanceMeterClass>
                <parabolicDistributionSizeMaximum>40</parabolicDistributionSizeMaximum>
              </nearbySelection>
            </secondaryEntitySelector>
          </swapMoveSelector>
          <tailChainSwapMoveSelector>
            <entitySelector id="entitySelector3"/>
            <valueSelector>
              <nearbySelection>
                <originEntitySelector mimicSelectorRef="entitySelector3"/>
                <nearbyDistanceMeterClass>org.optaplanner.examples.vehiclerouting.domain.solver.nearby.CustomerNearbyDistanceMeter</nearbyDistanceMeterClass>
                <parabolicDistributionSizeMaximum>40</parabolicDistributionSizeMaximum>
              </nearbySelection>
            </valueSelector>
          </tailChainSwapMoveSelector>
        </unionMoveSelector>
        <acceptor>
          <entityTabuSize>${entityTabuSize}</entityTabuSize>
        </acceptor>
        <forager>
          <acceptedCountLimit>2000</acceptedCountLimit>
        </forager>
      </localSearch>
    </solver>
  </solverBenchmark>
</#list>
<#list [100, 200, 400] as lateAcceptanceSize>
  <solverBenchmark>
    <name>Late Acceptance ${lateAcceptanceSize} Nearby</name>
    <solver>
      <constructionHeuristic>
        <constructionHeuristicType>FIRST_FIT_DECREASING</constructionHeuristicType>
      </constructionHeuristic>
      <localSearch>
        <unionMoveSelector>
          <changeMoveSelector>
            <entitySelector id="entitySelector1"/>
            <valueSelector>
              <nearbySelection>
                <originEntitySelector mimicSelectorRef="entitySelector1"/>
                <nearbyDistanceMeterClass>org.optaplanner.examples.vehiclerouting.domain.solver.nearby.CustomerNearbyDistanceMeter</nearbyDistanceMeterClass>
                <parabolicDistributionSizeMaximum>40</parabolicDistributionSizeMaximum>
              </nearbySelection>
            </valueSelector>
          </changeMoveSelector>
          <swapMoveSelector>
            <entitySelector id="entitySelector2"/>
            <secondaryEntitySelector>
              <nearbySelection>
                <originEntitySelector mimicSelectorRef="entitySelector2"/>
                <nearbyDistanceMeterClass>org.optaplanner.examples.vehiclerouting.domain.solver.nearby.CustomerNearbyDistanceMeter</nearbyDistanceMeterClass>
                <parabolicDistributionSizeMaximum>40</parabolicDistributionSizeMaximum>
              </nearbySelection>
            </secondaryEntitySelector>
          </swapMoveSelector>
          <tailChainSwapMoveSelector>
            <entitySelector id="entitySelector3"/>
            <valueSelector>
              <nearbySelection>
                <originEntitySelector mimicSelectorRef="entitySelector3"/>
                <nearbyDistanceMeterClass>org.optaplanner.examples.vehiclerouting.domain.solver.nearby.CustomerNearbyDistanceMeter</nearbyDistanceMeterClass>
                <parabolicDistributionSizeMaximum>40</parabolicDistributionSizeMaximum>
              </nearbySelection>
            </valueSelector>
          </tailChainSwapMoveSelector>
        </unionMoveSelector>
        <acceptor>
          <lateAcceptanceSize>${lateAcceptanceSize}</lateAcceptanceSize>
        </acceptor>
        <forager>
          <acceptedCountLimit>1</acceptedCountLimit>
        </forager>
      </localSearch>
    </solver>
  </solverBenchmark>
</#list>
</plannerBenchmark>
