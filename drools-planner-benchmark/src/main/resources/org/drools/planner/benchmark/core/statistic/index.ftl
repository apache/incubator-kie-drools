<!DOCTYPE html>
<html lang="en">
<head>
  <title>Statistic ${plannerStatistic.plannerBenchmark.name}</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link href="css/bootstrap.css" rel="stylesheet">
  <link href="css/bootstrap-responsive.css" rel="stylesheet">
  <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
  <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
  <![endif]-->
</head>
<body>
  <div class="navbar navbar-fixed-top">
    <div class="navbar-inner">
      <div class="container">
        <ul class="nav">
          <li><a href="#summary">Summary</a></li>
          <#list plannerStatistic.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>
          <li><a href="#problem_${problemBenchmark.name}">${problemBenchmark.name}</a></li>
          </#list>
        </ul>
      </div>
    </div>
  </div>

  <section id="summary">
    <h1>Summary</h1>
    <h2>Best score summary chart</h2>
    <img src="${plannerStatistic.bestScoreSummaryFile.name}"/>
    <h2>Winning score difference summary chart</h2>
    <img src="${plannerStatistic.winningScoreDifferenceSummaryFile.name}"/>
    <h2>Time spend summary chart</h2>
    <img src="${plannerStatistic.timeSpendSummaryFile.name}"/>
    <h2>Scalability summary chart</h2>
    <img src="${plannerStatistic.scalabilitySummaryFile.name}"/>
    <h2>Average calculate count summary chart</h2>
    <img src="${plannerStatistic.averageCalculateCountSummaryFile.name}"/>
    <h2>Best score summary table</h2>
    <table class="table table-striped table-bordered">
      <tr>
        <th>Solver</th>
        <#list plannerStatistic.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>
        <th>${problemBenchmark.name}</th>
        </#list>
        <th>Average</th>
        <th>Ranking</th>
      </tr>
      <#list plannerStatistic.plannerBenchmark.solverBenchmarkList as solverBenchmark>
      <tr>
        <th>${solverBenchmark.name}</th>
        <#list plannerStatistic.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>
        <#assign singleBenchmark = solverBenchmark.findSingleBenchmark(problemBenchmark)>
        <#if !singleBenchmark??>
        <td></td>
        <#elseif !singleBenchmark.success>
        <td><span class="label warning">Failed</span></td>
        <#else>
        <td>${singleBenchmark.score}</td>
        </#if>
        </#list>
        <td>${solverBenchmark.averageScore}</td>
        <#if !solverBenchmark.ranking??>
        <td></td>
        <#elseif solverBenchmark.rankingBest>
        <td><span class="badge badge-success">${solverBenchmark.ranking}</span></td>
        <#else>
        <td><span class="badge">${solverBenchmark.ranking}</span></td>
        </#if>
      </tr>
      </#list>
    </table>
  </section>

  <h1>Solver benchmarks</h1>
  <p>TODO</p>

  <h1>Problem benchmarks</h1>
  <#list plannerStatistic.plannerBenchmark.unifiedProblemBenchmarkList as problemBenchmark>
  <section id="problem_${problemBenchmark.name}">
    <div class="page-header">
      <h2>${problemBenchmark.name}</h2>
    </div>
    <#if problemBenchmark.hasFailure()>
    <p>This has ${problemBenchmark.failureCount} failures.</p>
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
          <h3>${problemStatistic.problemStatisticType}</h3>
          <div class="btn-group">
            <button class="btn" onclick="window.location.href='${problemStatistic.csvFilePath}'">CVS file</button>
          </div>
          <img src="${problemStatistic.graphFilePath}"/>
        </div>
        <#assign firstRow = false>
        </#list>
      </div>
    </div>
    </#if>
  </section>
  </#list>

  <script src="js/jquery.js"></script>
  <script src="js/bootstrap.js"></script>
</body>
</html>
