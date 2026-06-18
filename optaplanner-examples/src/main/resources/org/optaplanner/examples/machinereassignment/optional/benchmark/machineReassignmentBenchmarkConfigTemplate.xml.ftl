<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one
  ~ or more contributor license agreements.  See the NOTICE file
  ~ distributed with this work for additional information
  ~ regarding copyright ownership.  The ASF licenses this file
  ~ to you under the Apache License, Version 2.0 (the
  ~ "License"); you may not use this file except in compliance
  ~ with the License.  You may obtain a copy of the License at
  ~
  ~   http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied.  See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  -->
<plannerBenchmark>
  <benchmarkDirectory>local/data/machinereassignment/template</benchmarkDirectory>
  <!--<parallelBenchmarkCount>AUTO</parallelBenchmarkCount>-->
  <warmUpSecondsSpentLimit>60</warmUpSecondsSpentLimit>

  <inheritedSolverBenchmark>
    <problemBenchmarks>
      <solutionFileIOClass>org.optaplanner.examples.machinereassignment.persistence.MachineReassignmentFileIO</solutionFileIOClass>
      <!--<inputSolutionFile>data/machinereassignment/import/model_a1_1.txt</inputSolutionFile>-->
      <!--<inputSolutionFile>data/machinereassignment/import/model_a1_2.txt</inputSolutionFile>-->
      <!--<inputSolutionFile>data/machinereassignment/import/model_a1_3.txt</inputSolutionFile>-->
      <!--<inputSolutionFile>data/machinereassignment/import/model_a1_4.txt</inputSolutionFile>-->
      <!--<inputSolutionFile>data/machinereassignment/import/model_a1_5.txt</inputSolutionFile>-->
      <!--<inputSolutionFile>data/machinereassignment/import/model_a2_1.txt</inputSolutionFile>-->
      <!--<inputSolutionFile>data/machinereassignment/import/model_a2_2.txt</inputSolutionFile>-->
      <!--<inputSolutionFile>data/machinereassignment/import/model_a2_3.txt</inputSolutionFile>-->
      <!--<inputSolutionFile>data/machinereassignment/import/model_a2_4.txt</inputSolutionFile>-->
      <!--<inputSolutionFile>data/machinereassignment/import/model_a2_5.txt</inputSolutionFile>-->
      <inputSolutionFile>data/machinereassignment/import/model_b_1.txt</inputSolutionFile>
      <inputSolutionFile>data/machinereassignment/import/model_b_2.txt</inputSolutionFile>
      <inputSolutionFile>data/machinereassignment/import/model_b_3.txt</inputSolutionFile>
      <inputSolutionFile>data/machinereassignment/import/model_b_4.txt</inputSolutionFile>
      <inputSolutionFile>data/machinereassignment/import/model_b_5.txt</inputSolutionFile>
      <inputSolutionFile>data/machinereassignment/import/model_b_6.txt</inputSolutionFile>
      <inputSolutionFile>data/machinereassignment/import/model_b_7.txt</inputSolutionFile>
      <inputSolutionFile>data/machinereassignment/import/model_b_8.txt</inputSolutionFile>
      <inputSolutionFile>data/machinereassignment/import/model_b_9.txt</inputSolutionFile>
      <inputSolutionFile>data/machinereassignment/import/model_b_10.txt</inputSolutionFile>
      <problemStatisticEnabled>false</problemStatisticEnabled>
    </problemBenchmarks>

    <solver>
      <!--<environmentMode>FAST_ASSERT</environmentMode>-->
      <solutionClass>org.optaplanner.examples.machinereassignment.domain.MachineReassignment</solutionClass>
      <entityClass>org.optaplanner.examples.machinereassignment.domain.MrProcessAssignment</entityClass>

      <scoreDirectorFactory>
        <constraintProviderClass>org.optaplanner.examples.machinereassignment.score.MachineReassignmentConstraintProvider</constraintProviderClass>
      </scoreDirectorFactory>
      <termination>
        <minutesSpentLimit>5</minutesSpentLimit>
      </termination>
    </solver>
  </inheritedSolverBenchmark>

  <solverBenchmark>
    <name>original</name>
    <solver>
      <customPhase>
        <customPhaseCommandClass>org.optaplanner.examples.machinereassignment.solver.solution.initializer.ToOriginalMachineSolutionInitializer</customPhaseCommandClass>
      </customPhase>
    </solver>
  </solverBenchmark>
<#list [7] as entityTabuSize>
<#list [2000] as acceptedCountLimit>
    <solverBenchmark>
    <name>entityTabu${entityTabuSize}-mas${acceptedCountLimit}</name>
    <solver>
      <customPhase>
        <customPhaseCommandClass>org.optaplanner.examples.machinereassignment.solver.solution.initializer.ToOriginalMachineSolutionInitializer</customPhaseCommandClass>
      </customPhase>
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
<#list [100000, 1000000, 10000000] as simulatedAnnealingStartingTemperature>
    <solverBenchmark>
        <name>simulatedAnnealing${simulatedAnnealingStartingTemperature}soft</name>
        <solver>
            <customPhase>
                <customPhaseCommandClass>org.optaplanner.examples.machinereassignment.solver.solution.initializer.ToOriginalMachineSolutionInitializer</customPhaseCommandClass>
            </customPhase>
            <localSearch>
                <unionMoveSelector>
                    <changeMoveSelector/>
                    <swapMoveSelector/>
                </unionMoveSelector>
                <acceptor>
                    <simulatedAnnealingStartingTemperature>0hard/${simulatedAnnealingStartingTemperature}soft</simulatedAnnealingStartingTemperature>
                </acceptor>
                <forager>
                    <acceptedCountLimit>1</acceptedCountLimit>
                </forager>
            </localSearch>
        </solver>
    </solverBenchmark>
</#list>
<#list [500, 1000, 2000] as lateAcceptanceSize>
    <solverBenchmark>
    <name>lateAcceptance${lateAcceptanceSize}</name>
    <solver>
      <customPhase>
        <customPhaseCommandClass>org.optaplanner.examples.machinereassignment.solver.solution.initializer.ToOriginalMachineSolutionInitializer</customPhaseCommandClass>
      </customPhase>
      <localSearch>
        <unionMoveSelector>
          <changeMoveSelector/>
          <swapMoveSelector/>
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
