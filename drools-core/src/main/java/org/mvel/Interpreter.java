package org.mvel;

import org.mvel.util.ExecutionStack;
import static org.mvel.ExpressionParser.compileExpression;
import static org.mvel.ExpressionParser.executeExpression;

import java.io.*;
import static java.lang.String.valueOf;
import static java.lang.System.arraycopy;
import java.nio.ByteBuffer;
import static java.nio.ByteBuffer.allocateDirect;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import static java.util.Collections.synchronizedMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * The MVEL Template Interpreter.  Naming this an "Interpreter" is not inaccurate.   All template expressions
 * are pre-compiled by the the {@link TemplateCompiler} prior to being processed by this interpreter.<br/>
 * <br/>
 * Under normal circumstances, it is completely acceptable to execute the parser/interpreter from the static
 * convenience methods in this class.
 *
 * @author Christopher Brock
 */
public class Interpreter {

    public static boolean cacheAggressively = false;

    /**
     * Evaluates the template expression and returns a String value.  This is only a convenience method that
     * has the same semantics as using <tt>String.valueOf(eval(expr, vars, ctx))</tt>.
     *
     * @param template - the template to be evaluated
     * @param ctx      - the virtual root / context of the expression.
     * @return the resultant value represented in it's equivelant string value.
     */
    public static String evalToString(String template, Object ctx) {
        return valueOf(eval(template, ctx));
    }


    /**
     * Evaluates the template expression and returns a String value.  This is only a convenience method that
     * has the same semantics as using <tt>String.valueOf(eval(expr, vars, ctx))</tt>.
     *
     * @param template  - the template to be evaluated
     * @param variables - a map of variables for use in the expression.
     * @return the resultant value represented in it's equivelant string value.
     */
    public static String evalToString(String template, Map variables) {
        return valueOf(eval(template, variables));
    }

    /**
     * Evaluates the template expression and returns a String value.  This is only a convenience method that
     * has the same semantics as using <tt>String.valueOf(eval(expr, vars, ctx))</tt>.
     *
     * @param template  - the template to be evaluated
     * @param ctx       - the virtual root / context of the expression.
     * @param variables - a map of variables for use in the expression.
     * @return the resultant value represented in it's equivelant string value.
     */
    public static String evalToString(String template, Object ctx, Map variables) {
        return valueOf(eval(template, ctx, variables));
    }

    /**
     * @param template - the template to be evaluated
     * @param ctx      - the virtual root / context of the expression.
     * @return see description.
     * @see #eval(String,Object,Map)
     */
    public static Object eval(String template, Object ctx) {
        if (template == null) return null;
        return new Interpreter(template).execute(ctx, null);
    }

    /**
     * @param template  - the template to be evaluated
     * @param variables - a map of variables for use in the expression.
     * @return see description.
     * @see #eval(String,Object,Map)
     */
    public static Object eval(String template, Map variables) {
        return new Interpreter(template).execute(null, variables);
    }

    /**
     * Compiles, interprets and returns the result from a template.  The value that this returns is dependant
     * on whether or not the template actually contains any literal values.<br/>
     * <br/>
     * For example, an expression that is simply "<tt>@{foobar}</tt>" will return the value of <tt>foobar</tt>,
     * not a string value.  An expression that only contains a single tag is a defacto expression and is not
     * considered a template.<br/>
     * <br/>
     * An expression such as "<tt>Hello my name is: @{name}</tt>" will return the a String value as it clearly a
     * template.<br/>
     *
     * @param template  - the template to be evaluated
     * @param ctx       - the virtual root / context of the expression.
     * @param variables - a map of variables for use in the expression.
     * @return see description.
     */
    public static Object eval(String template, Object ctx, Map variables) {
        if (template == null) return null;
        //noinspection unchecked
        return new Interpreter(template).execute(ctx, variables);
    }

    private char[] expression;
    private boolean debug = false;
    private Node[] nodes;
    private int node = 0;

    private static Map<CharSequence, char[]> EX_PRECACHE;
    private static Map<Object, Node[]> EX_NODE_CACHE;
    private static Map<Object, Object> EX_PRECOMP_CACHE;

    static {
         configureFactory();
    }

    static void configureFactory() {
        if (MVEL.THREAD_SAFE) {
            EX_PRECACHE = synchronizedMap(new WeakHashMap<CharSequence, char[]>());
            EX_NODE_CACHE = synchronizedMap(EX_NODE_CACHE = new WeakHashMap<Object, Node[]>());
            EX_PRECOMP_CACHE = synchronizedMap(EX_PRECOMP_CACHE = new WeakHashMap<Object, Object>());
        }
        else {
            EX_PRECACHE = (new WeakHashMap<CharSequence, char[]>());
            EX_NODE_CACHE = (EX_NODE_CACHE = new WeakHashMap<Object, Node[]>());
            EX_PRECOMP_CACHE = (EX_PRECOMP_CACHE = new WeakHashMap<Object, Object>());
        }
    }

