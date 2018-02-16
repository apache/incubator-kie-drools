[
    $widInfo:{k|
    $openbracket$
        "name" : "$widInfo.(k).name$",
        "displayName" : "$widInfo.(k).displayName$",
        "category" : "$widInfo.(k).category$",
        "description" : "$widInfo.(k).description$",
        "defaultHandler" : "$widInfo.(k).defaultHandler$",
        $if(widInfo.(k).parameters)$
        "parameters" : [
            $widInfo.(k).parameters:{k1|
               $openbracket$
                   "name" : "$k1$",
                   "type" : "new $widInfo.(k).parameters.(k1).type$()"
               $closebracket$
            }; separator=","$
        ],
        $endif$
        $if(widInfo.(k).parameterValues)$
        "parameterValues" : [
            $widInfo.(k).parameterValues:{k1|
               $openbracket$
                   "name" : "$k1$",
                   "type" : "$widInfo.(k).parameterValues.(k1).values$"
               $closebracket$
            }; separator=","$
        ],
        $endif$
        $if(widInfo.(k).results)$
        "results" : [
            $widInfo.(k).results:{k1|
               $openbracket$
                   "name" : "$k1$",
                   "type" : "new $widInfo.(k).results.(k1).type$()"
               $closebracket$
            }; separator=","$
        ],
        $endif$
        $if(widInfo.(k).mavenDepends)$
        "mavenDependencies" : [
            $widInfo.(k).mavenDepends:{k1|
               $openbracket$
                   "groupId" : "$widInfo.(k).mavenDepends.(k1).group$",
                   "artifactId" : "$widInfo.(k).mavenDepends.(k1).artifact$",
                   "version" : "$widInfo.(k).mavenDepends.(k1).version$"
               $closebracket$
            }; separator=","$
        ],
        $endif$
        "icon" : "$widInfo.(k).icon$"
    $closebracket$
    }; separator=","$
]