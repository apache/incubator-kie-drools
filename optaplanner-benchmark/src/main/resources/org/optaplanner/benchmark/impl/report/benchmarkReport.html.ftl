<#-- @ftlvariable name="benchmarkReport" type="org.optaplanner.benchmark.impl.report.BenchmarkReport" -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>${benchmarkReport.plannerBenchmark.name} Planner benchmark report</title>
    <link href="twitterbootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="twitterbootstrap/css/bootstrap-responsive.css" rel="stylesheet"/>
    <link href="twitterbootstrap/css/prettify.css" rel="stylesheet"/>
    <link href="website/css/benchmarkReport.css" rel="stylesheet"/>
    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
</head>
<#macro addSolverRankingBadge solverBenchmark>
    <#if !solverBenchmark.ranking??>
    <span class="badge badge-warning">F</span>
    <#elseif solverBenchmark.favorite>
    <span class="badge badge-success">${solverBenchmark.ranking}</span>
    <#else>
    <span class="badge">${solverBenchmark.ranking}</span>
    </#if>
</#macro>
<#macro addSingleRankingBadge singleBenchmark>
    <#if !singleBenchmark.ranking??>
    <span class="badge badge-warning">F</span>
    <#elseif singleBenchmark.winner>
    <span class="badge badge-success">${singleBenchmark.ranking}</span>
    <#else>
    <span class="badge">${singleBenchmark.ranking}</span>
    </#if>
