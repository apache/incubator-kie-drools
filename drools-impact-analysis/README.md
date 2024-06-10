<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->

# drools-impact-analysis

This tool can:

* Analyze relationships between rules
* Analyze impacts of changing a rule
* Visualize the graph using Dot language format. Simple text format is also available.

This is an *experimental* feature. The APIs may change in future versions.

## Usage

You can find an example usage in ExampleUsageTest.java

1) Have `drools-impact-analysis-graph-graphviz` in your project dependency

```xml
    <dependency>
      <groupId>org.drools</groupId>
      <artifactId>drools-impact-analysis-graph-graphviz</artifactId>
      <version>${drools.version}</version>
    </dependency>
```

2) Create a `KieFileSystem` to store your assets as usual. Then call `KieBuilder.buildAll(ImpactAnalysisProject.class)`. You can get `AnalysisModel`.

```java
        // set up KieFileSystem
        ...
        KieBuilder kieBuilder = KieServices.Factory.get().newKieBuilder(kfs).buildAll(ImpactAnalysisProject.class);
        ImpactAnalysisKieModule analysisKieModule = (ImpactAnalysisKieModule) kieBuilder.getKieModule();
        AnalysisModel analysisModel = analysisKieModule.getAnalysisModel();
```
3) Convert the `AnalysisModel` to `Graph` using `ModelToGraphConverter`

```java
        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);
```

4) Specify a rule which you plan to change. `ImpactAnalysisHelper` will produce a sub graph which contains the changed rule and impacted rules

```java
        ImpactAnalysisHelper impactFilter = new ImpactAnalysisHelper();
        Graph impactedSubGraph = impactFilter.filterImpactedNodes(graph, "org.drools.impact.analysis.example.PriceCheck_11");
```

5) Generate a graph image using `GraphImageGenerator`. You can choose the format from DOT, SVG and PNG.

```java
        GraphImageGenerator generator = new GraphImageGenerator("example-impacted-sub-graph");
        generator.generateSvg(impactedSubGraph);
```

* DOT : Quick. Can be visualized by other Graphviz tools
* SVG : Quicker than PNG
* PNG : Slow. Probably not useful for a large number of rules

6) Simple text output is also available using `TextReporter`. You can choose the format from HierarchyText and FlatText.

```java
        String hierarchyText = TextReporter.toHierarchyText(impactedSubGraph);
        System.out.println(hierarchyText);
```

## Tips

* Typical use case is to view an impacted sub graph. Red node is a *changed* rule. Yellow nodes are *impacted* rules. Solid arrow represents *positive* impact, where the source rule activates the target rule. Dashed arrow represents *negative* impact, where the source rule deactivates the target rule. Dotted arrow represents *unknown* impact, where the source rule may activate or deactivate the target rule.

![example1](example1.svg)

* Whole graph could be too large if you have many rules.

![example2](example2.svg)

* You can *collapse* the graph based on rule name prefix (= RuleSet in spreadsheet) using `GraphCollapsionHelper`. It will help you to see the overview. You can also use `ImpactAnalysisHelper` to the collapsed graph.

```java
        Graph collapsedGraph = new GraphCollapsionHelper().collapseWithRuleNamePrefix(graph);
        Graph impactedCollapsedSubGraph = impactFilter.filterImpactedNodes(collapsedGraph, "org.drools.impact.analysis.example.PriceCheck");
```

![example3](example3.svg)

* You can filter the relations by giving `positiveOnly` to `true` for `ModelToGraphConverter`, `ImpactAnalysisHelper` and `GraphCollapsionHelper` constructor. So you can view only positive relations.

```java
        ModelToGraphConverter converter = new ModelToGraphConverter(true);
        Graph graph = converter.toGraph(analysisModel);
        ImpactAnalysisHelper impactFilter = new ImpactAnalysisHelper(true);
        Graph impactedSubGraph = impactFilter.filterImpactedNodes(graph, "org.drools.impact.analysis.example.PriceCheck_11");
```

![example4](example4.svg)

* If the number of rules is very large, text output would be useful. `[*]` is a *changed* rule. `[+]` is *impacted* rules. A rule with parentheses means a circular reference so it doesn't render further.

```
--- toHierarchyText ---
Inventory shortage[+]
PriceCheck_11[*]
  StatusCheck_12[+]
  (Inventory shortage)
  StatusCheck_13[+]
  StatusCheck_11[+]
    (PriceCheck_11)

--- toFlatText ---
Inventory shortage[+]
PriceCheck_11[*]
StatusCheck_11[+]
StatusCheck_12[+]
StatusCheck_13[+]
```

* You can also view backward relationship. Use `ImpactAnalysisHelper.filterImpactingNodes` and specify the target rule so you can view rules which impacts on it. Orange node is a *target* rule. Light blue nodes are *impacting* rules.

```java
        Graph impactingSubGraph = impactFilter.filterImpactingNodes(graph, "org.drools.impact.analysis.example.StatusCheck_11");
```

![example5](example5.svg)

## Troubleshooting

If you get the warning message when rendering SVG or PNG:
```
graphviz-java failed to render an image. Solutions would be:
1. Install graphviz tools in your local machine. graphviz-java will use graphviz command line binary (e.g. /usr/bin/dot) if available.
2. Consider generating a graph in DOT format and then visualize it with an external tool.
```

You would need to install graphviz tools in your local machine. If not possible, you would need to generate the graph in DOT format so that you can render it with another tool later on.
