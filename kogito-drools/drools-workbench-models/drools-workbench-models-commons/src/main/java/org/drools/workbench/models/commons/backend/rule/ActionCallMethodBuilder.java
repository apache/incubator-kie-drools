package org.drools.workbench.models.commons.backend.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.workbench.models.datamodel.oracle.MethodInfo;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.rule.ActionCallMethod;
import org.drools.workbench.models.datamodel.rule.ActionFieldFunction;
import org.drools.workbench.models.datamodel.rule.RuleModel;

import static org.drools.workbench.models.commons.backend.rule.RuleModelPersistenceHelper.*;

public class ActionCallMethodBuilder {

    private RuleModel model;
    private PackageDataModelOracle dmo;
    private boolean isJavaDialect;
    private Map<String, String> boundParams;
    private String methodName;
    private String variable;
    private String[] parameters;

    public ActionCallMethodBuilder( RuleModel model,
                                    PackageDataModelOracle dmo,
                                    boolean isJavaDialect,
                                    Map<String, String> boundParams ) {
        this.model = model;
        this.dmo = dmo;
        this.isJavaDialect = isJavaDialect;
        this.boundParams = boundParams;
    }

    public ActionCallMethod get( String variable,
                                 String methodName,
                                 String[] parameters ) {

        this.variable = variable;
        this.methodName = methodName;
        this.parameters = parameters;

        ActionCallMethod actionCallMethod = new ActionCallMethod();
        actionCallMethod.setMethodName( methodName );
        actionCallMethod.setVariable( variable );
        actionCallMethod.setState( 1 );

        for ( ActionFieldFunction parameter : getActionFieldFunctions() ) {
            actionCallMethod.addFieldValue( parameter );
        }

        return actionCallMethod;
    }

    private List<ActionFieldFunction> getActionFieldFunctions() {

        List<ActionFieldFunction> actionFieldFunctions = new ArrayList<ActionFieldFunction>();

        int index = 0;
        for ( String param : parameters ) {
            param = param.trim();
            if ( param.length() == 0 ) {
                continue;
            }

            actionFieldFunctions.add( getActionFieldFunction( param,
                                                              getDataType( param,
                                                                           index ) ) );
        }
        return actionFieldFunctions;
    }

    private String getAdjustedParameter( String param,
                                         String dataType ) {
        String adjustedParam;
        if ( boundParams.containsKey( param ) ) {
            adjustedParam = param;
        } else {
            adjustedParam = adjustParam( dataType,
                                         param,
                                         boundParams,
                                         isJavaDialect );
        }
        return adjustedParam;
    }

    private ActionFieldFunction getActionFieldFunction( String param,
                                                        String dataType ) {
        ActionFieldFunction actionFiled = new ActionFieldFunction( null,
                                                                   getAdjustedParameter( param,
                                                                                         dataType ),
                                                                   dataType );
        actionFiled.setNature( inferFieldNature( boundParams,
                                                 dataType,
                                                 param ) );
        actionFiled.setField( methodName );
        return actionFiled;
    }

    private String getDataType( String param,
                                int index ) {
        String dataType;

        MethodInfo methodInfo = getMethodInfo();

        if ( methodInfo != null ) {
            dataType = methodInfo.getParams().get( index++ );
        } else {
            dataType = boundParams.get( param );
        }
        if ( dataType == null ) {
            dataType = inferDataType( param,
                                      boundParams,
                                      isJavaDialect );
        }
        return dataType;
    }

    private MethodInfo getMethodInfo() {
        String variableType = boundParams.get( variable );
        if ( variableType != null ) {
            List<MethodInfo> methods = getMethodInfosForType( model,
                                                              dmo,
                                                              variableType );
            if ( methods != null ) {

                ArrayList<MethodInfo> methodInfos = getMethodInfos( methodName, methods );

                if ( methodInfos.size() > 1 ) {
                    // Now if there were more than one method with the same name
                    // we need to start figuring out what is the correct one.
                    for ( MethodInfo methodInfo : methodInfos ) {
                        if ( compareParameters( methodInfo.getParams() ) ) {
                            return methodInfo;
                        }
                    }
                } else if ( !methodInfos.isEmpty() ) {
                    // Not perfect, but works on most cases.
                    // There is no check if the parameter types match.
                    return methodInfos.get( 0 );
                }
            }
        }

        return null;
    }

    private ArrayList<MethodInfo> getMethodInfos( String methodName,
                                                  List<MethodInfo> methods ) {
        ArrayList<MethodInfo> result = new ArrayList<MethodInfo>();
        for ( MethodInfo method : methods ) {
            if ( method.getName().equals( methodName ) ) {
                result.add( method );
            }
        }
        return result;
    }

    private boolean compareParameters( List<String> methodParams ) {
        if ( methodParams.size() != parameters.length ) {
            return false;
        } else {
            for ( int index = 0; index < methodParams.size(); index++ ) {
                if ( !methodParams.get( index ).equals( boundParams.get( parameters[ index ] ) ) ) {
                    return false;
                }
            }
            return true;
        }
    }

}
