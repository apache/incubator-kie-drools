<html>
    <head>
    <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css" integrity="sha384-9gVQ4dYFwwWSjIDZnLEWnxCjeSWFphJiwGPXr1jddIhOegiu1FwO5qRGvFXOdJZ4" crossorigin="anonymous">
        <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.0/umd/popper.min.js"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.0/js/bootstrap.min.js"></script>
    </head>
    <body>
    <div class="container">
    $widInfo:{k|

    <a name="$widInfo.(k).name$"></a><div class="card">
            <div class="card-header"><b>$i$. $widInfo.(k).name$ Workitem Handler</b></div>
            <div class="card-body">
    <table class="table table-hover">
        <thead>
        <tr>
            <th>Name</th>
            <th>Display Name</th>
            <th>Category</th>
            <th>Description</th>
            <th>Handler</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>$widInfo.(k).name$</td>
            <td>$widInfo.(k).displayName$</td>
            <td>$widInfo.(k).category$</td>
            <td>$widInfo.(k).description$</td>
            <td>$widInfo.(k).defaultHandlerUnEscaped$</td>
        </tr>
        </tbody>
    </table>
    <br/>
    $if(widInfo.(k).parameters)$
    <b>Parameters</b>
    <table class="table table-hover">
        <thead>
        <tr>
            <th>Name</th>
            <th>Type</th>
            <th>Required</th>
        </tr>
        </thead>
        <tbody>
        $widInfo.(k).parameters:{k1|
        <tr>
            <td>$k1$</td>
            <td>$widInfo.(k).parameters.(k1).type$</td>
            <td>$widInfo.(k).parameters.(k1).required$</td>
        </tr>
        }$
        </tbody>
    </table>
    <br/>
    $endif$
    $if(widInfo.(k).parameterValues)$
    <b>Parameter Values</b>
    <table class="table table-hover">
        <thead>
        <tr>
            <th>Parameter Name</th>
            <th>Values</th>
        </tr>
        </thead>
        <tbody>
        $widInfo.(k).parameterValues:{k1|
        <tr>
            <td>$k1$</td>
            <td>$widInfo.(k).parameterValues.(k1).values$</td>
        </tr>
        }$
        </tbody>
    </table>
    <br/>
    $endif$
    $if(widInfo.(k).results)$
    <b>Results</b>
    <table class="table table-hover">
        <thead>
        <tr>
            <th>Name</th>
            <th>Type</th>
        </tr>
        </thead>
        <tbody>
        $widInfo.(k).results:{k1|
        <tr>
            <td>$k1$</td>
            <td>$widInfo.(k).results.(k1).type$</td>
        </tr>
        }$
        </tbody>
    </table>
    <br/>
    $endif$
    $if(widInfo.(k).mavenDepends)$
    <b>Maven Dependencies</b>
    <table class="table table-hover">
        <thead>
        <tr>
            <th>Group</th>
            <th>Artifact</th>
            <th>Version</th>
        </tr>
        </thead>
        <tbody>
        $widInfo.(k).mavenDepends:{k1|
        <tr>
            <td>$widInfo.(k).mavenDepends.(k1).group$</td>
            <td>$widInfo.(k).mavenDepends.(k1).artifact$</td>
            <td>$widInfo.(k).mavenDepends.(k1).version$</td>
        </tr>
        }$
        </tbody>
    </table>
    <br/>
    $endif$
    $if(widInfo.(k).serviceInfo.authInfo)$
        <b>Authentication Information</b>
        <table class="table table-hover">
            <thead>
            <tr>
                <th>Auth Required</th>
                <th>Params</th>
                <th>Params Description</th>
                <th>Reference Site</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td>$widInfo.(k).serviceInfo.authInfo.required$</td>
                <td>
                $widInfo.(k).serviceInfo.authInfo.params:{k1|
                    $k1$<br/>
                }$
                </td>
                <td>
                $widInfo.(k).serviceInfo.authInfo.paramsDescription:{k1|
                    $k1$<br>
                }$
                </td>
                <td>$widInfo.(k).serviceInfo.authInfo.referencesite$</td>
             </tr>
             </tbody>
        </table>
        <br/>
        $endif$
    </div></div><p/>
    }$
    </div>
    </body>
</html>