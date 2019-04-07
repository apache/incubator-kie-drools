# jBPM XES Export Service

## Overview

The jBPM XES export tool aims to facilitate exporting XES based logs from the jBPM runtime. These logs can then be consumed 
by different process mining tools to analyse different aspects from the process runtime execution such as conformance, performance, 
deviations, process discovery and more. 
eXtensible Event Stream (XES) is an xml based standard that unifies the interchange event data information between information systems on one side and analysis tools on the other side.
For more information regarding the XES standard please visit: http://www.xes-standard.org/

## Building

For building this project locally, you firstly need to have the following tools installed locally:
- git client
- Java 1.8
- Maven

Once you cloned the repository locally all you need to do is execute the following Maven build:

```
mvn clean install
```

Once the build is complete, you can use the generated JAR in 'target/jbpm-xes-${version}-jar-with-dependencies.jar' using:

```
java -jar target/jbpm-xes-1.0.0-SNAPSHOT-jar-with-dependencies.jar 
```


## Sample output XML

```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<log xes.version="2.0" xes.features="">
    <extension name="Lifecycle" prefix="lifecycle" uri="http://www.xes-standard.org/lifecycle.xesext"/>
    <extension name="Organizational" prefix="org" uri="http://www.xes-standard.org/org.xesext"/>
    <extension name="Time" prefix="time" uri="http://www.xes-standard.org/time.xesext"/>
    <extension name="Concept" prefix="concept" uri="http://www.xes-standard.org/concept.xesext"/>
    <global scope="trace">
        <string key="concept:name" value=""/>
        <date key="jbpm:start" value="2018-10-15T13:27:16.813+10:00"/>
        <string key="jbpm:status" value=""/>
        <string key="jbpm:version" value=""/>
        <string key="jbpm:description" value=""/>
        <int key="jbpm:instanceid" value="0"/>
        <string key="jbpm:correlationkey" value=""/>
        <int key="jbpm:logid" value="0"/>
    </global>
    <global scope="event">
        <date key="time:timestamp" value="2018-10-15T13:27:16.816+10:00"/>
        <string key="concept:name" value=""/>
        <string key="org:resource" value=""/>
        <string key="lifecycle:transition" value=""/>
        <string key="jbpm:nodeinstanceid" value=""/>
        <string key="jbpm:nodeid" value=""/>
        <string key="jbpm:nodetype" value=""/>
        <int key="jbpm:logid" value="0"/>
    </global>
    <classifier name="By Node Name" keys="concept:name"/>
    <classifier name="By Node Id" keys="jbpm:nodeid"/>
    <classifier name="By Name and Transition" keys="concept:name lifecycle:transition"/>
    <classifier name="By Resource" keys="org:resource"/>
    <string key="source" value="jBPM"/>
    <string key="lifecycle:model" value="standard"/>
    <string key="jbpm:processid" value="hello"/>
    <trace>
        <string key="concept:name" value="1"/>
        <int key="jbpm:logid" value="1"/>
        <string key="jbpm:correlationkey" value="1"/>
        <string key="jbpm:version" value="1.0"/>
        <string key="jbpm:description" value="Hello Process"/>
        <int key="jbpm:instanceid" value="1"/>
        <date key="jbpm:start" value="2018-10-15T13:27:16.137+10:00"/>
        <date key="jbpm:end" value="2018-10-15T13:27:16.176+10:00"/>
        <string key="jbpm:status" value="completed"/>
        <int key="jbpm:slacompliance" value="0"/>
        <event>
            <date key="time:timestamp" value="2018-10-15T13:27:16.151+10:00"/>
            <string key="concept:name" value="Start"/>
            <string key="concept:instance" value="0"/>
            <string key="lifecycle:transition" value="start"/>
            <string key="jbpm:nodeinstanceid" value="0"/>
            <int key="jbpm:logid" value="1"/>
            <string key="jbpm:nodeid" value="_1"/>
            <string key="jbpm:nodename" value="Start"/>
            <string key="jbpm:nodetype" value="StartNode"/>
            <string key="org:resource" value="jbpm"/>
        </event>
        <event>
            <date key="time:timestamp" value="2018-10-15T13:27:16.153+10:00"/>
            <string key="concept:name" value="Start"/>
            <string key="concept:instance" value="0"/>
            <string key="lifecycle:transition" value="complete"/>
            <string key="jbpm:nodeinstanceid" value="0"/>
            <int key="jbpm:logid" value="2"/>
            <string key="jbpm:nodeid" value="_1"/>
            <string key="jbpm:nodename" value="Start"/>
            <string key="jbpm:nodetype" value="StartNode"/>
            <string key="org:resource" value="jbpm"/>
        </event>
        <event>
            <date key="time:timestamp" value="2018-10-15T13:27:16.154+10:00"/>
            <string key="concept:name" value="Hello"/>
            <string key="concept:instance" value="1"/>
            <string key="lifecycle:transition" value="start"/>
            <string key="jbpm:nodeinstanceid" value="1"/>
            <int key="jbpm:logid" value="3"/>
            <string key="jbpm:nodeid" value="_2"/>
            <string key="jbpm:nodename" value="Hello"/>
            <string key="jbpm:nodetype" value="ActionNode"/>
            <string key="org:resource" value="jbpm"/>
        </event>
        <event>
            <date key="time:timestamp" value="2018-10-15T13:27:16.156+10:00"/>
            <string key="concept:name" value="Hello"/>
            <string key="concept:instance" value="1"/>
            <string key="lifecycle:transition" value="complete"/>
            <string key="jbpm:nodeinstanceid" value="1"/>
            <int key="jbpm:logid" value="4"/>
            <string key="jbpm:nodeid" value="_2"/>
            <string key="jbpm:nodename" value="Hello"/>
            <string key="jbpm:nodetype" value="ActionNode"/>
            <string key="org:resource" value="jbpm"/>
        </event>
        <event>
            <date key="time:timestamp" value="2018-10-15T13:27:16.156+10:00"/>
            <string key="concept:name" value="End"/>
            <string key="concept:instance" value="2"/>
            <string key="lifecycle:transition" value="start"/>
            <string key="jbpm:nodeinstanceid" value="2"/>
            <int key="jbpm:logid" value="5"/>
            <string key="jbpm:nodeid" value="_3"/>
            <string key="jbpm:nodename" value="End"/>
            <string key="jbpm:nodetype" value="EndNode"/>
            <string key="org:resource" value="jbpm"/>
        </event>
        <event>
            <date key="time:timestamp" value="2018-10-15T13:27:16.157+10:00"/>
            <string key="concept:name" value="End"/>
            <string key="concept:instance" value="2"/>
            <string key="lifecycle:transition" value="complete"/>
            <string key="jbpm:nodeinstanceid" value="2"/>
            <int key="jbpm:logid" value="6"/>
            <string key="jbpm:nodeid" value="_3"/>
            <string key="jbpm:nodename" value="End"/>
            <string key="jbpm:nodetype" value="EndNode"/>
            <string key="org:resource" value="jbpm"/>
        </event>
    </trace>
</log>
```

