<#-- @ftlvariable name="benchmarkReport" type="org.optaplanner.benchmark.impl.report.BenchmarkReport" -->
<#-- @ftlvariable name="reportHelper" type="org.optaplanner.benchmark.impl.report.ReportHelper" -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>${benchmarkReport.plannerBenchmarkResult.name} Planner benchmark report</title>
    <link href="twitterbootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="twitterbootstrap/css/bootstrap-responsive.css" rel="stylesheet"/>
    <link href="twitterbootstrap/css/prettify.css" rel="stylesheet"/>
    <link href="website/css/benchmarkReport.css" rel="stylesheet"/>
    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
</head>
<#macro addSolverRankingBadge solverBenchmarkResult>
    <#if !solverBenchmarkResult.ranking??>
    <span class="badge badge-important">F</span>
    <#elseif solverBenchmarkResult.favorite>
    <span class="badge badge-success">${solverBenchmarkResult.ranking}</span>
    <#else>
    <span class="badge">${solverBenchmarkResult.ranking}</span>
    </#if>
</#macro>
<#macro addSingleRankingBadge singleBenchmarkResult>
    <#if !singleBenchmarkResult.ranking??>
    <span class="badge badge-important">F</span>
    <#else>
        <#if singleBenchmarkResult.winner>
        <span class="badge badge-success">${singleBenchmarkResult.ranking}</span>
        <#else>
        <span class="badge">${singleBenchmarkResult.ranking}</span>
        </#if>
        <#if !singleBenchmarkResult.scoreFeasible>
        <span class="badge badge-warning">!</span>
        </#if>
    </#if>
