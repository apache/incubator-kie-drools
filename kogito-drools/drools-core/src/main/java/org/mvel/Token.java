package org.mvel;

import static org.mvel.DataConversion.convert;
import static org.mvel.Operator.*;
import org.mvel.compiled.CompiledAccessor;
import org.mvel.compiled.Deferral;
import org.mvel.integration.VariableResolverFactory;
import static org.mvel.util.ArrayTools.findFirst;
import static org.mvel.util.ParseTools.handleEscapeSequence;
import static org.mvel.util.PropertyTools.isNumber;

import java.io.Serializable;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Token implements Cloneable, Serializable {
    public static final int LITERAL = 1;
    public static final int DEEP_PROPERTY = 1 << 1;
    public static final int OPERATOR = 1 << 2;
    public static final int IDENTIFIER = 1 << 3;
    public static final int SUBEVAL = 1 << 4;
    public static final int NUMERIC = 1 << 5;
    public static final int NEGATION = 1 << 6;
    public static final int EVAL_RIGHT = 1 << 7;
    public static final int INVERT = 1 << 8;
    public static final int REQUIRE_REDUCTION = 1 << 9;
    public static final int BOOLEAN_MODE = 1 << 10;
    public static final int TERNARY = 1 << 11;
    public static final int ASSIGN = 1 << 12;
    public static final int LOOKAHEAD = 1 << 13;
    public static final int COLLECTION = 1 << 14;
    public static final int LISTCREATE = 1 << 15;
    public static final int DO_NOT_REDUCE = 1 << 16;
    public static final int CAPTURE_ONLY = 1 << 17;
    public static final int MAPCREATE = 1 << 18;
    public static final int THISREF = 1 << 19;
    public static final int ARRAYCREATE = 1 << 20;
    public static final int NOCOMPILE = 1 << 21;
    public static final int STR_LITERAL = 1 << 25;

    public static final int PUSH = 1 << 22;

    public static final int NEST = 1 << 23;   // token begins a nesting area
    public static final int ENDNEST = 1 << 24; // token ends a nesting area

    public static final int OPTIMIZED_REF = 1 << 31; // future use

    private int start;
    private int end;
    private int firstUnion;
    private int endOfName;

    private char[] name;
    private String nameCache;

    private transient Object value;
    private transient Object resetValue;

    private BigDecimal numericValue;

    private int fields = 0;

    private CompiledExpression compiledExpression;
    private CompiledAccessor compiledAccessor;
//    private Class knownType;

    public static final Map<String, Object> LITERALS =
            new HashMap<String, Object>(35, 0.6f);

    static {

        /**
         * Setup the basic literals
         */
        LITERALS.put("true", TRUE);
        LITERALS.put("false", FALSE);

        LITERALS.put("null", null);
        LITERALS.put("nil", null);

        LITERALS.put("empty", BlankLiteral.INSTANCE);

        LITERALS.put("this", ThisLiteral.class);

        /**
         * Add System and all the class wrappers from the JCL.
         */
        LITERALS.put("System", System.class);

        LITERALS.put("String", String.class);
        LITERALS.put("Integer", Integer.class);
        LITERALS.put("Long", Long.class);
        LITERALS.put("Boolean", Boolean.class);
        LITERALS.put("Short", Short.class);
        LITERALS.put("Character", Character.class);
        LITERALS.put("Double", Double.class);
        LITERALS.put("Float", Float.class);
        LITERALS.put("Math", Math.class);
        LITERALS.put("Void", Void.class);
        LITERALS.put("Object", Object.class);

        LITERALS.put("Class", Class.class);
        LITERALS.put("ClassLoader", ClassLoader.class);
        LITERALS.put("Runtime", Runtime.class);
        LITERALS.put("Thread", Thread.class);
        LITERALS.put("Compiler", Compiler.class);
        LITERALS.put("StringBuffer", StringBuffer.class);
        LITERALS.put("StringBuilder", StringBuilder.class);
        LITERALS.put("ThreadLocal", ThreadLocal.class);
        LITERALS.put("SecurityManager", SecurityManager.class);
        LITERALS.put("StrictMath", StrictMath.class);

        LITERALS.put("Array", java.lang.reflect.Array.class);
    }

    private static final Map<String, Operator> OPERATORS =
            new HashMap<String, Operator>(25 * 2, 0.6f);

    static {
        OPERATORS.put("+", ADD);
        OPERATORS.put("-", SUB);
        OPERATORS.put("*", MULT);
        OPERATORS.put("/", DIV);
        OPERATORS.put("%", MOD);
        OPERATORS.put("==", EQUAL);
        OPERATORS.put("!=", NEQUAL);
        OPERATORS.put(">", GTHAN);
        OPERATORS.put(">=", GETHAN);
        OPERATORS.put("<", LTHAN);
        OPERATORS.put("<=", LETHAN);
        OPERATORS.put("&&", AND);
        OPERATORS.put("and", AND);
        OPERATORS.put("||", OR);
        OPERATORS.put("or", CHOR);
        OPERATORS.put("~=", REGEX);
        OPERATORS.put("instanceof", INSTANCEOF);
        OPERATORS.put("is", INSTANCEOF);
        OPERATORS.put("contains", CONTAINS);
        OPERATORS.put("soundslike", SOUNDEX);
        OPERATORS.put("strsim", SIMILARITY);
        OPERATORS.put("convertable_to", CONVERTABLE_TO);

        OPERATORS.put("#", STR_APPEND);

        OPERATORS.put("&", BW_AND);
        OPERATORS.put("|", BW_OR);
        OPERATORS.put("^", BW_XOR);
        OPERATORS.put("<<", BW_SHIFT_LEFT);
        OPERATORS.put("<<<", BW_USHIFT_LEFT);
        OPERATORS.put(">>", BW_SHIFT_RIGHT);
        OPERATORS.put(">>>", BW_USHIFT_RIGHT);

        OPERATORS.put("?", Operator.TERNARY);
        OPERATORS.put(":", TERNARY_ELSE);

        OPERATORS.put("=", Operator.ASSIGN);

        OPERATORS.put(";", END_OF_STMT);

        OPERATORS.put("new", NEW);

        OPERATORS.put("in", PROJECTION);
    }


    public Token(char[] expr, int start, int end, int fields) {
        this.fields = fields;

        char[] name = new char[end - start];
        System.arraycopy(expr, this.start = start, name, 0, (this.end = end) - start);
        setName(name);
    }

    public Token(char expr, int fields) {
        this.fields = fields;
        setName(new char[]{expr});
    }

    public Token(char[] expr, int fields) {
        this.fields = fields;
        setName(expr);
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public boolean emptyRange() {
        return this.start == this.end;
    }

    public boolean isLiteral() {
        return (fields & LITERAL) != 0;
    }

    public void setLiteral(boolean literal) {
        setFlag(literal, LITERAL);
    }

    public boolean isDeepProperty() {
        return (fields & DEEP_PROPERTY) != 0;
    }

    public void setDeepProperty(boolean deepProperty) {
        setFlag(deepProperty, DEEP_PROPERTY);
    }

    public boolean isOperator() {
        return (fields & OPERATOR) != 0;
    }

    public Operator getOperator() {
        return (Operator) value;
    }

    public void setOperator(boolean operator) {
        setFlag(operator, OPERATOR);
    }

    public boolean isNegation() {
        return getFlag(NEGATION);
    }

    public void setNegation(boolean negation) {
        setFlag(negation, NEGATION);
    }

    public char[] createRootElementArray() {
        if ((fields & DEEP_PROPERTY) != 0) {
            char[] root = new char[(firstUnion)];
            System.arraycopy(name, 0, root, 0, root.length);
            return root;
        }
        return null;
    }

    public String getAbsoluteRootElement() {
        if ((fields & DEEP_PROPERTY) != 0 || (fields & COLLECTION) != 0) {
            return new String(name, 0, getAbsoluteFirstPart());
        }
        return null;
    }

    public String getRootElement() {
        return (fields & DEEP_PROPERTY) != 0 ? new String(name, 0, firstUnion) : getName();
        //   return new String(root);
    }

    public char[] createRemainderArray() {
        if ((fields & DEEP_PROPERTY) != 0) {
            char[] remainder = new char[(name.length - firstUnion - 1)];
            System.arraycopy(name, firstUnion + 1, remainder, 0, remainder.length);
            return remainder;
        }
        return null;
    }


    public String getRemainder() {
        //   return new String(remainder);
        return (fields & DEEP_PROPERTY) != 0 ? new String(name, firstUnion + 1, name.length - firstUnion - 1) : null;
    }

    public char[] getNameAsArray() {
        return name;
    }


    public int getEndOfName() {
        return endOfName;
    }

    public int getAbsoluteFirstPart() {
        if ((fields & Token.COLLECTION) != 0) {
            if (firstUnion < 0 || endOfName < firstUnion) return endOfName;
            else return firstUnion;
        }
        else if ((fields & Token.DEEP_PROPERTY) != 0) {
            return firstUnion;
        }
        else {
            return -1;
        }

    }

    public String getAbsoluteName() {
        if ((fields & COLLECTION) != 0) {
            return new String(name, 0, getAbsoluteFirstPart());
        }
        else {
            return getName();

        }
    }

    public String getName() {
        if (nameCache != null) return nameCache;
        else if (name != null) return nameCache = new String(name);
        return "";
    }

    public Object getValue() {
        return value;
    }

    public Token getOptimizedValue(Object ctx, Object elCtx, VariableResolverFactory variableFactory) throws Exception {
        if ((fields & NUMERIC) != 0) {
            value = numericValue = convert(compiledAccessor.getValue(ctx, elCtx, variableFactory), BigDecimal.class);
        }
        else
            value = compiledAccessor.getValue(ctx, elCtx, variableFactory);

        if ((fields & NEGATION) != 0) value = !((Boolean)value);

        return this;
    }


    public void createDeferralOptimization() {
        compiledAccessor = new CompiledAccessor(null, null, null);
        compiledAccessor.addAccessorNode(new Deferral());
    }


    public void optimizeAccessor(Object ctx, VariableResolverFactory variableFactory) {
        compiledAccessor = new CompiledAccessor(name, ctx, variableFactory);
        setNumeric(false);

        Object test = compiledAccessor.compileGetChain();

      //  if (test != null) knownType = test.getClass();

        setNumeric(isNumber(test));
        setFlag(true, Token.OPTIMIZED_REF);
    }

    public void deOptimize()  {
        compiledAccessor = null;
    }

    public boolean isOptimized() {
        return compiledAccessor != null;
    }

    public BigDecimal getNumericValue() {
        return numericValue;
    }

    public String getValueAsString() {
        if (value instanceof String) return (String) value;
        else if (value instanceof char[]) return new String((char[]) value);
        else return valueOf(value);
    }

    public char[] getValueAsCharArray() {
        if (value instanceof char[]) return ((char[]) value);
        else if (value instanceof String) return ((String) value).toCharArray();
        else return valueOf(value).toCharArray();
    }

    public Token setValue(Object value) {
        String s;
        try {
            if ((fields & NEGATION) != 0 && (fields & BOOLEAN_MODE) != 0) {
                this.value = BlankLiteral.INSTANCE.equals(value);
            }
            else if ((fields & NEGATION) != 0) {
                if (value instanceof Boolean) {
                    this.value = !((Boolean) value);
                }
                else {
                    throw new CompileException("illegal negation - not a boolean expression");
                }
            }
            else {
                if (value instanceof BigDecimal) {
                    fields |= NUMERIC;
                    this.numericValue = (BigDecimal) value;
                }
                else if (isNumber(value)) {
                    fields |= NUMERIC;
                    // this.numericValue = new BigDecimal(valueOf(value));
                    this.numericValue = convert(value, BigDecimal.class);
                }
                this.value = value;
            }
        }
        catch (NumberFormatException e) {
            throw new CompileException("unable to create numeric value from: '" + value + "'");
        }

        return this;
    }

    public Token setFinalValue(Object value) {
        this.value = value;
        return this;
    }

    public boolean isIdentifier() {
        return (fields & IDENTIFIER) != 0;
    }

    public void setIdentifier(boolean identifier) {
        setFlag(identifier, IDENTIFIER);
    }

    public boolean isExpand() {
        return (fields & SUBEVAL) != 0;
    }

    public void setExpand(boolean unreduced) {
        setFlag(unreduced, SUBEVAL);
    }

    public boolean isNumeric() {
        return (fields & NUMERIC) != 0;
    }

    public void setNumeric(boolean numeric) {
        setFlag(numeric, NUMERIC);
    }

    public boolean isEvalRight() {
        return (fields & EVAL_RIGHT) != 0;
    }

    public void setEvalRight(boolean evalRight) {
        setFlag(evalRight, EVAL_RIGHT);
    }

    public boolean isInvert() {
        return (fields & INVERT) != 0;
    }

    public void setInvert(boolean invert) {
        setFlag(invert, INVERT);
    }


    @SuppressWarnings({"SuspiciousMethodCalls"})
    public void setName(char[] name) {

        if ((fields & STR_LITERAL) != 0) {
            fields |= LITERAL;
            int escapes = 0;
            for (int i = 0; i < name.length; i++) {
                if (name[i] == '\\') {
                    name[i++] = 0;
                    name[i] = handleEscapeSequence(name[i]);
                    escapes++;
                }
            }

            char[] processedEscapeString = new char[name.length - escapes];
            int cursor = 0;
            for (char aName : name) {
                if (aName == 0) {
                    continue;
                }
                processedEscapeString[cursor++] = aName;
            }

            this.value = new String(this.name = processedEscapeString);

        }
        else {
            this.value = new String(this.name = name);
        }

        if ((fields & (SUBEVAL | LITERAL)) != 0) {
            //    return;
        }
        else if (LITERALS.containsKey(value)) {
            fields |= EVAL_RIGHT | LITERAL;
            if ((value = LITERALS.get(value)) == ThisLiteral.class) fields |= THISREF;
        }
        else if (OPERATORS.containsKey(value)) {
            fields |= OPERATOR;
            resetValue = value = OPERATORS.get(value);
            return;
        }
        else if (((fields & NUMERIC) != 0) || isNumber(name)) {
            if (((fields |= LITERAL | NUMERIC) & INVERT) != 0) {
                value = this.numericValue = new BigDecimal(~parseInt((String) value));
            }
            else {
                value = this.numericValue = new BigDecimal(valueOf(name));
            }
        }
        else if ((firstUnion = findFirst('.', name)) > 0) {
            fields |= DEEP_PROPERTY | IDENTIFIER;
        }
        else {
            fields |= IDENTIFIER;
        }

        if ((endOfName = findFirst('[', name)) > 0) fields |= COLLECTION;

        resetValue = value;
    }

    public int getFlags() {
        return fields;
    }

    private boolean getFlag(int field) {
        return (fields & field) != 0;
    }

    public void setFlag(boolean setting, int flag) {
        if (getFlag(flag) ^ setting) {
            fields = fields ^ flag;
        }
        else if (setting) {
            fields = fields | flag;
        }
    }


    public String toString() {
        return valueOf(value);
    }

    public boolean equals(Object obj) {
        if (obj instanceof Token)
            return value == null ? ((Token) obj).value == null : value.equals(((Token) obj).value);
        else
            return value == null ? obj == value : value.equals(obj);
    }

    public int hashCode() {
        return value == null ? super.hashCode() : value.hashCode();
    }

    public boolean isValidNameIdentifier() {
        return !Character.isDigit(name[0]);
    }


    public int getFirstUnion() {
        return firstUnion;
    }

    public CompiledExpression getCompiledExpression() {
        return compiledExpression;
    }

    public void setCompiledExpression(CompiledExpression compiledExpression) {
        this.compiledExpression = compiledExpression;
    }

    public boolean isCollectionCreation() {
        return ((fields & MAPCREATE) | (fields & ARRAYCREATE) | (fields & LISTCREATE)) != 0;
    }

    public int getCollectionCreationType() {
        return ((fields & MAPCREATE) | (fields & ARRAYCREATE) | (fields & LISTCREATE));
    }

    public boolean isNestBegin() {
        return (fields & Token.NEST) != 0;
    }


    public Token clone() throws CloneNotSupportedException {
        try {
            return (Token) super.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void reset() {
        if (resetValue == null) {
            setLiteral(false);
            setName(name);
        }
        else {
            value = resetValue;
        }
    }
}