## How it works

The jBPM XES Export tool, connects directly to the jBPM log database in order to extract the relevant information.
If you intend to export any logs, please ensure that you use the `JPA` as your KJAR's audit mode.
The tool will look into the `ProcessInstanceLog` and `NodeInstanceLog` tables and filter the data based on the parameters you provided to export.
As a primary rule for the the XES log, only one process can be exported at time, for that we use the `-process` parameter where you can specify
the process definition id you would that to filter on. That will filter down the logs to only process instances that matches the paramter.
You can also use other filters like `version`, `status`, and more.  

## Usage

Out-of-box, the jBPM XES Export tool allows you to connect with the following databases:
- H2
- MariaDB
- MySQL
- PostgreSQL

To use another database, simply provide the additional jar in the classpath of the application.

### Options

The tool allows a series of custom parameters to fine tune which logs should be exported, to see the full list of options please execute:

```
java -jar target/jbpm-xes-1.0.0-SNAPSHOT-jar-with-dependencies.jar 
```

Which will outcome the following:

```
usage: xes -driver <driver> [-file <file>] [-logtype <logtype>]
       [-nodetypes] [-password <password>] -process <process> [-status
       <status>] -url <url> -user <user> [-version <version>]
 -driver <driver>       Database driver class name
 -file <file>           File name to save result XES. Default will print
                        in the console.
 -logtype <logtype>     Use 0 for node entered events or 1 for exit
                        events. Default will export all types.
 -nodetypes             Export all node type. Default will only export
                        relevant activities.
 -password <password>   Database password
 -process <process>     Process Id to export
 -status <status>       Process status to export
 -url <url>             Database url
 -user <user>           Database username
 -version <version>     Process version to export
```

### Example using a local H2 database file

```
java -jar target/jbpm-xes-1.0.0-SNAPSHOT-jar-with-dependencies.jar -driver org.h2.Driver -user sa -password sa -url jdbc:h2:file:./spring-boot-jbpm -process evaluation -file jbpm.xes
```

### Example using MySQL database

```
java -jar target/jbpm-xes-1.0.0-SNAPSHOT-jar-with-dependencies.jar -driver com.mysql.jdbc.Driver -user jbpm -password jbpm -url jdbc:mysql://localhost/jbpm -process evaluation -file evaluation.xes
```

## Feedback

Feel free to open issues if you face any problems using this tool, we would love to help you in your process mining journey!