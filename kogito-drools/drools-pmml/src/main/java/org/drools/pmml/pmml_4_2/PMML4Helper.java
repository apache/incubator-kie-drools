/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.pmml.pmml_4_2;

import org.dmg.pmml.pmml_4_2.descr.AnyDistribution;
import org.dmg.pmml.pmml_4_2.descr.COMPAREFUNCTION;
import org.dmg.pmml.pmml_4_2.descr.DATATYPE;
import org.dmg.pmml.pmml_4_2.descr.DataField;
import org.dmg.pmml.pmml_4_2.descr.GaussianDistribution;
import org.dmg.pmml.pmml_4_2.descr.PoissonDistribution;
import org.dmg.pmml.pmml_4_2.descr.REGRESSIONNORMALIZATIONMETHOD;
import org.dmg.pmml.pmml_4_2.descr.RESULTFEATURE;
import org.dmg.pmml.pmml_4_2.descr.UniformDistribution;
import org.dmg.pmml.pmml_4_2.descr.Value;
import org.drools.pmml.pmml_4_2.extensions.AggregationStrategy;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;


public class PMML4Helper {


    private static final String innerFieldPrefix = "__$Inner";

    private static int counter = 0;

    private Set<String> definedModelBeans;
    private ClassLoader resolver;

    public int nextCount() {
        return counter++;
    }


    public String nextInnerFieldName() {
        return innerFieldPrefix + nextCount();
    }

    public boolean isInnerFieldName( String name ) {
        return name != null && name.startsWith( innerFieldPrefix );
    }

    private String context = null;
    private String pack;


    public PMML4Helper() {
        definedModelBeans = new HashSet<String>();

    }


    public static String pmmlDefaultPackageName() {
        return PMML4Compiler.PMML_DROOLS;
    }

    public String getPmmlPackageName() {
        return PMML4Helper.pmmlDefaultPackageName();
    }

    public String getContext() {
        return context;
    }

    public void setContext( String context ) {
        this.context = context;
    }

    public void addModelBeanDefinition(String beanType) {
        this.definedModelBeans.add(beanType);
    }

    public ClassLoader getResolver() {
        return resolver;
    }

    public void setResolver( ClassLoader resolver ) {
        this.resolver = resolver;
    }

    public boolean isModelBeanDefined(String beanType) {
//        boolean flag =  definedModelBeans.contains(beanType);
//        if (flag) {
//            try {
//                Class.forName(pack+"."+beanType);
//                return true;
//            } catch (ClassNotFoundException cnfe) {
//                definedModelBeans.remove(beanType);
//                return false;
//            }
//        } else {
//            return false;
//        }
        boolean flag =  definedModelBeans.contains(beanType);
        if (resolver == null) {
            try {
                Class.forName(pack+"."+beanType);
                return true;
            } catch (ClassNotFoundException cnfe) {
                definedModelBeans.remove(beanType);
                return false;
            }
        } else if (flag) {
            try {
                Class.forName( pack + "." + beanType, false, resolver );
                return true;
            } catch (ClassNotFoundException cnfe) {
                definedModelBeans.remove(beanType);
                return false;
            }
        } else {
            return false;
        }
    }


    public void applyTemplate(String templateName, Object context, TemplateRegistry registry, Map vars, StringBuilder builder) {
        CompiledTemplate template = registry.getNamedTemplate(templateName);
        String result = (String) TemplateRuntime.execute( template, context, vars );
        builder.append( result );
    }


    public String getPack() {
        return pack;
    }
    public void setPack(String packageName) {
        pack = packageName;
    }