</#macro>
<body onload="prettyPrint()">

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span2">
            <div class="benchmark-report-nav">
                <a href="http://www.optaplanner.org"><img src="website/img/optaPlannerLogo.png" alt="OptaPlanner"/></a>
                <ul class="nav nav-list">
                    <li><a href="#summary">Summary</a></li>
                    <li>
                        <ul class="nav nav-list">
                            <li><a href="#summary_result">Result</a></li>
                            <li><a href="#summary_performance">Performance</a></li>
                        </ul>
                    </li>
                    <li class="divider"></li>
                    <li><a href="#problemBenchmark">Problem benchmarks</a></li>
                    <li>
                        <ul class="nav nav-list">
                        <#list benchmarkReport.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>
                            <li><a href="#problemBenchmark_${problemBenchmark.name}">${problemBenchmark.name}</a></li>
                        </#list>
                        </ul>
                    </li>
                    <li class="divider"></li>
                    <li><a href="#solverBenchmark">Solver benchmarks</a></li>
                    <li>
                        <ul class="nav nav-list">
                        <#list benchmarkReport.plannerBenchmark.solverBenchmarkList as solverBenchmark>
                            <li><a href="#solverBenchmark_${solverBenchmark.name}">${solverBenchmark.name}&nbsp;<@addSolverRankingBadge solverBenchmark=solverBenchmark/></a></li>
                        </#list>
                        </ul>
                    </li>
                    <li class="divider"></li>
                    <li><a href="#benchmarkInformation">Benchmark information</a></li>
                </ul>
            </div>
        </div>
        <div class="span10">
            <header class="main-page-header">
                <h1>Benchmark report</h1>
            </header>
            <section id="summary">
                <div class="page-header">
                    <h1>Summary</h1>
                </div>
            <#if benchmarkReport.plannerBenchmark.hasAnyFailure()>
                <div class="alert alert-error">
                    <p>${benchmarkReport.plannerBenchmark.failureCount} benchmarks have failed!</p>
                </div>
            </#if>
            <#list benchmarkReport.warningList as warning>
                <div class="alert alert-error">
                    <p>${warning}</p>
                </div>
            </#list>

                <section id="summary_result">
                    <h2>Result summary</h2>
                    <div class="tabbable">
                        <ul class="nav nav-pills">
                            <li class="active">
                                <a href="#summary_bestScore" data-toggle="tab">Best score</a>
                            </li>
                            <li>
                                <a href="#summary_bestScoreScalability" data-toggle="tab">Best score scalability</a>
                            </li>
                            <li>
                                <a href="#summary_winningScoreDifference" data-toggle="tab">Winning score difference</a>
                            </li>
                            <li>
                                <a href="#summary_worstScoreDifferencePercentage" data-toggle="tab">Worst score difference percentage (ROI)</a>
                            </li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane active" id="summary_bestScore">
                                <h3>Best score summary</h3>
                                <div class="tabbable tabs-right">
                                    <ul class="nav nav-tabs">
                                        <#assign scoreLevelIndex = 0>
                                        <#list benchmarkReport.bestScoreSummaryChartFileList as bestScoreSummaryChartFile>
                                            <li<#if scoreLevelIndex == benchmarkReport.defaultShownScoreLevelIndex> class="active"</#if>>
                                                <a href="#summary_bestScore_chart_${scoreLevelIndex}" data-toggle="tab">Score level ${scoreLevelIndex}</a>
                                            </li>
                                            <#assign scoreLevelIndex = scoreLevelIndex + 1>
                                        </#list>
                                    </ul>
                                    <div class="tab-content">
                                        <#assign scoreLevelIndex = 0>
                                        <#list benchmarkReport.bestScoreSummaryChartFileList as bestScoreSummaryChartFile>
                                            <div class="tab-pane<#if scoreLevelIndex == benchmarkReport.defaultShownScoreLevelIndex> active</#if>" id="summary_bestScore_chart_${scoreLevelIndex}">
                                                <div class="benchmark-chart">
                                                    <img src="summary/${bestScoreSummaryChartFile.name}"/>
                                                </div>
                                            </div>
                                            <#assign scoreLevelIndex = scoreLevelIndex + 1>
                                        </#list>
                                    </div>
                                </div>
                                <table class="benchmark-table table table-striped table-bordered">
                                    <tr>
                                        <th>Solver</th>
                                    <#list benchmarkReport.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>
                                        <th>${problemBenchmark.name}</th>
                                    </#list>
                                        <th>Average</th>
                                        <th>Ranking</th>
                                    </tr>
                                <#list benchmarkReport.plannerBenchmark.solverBenchmarkList as solverBenchmark>
                                    <tr<#if solverBenchmark.favorite> class="favoriteSolverBenchmark"</#if>>
                                        <th>${solverBenchmark.name}&nbsp;<@addSolverRankingBadge solverBenchmark=solverBenchmark/></th>
                                        <#list benchmarkReport.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>
                                            <#if !solverBenchmark.findSingleBenchmark(problemBenchmark)??>
                                                <td></td>
                                            <#else>
                                                <#assign singleBenchmark = solverBenchmark.findSingleBenchmark(problemBenchmark)>
                                                <#if !singleBenchmark.success>
                                                    <td><span class="label warning">Failed</span></td>
                                                <#else>
                                                    <td>${singleBenchmark.score}&nbsp;<@addSingleRankingBadge singleBenchmark=singleBenchmark/></td>
                                                </#if>
                                            </#if>
                                        </#list>
                                        <td>${solverBenchmark.averageScore!""}</td>
                                        <td><@addSolverRankingBadge solverBenchmark=solverBenchmark/></td>
                                    </tr>
                                </#list>
                                </table>
                            </div>
                            <div class="tab-pane" id="summary_bestScoreScalability">
                                <h3>Best score scalability summary</h3>
                                <div class="tabbable tabs-right">
                                    <ul class="nav nav-tabs">
                                        <#assign scoreLevelIndex = 0>
                                        <#list benchmarkReport.bestScoreScalabilitySummaryChartFileList as bestScoreScalabilitySummaryChartFile>
                                            <li<#if scoreLevelIndex == benchmarkReport.defaultShownScoreLevelIndex> class="active"</#if>>
                                                <a href="#summary_bestScoreScalability_chart_${scoreLevelIndex}" data-toggle="tab">Score level ${scoreLevelIndex}</a>
                                            </li>
                                            <#assign scoreLevelIndex = scoreLevelIndex + 1>
                                        </#list>
                                    </ul>
                                    <div class="tab-content">
                                        <#assign scoreLevelIndex = 0>
                                        <#list benchmarkReport.bestScoreScalabilitySummaryChartFileList as bestScoreScalabilitySummaryChartFile>
                                            <div class="tab-pane<#if scoreLevelIndex == benchmarkReport.defaultShownScoreLevelIndex> active</#if>" id="summary_bestScoreScalability_chart_${scoreLevelIndex}">
                                                <div class="benchmark-chart">
                                                    <img src="summary/${bestScoreScalabilitySummaryChartFile.name}"/>
                                                </div>
                                            </div>
                                            <#assign scoreLevelIndex = scoreLevelIndex + 1>
                                        </#list>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-pane" id="summary_winningScoreDifference">
                                <h3>Winning score difference summary</h3>
                                <div class="tabbable tabs-right">
                                    <ul class="nav nav-tabs">
                                    <#assign scoreLevelIndex = 0>
                                    <#list benchmarkReport.winningScoreDifferenceSummaryChartFileList as winningScoreDifferenceSummaryChartFile>
                                        <li<#if scoreLevelIndex == benchmarkReport.defaultShownScoreLevelIndex> class="active"</#if>>
                                            <a href="#summary_winningScoreDifference_chart_${scoreLevelIndex}" data-toggle="tab">Score level ${scoreLevelIndex}</a>
                                        </li>
                                        <#assign scoreLevelIndex = scoreLevelIndex + 1>
                                    </#list>
                                    </ul>
                                    <div class="tab-content">
                                    <#assign scoreLevelIndex = 0>
                                    <#list benchmarkReport.winningScoreDifferenceSummaryChartFileList as winningScoreDifferenceSummaryChartFile>
                                        <div class="tab-pane<#if scoreLevelIndex == benchmarkReport.defaultShownScoreLevelIndex> active</#if>" id="summary_winningScoreDifference_chart_${scoreLevelIndex}">
                                            <div class="benchmark-chart">
                                                <img src="summary/${winningScoreDifferenceSummaryChartFile.name}"/>
                                            </div>
                                        </div>
                                        <#assign scoreLevelIndex = scoreLevelIndex + 1>
                                    </#list>
                                    </div>
                                </div>
                                <table class="benchmark-table table table-striped table-bordered">
                                    <tr>
                                        <th>Solver</th>
                                    <#list benchmarkReport.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>
                                        <th>${problemBenchmark.name}</th>
                                    </#list>
                                        <th>Average</th>
                                    </tr>
                                <#list benchmarkReport.plannerBenchmark.solverBenchmarkList as solverBenchmark>
                                    <tr<#if solverBenchmark.favorite> class="favoriteSolverBenchmark"</#if>>
                                        <th>${solverBenchmark.name}&nbsp;<@addSolverRankingBadge solverBenchmark=solverBenchmark/></th>
                                        <#list benchmarkReport.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>
                                            <#if !solverBenchmark.findSingleBenchmark(problemBenchmark)??>
                                                <td></td>
                                            <#else>
                                                <#assign singleBenchmark = solverBenchmark.findSingleBenchmark(problemBenchmark)>
                                                <#if !singleBenchmark.success>
                                                    <td><span class="label warning">Failed</span></td>
                                                <#else>
                                                    <td>${singleBenchmark.winningScoreDifference}&nbsp;<@addSingleRankingBadge singleBenchmark=singleBenchmark/></td>
                                                </#if>
                                            </#if>
                                        </#list>
                                        <td>${solverBenchmark.averageWinningScoreDifference!""}</td>
                                    </tr>
                                </#list>
                                </table>
                            </div>
                            <div class="tab-pane" id="summary_worstScoreDifferencePercentage">
                                <h3>Worst score difference percentage summary (ROI)</h3>
                                <div class="tabbable tabs-right">
                                    <ul class="nav nav-tabs">
                                    <#assign scoreLevelIndex = 0>
                                    <#list benchmarkReport.worstScoreDifferencePercentageSummaryChartFileList as worstScoreDifferencePercentageSummaryChartFile>
                                        <li<#if scoreLevelIndex == benchmarkReport.defaultShownScoreLevelIndex> class="active"</#if>>
                                            <a href="#summary_worstScoreDifferencePercentage_chart_${scoreLevelIndex}" data-toggle="tab">Score level ${scoreLevelIndex}</a>
                                        </li>
                                        <#assign scoreLevelIndex = scoreLevelIndex + 1>
                                    </#list>
                                    </ul>
                                    <div class="tab-content">
                                    <#assign scoreLevelIndex = 0>
                                    <#list benchmarkReport.worstScoreDifferencePercentageSummaryChartFileList as worstScoreDifferencePercentageSummaryChartFile>
                                        <div class="tab-pane<#if scoreLevelIndex == benchmarkReport.defaultShownScoreLevelIndex> active</#if>" id="summary_worstScoreDifferencePercentage_chart_${scoreLevelIndex}">
                                            <div class="benchmark-chart">
                                                <img src="summary/${worstScoreDifferencePercentageSummaryChartFile.name}"/>
                                            </div>
                                        </div>
                                        <#assign scoreLevelIndex = scoreLevelIndex + 1>
                                    </#list>
                                    </div>
                                </div>
                                <table class="benchmark-table table table-striped table-bordered">
                                    <tr>
                                        <th>Solver</th>
                                    <#list benchmarkReport.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>
                                        <th>${problemBenchmark.name}</th>
                                    </#list>
                                        <th>Average</th>
                                    </tr>
                                <#list benchmarkReport.plannerBenchmark.solverBenchmarkList as solverBenchmark>
                                    <tr<#if solverBenchmark.favorite> class="favoriteSolverBenchmark"</#if>>
                                        <th>${solverBenchmark.name}&nbsp;<@addSolverRankingBadge solverBenchmark=solverBenchmark/></th>
                                        <#list benchmarkReport.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>
                                            <#if !solverBenchmark.findSingleBenchmark(problemBenchmark)??>
                                                <td></td>
                                            <#else>
                                                <#assign singleBenchmark = solverBenchmark.findSingleBenchmark(problemBenchmark)>
                                                <#if !singleBenchmark.success>
                                                    <td><span class="label warning">Failed</span></td>
                                                <#else>
                                                    <td>${singleBenchmark.worstScoreDifferencePercentage.toString(.locale)}&nbsp;<@addSingleRankingBadge singleBenchmark=singleBenchmark/></td>
                                                </#if>
                                            </#if>
                                        </#list>
                                    <#if !solverBenchmark.averageWorstScoreDifferencePercentage??>
                                        <td></td>
                                    <#else>
                                        <td>${solverBenchmark.averageWorstScoreDifferencePercentage.toString(.locale)}</td>
                                    </#if>
                                    </tr>
                                </#list>
                                </table>
                            </div>
                        </div>
                        <!-- HACK Duplication to show the navigation tabs in the same viewport as the tables -->
                        <ul class="nav nav-pills">
                            <li class="active">
                                <a href="#summary_bestScore" data-toggle="tab">Best score</a>
                            </li>
                            <li>
                                <a href="#summary_bestScoreScalability" data-toggle="tab">Best score scalability</a>
                            </li>
                            <li>
                                <a href="#summary_winningScoreDifference" data-toggle="tab">Winning score difference</a>
                            </li>
                            <li>
                                <a href="#summary_worstScoreDifferencePercentage" data-toggle="tab">Worst score difference percentage (ROI)</a>
                            </li>
                        </ul>
                    </div>
                </section>

                <section id="summary_performance">
                    <h2>Performance summary</h2>
                    <div class="tabbable">
                        <ul class="nav nav-pills">
                            <li>
                                <a href="#summary_timeSpend" data-toggle="tab">Time spend</a>
                            </li>
                            <li>
                                <a href="#summary_timeSpendScalability" data-toggle="tab">Time spend scalability</a>
                            </li>
                            <li class="active">
                                <a href="#summary_averageCalculateCount" data-toggle="tab">Average calculation count</a>
                            </li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane" id="summary_timeSpend">
                                <h3>Time spend summary</h3>
                                <div class="benchmark-chart">
                                    <img src="summary/${benchmarkReport.timeSpendSummaryChartFile.name}"/>
                                </div>
                                <table class="benchmark-table table table-striped table-bordered">
                                    <tr>
                                        <th>Solver</th>
                                    <#list benchmarkReport.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>
                                        <th>${problemBenchmark.name}</th>
                                    </#list>
                                    </tr>
                                    <tr>
                                        <th class="problemScale">Problem scale</th>
                                    <#list benchmarkReport.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>
                                        <td class="problemScale">${problemBenchmark.problemScale!""}</td>
                                    </#list>
                                    </tr>
                                <#list benchmarkReport.plannerBenchmark.solverBenchmarkList as solverBenchmark>
                                    <tr<#if solverBenchmark.favorite> class="favoriteSolverBenchmark"</#if>>
                                        <th>${solverBenchmark.name}&nbsp;<@addSolverRankingBadge solverBenchmark=solverBenchmark/></th>
                                        <#list benchmarkReport.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>
                                            <#if !solverBenchmark.findSingleBenchmark(problemBenchmark)??>
                                                <td></td>
                                            <#else>
                                                <#assign singleBenchmark = solverBenchmark.findSingleBenchmark(problemBenchmark)>
                                                <#if !singleBenchmark.success>
                                                    <td><span class="label warning">Failed</span></td>
                                                <#else>
                                                    <td>${singleBenchmark.timeMillisSpend}</td>
                                                </#if>
                                            </#if>
                                        </#list>
                                    </tr>
                                </#list>
                                </table>
                            </div>
                            <div class="tab-pane" id="summary_timeSpendScalability">
                                <h3>Time spend scalability summary</h3>
                                <div class="benchmark-chart">
                                    <img src="summary/${benchmarkReport.timeSpendScalabilitySummaryChartFile.name}"/>
                                </div>
                            </div>
                            <div class="tab-pane active" id="summary_averageCalculateCount">
                                <h3>Average calculate count summary</h3>
                                <div class="benchmark-chart">
                                    <img src="summary/${benchmarkReport.averageCalculateCountSummaryChartFile.name}"/>
                                </div>
                                <table class="benchmark-table table table-striped table-bordered">
                                    <tr>
                                        <th>Solver</th>
                                    <#list benchmarkReport.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>
                                        <th>${problemBenchmark.name}</th>
                                    </#list>
                                        <th>Average</th>
                                    </tr>
                                    <tr>
                                        <th class="problemScale">Problem scale</th>
                                    <#list benchmarkReport.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>
                                        <td class="problemScale">${problemBenchmark.problemScale!""}</td>
                                    </#list>
                                        <td class="problemScale">${benchmarkReport.plannerBenchmark.averageProblemScale!""}</td>
                                    </tr>
                                <#list benchmarkReport.plannerBenchmark.solverBenchmarkList as solverBenchmark>
                                    <tr<#if solverBenchmark.favorite> class="favoriteSolverBenchmark"</#if>>
                                        <th>${solverBenchmark.name}&nbsp;<@addSolverRankingBadge solverBenchmark=solverBenchmark/></th>
                                        <#list benchmarkReport.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>
                                            <#if !solverBenchmark.findSingleBenchmark(problemBenchmark)??>
                                                <td></td>
                                            <#else>
                                                <#assign singleBenchmark = solverBenchmark.findSingleBenchmark(problemBenchmark)>
                                                <#if !singleBenchmark.success>
                                                    <td><span class="label warning">Failed</span></td>
                                                <#else>
                                                    <td>${singleBenchmark.averageCalculateCountPerSecond}/s</td>
                                                </#if>
                                            </#if>
                                        </#list>
                                        <td>${solverBenchmark.averageAverageCalculateCountPerSecond!""}/s</td>
                                    </tr>
                                </#list>
                                </table>
                            </div>
                        </div>
                        <!-- HACK Duplication to show the navigation tabs in the same viewport as the tables -->
                        <ul class="nav nav-pills">
                            <li>
                                <a href="#summary_timeSpend" data-toggle="tab">Time spend</a>
                            </li>
                            <li>
                                <a href="#summary_timeSpendScalability" data-toggle="tab">Time spend scalability</a>
                            </li>
                            <li class="active">
                                <a href="#summary_averageCalculateCount" data-toggle="tab">Average calculation count</a>
                            </li>
                        </ul>
                    </div>
                </section>
            </section>

            <section id="problemBenchmark">
                <div class="page-header">
                    <h1>Problem benchmarks</h1>
                </div>
            <#list benchmarkReport.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>
                <section id="problemBenchmark_${problemBenchmark.name}">
                    <h2>${problemBenchmark.name}</h2>
                    <#if problemBenchmark.hasAnyFailure()>
                        <div class="alert alert-error">
                            <p>${problemBenchmark.failureCount} benchmarks have failed!</p>
                        </div>
                    </#if>
                    <#if problemBenchmark.averageUsedMemoryAfterInputSolution??>
                        <p>Memory usage after loading the inputSolution (before creating the Solver): ${problemBenchmark.averageUsedMemoryAfterInputSolution?string.number} bytes on average.</p>
                    </#if>
                    <#if problemBenchmark.hasAnySuccess() && problemBenchmark.hasAnyProblemStatistic()>
                        <div class="tabbable">
                            <ul class="nav nav-tabs">
                                <#assign firstRow = true>
                                <#list problemBenchmark.problemStatisticList as problemStatistic>
                                    <li<#if firstRow> class="active"</#if>>
                                        <a href="#problemStatistic_${problemStatistic.anchorId}" data-toggle="tab">${problemStatistic.problemStatisticType}</a>
                                    </li>
                                    <#assign firstRow = false>
                                </#list>
                            </ul>
                            <div class="tab-content">
                                <#assign firstRow = true>
                                <#list problemBenchmark.problemStatisticList as problemStatistic>
                                    <div class="tab-pane<#if firstRow> active</#if>" id="problemStatistic_${problemStatistic.anchorId}">
                                        <#list problemStatistic.warningList as warning>
                                            <div class="alert alert-error">
                                                <p>${warning}</p>
                                            </div>
                                        </#list>
                                        <#if problemStatistic.problemStatisticType.name() == "BEST_SOLUTION_CHANGED">
                                            <div class="tabbable tabs-right">
                                                <ul class="nav nav-tabs">
                                                    <#assign scoreLevelIndex = 0>
                                                    <#list problemStatistic.graphFilePathList as graphFilePath>
                                                        <li<#if scoreLevelIndex == benchmarkReport.defaultShownScoreLevelIndex> class="active"</#if>>
                                                            <a href="#problemStatistic_${problemStatistic.anchorId}_${scoreLevelIndex}" data-toggle="tab">Score level ${scoreLevelIndex}</a>
                                                        </li>
                                                        <#assign scoreLevelIndex = scoreLevelIndex + 1>
                                                    </#list>
                                                </ul>
                                                <div class="tab-content">
                                                    <#assign scoreLevelIndex = 0>
                                                    <#list problemStatistic.graphFilePathList as graphFilePath>
                                                        <div class="tab-pane<#if scoreLevelIndex == benchmarkReport.defaultShownScoreLevelIndex> active</#if>" id="problemStatistic_${problemStatistic.anchorId}_${scoreLevelIndex}">
                                                            <div class="benchmark-chart">
                                                                <img src="${graphFilePath}"/>
                                                            </div>
                                                        </div>
                                                        <#assign scoreLevelIndex = scoreLevelIndex + 1>
                                                    </#list>
                                                </div>
                                            </div>
                                        <#elseif problemStatistic.problemStatisticType.name() == "IMPROVING_STEP_PERCENTAGE">
                                            <div class="tabbable tabs-right">
                                                <ul class="nav nav-tabs">
                                                    <#assign itemIndex = 0>
                                                    <#list problemStatistic.moveClasses as moveClass>
                                                        <#-- Escape illegal html element id characters -->
                                                        <#assign moveClassId = moveClass.canonicalName?replace('.', '_')?replace('$', '__')>
                                                        <li<#if itemIndex == 0> class="active"</#if>>
                                                            <a href="#problemStatistic_${problemStatistic.anchorId}_${moveClassId}" data-toggle="tab">${moveClass.getSimpleName()}</a>
                                                        </li>
                                                        <#assign itemIndex = itemIndex + 1>
                                                    </#list>
                                                </ul>
                                                <div class="tab-content">
                                                    <#assign itemIndex = 0>
                                                    <#list problemStatistic.moveClasses as moveClass>
                                                        <#-- Escape illegal html element id characters -->
                                                        <#assign moveClassId = moveClass.canonicalName?replace('.', '_')?replace('$', '__')>
                                                        <div class="tab-pane<#if itemIndex == 0> active</#if>" id="problemStatistic_${problemStatistic.anchorId}_${moveClassId}">
                                                            <div class="benchmark-chart">
                                                                <img src="${problemStatistic.getGraphFilePath(moveClass)}"/>
                                                            </div>
                                                        </div>
                                                        <#assign itemIndex = itemIndex + 1>
                                                    </#list>
                                                </div>
                                            </div>
                                        <#else>
                                            <div class="benchmark-chart">
                                                <img src="${problemStatistic.graphFilePath}"/>
                                            </div>
                                        </#if>
                                        <#if problemStatistic.problemStatisticType.name() != "IMPROVING_STEP_PERCENTAGE">
                                            <div class="btn-group download-btn-group">
                                                <button class="btn" onclick="window.location.href='${problemStatistic.csvFilePath}'"><i class="icon-download"></i> CSV file</button>
                                            </div>
                                        </#if>
                                    </div>
                                    <#assign firstRow = false>
                                </#list>
                            </div>
                        </div>
                    </#if>
                </section>
            </#list>
            </section>

            <section id="solverBenchmark">
                <div class="page-header">
                    <h1>Solver benchmarks</h1>
                </div>
            <#list benchmarkReport.plannerBenchmark.solverBenchmarkList as solverBenchmark>
                <section id="solverBenchmark_${solverBenchmark.name}">
                    <h2>${solverBenchmark.name}&nbsp;<@addSolverRankingBadge solverBenchmark=solverBenchmark/></h2>
                    <#if solverBenchmark.hasAnyFailure()>
                        <div class="alert alert-error">
                            <p>${solverBenchmark.failureCount} benchmarks have failed!</p>
                        </div>
                    </#if>
                    <button class="btn showSolverConfiguration" data-toggle="collapse" data-target="#solverBenchmark_${solverBenchmark.name}_config">
                        Show/hide Solver configuration
                    </button>
                    <div id="solverBenchmark_${solverBenchmark.name}_config" class="collapse in">
                        <pre class="prettyprint lang-xml">${solverBenchmark.solverConfigAsHtmlEscapedXml}</pre>
                    </div>
                </section>
            </#list>
            </section>

            <section id="benchmarkInformation">
                <div class="page-header">
                    <h1>Benchmark information</h1>
                </div>
                <table class="benchmark-table table table-striped">
                    <tr>
                        <th>name</th>
                        <td>${benchmarkReport.plannerBenchmark.name}</td>
                    </tr>
                    <tr>
                        <th>startingTimestamp</th>
                        <td>${benchmarkReport.plannerBenchmark.startingTimestamp?datetime}</td>
                    </tr>
                    <tr>
                        <th>warmUpTimeMillisSpend</th>
                        <td>${benchmarkReport.plannerBenchmark.warmUpTimeMillisSpend} ms</td>
                    </tr>
                    <tr>
                        <th>parallelBenchmarkCount / availableProcessors</th>
                        <td>${benchmarkReport.plannerBenchmark.parallelBenchmarkCount} / ${benchmarkReport.availableProcessors}</td>
                    </tr>
                    <tr>
                        <th>benchmarkTimeMillisSpend</th>
                        <td>${benchmarkReport.plannerBenchmark.benchmarkTimeMillisSpend} ms</td>
                    </tr>
                    <tr>
                        <th>failureCount</th>
                        <td>${benchmarkReport.plannerBenchmark.failureCount}</td>
                    </tr>
                    <tr>
                        <th>VM max memory (as in -Xmx but lower)</th>
                        <td>${benchmarkReport.maxMemory?string.number} bytes</td>
                    </tr>
                    <tr>
                        <th>Operating system</th>
                        <td>${benchmarkReport.operatingSystem}</td>
                    </tr>
                    <tr>
                        <th>Java version</th>
                        <td>${benchmarkReport.javaVersion}</td>
                    </tr>
                    <tr>
                        <th>Java VM</th>
                        <td>${benchmarkReport.javaVM}</td>
                    </tr>
                    <tr>
                        <th>Planner version</th>
                        <td>${benchmarkReport.plannerVersion!"Unjarred development snapshot"}</td>
                    </tr>
                    <tr>
                        <th>Report locale</th>
                        <td>${benchmarkReport.locale!"Unknown"}</td>
                    </tr>
                </table>
            </section>
        </div>
    </div>
</div>

<script src="twitterbootstrap/js/jquery.js"></script>
<script src="twitterbootstrap/js/bootstrap.js"></script>
<script src="twitterbootstrap/js/prettify.js"></script>
</body>
</html>
