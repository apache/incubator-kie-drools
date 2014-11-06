package org.drools.workbench.models.commons.backend.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.workbench.models.datamodel.oracle.MethodInfo;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.rule.ActionCallMethod;
import org.drools.workbench.models.datamodel.rule.ActionFieldFunction;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
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
    private int index;

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
        actionCallMethod.setState( ActionCallMethod.TYPE_DEFINED );

        for ( ActionFieldFunction parameter : getActionFieldFunctions() ) {
            actionCallMethod.addFieldValue( parameter );
        }

        return actionCallMethod;
    }

    private List<ActionFieldFunction> getActionFieldFunctions() {

        List<ActionFieldFunction> actionFieldFunctions = new ArrayList<ActionFieldFunction>();

        this.index = 0;
        for ( String param : parameters ) {
            param = param.trim();
            if ( param.length() == 0 ) {
                continue;
            }

            actionFieldFunctions.add( getActionFieldFunction( param,
                                                              getDataType( param ) ) );
        }
        return actionFieldFunctions;
    }

    private ActionFieldFunction getActionFieldFunction( String param,
                                                        String dataType ) {
        final int fieldNature = inferFieldNature( dataType,
                                                  param,
                                                  boundParams,
                                                  isJavaDialect );

        //If the field is a formula don't adjust the param value
        String paramValue = param;
        switch ( fieldNature ) {
            case FieldNatureType.TYPE_FORMULA:
                break;
            case FieldNatureType.TYPE_VARIABLE:
                break;
            default:
                paramValue = adjustParam( dataType,
                                          param,
                                          boundParams,
                                          isJavaDialect );
        }
        ActionFieldFunction actionField = new ActionFieldFunction( methodName,
                                                                   paramValue,
                                                                   dataType );
        actionField.setNature( fieldNature );
        return actionField;
    }

    private String getDataType( String param ) {
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
