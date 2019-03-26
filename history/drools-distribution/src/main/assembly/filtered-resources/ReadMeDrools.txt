Welcome to Drools
=================

Third party jars
----------------

Following third party jars are optional and are not included in Drools distribution:

1.Quartz:  Quartz is an optional jar only needed if you wish to adapt existing quartz calendars. It can be 
downloaded from http://www.quartz-scheduler.org/. 


Run the examples
----------------

It's easy to run the examples:
On Linux, Mac or Cygwin:
  examples/runExamples.sh
On Windows:
  examples\runExamples.bat


Run the examples in Eclipse
---------------------------

Open Eclipse, install the Drools plugin, as described in the introduction reference manual.
Open menu Window, menu item Preferences, tree item Drools, tree item Installed Drools Runtime environments
Add the dir "binaries/" from the zip as a new Drools Runtime Environment.
Activate the checkbox of that newly created runtime environment.
Restart eclipse.

Open menu File, menu item Import..., tree item General, tree item Existing Projects into Workspace, button Next
and select root directory "examples/sources/" from the zip, button Finish.
Alternatively, if you have the m2eclipse plugin installed:
Open menu File, menu item Import, tree item Maven, tree item Existing Maven Projects, button Next
and select "examples/sources/" from the zip, button Finish.

Open menu Run, menu item Run configurations, add new Java Application
with main class "org.drools.examples.DroolsExamplesApp"
and VM arguments "-Xms256m -Xmx512m -server", click button Run.


Run the examples in IntelliJ
----------------------------

Open menu File, menu item Open project, select "examples/sources/pom.xml".

Open menu Run, menu item Edit Configurations, add a new Application
with main class "org.drools.examples.DroolsExamplesApp"
and VM parameters "-Xms256m -Xmx512m -server" and run that.


Read the reference manual
-------------------------

To see the reference_manual, just open:
  reference_manual/html_single/index.html
It contains information how to use it on your project (with Maven, ANT, ...).


Sources
-------

The source jars are in the sources directory.

But to build from sources, pull the sources with git:
  https://github.com/kiegroup
and follow these instructions:
  https://github.com/kiegroup/droolsjbpm-build-bootstrap/blob/master/README.md
