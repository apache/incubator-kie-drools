package org.drools.pmml_4_0;

import org.drools.informer.Question;
import org.drools.pmml_4_0.descr.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class PMML4Wrapper {

	private String pack;

    private static int counter = 0;

    public int nextCount() {
        return counter++;
    }


    public static String context = null;


    public PMML4Wrapper() {

    }






	public String getPack() {
		return pack;
	}
	public void setPack(String pack) {
		this.pack = pack;
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


	public String mapDatatype(DATATYPE datatype) {
        String s = datatype != null ? datatype.value() : null;
		if (s == null) return Object.class.getName();
		if ("Integer".equalsIgnoreCase(s))
//			return Integer.class.getName();
            return "int";
		else if ("Float".equalsIgnoreCase(s))
//			return Float.class.getName();
		    return "float";
        else if ("Double".equalsIgnoreCase(s))
//			return Double.class.getName();
		    return "double";
        else if ("Boolean".equalsIgnoreCase(s))
//          return Boolean.class.getName();
            return "boolean";
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
		else
			return Object.class.getName();
	}









    public String format(DataField fld, Value val) {
        String s = fld.getDataType().value();
        return format(s, val);
    }

    public String format(String type, Value val) {
        return format(type,val.getValue());
    }

    public String format(DataField fld, String val) {
        String s = fld.getDataType().value();
        return format(s, val);
    }


    public String format(DATATYPE type, String val) {
        return format(type != null ? type.value() : null, val);
    }


    public String format(String type, String val) {
        if (type == null)
            return val;
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
			return val;
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

     public String format(String type, Number val) {
        if (type == null)
            return val.toString();
		if ("Integer".endsWith(type) || "int".equalsIgnoreCase(type) )
			return ""+val.intValue();
		else if ("Float".endsWith(type) || "float".equalsIgnoreCase(type))
			return ""+val.floatValue();
		else if ("Double".endsWith(type) || "double".equalsIgnoreCase(type))
			return ""+val.doubleValue();
		else if ("Boolean".endsWith(type)  || "boolean".equalsIgnoreCase(type)) {
            if (val.doubleValue() == 1.0)
                return "true";
            if (val.doubleValue() == 0.0)
                return "false";
			throw new NumberFormatException("Boolean expected, found " + val);
        }
		else if ("String".endsWith(type))
			return "\""+val.toString()+"\"";


		else if ("Date".endsWith(type))
			return "\""+ (new SimpleDateFormat().format(new Date(val.longValue())) )+"\"";
		else if ("Time".endsWith(type))
			return "\""+val.toString()+"\"";
		else if ("DateTime".endsWith(type))
			return "\""+ (new SimpleDateFormat().format(new Date(val.longValue())) )+"\"";
		else if ("DateDaysSince[0]".equalsIgnoreCase(type))
			throw new UnsupportedOperationException("TODO");
		else if ("DateDaysSince[1960]".equalsIgnoreCase(type))
			throw new UnsupportedOperationException("TODO");
		else if ("DateDaysSince[1970]".equalsIgnoreCase(type))
			throw new UnsupportedOperationException("TODO");
		else if ("DateDaysSince[1980]".equalsIgnoreCase(type))
			throw new UnsupportedOperationException("TODO");
		else if ("TimeSeconds".equalsIgnoreCase(type))
			throw new UnsupportedOperationException("TODO");
		else if ("DateTimeSecondsSince[0]".equalsIgnoreCase(type))
			throw new UnsupportedOperationException("TODO");
		else if ("DateTimeSecondsSince[1960]".equalsIgnoreCase(type))
			throw new UnsupportedOperationException("TODO");
		else if ("DateTimeSecondsSince[1970]".equalsIgnoreCase(type))
			throw new UnsupportedOperationException("TODO");
		else if ("DateTimeSecondsSince[1980]".equalsIgnoreCase(type))
			throw new UnsupportedOperationException("TODO");
		else
			return val.toString();
    }



    public String mapFunction(String functor, List args) {
        System.err.print("Received list of args for functor " + functor + " : ");
            for (Object arg : args)
                System.err.print(arg + "\t");
        System.err.println();
        String[] argz = new String[args.size()];
            for (int j = 0; j < args.size(); j++)
                argz[j] = (String) args.get(j);
        return mapFunction(functor, argz);
    }

    public String mapFunction(String functor, String... args) {
        String ans = "(";
        if ("+".equals(functor)) {
            ans += args[0];
            for (int j = 1; j < args.length; j++)
                ans += " + " + args[j];
        } else if ("-".equals(functor)) {
            ans += args[0];
            for (int j = 1; j < args.length; j++)
                ans += " - " + args[j];
        } else if ("*".equals(functor)) {
            ans += args[0];
            for (int j = 1; j < args.length; j++)
                ans += " * " + args[j];
        } else if ("/".equals(functor)) {
            ans += args[0];
            if (ans.length() > 1)
                ans += " / " + args[1];
        } else if ("identity".equals(functor)) {
            ans += args[0];
        } else if ("min".equals(functor)) {
            ans += associativeNaryToBinary("Math.min",0,args);
        } else if ("max".equals(functor)) {
            ans += associativeNaryToBinary("Math.max",0,args);
        } else if ("sum".equals(functor)) {
            ans += args[0];
            for (int j = 1; j < args.length; j++)
                ans += " + " + args[j];
        } else if ("avg".equals(functor)) {
            ans += "(" + args[0];
            for (int j = 1; j < args.length; j++)
                ans += " + " + args[j];
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
            ans += "Math.pow(" + args[0] +","+ args[1] +")";
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
                    ").format(new SimpleDateFormat().parse(" + args[0] +"))";
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
            for (int j = 1; j < args.length; j++)
                ans += " && " + args[j];
        } else if ("or".equals(functor)) {
            ans += args[0];
            for (int j = 1; j < args.length; j++)
                ans += " || " + args[j];
        } else if ("if".equals(functor)) {
            ans += args[0] + " ? " + args[1] + " : " + ( args.length > 2 ? args[2] : "null" );
        } else {
            if (args.length == 0)
                return functor+"()";
            String tmp = args[0];
            for (int j = 1; j < args.length; j++)
                tmp += "," + args[j];
            return functor+"("+tmp+")";
        }
        ans += ")";
        return ans;
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


     public String compactUpperCase(String s) {
		java.util.StringTokenizer tok = new java.util.StringTokenizer(s);
		StringBuilder sb = new StringBuilder();

		while (tok.hasMoreTokens())
			sb.append(capitalize(tok.nextToken()));

	    return sb.toString();
	 }

     public String capitalize(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
     }
























    public String mapDatatypeToQuestion(DATATYPE datatype) {
        String s = datatype != null ? datatype.value() : null;
		if (s == null) return Object.class.getName();
		if ("Integer".equalsIgnoreCase(s))
            return Question.TYPE_NUMBER;
		else if ("Float".equalsIgnoreCase(s))
		    return Question.TYPE_DECIMAL;
        else if ("Double".equalsIgnoreCase(s))
		    return Question.TYPE_DECIMAL;
        else if ("Boolean".equalsIgnoreCase(s))
            return Question.TYPE_BOOLEAN;
		else if ("String".equalsIgnoreCase(s))
			return Question.TYPE_TEXT;
		else if ("Date".equalsIgnoreCase(s))
			return Question.TYPE_DATE;
		else if ("Time".equalsIgnoreCase(s))
			return Question.TYPE_DATE;
		else if ("DateTime".equalsIgnoreCase(s))
			return Question.TYPE_DATE;
		else if ("DateDaysSince[0]".equalsIgnoreCase(s))
			return Question.TYPE_NUMBER;
		else if ("DateDaysSince[1960]".equalsIgnoreCase(s))
			return Question.TYPE_NUMBER;
		else if ("DateDaysSince[1970]".equalsIgnoreCase(s))
			return Question.TYPE_NUMBER;
		else if ("DateDaysSince[1980]".equalsIgnoreCase(s))
			return Question.TYPE_NUMBER;
		else if ("TimeSeconds".equalsIgnoreCase(s))
			return Question.TYPE_NUMBER;
		else if ("DateTimeSecondsSince[0]".equalsIgnoreCase(s))
			return Question.TYPE_NUMBER;
		else if ("DateTimeSecondsSince[1960]".equalsIgnoreCase(s))
			return Question.TYPE_NUMBER;
		else if ("DateTimeSecondsSince[1970]".equalsIgnoreCase(s))
			return Question.TYPE_NUMBER;
		else if ("DateTimeSecondsSince[1980]".equalsIgnoreCase(s))
			return Question.TYPE_NUMBER;
        else if ("collection".equalsIgnoreCase(s))
			return Question.TYPE_LIST;
		else
			return Question.TYPE_TEXT;
	}

}
