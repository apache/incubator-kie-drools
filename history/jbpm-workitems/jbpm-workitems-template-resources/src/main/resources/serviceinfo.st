$widInfo:{k|
$openbracket$
        "name" : "$widInfo.(k).name$",
        "displayName" : "$widInfo.(k).displayName$",
        "defaultHandler" : "$widInfo.(k).defaultHandler$",
        "documentation" : "$widInfo.(k).documentation$",
        "module" : "$widInfo.(k).category$",
        "icon" : "$widInfo.(k).icon$",
        $if(widInfo.(k).serviceInfo)$
            "category" : "$widInfo.(k).serviceInfo.category$",
            "description" : "$widInfo.(k).serviceInfo.description$",
            "keywords" : [
                $widInfo.(k).serviceInfo.keywords:{k1|
                "$k1$"
                }; separator=","$
            ],
            $if(widInfo.(k).serviceInfo.hasAction)$
                "isaction" : "true",
                "actiontitle" : "$widInfo.(k).serviceInfo.action.title$",
            $endif$
            $if(widInfo.(k).serviceInfo.hasTrigger)$
                "istrigger" : "true",
                "triggertitle" : "$widInfo.(k).serviceInfo.trigger.title$",
            $endif$
            "requiresauth" : "$widInfo.(k).serviceInfo.authInfo.required$",
            $if(widInfo.(k).serviceInfo.authInfo.required)$
                "authparams" : [
                    $widInfo.(k).serviceInfo.authInfo.paramsDescription:{k1|
                       $openbracket$
                           "name" : "$k1$"
                       $closebracket$
                    }; separator=","$
                ],
                "authreferencesite" : "$widInfo.(k).serviceInfo.authInfo.referencesite$",
            $endif$
        $endif$
        $if(widInfo.(k).parameters)$
            "parameters" : [
                $widInfo.(k).parameters:{k1|
                   $openbracket$
                       "name" : "$k1$",
                       "type" : "$widInfo.(k).parameters.(k1).type$"
                   $closebracket$
                }; separator=","$
            ],
        $endif$
        $if(widInfo.(k).results)$
            "results" : [
                $widInfo.(k).results:{k1|
                   $openbracket$
                       "name" : "$k1$",
                       "type" : "$widInfo.(k).results.(k1).type$"
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
            ]
        $endif$
$closebracket$
}; separator=","$
,
