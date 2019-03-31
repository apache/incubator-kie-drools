Design guidelines Drools and jBPM
=================================

The document documents the code design guidelines of Drools and jBPM.
Code style, tool configuration, ... are all documented in the [README](README.md).

Artifact design
---------------

* Each jar has a unique package namespace to avoid split packages.

    * For example: everything under `drools-core` is in `org.drools.core`

Experimental features
---------------------

* Experimental features should not be in knowledge-api.jar

* Experimental features should be documented in a separate chapter *Experimental*, at the end of the reference manual.
