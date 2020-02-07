Welcome to OptaPlanner
======================

Run the examples
----------------

It's easy to run the examples:
On Linux, Mac or Cygwin:
  examples/runExamples.sh
On Windows:
  examples\runExamples.bat


Run the examples in IntelliJ
----------------------------

Open menu File, menu item Open project, select "examples/sources/pom.xml".

Open menu Run, menu item Edit Configurations, add a new Application
with main class "org.optaplanner.examples.app.OptaPlannerExamplesApp"
and run that.


Run the examples in Eclipse
---------------------------

Import "examples/sources/pom.xml" as a new project from Maven sources.

Open menu Run, menu item Run configurations, add new Java Application
with main class "org.optaplanner.examples.app.OptaPlannerExamplesApp"
and click button Run.


Read the reference manual
-------------------------

To see the reference_manual, just open:
  reference_manual/html_single/index.html
It contains information how to use it on your project (with Maven, Gradle, ...).


Sources
-------

The source jars are in the sources directory.

But to build from sources, pull the sources with git:
  https://github.com/kiegroup/optaplanner
and follow these instructions:
  https://github.com/kiegroup/droolsjbpm-build-bootstrap/blob/master/README.md


Backwards compatibility
-----------------------

OptaPlanner's api packages are backwards compatible.
The impl packages are not, apply the upgrade recipe if you use them:
  https://www.optaplanner.org/download/upgradeRecipe/
For more specific information, see the first chapter of the reference manual.


Questions?
----------

If you have any questions, visit:
  https://www.optaplanner.org/community/getHelp.html
