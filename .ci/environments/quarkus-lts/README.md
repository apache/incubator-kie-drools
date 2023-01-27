# How to create a patch from a revert commit ?

**NOTE:** Execute those commands at the root of your project

First, set the variables to be used (change the values):

``` bash
commit_id={COMMIT_HASH}
patch_name={ANY_MEANINGFUL_NAME}
```

Finally, create the patch file:

``` bash
git revert --no-commit ${commit_id}
git commit -m "Revert ${patch_name}"
git show $(git rev-parse HEAD) > .ci/environments/quarkus-lts/patches/${patch_name}
git reset HEAD~1 --hard
```