</#macro>
<#macro addScoreLevelChartList chartFileList idPrefix>
    <div class="tabbable tabs-right">
        <ul class="nav nav-tabs">
        <#assign scoreLevelIndex = 0>
        <#list chartFileList as chartFile>
            <li<#if scoreLevelIndex == benchmarkReport.defaultShownScoreLevelIndex> class="active"</#if>>
                <a href="#${idPrefix}_chart_${scoreLevelIndex}" data-toggle="tab">Score level ${scoreLevelIndex}</a>
            </li>
            <#assign scoreLevelIndex = scoreLevelIndex + 1>
        </#list>
        </ul>
        <div class="tab-content">
        <#assign scoreLevelIndex = 0>
        <#list chartFileList as chartFile>
            <div class="tab-pane<#if scoreLevelIndex == benchmarkReport.defaultShownScoreLevelIndex> active</#if>" id="${idPrefix}_chart_${scoreLevelIndex}">
                <div class="benchmark-chart">
                    <img src="summary/${chartFile.name}"/>
                </div>
            </div>
            <#assign scoreLevelIndex = scoreLevelIndex + 1>
        </#list>
        </div>
    </div>
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
                    <li><a href="#problemBenchmarkResult">Problem benchmarks</a></li>
                    <li>
                        <ul class="nav nav-list">
                        <#list benchmarkReport.plannerBenchmarkResult.unifiedProblemBenchmarkResultList as problemBenchmarkResult>
                            <li><a href="#problemBenchmark_${problemBenchmarkResult.name}">${problemBenchmarkResult.name}</a></li>
                        </#list>
                        </ul>
                    </li>
                    <li class="divider"></li>
                    <li><a href="#solverBenchmarkResult">Solver benchmarks</a></li>
                    <li>
                        <ul class="nav nav-list">
                        <#list benchmarkReport.plannerBenchmarkResult.solverBenchmarkResultList as solverBenchmarkResult>
                            <li><a href="#solverBenchmark_${solverBenchmarkResult.name}">${solverBenchmarkResult.name}&nbsp;<@addSolverRankingBadge solverBenchmarkResult=solverBenchmarkResult/></a></li>
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
            <#if benchmarkReport.plannerBenchmarkResult.hasAnyFailure()>
                <div class="alert alert-error">
                    <p>${benchmarkReport.plannerBenchmarkResult.failureCount} benchmarks have failed!</p>
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
                                <p>Useful for visualizing the best solver configuration.</p>
                                <@addScoreLevelChartList chartFileList=benchmarkReport.bestScoreSummaryChartFileList idPrefix="summary_bestScore" />
                                <table class="benchmark-table table table-striped table-bordered">
                                    <tr>
                                        <th rowspan="2">Solver</th>
                                        <th rowspan="2">Total</th>
                                        <th rowspan="2">Average</th>
                                        <th rowspan="2">Standard Deviation</th>
                                        <th colspan="${benchmarkReport.plannerBenchmarkResult.unifiedProblemBenchmarkResultList?size}">Problem</th>
                                    </tr>
                                    <tr>
                                    <#list benchmarkReport.plannerBenchmarkResult.unifiedProblemBenchmarkResultList as problemBenchmarkResult>
                                        <th>${problemBenchmarkResult.name}</th>
                                    </#list>
                                    </tr>
                                <#list benchmarkReport.plannerBenchmarkResult.solverBenchmarkResultList as solverBenchmarkResult>
                                    <tr<#if solverBenchmarkResult.favorite> class="favoriteSolverBenchmark"</#if>>
                                        <th>${solverBenchmarkResult.name}&nbsp;<@addSolverRankingBadge solverBenchmarkResult=solverBenchmarkResult/></th>
                                        <td>${solverBenchmarkResult.totalScore!""}</td>
                                        <td>${solverBenchmarkResult.averageScore!""}</td>
                                        <td>${solverBenchmarkResult.standardDeviationString!""}</td>
                                        <#list benchmarkReport.plannerBenchmarkResult.unifiedProblemBenchmarkResultList as problemBenchmarkResult>
                                            <#if !solverBenchmarkResult.findSingleBenchmark(problemBenchmarkResult)??>
                                                <td></td>
                                            <#else>
                                                <#assign singleBenchmarkResult = solverBenchmarkResult.findSingleBenchmark(problemBenchmarkResult)>
                                                <#if !singleBenchmarkResult.success>
                                                    <td><span class="label label-important">Failed</span></td>
                                                <#else>
                                                    <td>${singleBenchmarkResult.score}&nbsp;<@addSingleRankingBadge singleBenchmarkResult=singleBenchmarkResult/></td>
                                                </#if>
                                            </#if>
                                        </#list>
                                    </tr>
                                </#list>
                                </table>
                            </div>
                            <div class="tab-pane" id="summary_bestScoreScalability">
                                <h3>Best score scalability summary</h3>
                                <p>Useful for visualizing the scalability of each solver configuration.</p>
                                <@addScoreLevelChartList chartFileList=benchmarkReport.bestScoreScalabilitySummaryChartFileList idPrefix="summary_bestScoreScalability" />
                            </div>
                            <div class="tab-pane" id="summary_winningScoreDifference">
                                <h3>Winning score difference summary</h3>
                                <p>Useful for zooming in on the results of the best score summary.</p>
                                <@addScoreLevelChartList chartFileList=benchmarkReport.winningScoreDifferenceSummaryChartFileList idPrefix="summary_winningScoreDifference" />
                                <table class="benchmark-table table table-striped table-bordered">
                                    <tr>
                                        <th rowspan="2">Solver</th>
                                        <th rowspan="2">Total</th>
                                        <th rowspan="2">Average</th>
                                        <th colspan="${benchmarkReport.plannerBenchmarkResult.unifiedProblemBenchmarkResultList?size}">Problem</th>
                                    </tr>
                                    <tr>
                                    <#list benchmarkReport.plannerBenchmarkResult.unifiedProblemBenchmarkResultList as problemBenchmarkResult>
                                        <th>${problemBenchmarkResult.name}</th>
                                    </#list>
                                    </tr>
                                <#list benchmarkReport.plannerBenchmarkResult.solverBenchmarkResultList as solverBenchmarkResult>
                                    <tr<#if solverBenchmarkResult.favorite> class="favoriteSolverBenchmark"</#if>>
                                        <th>${solverBenchmarkResult.name}&nbsp;<@addSolverRankingBadge solverBenchmarkResult=solverBenchmarkResult/></th>
                                        <td>${solverBenchmarkResult.totalWinningScoreDifference!""}</td>
                                        <td>${solverBenchmarkResult.averageWinningScoreDifference!""}</td>
                                        <#list benchmarkReport.plannerBenchmarkResult.unifiedProblemBenchmarkResultList as problemBenchmarkResult>
                                            <#if !solverBenchmarkResult.findSingleBenchmark(problemBenchmarkResult)??>
                                                <td></td>
                                            <#else>
                                                <#assign singleBenchmarkResult = solverBenchmarkResult.findSingleBenchmark(problemBenchmarkResult)>
                                                <#if !singleBenchmarkResult.success>
                                                    <td><span class="label label-important">Failed</span></td>
                                                <#else>
                                                    <td>${singleBenchmarkResult.winningScoreDifference}&nbsp;<@addSingleRankingBadge singleBenchmarkResult=singleBenchmarkResult/></td>
                                                </#if>
                                            </#if>
                                        </#list>
                                    </tr>
                                </#list>
                                </table>
                            </div>
                            <div class="tab-pane" id="summary_worstScoreDifferencePercentage">
                                <h3>Worst score difference percentage summary (ROI)</h3>
                                <p>Useful for visualizing the return on investment (ROI) to decision makers.</p>
                                <@addScoreLevelChartList chartFileList=benchmarkReport.worstScoreDifferencePercentageSummaryChartFileList idPrefix="summary_worstScoreDifferencePercentage" />
                                <table class="benchmark-table table table-striped table-bordered">
                                    <tr>
                                        <th rowspan="2">Solver</th>
                                        <th rowspan="2">Average</th>
                                        <th colspan="${benchmarkReport.plannerBenchmarkResult.unifiedProblemBenchmarkResultList?size}">Problem</th>
                                    </tr>
                                    <tr>
                                    <#list benchmarkReport.plannerBenchmarkResult.unifiedProblemBenchmarkResultList as problemBenchmarkResult>
                                        <th>${problemBenchmarkResult.name}</th>
                                    </#list>
                                    </tr>
                                <#list benchmarkReport.plannerBenchmarkResult.solverBenchmarkResultList as solverBenchmarkResult>
                                    <tr<#if solverBenchmarkResult.favorite> class="favoriteSolverBenchmark"</#if>>
                                        <th>${solverBenchmarkResult.name}&nbsp;<@addSolverRankingBadge solverBenchmarkResult=solverBenchmarkResult/></th>
                                        <#if !solverBenchmarkResult.averageWorstScoreDifferencePercentage??>
                                            <td></td>
                                        <#else>
                                            <td>${solverBenchmarkResult.averageWorstScoreDifferencePercentage.toString(.locale)}</td>
                                        </#if>
                                        <#list benchmarkReport.plannerBenchmarkResult.unifiedProblemBenchmarkResultList as problemBenchmarkResult>
                                            <#if !solverBenchmarkResult.findSingleBenchmark(problemBenchmarkResult)??>
                                                <td></td>
                                            <#else>
                                                <#assign singleBenchmarkResult = solverBenchmarkResult.findSingleBenchmark(problemBenchmarkResult)>
                                                <#if !singleBenchmarkResult.success>
                                                    <td><span class="label label-important">Failed</span></td>
                                                <#else>
                                                    <td>${singleBenchmarkResult.worstScoreDifferencePercentage.toString(.locale)}&nbsp;<@addSingleRankingBadge singleBenchmarkResult=singleBenchmarkResult/></td>
                                                </#if>
                                            </#if>
                                        </#list>
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
                            <li class="active">
                                <a href="#summary_averageCalculateCount" data-toggle="tab">Average calculation count</a>
                            </li>
                            <li>
                                <a href="#summary_timeSpend" data-toggle="tab">Time spend</a>
                            </li>
                            <li>
                                <a href="#summary_timeSpendScalability" data-toggle="tab">Time spend scalability</a>
                            </li>
                            <li>
                                <a href="#summary_bestScorePerTimeSpend" data-toggle="tab">Best score per time spend</a>
                            </li>
                        </ul>
                        <div class="tab-content">
                            <div class="tab-pane active" id="summary_averageCalculateCount">
                                <h3>Average calculate count summary</h3>
                                <p>
                                    Useful for comparing different score calculators and/or score rule implementations
                                    (presuming that the solver configurations do not differ otherwise).
                                    Also useful to measure the scalability cost of an extra constraint.
                                </p>
                                <div class="benchmark-chart">
                                    <img src="summary/${benchmarkReport.averageCalculateCountSummaryChartFile.name}"/>
                                </div>
                                <table class="benchmark-table table table-striped table-bordered">
                                    <tr>
                                        <th rowspan="2">Solver</th>
                                        <th rowspan="2">Average</th>
                                        <th colspan="${benchmarkReport.plannerBenchmarkResult.unifiedProblemBenchmarkResultList?size}">Problem</th>
                                    </tr>
                                    <tr>
                                    <#list benchmarkReport.plannerBenchmarkResult.unifiedProblemBenchmarkResultList as problemBenchmarkResult>
                                        <th>${problemBenchmarkResult.name}</th>
                                    </#list>
                                    </tr>
                                    <tr>
                                        <th class="problemScale">Problem scale</th>
                                        <td class="problemScale">${benchmarkReport.plannerBenchmarkResult.averageProblemScale!""}</td>
                                    <#list benchmarkReport.plannerBenchmarkResult.unifiedProblemBenchmarkResultList as problemBenchmarkResult>
                                        <td class="problemScale">${problemBenchmarkResult.problemScale!""}</td>
                                    </#list>
                                    </tr>
                                <#list benchmarkReport.plannerBenchmarkResult.solverBenchmarkResultList as solverBenchmarkResult>
                                    <tr<#if solverBenchmarkResult.favorite> class="favoriteSolverBenchmark"</#if>>
                                        <th>${solverBenchmarkResult.name}&nbsp;<@addSolverRankingBadge solverBenchmarkResult=solverBenchmarkResult/></th>
                                        <td>${solverBenchmarkResult.averageAverageCalculateCountPerSecond!""}/s</td>
                                        <#list benchmarkReport.plannerBenchmarkResult.unifiedProblemBenchmarkResultList as problemBenchmarkResult>
                                            <#if !solverBenchmarkResult.findSingleBenchmark(problemBenchmarkResult)??>
                                                <td></td>
                                            <#else>
                                                <#assign singleBenchmarkResult = solverBenchmarkResult.findSingleBenchmark(problemBenchmarkResult)>
                                                <#if !singleBenchmarkResult.success>
                                                    <td><span class="label label-important">Failed</span></td>
                                                <#else>
                                                    <td>${singleBenchmarkResult.averageCalculateCountPerSecond}/s</td>
                                                </#if>
                                            </#if>
                                        </#list>
                                    </tr>
                                </#list>
                                </table>
                            </div>
                            <div class="tab-pane" id="summary_timeSpend">
                                <h3>Time spend summary</h3>
                                <p>Useful for visualizing the performance of construction heuristics (presuming that no other solver phases are configured).</p>
                                <div class="benchmark-chart">
                                    <img src="summary/${benchmarkReport.timeSpendSummaryChartFile.name}"/>
                                </div>
                                <table class="benchmark-table table table-striped table-bordered">
                                    <tr>
                                        <th rowspan="2">Solver</th>
                                        <th rowspan="2">Average</th>
                                        <th colspan="${benchmarkReport.plannerBenchmarkResult.unifiedProblemBenchmarkResultList?size}">Problem</th>
                                    </tr>
                                    <tr>
                                    <#list benchmarkReport.plannerBenchmarkResult.unifiedProblemBenchmarkResultList as problemBenchmarkResult>
                                        <th>${problemBenchmarkResult.name}</th>
                                    </#list>
                                    </tr>
                                    <tr>
                                        <th class="problemScale">Problem scale</th>
                                        <td class="problemScale">${benchmarkReport.plannerBenchmarkResult.averageProblemScale!""}</td>
                                    <#list benchmarkReport.plannerBenchmarkResult.unifiedProblemBenchmarkResultList as problemBenchmarkResult>
                                        <td class="problemScale">${problemBenchmarkResult.problemScale!""}</td>
                                    </#list>
                                    </tr>
                                <#list benchmarkReport.plannerBenchmarkResult.solverBenchmarkResultList as solverBenchmarkResult>
                                    <tr<#if solverBenchmarkResult.favorite> class="favoriteSolverBenchmark"</#if>>
                                        <th>${solverBenchmarkResult.name}&nbsp;<@addSolverRankingBadge solverBenchmarkResult=solverBenchmarkResult/></th>
                                        <td>${solverBenchmarkResult.averageTimeMillisSpend!""}</td>
                                        <#list benchmarkReport.plannerBenchmarkResult.unifiedProblemBenchmarkResultList as problemBenchmarkResult>
                                            <#if !solverBenchmarkResult.findSingleBenchmark(problemBenchmarkResult)??>
                                                <td></td>
                                            <#else>
                                                <#assign singleBenchmarkResult = solverBenchmarkResult.findSingleBenchmark(problemBenchmarkResult)>
                                                <#if !singleBenchmarkResult.success>
                                                    <td><span class="label label-important">Failed</span></td>
                                                <#else>
                                                    <td>${singleBenchmarkResult.timeMillisSpend}</td>
                                                </#if>
                                            </#if>
                                        </#list>
                                    </tr>
                                </#list>
                                </table>
                            </div>
                            <div class="tab-pane" id="summary_timeSpendScalability">
                                <h3>Time spend scalability summary</h3>
                                <p>Useful for extrapolating the scalability of construction heuristics (presuming that no other solver phases are configured).</p>
                                <div class="benchmark-chart">
                                    <img src="summary/${benchmarkReport.timeSpendScalabilitySummaryChartFile.name}"/>
                                </div>
                            </div>
                            <div class="tab-pane" id="summary_bestScorePerTimeSpend">
                                <h3>Best score per time spend summary</h3>
                                <p>Useful for visualizing trade-off between the best score versus the time spend for construction heuristics (presuming that no other solver phases are configured).</p>
                                <@addScoreLevelChartList chartFileList=benchmarkReport.bestScorePerTimeSpendSummaryChartFileList idPrefix="summary_bestScorePerTimeSpend" />
                            </div>
                        </div>
                        <!-- HACK Duplication to show the navigation tabs in the same viewport as the tables -->
                        <ul class="nav nav-pills">
                            <li class="active">
                                <a href="#summary_averageCalculateCount" data-toggle="tab">Average calculation count</a>
                            </li>
                            <li>
                                <a href="#summary_timeSpend" data-toggle="tab">Time spend</a>
                            </li>
                            <li>
                                <a href="#summary_timeSpendScalability" data-toggle="tab">Time spend scalability</a>
                            </li>
                            <li>
                                <a href="#summary_bestScorePerTimeSpend" data-toggle="tab">Best score per time spend</a>
                            </li>
                        </ul>
                    </div>
                </section>
            </section>

            <section id="problemBenchmarkResult">
                <div class="page-header">
                    <h1>Problem benchmarks</h1>
                </div>
            <#list benchmarkReport.plannerBenchmarkResult.unifiedProblemBenchmarkResultList as problemBenchmarkResult>
                <section id="problemBenchmark_${problemBenchmarkResult.name}">
                    <h2>${problemBenchmarkResult.name}</h2>
                    <#if problemBenchmarkResult.hasAnyFailure()>
                        <div class="alert alert-error">
                            <p>${problemBenchmarkResult.failureCount} benchmarks have failed!</p>
                        </div>
                    </#if>
                    <#if problemBenchmarkResult.averageUsedMemoryAfterInputSolution??>
                        <p>Memory usage after loading the inputSolution (before creating the Solver): ${problemBenchmarkResult.averageUsedMemoryAfterInputSolution?string.number} bytes on average.</p>
                    </#if>
                    <#if problemBenchmarkResult.hasAnySuccess() && problemBenchmarkResult.hasAnyProblemStatistic()>
                        <div class="tabbable">
                            <ul class="nav nav-tabs">
                                <#assign firstRow = true>
                                <#list problemBenchmarkResult.problemStatisticList as problemStatistic>
                                    <li<#if firstRow> class="active"</#if>>
                                        <a href="#problemStatistic_${problemStatistic.anchorId}" data-toggle="tab">${problemStatistic.problemStatisticType}</a>
                                    </li>
                                    <#assign firstRow = false>
                                </#list>
                            </ul>
                            <div class="tab-content">
                                <#assign firstRow = true>
                                <#list problemBenchmarkResult.problemStatisticList as problemStatistic>
                                    <div class="tab-pane<#if firstRow> active</#if>" id="problemStatistic_${problemStatistic.anchorId}">
                                        <#list problemStatistic.warningList as warning>
                                            <div class="alert alert-error">
                                                <p>${warning}</p>
                                            </div>
                                        </#list>
                                        <#if problemStatistic.problemStatisticType.name() == "BEST_SCORE" || problemStatistic.problemStatisticType.name() == "STEP_SCORE" >
                                            <div class="tabbable tabs-right">
                                                <ul class="nav nav-tabs">
                                                    <#assign scoreLevelIndex = 0>
                                                    <#list problemStatistic.graphFileList as graphFile>
                                                        <li<#if scoreLevelIndex == benchmarkReport.defaultShownScoreLevelIndex> class="active"</#if>>
                                                            <a href="#problemStatistic_${problemStatistic.anchorId}_${scoreLevelIndex}" data-toggle="tab">Score level ${scoreLevelIndex}</a>
                                                        </li>
                                                        <#assign scoreLevelIndex = scoreLevelIndex + 1>
                                                    </#list>
                                                </ul>
                                                <div class="tab-content">
                                                    <#assign scoreLevelIndex = 0>
                                                    <#list problemStatistic.graphFileList as graphFile>
                                                        <div class="tab-pane<#if scoreLevelIndex == benchmarkReport.defaultShownScoreLevelIndex> active</#if>" id="problemStatistic_${problemStatistic.anchorId}_${scoreLevelIndex}">
                                                            <div class="benchmark-chart">
                                                                <img src="${benchmarkReport.getRelativePathToBenchmarkReportDirectory(graphFile)}"/>
                                                            </div>
                                                        </div>
                                                        <#assign scoreLevelIndex = scoreLevelIndex + 1>
                                                    </#list>
                                                </div>
                                            </div>
                                        <#else>
                                            <div class="benchmark-chart">
                                                <img src="${benchmarkReport.getRelativePathToBenchmarkReportDirectory(problemStatistic.graphFile)}"/>
                                            </div>
                                        </#if>
                                        <span>CSV files per solver:</span>
                                        <div class="btn-group download-btn-group">
                                        <#list problemStatistic.singleStatisticList as singleStatistic>
                                            <button class="btn" onclick="window.location.href='${benchmarkReport.getRelativePathToBenchmarkReportDirectory(singleStatistic.csvFile)}'"><i class="icon-download"></i></button>
                                        </#list>
                                        </div>
                                    </div>
                                    <#assign firstRow = false>
                                </#list>
                            </div>
                        </div>
                    </#if>
                </section>
            </#list>
            </section>

            <section id="solverBenchmarkResult">
                <div class="page-header">
                    <h1>Solver benchmarks</h1>
                </div>
            <#list benchmarkReport.plannerBenchmarkResult.solverBenchmarkResultList as solverBenchmarkResult>
                <section id="solverBenchmark_${solverBenchmarkResult.name}">
                    <h2>${solverBenchmarkResult.name}&nbsp;<@addSolverRankingBadge solverBenchmarkResult=solverBenchmarkResult/></h2>
                    <#if solverBenchmarkResult.hasAnyFailure()>
                        <div class="alert alert-error">
                            <p>${solverBenchmarkResult.failureCount} benchmarks have failed!</p>
                        </div>
                    </#if>
                    <button class="btn showSolverConfiguration" data-toggle="collapse" data-target="#solverBenchmark_${solverBenchmarkResult.name}_config">
                        Show/hide Solver configuration
                    </button>
                    <div id="solverBenchmark_${solverBenchmarkResult.name}_config" class="collapse in">
                        <pre class="prettyprint lang-xml">${solverBenchmarkResult.solverConfigAsHtmlEscapedXml}</pre>
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
                        <td>${benchmarkReport.plannerBenchmarkResult.name}</td>
                    </tr>
                    <tr>
                        <th>startingTimestamp</th>
                        <td>${benchmarkReport.plannerBenchmarkResult.startingTimestamp?datetime}</td>
                    </tr>
                    <tr>
                        <th>warmUpTimeMillisSpend</th>
                        <td>${benchmarkReport.plannerBenchmarkResult.warmUpTimeMillisSpend} ms</td>
                    </tr>
                    <tr>
                        <th>parallelBenchmarkCount / availableProcessors</th>
                        <td>${benchmarkReport.plannerBenchmarkResult.parallelBenchmarkCount} / ${benchmarkReport.availableProcessors}</td>
                    </tr>
                    <tr>
                        <th>benchmarkTimeMillisSpend</th>
                        <td>${benchmarkReport.plannerBenchmarkResult.benchmarkTimeMillisSpend!""} ms</td>
                    </tr>
                    <tr>
                        <th>failureCount</th>
                        <td>${benchmarkReport.plannerBenchmarkResult.failureCount}</td>
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
