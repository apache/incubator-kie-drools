<#-- @ftlvariable name="benchmarkHistoryReport" type="org.optaplanner.benchmark.impl.history.BenchmarkHistoryReport" -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Planner benchmark history report</title>
    <link href="twitterbootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="twitterbootstrap/css/bootstrap-responsive.css" rel="stylesheet"/>
    <link href="twitterbootstrap/css/prettify.css" rel="stylesheet"/>
    <link href="website/css/benchmarkReport.css" rel="stylesheet"/>
    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
</head>
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
                    <li><a href="#benchmarkInformation">Benchmark information</a></li>
                </ul>
            </div>
        </div>
        <div class="span10">
            <header class="main-page-header">
                <h1>Benchmark history report</h1>
            </header>
            <section id="summary">
                <div class="page-header">
                    <h1>Summary</h1>
                </div>

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
                                <p>TODO</p><#-- TODO -->
                                <#--<table class="benchmark-table table table-striped table-bordered">-->
                                    <#--<tr>-->
                                        <#--<th>Solver</th>-->
                                    <#--<#list benchmarkHistoryReport.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>-->
                                        <#--<th>${problemBenchmark.name}</th>-->
                                    <#--</#list>-->
                                        <#--<th>Average</th>-->
                                        <#--<th>Ranking</th>-->
                                    <#--</tr>-->
                                <#--<#list benchmarkHistoryReport.plannerBenchmark.solverBenchmarkList as solverBenchmark>-->
                                    <#--<tr<#if solverBenchmark.favorite> class="favoriteSolverBenchmark"</#if>>-->
                                        <#--<th>${solverBenchmark.name}&nbsp;<@addSolverRankingBadge solverBenchmark=solverBenchmark/></th>-->
                                        <#--<#list benchmarkHistoryReport.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>-->
                                            <#--<#if !solverBenchmark.findSingleBenchmark(problemBenchmark)??>-->
                                                <#--<td></td>-->
                                            <#--<#else>-->
                                                <#--<#assign singleBenchmark = solverBenchmark.findSingleBenchmark(problemBenchmark)>-->
                                                <#--<#if !singleBenchmark.success>-->
                                                    <#--<td><span class="label warning">Failed</span></td>-->
                                                <#--<#else>-->
                                                    <#--<td>${singleBenchmark.score}&nbsp;<@addSingleRankingBadge singleBenchmark=singleBenchmark/></td>-->
                                                <#--</#if>-->
                                            <#--</#if>-->
                                        <#--</#list>-->
                                        <#--<td>${solverBenchmark.averageScore!""}</td>-->
                                        <#--<td><@addSolverRankingBadge solverBenchmark=solverBenchmark/></td>-->
                                    <#--</tr>-->
                                <#--</#list>-->
                                <#--</table>-->
                            </div>
                            <div class="tab-pane" id="summary_bestScoreScalability">
                                <h3>Best score scalability summary</h3>
                                <p>TODO</p><#-- TODO -->
                            </div>
                            <div class="tab-pane" id="summary_winningScoreDifference">
                                <h3>Winning score difference summary</h3>
                                <p>TODO</p><#-- TODO -->
                            </div>
                            <div class="tab-pane" id="summary_worstScoreDifferencePercentage">
                                <h3>Worst score difference percentage summary (ROI)</h3>
                                <p>TODO</p><#-- TODO -->
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
                                <p>TODO</p><#-- TODO -->
                            </div>
                            <div class="tab-pane" id="summary_timeSpendScalability">
                                <h3>Time spend scalability summary</h3>
                                <p>TODO</p><#-- TODO -->
                            </div>
                            <div class="tab-pane active" id="summary_averageCalculateCount">
                                <h3>Average calculate count summary</h3>
                                <p>TODO</p><#-- TODO -->
                                <#--<div class="benchmark-chart">-->
                                    <#--<img src="summary/${benchmarkHistoryReport.averageCalculateCountSummaryChartFile.name}"/>-->
                                <#--</div>-->
                                <#--<table class="benchmark-table table table-striped table-bordered">-->
                                    <#--<tr>-->
                                        <#--<th>Solver</th>-->
                                    <#--<#list benchmarkHistoryReport.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>-->
                                        <#--<th>${problemBenchmark.name}</th>-->
                                    <#--</#list>-->
                                        <#--<th>Average</th>-->
                                    <#--</tr>-->
                                    <#--<tr>-->
                                        <#--<th class="problemScale">Problem scale</th>-->
                                    <#--<#list benchmarkHistoryReport.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>-->
                                        <#--<td class="problemScale">${problemBenchmark.problemScale!""}</td>-->
                                    <#--</#list>-->
                                        <#--<td class="problemScale">${benchmarkHistoryReport.plannerBenchmark.averageProblemScale!""}</td>-->
                                    <#--</tr>-->
                                <#--<#list benchmarkHistoryReport.plannerBenchmark.solverBenchmarkList as solverBenchmark>-->
                                    <#--<tr<#if solverBenchmark.favorite> class="favoriteSolverBenchmark"</#if>>-->
                                        <#--<th>${solverBenchmark.name}&nbsp;<@addSolverRankingBadge solverBenchmark=solverBenchmark/></th>-->
                                        <#--<#list benchmarkHistoryReport.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>-->
                                            <#--<#if !solverBenchmark.findSingleBenchmark(problemBenchmark)??>-->
                                                <#--<td></td>-->
                                            <#--<#else>-->
                                                <#--<#assign singleBenchmark = solverBenchmark.findSingleBenchmark(problemBenchmark)>-->
                                                <#--<#if !singleBenchmark.success>-->
                                                    <#--<td><span class="label warning">Failed</span></td>-->
                                                <#--<#else>-->
                                                    <#--<td>${singleBenchmark.averageCalculateCountPerSecond}/s</td>-->
                                                <#--</#if>-->
                                            <#--</#if>-->
                                        <#--</#list>-->
                                        <#--<td>${solverBenchmark.averageAverageCalculateCountPerSecond!""}/s</td>-->
                                    <#--</tr>-->
                                <#--</#list>-->
                                <#--</table>-->
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

            <#-- TODO problemBenchmark, solverBenchmark ? -->

            <section id="benchmarkInformation">
                <div class="page-header">
                    <h1>Benchmark information</h1>
                </div>
                <table class="benchmark-table table table-striped">
                    <#--<tr>-->
                        <#--<th>historyTimestamp</th>-->
                        <#--<td>${benchmarkHistoryReport.plannerBenchmark.historyTimestamp?datetime}</td>-->
                    <#--</tr>-->
                    <tr>
                        <th>Report locale</th>
                        <td>${benchmarkHistoryReport.locale!"Unknown"}</td>
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