    /**
     * Returns a copy of the object, or null if the object cannot
     * be serialized.
     */
    public Object copy(Object orig) {
        Object obj = null;
        try {
            // Write the object out to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(orig);
            out.flush();
            out.close();

            // Make an input stream from the byte array and read
            // a copy of the object back in.
            ObjectInputStream in = new ObjectInputStream(
                    new ByteArrayInputStream(bos.toByteArray()));
            obj = in.readObject();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return obj;
    }


    public String box(String s) {
        if ("int".equalsIgnoreCase(s))
            return Integer.class.getName();
        else if ("float".equalsIgnoreCase(s))
            return Float.class.getName();
        else if ("double".equalsIgnoreCase(s))
            return Double.class.getName();
        else if ("boolean".equalsIgnoreCase(s))
            return Boolean.class.getName();
        else if ("short".equalsIgnoreCase(s))
            return Short.class.getName();
        else if ("long".equalsIgnoreCase(s))
            return Long.class.getName();
        else
            return s;
    }


    public String streamInputType(String s) {
        if ("int".equalsIgnoreCase(s))
            return Number.class.getName();
        else if ("float".equalsIgnoreCase(s))
            return Number.class.getName();
        else if ("double".equalsIgnoreCase(s))
            return Number.class.getName();
        else if ("boolean".equalsIgnoreCase(s))
            return Boolean.class.getName();
        else if ("short".equalsIgnoreCase(s))
            return Number.class.getName();
        else if ("long".equalsIgnoreCase(s))
            return Number.class.getName();
        else
            return s;
    }


    public String mapDatatype( DATATYPE datatype ) {
        return mapDatatype( datatype, false );
    }


    public String mapDatatype(DATATYPE datatype, boolean box) {
        String s = datatype != null ? datatype.value() : null;
        if (s == null) return Object.class.getName();
        if ("Integer".equalsIgnoreCase(s))
//			return Integer.class.getName();
            return box ? "Integer" : "int";
        else if ("Float".equalsIgnoreCase(s))
//			return Float.class.getName();
            return box ? "Float" : "float";
        else if ("Double".equalsIgnoreCase(s))
//			return Double.class.getName();
            return box ? "Double" : "double";
        else if ("Boolean".equalsIgnoreCase(s))
//          return Boolean.class.getName();
            return box ? "Boolean" : "boolean";
        else if ("String".equalsIgnoreCase(s))
            return String.class.getName();
        else if ("Date".equalsIgnoreCase(s))
            return java.util.Date.class.getName();
        else if ("Time".equalsIgnoreCase(s))
            return java.util.Date.class.getName();
        else if ("DateTime".equalsIgnoreCase(s))
            return java.util.Date.class.getName();
        else if ("DateDaysSince[0]".equalsIgnoreCase(s))
            return Long.class.getName();
        else if ("DateDaysSince[1960]".equalsIgnoreCase(s))
            return Long.class.getName();
        else if ("DateDaysSince[1970]".equalsIgnoreCase(s))
            return Long.class.getName();
        else if ("DateDaysSince[1980]".equalsIgnoreCase(s))
            return Long.class.getName();
        else if ("TimeSeconds".equalsIgnoreCase(s))
            return Long.class.getName();
        else if ("DateTimeSecondsSince[0]".equalsIgnoreCase(s))
            return Long.class.getName();
        else if ("DateTimeSecondsSince[1960]".equalsIgnoreCase(s))
            return Long.class.getName();
        else if ("DateTimeSecondsSince[1970]".equalsIgnoreCase(s))
            return Long.class.getName();
        else if ("DateTimeSecondsSince[1980]".equalsIgnoreCase(s))
            return Long.class.getName();
        else if ("collection".equalsIgnoreCase(s))
            return java.util.Collection.class.getName();
        else {
            return Object.class.getName();
        }
    }









    public String format(DataField fld, Value val) {
        if (val == null) {
            return "null";
        }
        String s = fld.getDataType().value();
        return format(s, val);
    }

    public String format(String type, Value val) {
        if (val == null) {
            return "null";
        }
        return format(type,val.getValue());
    }

    public String format(DataField fld, String val) {
        if (val == null) {
            return "null";
        }
        String s = fld.getDataType().value();
        return format(s, val);
    }


    public String format(DATATYPE type, String val) {
        if (val == null) {
            return "null";
        }
        return format(type != null ? type.value() : null, val);
    }


    public String format(String type, String val) {
        if (type == null)
            return val;
        if (val == null)
            return "null";
        if (Integer.class.getName().equals(type)
                || "Integer".equalsIgnoreCase(type) || "int".equalsIgnoreCase(type) )
            return val;
        else if (Float.class.getName().equals(type)
                || "Float".equalsIgnoreCase(type) || "float".equalsIgnoreCase(type) )
            return val;
        else if (Double.class.getName().equalsIgnoreCase(type)
                || "Double".equalsIgnoreCase(type) || "double".equalsIgnoreCase(type) )
            return val;
        else if (Long.class.getName().equalsIgnoreCase(type)
                || "Long".equalsIgnoreCase(type) || "long".equalsIgnoreCase(type) )
            return val;
        else if (Short.class.getName().equalsIgnoreCase(type)
                || "Short".equalsIgnoreCase(type) || "short".equalsIgnoreCase(type) )
            return val;
        else if (Byte.class.getName().equalsIgnoreCase(type)
                || "Byte".equalsIgnoreCase(type) || "byte".equalsIgnoreCase(type) )
            return val;
        else if (Boolean.class.getName().equalsIgnoreCase(type)
                || "Boolean".equalsIgnoreCase(type) || "boolean".equalsIgnoreCase(type) )
            return val.toLowerCase();
        else if (String.class.getName().equalsIgnoreCase(type)
                || "String".equalsIgnoreCase(type))
            return "\""+val+"\"";
        else if ("Date".equalsIgnoreCase(type))
            return "\""+val+"\"";
        else if ("Time".equalsIgnoreCase(type))
            return "\""+val+"\"";
        else if ("DateTime".equalsIgnoreCase(type))
            return "\""+val+"\"";
        else if ("DateDaysSince[0]".equalsIgnoreCase(type))
            return val;
        else if ("DateDaysSince[1960]".equalsIgnoreCase(type))
            return val;
        else if ("DateDaysSince[1970]".equalsIgnoreCase(type))
            return val;
        else if ("DateDaysSince[1980]".equalsIgnoreCase(type))
            return val;
        else if ("TimeSeconds".equalsIgnoreCase(type))
            return val;
        else if ("DateTimeSecondsSince[0]".equalsIgnoreCase(type))
            return val;
        else if ("DateTimeSecondsSince[1960]".equalsIgnoreCase(type))
            return val;
        else if ("DateTimeSecondsSince[1970]".equalsIgnoreCase(type))
            return val;
        else if ("DateTimeSecondsSince[1980]".equalsIgnoreCase(type))
            return val;
        else
            return val;
    }

    public String format( String type, Number val ) {
        if (type == null) {
            return val.toString();
        } else if ( val == null ) {
            return null;
        } else if ("Integer".endsWith(type) || "int".equalsIgnoreCase(type) ) {
            return ""+val.intValue();
        } else if ("Float".endsWith(type) || "float".equalsIgnoreCase(type)) {
            return ""+val.floatValue();
        } else if ("Double".endsWith(type) || "double".equalsIgnoreCase(type)) {
            return ""+val.doubleValue();
        } else if ("Boolean".endsWith(type)  || "boolean".equalsIgnoreCase(type)) {
            if (val.doubleValue() == 1.0)
                return "true";
            if (val.doubleValue() == 0.0)
                return "false";
            throw new NumberFormatException("Boolean expected, found " + val);
        } else if ("String".endsWith(type)) {
            return "\""+val.toString()+"\"";
        } else if ("Date".endsWith(type)) {
            return "\""+ (new SimpleDateFormat().format(new Date(val.longValue())) )+"\"";
        } else if ("Time".endsWith(type)) {
            return "\""+val.toString()+"\"";
        } else if ("DateTime".endsWith(type)) {
            return "\""+ (new SimpleDateFormat().format(new Date(val.longValue())) )+"\"";
        } else if ("DateDaysSince[0]".equalsIgnoreCase(type)) {
            throw new UnsupportedOperationException("TODO");
        } else if ("DateDaysSince[1960]".equalsIgnoreCase(type)) {
            throw new UnsupportedOperationException("TODO");
        } else if ("DateDaysSince[1970]".equalsIgnoreCase(type)) {
            throw new UnsupportedOperationException("TODO");
        } else if ("DateDaysSince[1980]".equalsIgnoreCase(type)) {
            throw new UnsupportedOperationException("TODO");
        } else if ("TimeSeconds".equalsIgnoreCase(type)) {
            throw new UnsupportedOperationException("TODO");
        } else if ("DateTimeSecondsSince[0]".equalsIgnoreCase(type)) {
            throw new UnsupportedOperationException("TODO");
        } else if ("DateTimeSecondsSince[1960]".equalsIgnoreCase(type)) {
            throw new UnsupportedOperationException("TODO");
        } else if ("DateTimeSecondsSince[1970]".equalsIgnoreCase(type)) {
            throw new UnsupportedOperationException("TODO");
        } else if ("DateTimeSecondsSince[1980]".equalsIgnoreCase(type)) {
            throw new UnsupportedOperationException("TODO");
        } else {
            return val.toString();
        }
    }



    public String zeroForDatatype( DATATYPE type ) {
        return zeroForDatatype( type != null ? type.value() : null );
    }

    public String zeroForDatatype( String type ) {
        if (type == null) {
            return "null";
        } if (Integer.class.getName().equals(type)
              || "Integer".equalsIgnoreCase(type) || "int".equalsIgnoreCase(type) ) {
            return "0";
        } else if (Float.class.getName().equals(type)
                   || "Float".equalsIgnoreCase(type) || "float".equalsIgnoreCase(type) ) {
            return "0.0f";
        } else if (Double.class.getName().equalsIgnoreCase(type)
                   || "Double".equalsIgnoreCase(type) || "double".equalsIgnoreCase(type) ) {
            return "0.0";
        } else if (Long.class.getName().equalsIgnoreCase(type)
                   || "Long".equalsIgnoreCase(type) || "long".equalsIgnoreCase(type) ) {
            return "0L";
        } else if (Short.class.getName().equalsIgnoreCase(type)
                   || "Short".equalsIgnoreCase(type) || "short".equalsIgnoreCase(type) ) {
            return "0";
        } else if (Byte.class.getName().equalsIgnoreCase(type)
                   || "Byte".equalsIgnoreCase(type) || "byte".equalsIgnoreCase(type) ) {
            return "0";
        } else if (Boolean.class.getName().equalsIgnoreCase(type)
                   || "Boolean".equalsIgnoreCase(type) || "boolean".equalsIgnoreCase(type) ) {
            return "false";
        } else if (String.class.getName().equalsIgnoreCase(type)
                   || "String".equalsIgnoreCase(type)) {
            return "null";
        } else
            return "null";
    }


    public String numberFromNumber( String obj, String datatype ) {
        if ( Integer.class.getSimpleName().equalsIgnoreCase( datatype ) || Integer.class.getName().equalsIgnoreCase( datatype ) ) {
            return obj + ".intValue()";
        } else if ( Float.class.getSimpleName().equalsIgnoreCase( datatype ) || Float.class.getName().equalsIgnoreCase( datatype ) ) {
            return obj + ".floatValue()";
        } else if ( Double.class.getSimpleName().equalsIgnoreCase( datatype ) || Double.class.getName().equalsIgnoreCase( datatype ) ) {
            return obj + ".doubleValue()";
        }
        return obj;
    }



    public String mapFunctionAsQuery( String functor, List args ) {
        String[] argz = new String[args.size()];
        for (int j = 0; j < args.size(); j++) {
            argz[j] = args.get(j).toString();
        }
        return mapFunction( functor, true, argz );
    }

    public String mapFunctionAsQuery( String functor, String... args ) {
        return mapFunction( functor, true, args );
    }

    public String mapFunction( String functor, List args ) {
        String[] argz = new String[args.size()];
        for (int j = 0; j < args.size(); j++) {
            argz[j] = args.get(j).toString();
        }
        return mapFunction( functor, false, argz );
    }

    public String mapFunction( String functor, String... args ) {
        return mapFunction( functor, false, args );
    }

    public String mapFunction( String functor, boolean asQuery, String... args ) {
        String ans = "(";
        if ("+".equals(functor)) {
            ans += args[0];
            for (int j = 1; j < args.length; j++) {
                ans += " + " + args[j];
            }
        } else if ("-".equals(functor)) {
            ans += args[0];
            for (int j = 1; j < args.length; j++) {
                ans += " - " + args[j];
            }
        } else if ("*".equals(functor)) {
            ans += args[0];
            for (int j = 1; j < args.length; j++) {
                ans += " * " + args[j];
            }
        } else if ("/".equals(functor)) {
            ans += args[0];
            if (ans.length() > 1) {
                ans += " / " + args[1];
            }
        } else if ("identity".equals(functor)) {
            ans += args[0];
        } else if ("min".equals(functor)) {
            ans += associativeNaryToBinary("Math.min",0,args);
        } else if ("max".equals(functor)) {
            ans += associativeNaryToBinary("Math.max",0,args);
        } else if ("sum".equals(functor)) {
            ans += args[0];
            for (int j = 1; j < args.length; j++) {
                ans += " + " + args[j];
            }
        } else if ("median".equals(functor)) {
            if ( args.length % 2 == 0 ) {
                ans += " 0.5 * " + args[ args.length / 2 - 1 ] + " + 0.5 * " + args[ args.length / 2 ] + " ";
            } else {
                ans += args[ args.length / 2 ];
            }
        } else if ("product".equals(functor)) {
            ans += args[0];
            for (int j = 1; j < args.length; j++) {
                ans += " * " + args[j];
            }
        } else if ("avg".equals(functor)) {
            ans += "(" + args[0];
            for (int j = 1; j < args.length; j++) {
                ans += " + " + args[j];
            }
            ans += ") / " + args.length;
        } else if ("log10".equals(functor)) {
            ans += "Math.log10(" + args[0] +")";
        } else if ("ln".equals(functor)) {
            ans += "Math.log(" + args[0] +")";
        } else if ("sqrt".equals(functor)) {
            ans += "Math.sqrt(" + args[0] +")";
        } else if ("abs".equals(functor)) {
            ans += "Math.abs(" + args[0] +")";
        } else if ("exp".equals(functor)) {
            ans += "Math.exp(" + args[0] +")";
        } else if ("pow".equals(functor)) {
            if ( "0".equals( args[0] ) && "0".equals( args[1] ) ) {
                ans += "1";
            } else {
                ans += "Math.pow(" + args[0] +","+ args[1] +")";
            }
        } else if ("threshold".equals(functor)) {
            ans += args[0] + " > " + args[1] + " ? 1 : 0";
        } else if ("floor".equals(functor)) {
            ans += "Math.floor(" + args[0] +")";
        } else if ("round".equals(functor)) {
            ans += "Math.round(" + args[0] +")";
        } else if ("ceil".equals(functor)) {
            ans += "Math.ceil(" + args[0] +")";
        } else if ("uppercase".equals(functor)) {
            ans += args[0] + ".toString().toUpperCase()" ;
        } else if ("substring".equals(functor)) {
            int start = Integer.valueOf(args[1]) - 1;
            int len = Integer.valueOf(args[2]);
            ans += args[0] + ".toString().substring(" + start + "," + (start+len) +")";
        } else if ("trimBlanks".equals(functor)) {
            ans += args[0] + ".toString().trim()" ;
        } else if ("formatNumber".equals(functor)) {
            ans += "new java.util.Formatter(new StringBuilder(),java.util.Locale.getDefault()).format(" +
                    args[0] + "," + Double.valueOf(args[1]) + ")";
        } else if ("formatDatetime".equals(functor)) {
            //TODO : PMML Uses Posix
            ans += "new java.text.SimpleDateFormat(" + posix2Java(args[1]) +
                    ").format(new SimpleDateFormat().parse(" + args[0] +", java.util.Locale.ENGLISH))";
        } else if ("dateDaysSinceYear".equals(functor)) {
            ans += "( (new java.text.SimpleDateFormat()).parse(" + args[0] + ").getTime()" +
                    " - (new java.text.SimpleDateFormat()).parse(\"01/01/" + args[1] + "\").getTime() ) / (1000*60*60*24)";
        } else if ("dateSecondsSinceYear".equals(functor)) {
            ans += "( (new java.text.SimpleDateFormat()).parse(" + args[0] + ").getTime()" +
                    " - (new java.text.SimpleDateFormat()).parse(\"01/01/" + args[1] + "\").getTime() ) / 1000";
        } else if ("dateSecondsSinceMidnight".equals(functor)) {
            ans += "(new java.text.SimpleDateFormat()).parse(" + args[0] + ").getTime() % 1000";
        } else if ("equal".equals(functor)) {
            ans += args[0] + " == " + args[1];
        } else if ("notEqual".equals(functor)) {
            ans += args[0] + " != " + args[1];
        } else if ("lessThan".equals(functor)) {
            ans += args[0] + " < " + args[1];
        } else if ("lessOrEqual".equals(functor)) {
            ans += args[0] + " <= " + args[1];
        } else if ("greaterThan".equals(functor)) {
            ans += args[0] + " > " + args[1];
        } else if ("greaterOrEqual".equals(functor)) {
            ans += args[0] + " >= " + args[1];
        } else if ("isIn".equals(functor)) {
            ans += args[0] + ".contains(" + args[1] + ")";
        } else if ("isNotIn".equals(functor)) {
            ans += "(! " + args[0] + ".contains(" + args[1] + "))";
        } else if ("not".equals(functor)) {
            ans += "( ! " + args[0] + " )";
        } else if ("and".equals(functor)) {
            ans += args[0];
            for (int j = 1; j < args.length; j++) {
                ans += " && " + args[j];
            }
        } else if ("or".equals(functor)) {
            ans += args[0];
            for (int j = 1; j < args.length; j++) {
                ans += " || " + args[j];
            }
        } else if ("if".equals(functor)) {
            ans += args[0] + " ? " + args[1] + " : " + ( args.length > 2 ? args[2] : "null" );
        } else {
            // custom function!
            if (args.length == 0) {
                if ( asQuery ) {
                    return functor + "( $" + functor + "_return ; )";
                } else {
                    return functor + "( )";
                }
            }
            String tmp = args[0];
            for (int j = 1; j < args.length; j++) {
                tmp += ", " + args[j];
            }
            if ( asQuery ) {
                tmp += ", $ctx, $" + functor + "_return ; ";
            }
            return functor + "( " + tmp + " )";
        }
        if ( asQuery ) {
            ans += ", $ctx, $" + functor + "_return ; ";
        }
        ans += ")";
        return ans;
    }


    public boolean isBuiltIn( String functor ) {
        if ("+".equals(functor)) {
            return true;
        } else if ("-".equals(functor)) {
            return true;
        } else if ("*".equals(functor)) {
            return true;
        } else if ("/".equals(functor)) {
            return true;
        } else if ("identity".equals(functor)) {
            return true;
        } else if ("min".equals(functor)) {
            return true;
        } else if ("max".equals(functor)) {
            return true;
        } else if ("sum".equals(functor)) {
            return true;
        } else if ("median".equals(functor)) {
            return true;
        } else if ("product".equals(functor)) {
            return true;
        } else if ("avg".equals(functor)) {
            return true;
        } else if ("log10".equals(functor)) {
            return true;
        } else if ("ln".equals(functor)) {
            return true;
        } else if ("sqrt".equals(functor)) {
            return true;
        } else if ("abs".equals(functor)) {
            return true;
        } else if ("exp".equals(functor)) {
            return true;
        } else if ("pow".equals(functor)) {
            return true;
        } else if ("threshold".equals(functor)) {
            return true;
        } else if ("floor".equals(functor)) {
            return true;
        } else if ("round".equals(functor)) {
            return true;
        } else if ("ceil".equals(functor)) {
            return true;
        } else if ("uppercase".equals(functor)) {
            return true;
        } else if ("substring".equals(functor)) {
            return true;
        } else if ("trimBlanks".equals(functor)) {
            return true;
        } else if ("formatNumber".equals(functor)) {
            return true;
        } else if ("formatDatetime".equals(functor)) {
            return true;
        } else if ("dateDaysSinceYear".equals(functor)) {
            return true;
        } else if ("dateSecondsSinceYear".equals(functor)) {
            return true;
        } else if ("dateSecondsSinceMidnight".equals(functor)) {
            return true;
        } else if ("equal".equals(functor)) {
            return true;
        } else if ("notEqual".equals(functor)) {
            return true;
        } else if ("lessThan".equals(functor)) {
            return true;
        } else if ("lessOrEqual".equals(functor)) {
            return true;
        } else if ("greaterThan".equals(functor)) {
            return true;
        } else if ("greaterOrEqual".equals(functor)) {
            return true;
        } else if ("isIn".equals(functor)) {
            return true;
        } else if ("isNotIn".equals(functor)) {
            return true;
        } else if ("not".equals(functor)) {
            return true;
        } else if ("and".equals(functor)) {
            return true;
        } else if ("or".equals(functor)) {
            return true;
        } else if ("if".equals(functor)) {
            return true;
        } else {
            return false;
        }
    }

    private String posix2Java(String posixFormat) {
        //TODO
        return posixFormat;
    }





    private String associativeNaryToBinary(String f, int j, Object... args) {
        if (j < args.length -1)
            return f + "(" + args[j] + "," + associativeNaryToBinary(f,j+1,args) + ")";
        else
            return args[j].toString();
    }


    public String mapNeuralActivation(String functor, double threshold) {
        if ("threshold".equals(functor)) {
            return " x > " + threshold + " ? 1 : 0";
        } else if ("logistic".equals(functor)) {
            return "1.0/(1+Math.exp(-x))";
        } else if ("tanh".equals(functor)) {
            return "(1.0-Math.exp(-2*x))/(1.0+Math.exp(2*x))";
        } else if ("identity".equals(functor)) {
            return "x";
        } else if ("exponential".equals(functor)) {
            return "Math.exp(x)";
        } else if ("reciprocal".equals(functor)) {
            return "1.0/x";
        } else if ("square".equals(functor)) {
            return "x*x";
        } else if ("Gauss".equals(functor)) {
            return "Math.exp(-x*x)";
        } else if ("sine".equals(functor)) {
            return "Math.sin(x)";
        } else if ("cosine".equals(functor)) {
            return "Math.cos(x)";
        } else if ("Elliott".equals(functor)) {
            return "x/(1.0+Math.abs(x))";
        } else if ("arctan".equals(functor)) {
            return "2.0*Math.atan(x)/Math.PI";
        } else {
            return "x";
        }
    }

    public String mapRegModelRegressionNormalization( String method, String arg  ) {
        if ( method == null || REGRESSIONNORMALIZATIONMETHOD.NONE.value().equals( method ) ) {
            return arg;
        } else if ( REGRESSIONNORMALIZATIONMETHOD.EXP.value().equals( method ) ) {
            return "Math.exp( " + arg + " )";
        } else if ( REGRESSIONNORMALIZATIONMETHOD.SOFTMAX.value().equals( method ) || REGRESSIONNORMALIZATIONMETHOD.LOGIT.value().equals( method ) ) {
            return "1.0 / ( 1.0 + Math.exp( -" + arg + " ) ) ";
        } else {
            throw new UnsupportedOperationException( "Regression models can't support " + method + ", check that a classification model was not required instead. " );
        }
    }

    public String mapRegModelClassificationNormalization( String method, String arg  ) {
        if ( method == null || REGRESSIONNORMALIZATIONMETHOD.NONE.value().equals( method ) ) {
            return arg;
        } else if ( REGRESSIONNORMALIZATIONMETHOD.EXP.value().equals( method ) ) {
            return "Math.exp( " + arg + " )";
        } else if ( REGRESSIONNORMALIZATIONMETHOD.SOFTMAX.value().equals( method ) ) {
            return "Math.exp( " + arg + " )";
        } else if ( REGRESSIONNORMALIZATIONMETHOD.LOGIT.value().equals( method ) ) {
            return "1.0 / ( 1.0 + Math.exp( -" + arg + " ) )";
        } else if ( REGRESSIONNORMALIZATIONMETHOD.PROBIT.value().equals( method ) ) {
            return "probitPhi( " + arg + " )";
        } else if ( REGRESSIONNORMALIZATIONMETHOD.CLOGLOG.value().equals( method ) ) {
            return "1.0 - Math.exp( - Math.exp( " + arg + " ) )";
        } else if ( REGRESSIONNORMALIZATIONMETHOD.LOGLOG.value().equals( method ) ) {
            return "Math.exp( - Math.exp( -" + arg + " ) )";
        } else if ( REGRESSIONNORMALIZATIONMETHOD.CAUCHIT.value().equals( method ) ) {
            return "0.5 + Math.atan( " + arg + " ) / Math.PI";
        } else {
            throw new UnsupportedOperationException( "Unknown normalization method :" + method );
        }

    }


    public String compactUpperCase(String s) {
        java.util.StringTokenizer tok = new java.util.StringTokenizer(s);
        StringBuilder sb = new StringBuilder();

        while ( tok.hasMoreTokens() ) {
            sb.append( capitalize( tok.nextToken() ) );
        }

        String out = sb.toString();
        if( out.matches( "\\d.*" ) ) {
            out = "DF_" + out;
        }
        return out;

    }

    public String compactAsJavaId(String s) {
        return compactAsJavaId( s, false );
    }
    
    public String compactAsJavaId(String s, boolean forceCapital) {
        s = s.replaceAll( ",", "_" );
        java.util.StringTokenizer tok = new java.util.StringTokenizer(s.trim());
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        while ( tok.hasMoreTokens() ) {
            String nt = tok.nextToken();
            sb.append( (first && ! allCapital( nt ) && ! forceCapital ) ? lowerCase( nt ) : capitalize( nt ) );
            first = false;
        }

        String out = sb.toString();
        if( out.matches( "\\d.*" ) ) {
            out = "DF_" + out;
        }
        return out;

    }

    private boolean allCapital( String s ) {
        return s.length() >= 2 && Character.isUpperCase( s.charAt( 0 ) ) && Character.isUpperCase( s.charAt( 1 ) );
    }

    public String lowerCase(String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    public String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }


    public String getToken( String s, int j ) {
        StringTokenizer tok = new StringTokenizer( s );
        for ( int i = 0; i < j; i++ ) {
            tok.nextToken();
            if ( ! tok.hasMoreTokens() ) {
                return null;
            }
        }
        return tok.nextToken();
    }



    public String[] tokenize( String s, String delimiters ) {
        StringTokenizer tok = new StringTokenizer( s, delimiters );
        return tokenize( tok );
    }


    public String[] tokenize( String s ) {
        StringTokenizer tok = new StringTokenizer( s );
        return tokenize( tok );
    }

    private String[] tokenize( StringTokenizer tok ) {
        int num = tok.countTokens();
        String[] toks = new String[ num ];

        for ( int j = 0; j < num; j++ ) {
            toks[ j ] = tok.nextToken();
        }
        return toks;
    }

    public String[] ones( Integer num ) {
        String[] ones = new String[ num ];
        Arrays.fill( ones, "1.0" );
        return ones;
    }


    public String mapComparisonFunction( COMPAREFUNCTION fun, String arg1, String arg2, String scope, String local ) {
        switch ( fun ) {
            case ABS_DIFF:
                return "Math.abs( " + arg1 + " - " + arg2 + ")";
            case DELTA:
                return " ( " + arg1 + " == " + arg2 + " ) ? 0.0 : 1.0";
            case EQUAL:
                return " ( " + arg1 + " == " + arg2 + " ) ? 1.0 : 0.0";
            case GAUSS_SIM:
                return "Math.exp( - Math.log( 2.0 ) * ( " + arg1 + " - " + arg2 + " ) * ( " + arg1 + " - " + arg2 + " ) / ( " + scope + " * " + scope + " ) )";
            case TABLE:
                return local;
        }
        throw new IllegalStateException( "Unrecognized PMML CompareFunction " + fun );
    }



    public String mapOperator( String op ) {
        if ( "equal".equals( op ) ) {
            return "==";
        } else if ( "notEqual".equals( op ) ) {
            return "!=";
        } else if ( "lessThan".equals( op ) ) {
            return "<";
        } else if ( "lessOrEqual".equals( op ) ) {
            return "<=";
        } else if ( "greaterThan".equals( op ) ) {
            return ">";
        } else if ( "greaterOrEqual".equals( op ) ) {
            return ">=";
        } else if ( "and".equals( op ) ) {
            return "&&";
        } else if ( "or".equals( op ) ) {
            return "||";
        } else if ( "xor".equals( op ) ) {
            return "^^";
        }
        throw new IllegalStateException( "Unrecognized PMML Operator " + op );
    }












//
//    public String mapDatatypeToQuestion(DATATYPE datatype) {
//        String s = datatype != null ? datatype.value() : null;
//		if (s == null) return Object.class.getName();
//		if ("Integer".equalsIgnoreCase(s))
//            return Question.QuestionType.TYPE_NUMBER.getValue();
//		else if ("Float".equalsIgnoreCase(s))
//		    return Question.QuestionType.TYPE_DECIMAL.getValue();
//        else if ("Double".equalsIgnoreCase(s))
//		    return Question.QuestionType.TYPE_DECIMAL.getValue();
//        else if ("Boolean".equalsIgnoreCase(s))
//            return Question.QuestionType.TYPE_BOOLEAN.getValue();
//		else if ("String".equalsIgnoreCase(s))
//			return Question.QuestionType.TYPE_TEXT.getValue();
//		else if ("Date".equalsIgnoreCase(s))
//			return Question.QuestionType.TYPE_DATE.getValue();
//		else if ("Time".equalsIgnoreCase(s))
//			return Question.QuestionType.TYPE_DATE.getValue();
//		else if ("DateTime".equalsIgnoreCase(s))
//			return Question.QuestionType.TYPE_DATE.getValue();
//		else if ("DateDaysSince[0]".equalsIgnoreCase(s))
//			return Question.QuestionType.TYPE_NUMBER.getValue();
//		else if ("DateDaysSince[1960]".equalsIgnoreCase(s))
//			return Question.QuestionType.TYPE_NUMBER.getValue();
//		else if ("DateDaysSince[1970]".equalsIgnoreCase(s))
//			return Question.QuestionType.TYPE_NUMBER.getValue();
//		else if ("DateDaysSince[1980]".equalsIgnoreCase(s))
//			return Question.QuestionType.TYPE_NUMBER.getValue();
//		else if ("TimeSeconds".equalsIgnoreCase(s))
//			return Question.QuestionType.TYPE_NUMBER.getValue();
//		else if ("DateTimeSecondsSince[0]".equalsIgnoreCase(s))
//			return Question.QuestionType.TYPE_NUMBER.getValue();
//		else if ("DateTimeSecondsSince[1960]".equalsIgnoreCase(s))
//			return Question.QuestionType.TYPE_NUMBER.getValue();
//		else if ("DateTimeSecondsSince[1970]".equalsIgnoreCase(s))
//			return Question.QuestionType.TYPE_NUMBER.getValue();
//		else if ("DateTimeSecondsSince[1980]".equalsIgnoreCase(s))
//			return Question.QuestionType.TYPE_NUMBER.getValue();
//        else if ("collection".equalsIgnoreCase(s))
//			return Question.QuestionType.TYPE_LIST.getValue();
//		else
//			return Question.QuestionType.TYPE_TEXT.getValue();
//	}


    public String mapDatatypeToQuestion(DATATYPE datatype) {
        String s = datatype != null ? datatype.value() : null;
        if (s == null) return Object.class.getName();
        if ("Integer".equalsIgnoreCase(s))
            return "Question.QuestionType.TYPE_NUMBER";
        else if ("Float".equalsIgnoreCase(s))
            return "Question.QuestionType.TYPE_DECIMAL";
        else if ("Double".equalsIgnoreCase(s))
            return "Question.QuestionType.TYPE_DECIMAL";
        else if ("Boolean".equalsIgnoreCase(s))
            return "Question.QuestionType.TYPE_BOOLEAN";
        else if ("String".equalsIgnoreCase(s))
            return "Question.QuestionType.TYPE_TEXT";
        else if ("Date".equalsIgnoreCase(s))
            return "Question.QuestionType.TYPE_DATE";
        else if ("Time".equalsIgnoreCase(s))
            return "Question.QuestionType.TYPE_DATE";
        else if ("DateTime".equalsIgnoreCase(s))
            return "Question.QuestionType.TYPE_DATE";
        else if ("DateDaysSince[0]".equalsIgnoreCase(s))
            return "Question.QuestionType.TYPE_NUMBER";
        else if ("DateDaysSince[1960]".equalsIgnoreCase(s))
            return "Question.QuestionType.TYPE_NUMBER";
        else if ("DateDaysSince[1970]".equalsIgnoreCase(s))
            return "Question.QuestionType.TYPE_NUMBER";
        else if ("DateDaysSince[1980]".equalsIgnoreCase(s))
            return "Question.QuestionType.TYPE_NUMBER";
        else if ("TimeSeconds".equalsIgnoreCase(s))
            return "Question.QuestionType.TYPE_NUMBER";
        else if ("DateTimeSecondsSince[0]".equalsIgnoreCase(s))
            return "Question.QuestionType.TYPE_NUMBER";
        else if ("DateTimeSecondsSince[1960]".equalsIgnoreCase(s))
            return "Question.QuestionType.TYPE_NUMBER";
        else if ("DateTimeSecondsSince[1970]".equalsIgnoreCase(s))
            return "Question.QuestionType.TYPE_NUMBER";
        else if ("DateTimeSecondsSince[1980]".equalsIgnoreCase(s))
            return "Question.QuestionType.TYPE_NUMBER";
        else if ("collection".equalsIgnoreCase(s))
            return "Question.QuestionType.TYPE_LIST";
        else
            return "Question.QuestionType.TYPE_TEXT";
    }

    public String mapTreeOp( String op ) {
        if ( "and".equals( op ) ) {
            return " && ";
        } else if ( "or".equals( op ) ) {
            return " || ";
        } else if ( "xor".equals( op ) ) {
            return " ^^ ";
        } else if ( "surrogate".equals( op ) ) {
            return " || ";
        } else {
            return " , ";
        }
    }


    public DATATYPE mapFeatureType( DATATYPE srcType, RESULTFEATURE feat ) {
        if ( feat == null ) {
            return srcType;
        } else switch ( feat ) {
            case PREDICTED_VALUE: return srcType;
            case PREDICTED_DISPLAY_VALUE: return DATATYPE.STRING;
            case PROBABILITY: return DATATYPE.DOUBLE;
            case REASON_CODE: return DATATYPE.STRING;
        }
        return srcType;
    }


    public void reset() {
        definedModelBeans = new HashSet<String>();
    }


    public static AggregationStrategy resolveAggregationStrategy( String strat ) {
        if ( strat == null || strat.isEmpty() ) {
            return AggregationStrategy.AGGREGATE_SCORE;
        }
        AggregationStrategy agg = AggregationStrategy.valueOf( strat );
        return agg != null ? agg : AggregationStrategy.AGGREGATE_SCORE;
    }

    public boolean isWeighted( String strat ) {
        return resolveAggregationStrategy( strat ).isWeighted();
    }

    public String mapWeightStrategy( String strat ) {
        return resolveAggregationStrategy( strat ).getAggregator();
    }

    public String extractDistributionParameters( Object d ) {
        if ( d instanceof GaussianDistribution ) {
            GaussianDistribution gd = (GaussianDistribution) d;
            return "new Double[] { " + gd.getMean() + ", " + gd.getVariance() + " }";
        } else if ( d instanceof PoissonDistribution ) {
            PoissonDistribution pd = (PoissonDistribution) d;
            return "new Double[] { " + pd.getMean() + " }";
        } else if ( d instanceof UniformDistribution ) {
            UniformDistribution ud = (UniformDistribution) d;
            return "new Double[] { " + ud.getLower() + ", " + ud.getUpper() + " }";
        } else if ( d instanceof AnyDistribution ) {
            AnyDistribution ad = (AnyDistribution) d;
            return "new Double[] { " + ad.getMean() + ", " + ad.getVariance() + " }";
        }
        throw new IllegalStateException( "Unrecognized Distribution type " + d.getClass().getName() );
    }

    public String evaluateDistribution( String x, String type ) {
        if ( "Poisson".equals( type ) ) {
            throw new UnsupportedOperationException( "TODO" );
        } else if ( "Uniform".equals( type ) ) {
            throw new UnsupportedOperationException( "TODO" );
        } else {
            // Gauss and Any
            return " Math.exp( -( " + x + " - p[0] ) * ( " + x + " - p[0] ) / 2 * p[1] ) / Math.sqrt( 2 * Math.PI * p[1] ) ";
        }

    }
}
