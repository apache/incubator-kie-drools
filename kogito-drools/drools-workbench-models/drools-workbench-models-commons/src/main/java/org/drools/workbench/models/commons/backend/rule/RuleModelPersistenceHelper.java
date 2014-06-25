package org.drools.workbench.models.commons.backend.rule;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.drools.core.util.DateUtils;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.MethodInfo;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.rule.ActionFieldList;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.RuleModel;

class RuleModelPersistenceHelper {

    static String unwrapParenthesis( final String s ) {
        int start = s.indexOf( '(' );
        int end = s.lastIndexOf( ')' );
        return s.substring( start + 1,
                            end ).trim();
    }

    static String getSimpleFactType( final String className,
                                     final PackageDataModelOracle dmo ) {
        for ( String type : dmo.getProjectModelFields().keySet() ) {
            if ( type.equals( className ) ) {
                return type.substring( type.lastIndexOf( "." ) + 1 );
            }
        }
        return className;
    }

    static int inferFieldNature( final String dataType,
                                 final String value,
                                 final Map<String, String> boundParams,
                                 final boolean isJavaDialect ) {

        if ( boundParams.containsKey( value ) ) {
            return FieldNatureType.TYPE_VARIABLE;
        }

        return inferFieldNature( dataType,
                                 value,
                                 isJavaDialect );
    }

    static int inferFieldNature( final String dataType,
                                 final String value,
                                 final boolean isJavaDialect ) {
        int nature = ( StringUtils.isEmpty( value ) ? FieldNatureType.TYPE_UNDEFINED : FieldNatureType.TYPE_LITERAL );

        if ( dataType == DataType.TYPE_COLLECTION ) {
            return FieldNatureType.TYPE_FORMULA;

        } else if ( DataType.TYPE_BOOLEAN.equals( dataType ) ) {
            if ( !( Boolean.TRUE.equals( Boolean.parseBoolean( value ) ) || Boolean.FALSE.equals( Boolean.parseBoolean( value ) ) ) ) {
                return FieldNatureType.TYPE_FORMULA;
            } else {
                return FieldNatureType.TYPE_LITERAL;
            }

        } else if ( DataType.TYPE_DATE.equals( dataType ) ) {
            try {
                new SimpleDateFormat( DateUtils.getDateFormatMask() ).parse( adjustParam( dataType,
                                                                                          value,
                                                                                          Collections.EMPTY_MAP,
                                                                                          isJavaDialect ) );
                return FieldNatureType.TYPE_LITERAL;
            } catch ( ParseException e ) {
                return FieldNatureType.TYPE_FORMULA;
            }

        } else if ( DataType.TYPE_STRING.equals( dataType ) ) {
            if ( value.startsWith( "\"" ) ) {
                return FieldNatureType.TYPE_LITERAL;
            } else {
                return FieldNatureType.TYPE_FORMULA;
            }

        } else if ( DataType.TYPE_NUMERIC.equals( dataType ) ) {
            if ( !NumberUtils.isNumber( value ) ) {
                return FieldNatureType.TYPE_FORMULA;
            }
            return FieldNatureType.TYPE_LITERAL;

        } else if ( DataType.TYPE_NUMERIC_BIGDECIMAL.equals( dataType ) ) {
            try {
                new BigDecimal( adjustParam( dataType,
                                             value,
                                             Collections.EMPTY_MAP,
                                             isJavaDialect ) );
                return FieldNatureType.TYPE_LITERAL;
            } catch ( NumberFormatException e ) {
                return FieldNatureType.TYPE_FORMULA;
            }

        } else if ( DataType.TYPE_NUMERIC_BIGINTEGER.equals( dataType ) ) {
            try {
                new BigInteger( adjustParam( dataType,
                                             value,
                                             Collections.EMPTY_MAP,
                                             isJavaDialect ) );
                return FieldNatureType.TYPE_LITERAL;
            } catch ( NumberFormatException e ) {
                return FieldNatureType.TYPE_FORMULA;
            }

        } else if ( DataType.TYPE_NUMERIC_BYTE.equals( dataType ) ) {
            try {
                new Byte( value );
                return FieldNatureType.TYPE_LITERAL;
            } catch ( NumberFormatException e ) {
                return FieldNatureType.TYPE_FORMULA;
            }

        } else if ( DataType.TYPE_NUMERIC_DOUBLE.equals( dataType ) ) {
            try {
                new Double( value );
                return FieldNatureType.TYPE_LITERAL;
            } catch ( NumberFormatException e ) {
                return FieldNatureType.TYPE_FORMULA;
            }

        } else if ( DataType.TYPE_NUMERIC_FLOAT.equals( dataType ) ) {
            try {
                new Float( value );
                return FieldNatureType.TYPE_LITERAL;
            } catch ( NumberFormatException e ) {
                return FieldNatureType.TYPE_FORMULA;
            }

        } else if ( DataType.TYPE_NUMERIC_INTEGER.equals( dataType ) ) {
            try {
                new Integer( value );
                return FieldNatureType.TYPE_LITERAL;
            } catch ( NumberFormatException e ) {
                return FieldNatureType.TYPE_FORMULA;
            }

        } else if ( DataType.TYPE_NUMERIC_LONG.equals( dataType ) ) {
            try {
                new Long( value );
                return FieldNatureType.TYPE_LITERAL;
            } catch ( NumberFormatException e ) {
                return FieldNatureType.TYPE_FORMULA;
            }

        } else if ( DataType.TYPE_NUMERIC_SHORT.equals( dataType ) ) {
            try {
                new Short( value );
                return FieldNatureType.TYPE_LITERAL;
            } catch ( NumberFormatException e ) {
                return FieldNatureType.TYPE_FORMULA;
            }

        }

        return nature;
    }

