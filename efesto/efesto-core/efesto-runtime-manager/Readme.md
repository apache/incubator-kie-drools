Runtime Manager
===================

The code in these modules will be responsible for actual execution of models; most of the time this is represented by
class-loading and method invocation, but exceptions should be considered as well.

The code in `runtime-manager-api` should be the only one visible outside the `core` of the system, while the code
inside `runtime-manager-common` should be considered **private** and hidden from outside.