    private ExecutionStack stack;

    /**
     * Creates a new intepreter
     *
     * @param template -
     */
    public Interpreter(CharSequence template) {
        if (!EX_PRECACHE.containsKey(template)) {
            EX_PRECACHE.put(template, this.expression = template.toString().toCharArray());
            EX_NODE_CACHE.put(template, nodes = new TemplateCompiler(this).compileExpression());
        }
        else {
            this.expression = EX_PRECACHE.get(template);
            this.nodes = EX_NODE_CACHE.get(template);
        }
    }

    public Interpreter(String expression) {
        if (!EX_PRECACHE.containsKey(expression)) {
            EX_PRECACHE.put(expression, this.expression = expression.toCharArray());
            EX_NODE_CACHE.put(expression, nodes = new TemplateCompiler(this).compileExpression());
        }
        else {
            this.expression = EX_PRECACHE.get(expression);
            this.nodes = EX_NODE_CACHE.get(expression);
        }
    }

    public Interpreter(char[] expression) {
        this.expression = expression;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public static void parseToStream(File template, Object ctx, Map<String, Object> tokens, OutputStream out)
            throws IOException {
        Object result = parse(template, ctx, tokens);
        CharSequence cs;

        if (result == null) return;
        else if (result instanceof CharSequence) {
            cs = (CharSequence) result;
        }
        else {
            cs = valueOf(result);
        }

        OutputStreamWriter writer = new OutputStreamWriter(out);

        int len = cs.length();
        for (int i = 0; i < len; i++) {
            writer.write(cs.charAt(i));
        }
        writer.flush();
        writer.close();
    }

    public static Object parse(File file, Object ctx, Map<String, Object> tokens) throws IOException {
        if (!file.exists())
            throw new CompileException("cannot find file: " + file.getName());

        FileInputStream inStream = null;
        ReadableByteChannel fc = null;
        try {
            inStream = new FileInputStream(file);
            fc = inStream.getChannel();
            ByteBuffer buf = allocateDirect(10);

            StringBuilder sb = new StringBuilder((int) file.length());

            int read = 0;
            while (read >= 0) {
                buf.rewind();
                read = fc.read(buf);
                buf.rewind();

                for (; read > 0; read--) {
                    sb.append((char) buf.get());
                }
            }

            return parse(sb, ctx, tokens);

        }
        catch (FileNotFoundException e) {
            // this can't be thrown, we check for this explicitly.
        }
        finally {
            if (inStream != null) inStream.close();
            if (fc != null) fc.close();
        }

        return null;
    }


    public static Object parse(CharSequence expression, Object ctx, Map<String, Object> vars) {
        if (expression == null) return null;
        return new Interpreter(expression).execute(ctx, vars);
    }


    public static Object parse(String expression, Object ctx, Map<String, Object> vars) {
        if (expression == null) return null;

        return new Interpreter(expression).execute(ctx, vars);
    }


    public Object execute(Object ctx, Map tokens) {
        if (nodes == null) {
            return new String(expression);
        }
        else if (nodes.length == 2) {
            switch (nodes[0].getToken()) {
                case PROPERTY_EX:
                    //noinspection unchecked
                    //  return ExpressionParser.eval(getInternalSegment(nodes[0]), ctx, tokens);

                    if (!cacheAggressively) {
                        char[] seg = new char[expression.length - 3];
                        arraycopy(expression, 2, seg, 0, seg.length);

                        return ExpressionParser.eval(seg, ctx, tokens);
                    }
                    else {
                        String s = new String(expression, 2, expression.length - 3);
                        if (!EX_PRECOMP_CACHE.containsKey(s)) {
                            EX_PRECOMP_CACHE.put(s, compileExpression(s));
                        }

                        return executeExpression(EX_PRECOMP_CACHE.get(s), ctx, tokens);

                    }
                case LITERAL:
                    return new String(expression);
            }

            return new String(expression);
        }

        Object register = null;

        StringBuilder sbuf = new StringBuilder(10);
        Node currNode = null;

        try {
            ExpressionParser oParser = new ExpressionParser(ctx, tokens);

            initStack();
            pushAndForward();

            while ((currNode = pop()) != null) {
                node = currNode.getNode();

                switch (currNode.getToken()) {
                    case LITERAL: {
                        sbuf.append(register = new String(expression, currNode.getStartPos(),
                                currNode.getEndPos() - currNode.getStartPos()));
                        break;
                    }
                    case PROPERTY_EX: {
                        sbuf.append(
                                valueOf(register = oParser.setExpressionArray(getInternalSegment(currNode)).parse())
                        );
                        break;
                    }
                    case IF:
                    case ELSEIF: {
                        try {
                            oParser.setBooleanModeOnly(true);
                            if (!((Boolean) oParser.setExpressionArray(getInternalSegment(currNode)).parse())) {
                                exitContext();
                            }
                            oParser.setBooleanModeOnly(false);
                        }
                        catch (ClassCastException e) {
                            throw new CompileException("IF expression does not return a boolean: " + new String(getSegment(currNode)));
                        }
                        break;
                    }

                    case FOREACH: {
                        if (currNode.getRegister() == null) {
                            try {
                                currNode.setRegister(
                                        ((Collection) new ExpressionParser(getForEachSegment(currNode), ctx, tokens).parse()).iterator()
                                );
                            }
                            catch (ClassCastException e) {
                                throw new CompileException("expression for collection does not return a collection object: " + new String(getSegment(currNode)));
                            }
                            catch (NullPointerException e) {
                                throw new CompileException("null returned for foreach in expression: " + (getForEachSegment(currNode)));
                            }
                        }

                        Iterator iter = (Iterator) currNode.getRegister();
                        if (iter.hasNext()) {
                            push();
                            //noinspection unchecked
                            tokens.put(currNode.getAlias(), iter.next());
                        }
                        else {
                            tokens.remove(currNode.getAlias());
                            exitContext();
                        }
                        break;
                    }
                    case ELSE:
                    case END:
                        if (stack.isEmpty()) forwardAndPush();
                        continue;
                    case GOTO:
                        pushNode(currNode.getEndNode());
                        continue;
                    case TERMINUS: {
                        if (nodes.length == 2) {
                            return register;
                        }
                        else {
                            return sbuf.toString();
                        }
                    }
                }

                forwardAndPush();
            }
            throw new CompileException("expression did not end properly: expected TERMINUS node");
        }
        catch (CompileException e) {
            throw e;
        }
        catch (Exception e) {
            if (currNode != null) {
                throw new CompileException("problem encountered at node [" + currNode.getNode() + "] "
                        + currNode.getToken() + "{" + currNode.getStartPos() + "," + currNode.getEndPos() + "}", e);
            }
            throw new CompileException("unhandled fatal exception (node:" + node + ")", e);
        }
    }


    private void initStack() {
        stack = new ExecutionStack();
    }

    private void push() {
        push(nodes[node]);
    }

    private void push(Node node) {
        if (node == null) return;
        stack.push(node);
    }

    private void pushNode(int i) {
        stack.push(nodes[i]);
    }

    private void exitContext() {
        node = nodes[node].getEndNode();
    }


    public void forwardAndPush() {
        node++;
        push();
    }

    private void pushAndForward() {
        push();
        node++;
    }

    private Node pop() {
        return (Node) stack.pop();
    }


 
    /**
     * @param expression -
     * @param ctx -
     * @param tokens -
     * @return -
     * 
     * @deprecated
     */
    public static Object getValuePE(String expression, Object ctx, Map<String, Object> tokens) {
        return new Interpreter(expression).execute(ctx, tokens);
    }


    /**
     * @param expression -
     * @param ctx -
     * @param preParseCx -
     * @param value -
     * @deprecated
     */
    public static void setValuePE(String expression, Object ctx, Object preParseCx, Object value) {
        PropertyAccessor.set(ctx, valueOf(eval(expression, preParseCx)), value);
    }

    public char[] getExpression() {
        return expression;
    }

    public void setExpression(char[] expression) {
        this.expression = expression;
    }

    private char[] getSegment(Node n) {
        char[] ca = new char[n.getLength()];
        arraycopy(expression, n.getStartPos(), ca, 0, ca.length);
        return ca;
    }

    private char[] getInternalSegment(Node n) {
        int start = n.getStartPos();
        int depth = 1;

        //noinspection StatementWithEmptyBody
        while ((expression[start++] != '{')) ;

        int end = start;
        while (depth > 0) {
            switch (expression[++end]) {
                case'{':
                    depth++;
                    break;
                case'}':
                    depth--;
                    break;
            }
        }

        char[] ca = new char[end - start];
        arraycopy(expression, start, ca, 0, ca.length);
        return ca;
    }

    private String getForEachSegment(Node n) {
        if (n.getAlias() == null) return new String(getInternalSegment(n));
        else {
            return n.getName();
        }
    }


    public static boolean isCacheAggressively() {
        return cacheAggressively;
    }

    public static void setCacheAggressively(boolean cacheAggressively) {
        Interpreter.cacheAggressively = cacheAggressively;
    }
}
