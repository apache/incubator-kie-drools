Engines
=======

The code in these modules will be the actual implementation of the different engines. For each model/engine there will
be a specific module; in turns, for each engine-module there should be a `compilation` and a `runtime` submodule.
`compilation` submodules should depend only on `compilation-manager-api`, while `runtime` submodules should depend on
both `compilation-manager-api` and `runtime-manager-api`
***(TO-BE-VERIFIED)***
Beside that, every engine should implement a `test` module for integration tests.