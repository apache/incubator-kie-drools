<html>
    <head>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    </head>
    <body>
    <div class="container">
    $widInfo:{k|
    <div class="well">
    <h2>$i$. Workitem Info - $widInfo.(k).name$</h2>
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
            <td>$widInfo.(k).defaultHandler$</td>
        </tr>
        </tbody>
    </table>
    <br/>
    $if(widInfo.(k).parameters)$
    <h2>Parameters</h2>
    <table class="table table-hover">
        <thead>
        <tr>
            <th>Name</th>
            <th>Type</th>
        </tr>
        </thead>
        <tbody>
        $widInfo.(k).parameters:{k1|
        <tr>
            <td>$k1$</td>
            <td>$widInfo.(k).parameters.(k1).type$</td>
        </tr>
        }$
        </tbody>
    </table>
    <br/>
    $endif$
    $if(widInfo.(k).parameterValues)$
    <h2>Parameter Values</h2>
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
    <h2>Results</h2>
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
    <h2>Maven Dependencies</h2>
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
    </div>
    }$
    </div>
    </body>
</html>