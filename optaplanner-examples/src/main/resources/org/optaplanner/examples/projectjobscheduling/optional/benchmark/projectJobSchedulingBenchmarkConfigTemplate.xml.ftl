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
  <benchmarkDirectory>local/data/projectjobscheduling/template</benchmarkDirectory>
  <parallelBenchmarkCount>AUTO</parallelBenchmarkCount>

  <inheritedSolverBenchmark>
    <problemBenchmarks>
      <solutionFileIOClass>org.optaplanner.examples.projectjobscheduling.persistence.ProjectJobSchedulingSolutionFileIO</solutionFileIOClass>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-1.json</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-2.json</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-3.json</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-4.json</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-5.json</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-6.json</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-7.json</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-8.json</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-9.json</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/A-10.json</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-1.json</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-2.json</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-3.json</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-4.json</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-5.json</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-6.json</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-7.json</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-8.json</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-9.json</inputSolutionFile>
      <inputSolutionFile>data/projectjobscheduling/unsolved/B-10.json</inputSolutionFile>
    </problemBenchmarks>

    <solver>
      <solutionClass>org.optaplanner.examples.projectjobscheduling.domain.Schedule</solutionClass>
      <entityClass>org.optaplanner.examples.projectjobscheduling.domain.Allocation</entityClass>

      <scoreDirectorFactory>
        <constraintProviderClass>org.optaplanner.examples.projectjobscheduling.score.ProjectJobSchedulingConstraintProvider</constraintProviderClass>
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
