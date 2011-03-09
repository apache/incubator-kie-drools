Welcome to Drools Planner
=========================

Introduction
------------

Drools Planner is in a beta state: backward incompatible changes may occur in the next version,
but the upgrade recipe makes it easy to upgrade your code to every new version.

Run the examples
----------------

To run the examples, run either of these:
Linux, Cygwin: ./runExamples.sh
Windows: runExamples.bat

Using Drools Planner with maven 2
---------------------------------

If you're using maven 2 to handle your dependencies,
the jars and poms should be available in the jboss maven repository:
  http://repository.jboss.org/maven2/
You 'll want to add a dependency to drools-planner-core in your project:
<dependency>
  <groupId>org.drools.planner</groupId>
  <artifactId>drools-planner-core</artifactId>
  <version>${project.version}</version>
</dependency>

Using Drools Planner otherwise
------------------------------

The drools-planner jars are located in the lib directory.
To use drools-planner, you need all the jars except the drools-planner-examples jar.

Reference manual
----------------

Take a look at the online reference manual!
