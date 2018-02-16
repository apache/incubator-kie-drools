[
    $widInfo:{k|
    [
        "name" : "$widInfo.(k).name$",
        "displayName" : "$widInfo.(k).displayName$",
        "category" : "$widInfo.(k).category$",
        "description" : "$widInfo.(k).description$",
        "defaultHandler" : "$widInfo.(k).defaultHandler$",

        $if(widInfo.(k).parameters)$
        "parameters" : [
            $widInfo.(k).parameters:{k1|
                "$k1$" : new $widInfo.(k).parameters.(k1).type$()
            }; separator=","$
        ],
        $endif$
        $if(widInfo.(k).parameterValues)$
        "parameterValues" : [
            $widInfo.(k).parameterValues:{k1|
                "$k1$" : "$widInfo.(k).parameterValues.(k1).values$"
            }; separator=","$
        ],
        $endif$
        $if(widInfo.(k).results)$
        "results" : [
            $widInfo.(k).results:{k1|
                "$k1$" : new $widInfo.(k).results.(k1).type$()
            }; separator=","$
        ],
        $endif$
        $if(widInfo.(k).mavenDepends)$
        "mavenDependencies" : [
            $widInfo.(k).mavenDepends:{k1|
                 "$widInfo.(k).mavenDepends.(k1).group$:$widInfo.(k).mavenDepends.(k1).artifact$:$widInfo.(k).mavenDepends.(k1).version$"
            }; separator=","$
        ],
        $endif$
        "icon" : "$widInfo.(k).icon$"

    ]
    }; separator=","$
]