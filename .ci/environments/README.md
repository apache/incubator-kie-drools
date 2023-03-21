# Environments CI scripts

This folder contains update scripts which would be called for a specific environments.


Updating branch.
================

The `.ci/environments/update.sh` script is responsible to execute the update/migration.

It expect at least one parameter, namely the "environment" and optionally a second one, when the rewrite execution is required.

The first parameter defines which folder will be executed (e.g. *quarkus-3*, *quarkus-main*, etc).
The second parameter, if provided and equals to *rewrite*, fires the **rewrite** execution.

The process of define the migration changes is split in two parts

1. rewrite execution: this is done manually, since rewrite execution take a lot of time; after successfully execution, the changes must be saved as *patches*
2. patches apply: this is done automatically, and all *patches* found in the `${environment}/patches` are applied
3. if executed, rewrite runs ***BEFORE*** the patch apply

To execute migration in the *quarkus-3* ***WITHOUT*** rewrite

`.ci/environments/update.sh quarkus-3`

To execute migration in the *quarkus-3* ***WITH*** rewrite

`.ci/environments/update.sh quarkus-3 rewrite`