    static ModelField[] findFields( final RuleModel m,
                                    final PackageDataModelOracle dmo,
                                    final String type ) {
        ModelField[] fields = dmo.getProjectModelFields().get( type );
        if ( fields != null ) {
            return fields;
        }
        for ( String i : m.getImports().getImportStrings() ) {
            if ( i.endsWith( "." + type ) ) {
                fields = dmo.getProjectModelFields().get( i );
                if ( fields != null ) {
                    return fields;
                }
            }
        }

        return dmo.getProjectModelFields().get( m.getPackageName() + "." + type );
    }

    static ModelField findField( final ModelField[] typeFields,
                                 final String fieldName ) {
        if ( typeFields != null && fieldName != null ) {
            for ( ModelField typeField : typeFields ) {
                if ( typeField.getName().equals( fieldName ) ) {
                    return typeField;
                }
            }
        }
        return null;
    }

    static MethodInfo findMethodInfo( final List<MethodInfo> methodInfos,
                                      final String fieldName ) {
        if ( methodInfos != null && fieldName != null ) {
            for ( MethodInfo methodInfo : methodInfos ) {
                if ( methodInfo.getName().equals( fieldName ) ) {
                    return methodInfo;
                }
            }
        }
        return null;

    }

    static String inferDataType( final ActionFieldList action,
                                 final String field,
                                 final Map<String, String> boundParams,
                                 final PackageDataModelOracle dmo,
                                 final Imports imports ) {
        String factType = null;
        if ( action instanceof ActionInsertFact ) {
            factType = ( (ActionInsertFact) action ).getFactType();

        } else if ( action instanceof ActionSetField ) {
            String boundParam = ( (ActionSetField) action ).getVariable();
            factType = boundParams.get( boundParam );
        }
        if ( factType == null ) {
            return null;
        }
        //Lookup without package prefix or imports
        ModelField[] modelFields = dmo.getProjectModelFields().get( factType );

        //Lookup with package prefix
        if ( modelFields == null ) {
            String fqcn = dmo.getPackageName() + "." + factType;
            modelFields = dmo.getProjectModelFields().get( fqcn );
        }

        //Lookup from imports
        if ( modelFields == null ) {
            for ( Import item : imports.getImports() ) {
                if ( item.getType().endsWith( factType ) ) {
                    modelFields = dmo.getProjectModelFields().get( item.getType() );
                    if ( modelFields != null ) {
                        break;
                    }
                }
            }
        }

        if ( modelFields == null ) {
            return null;
        }
        for ( ModelField modelField : modelFields ) {
            if ( modelField.getName().equals( field ) ) {
                return modelField.getType();
            }
        }
        return null;
    }

    static String inferDataType( final String param,
                                 final Map<String, String> boundParams,
                                 final boolean isJavaDialect ) {
        if ( param.startsWith( "sdf.parse(\"" ) ) {
            return DataType.TYPE_DATE;
        } else if ( param.startsWith( "\"" ) ) {
            return DataType.TYPE_STRING;
        } else if ( param.equals( "true" ) || param.equals( "false" ) ) {
            return DataType.TYPE_BOOLEAN;
        } else if ( param.endsWith( "B" ) || ( isJavaDialect && param.startsWith( "new java.math.BigDecimal" ) ) ) {
            return DataType.TYPE_NUMERIC_BIGDECIMAL;
        } else if ( param.endsWith( "I" ) || ( isJavaDialect && param.startsWith( "new java.math.BigInteger" ) ) ) {
            return DataType.TYPE_NUMERIC_BIGINTEGER;
        } else if ( param.startsWith( "[" ) && param.endsWith( "]" ) ) {
            return DataType.TYPE_COLLECTION;
        } else if ( boundParams.containsKey( param ) ) {
            return DataType.TYPE_OBJECT;
        }
        return DataType.TYPE_NUMERIC;
    }

    static String adjustParam( final String dataType,
                               final String param,
                               final Map<String, String> boundParams,
                               final boolean isJavaDialect ) {
        if ( dataType == DataType.TYPE_DATE ) {
            return param.substring( "sdf.parse(\"".length(),
                                    param.length() - 2 );
        } else if ( dataType == DataType.TYPE_STRING ) {
            return param.substring( 1,
                                    param.length() - 1 );
        } else if ( dataType == DataType.TYPE_NUMERIC_BIGDECIMAL ) {
            if ( isJavaDialect ) {
                return param.substring( "new java.math.BigDecimal(\"".length(),
                                        param.length() - 2 );
            } else {
                return param.substring( 0, param.length() - 1 );
            }
        } else if ( dataType == DataType.TYPE_NUMERIC_BIGINTEGER ) {
            if ( isJavaDialect ) {
                return param.substring( "new java.math.BigInteger(\"".length(),
                                        param.length() - 2 );
            } else {
                return param.substring( 0,
                                        param.length() - 1 );
            }
        } else if ( boundParams.containsKey( param ) ) {
            return "=" + param;
        }
        return param;
    }

    static List<MethodInfo> getMethodInfosForType( final RuleModel model,
                                                   final PackageDataModelOracle dmo,
                                                   final String variableType ) {
        List<MethodInfo> methods = dmo.getProjectMethodInformation().get( variableType );
        if ( methods == null ) {
            for ( String imp : model.getImports().getImportStrings() ) {
                if ( imp.endsWith( "." + variableType ) ) {
                    methods = dmo.getProjectMethodInformation().get( imp );
                    if ( methods != null ) {
                        break;
                    }
                }
            }
        }
        return methods;
    }

}
