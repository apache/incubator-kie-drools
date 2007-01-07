package org.mvel.compiled;

import org.mvel.*;
import static org.mvel.ExpressionParser.compileExpression;
import static org.mvel.ExpressionParser.executeExpression;
import org.mvel.integration.VariableResolverFactory;
import org.mvel.util.ParseTools;
import static org.mvel.util.ParseTools.parseParameterList;
import org.mvel.util.PropertyTools;

import java.io.Serializable;
import static java.lang.Class.forName;
import static java.lang.Integer.parseInt;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.*;

public class CompiledAccessor {
    private int start = 0;
    private int cursor = 0;

    private char[] property;
    private int length;

    private AccessorNode rootNode;
    private AccessorNode currNode;

    private Object ctx;

    private VariableResolverFactory variableFactory;

    private static final int DONE = -1;
    private static final int BEAN = 0;
    private static final int METH = 1;
    private static final int COL = 2;

    private static final Object[] EMPTYARG = new Object[0];

    public CompiledAccessor(char[] property, Object ctx) {
        this.property = property;
        this.length = property.length;
        this.ctx = ctx;
    }

    public CompiledAccessor(char[] property, Object ctx, VariableResolverFactory variableFactory) {
        this.property = property;
        this.length = property != null ? property.length : 0;
        this.ctx = ctx;
        this.variableFactory = variableFactory;
    }



    public CompiledAccessor(String property, Object ctx) {
        this.length = (this.property = property.toCharArray()).length;
        this.ctx = ctx;
    }


    public Object compileGetChain() {
        Object curr = ctx;

        try {
            while (cursor < length) {
                switch (nextToken()) {
                    case BEAN:
                        curr = getBeanProperty(curr, capture());
                        break;
                    case METH:
                        curr = getMethod(curr, capture());
                        break;
                    case COL:
                        curr = getCollectionProperty(curr, capture());
                        break;
                    case DONE:
                        break;
                }
            }

            return curr;
        }
        catch (InvocationTargetException e) {
            throw new PropertyAccessException("could not access property", e);
        }
        catch (IllegalAccessException e) {
            throw new PropertyAccessException("could not access property", e);
        }
        catch (IndexOutOfBoundsException e) {
            throw new PropertyAccessException("array or collection index out of bounds (property: " + new String(property) + ")", e);
        }
        catch (PropertyAccessException e) {
            throw new PropertyAccessException("failed to access property: <<" + new String(property) + ">> in: " + (ctx != null ? ctx.getClass() : null), e);
        }
        catch (CompileException e) {
            throw e;
        }
        catch (NullPointerException e) {
            throw new PropertyAccessException("null pointer exception in property: " + new String(property), e);
        }
        catch (Exception e) {
            throw new PropertyAccessException("unknown exception in expression: " + new String(property), e);
        }
    }


    private int nextToken() {
        switch (property[start = cursor]) {
            case'[':
                return COL;
            case'.':
                cursor = ++start;
        }

        //noinspection StatementWithEmptyBody
        while (++cursor < length && Character.isJavaIdentifierPart(property[cursor])) ;


        if (cursor < length) {
            switch (property[cursor]) {
                case'[':
                    return COL;
                case'(':
                    return METH;
                default:
                    return 0;
            }
        }
        return 0;
    }

    private String capture() {
        return new String(property, start, cursor - start);
    }

    public void addAccessorNode(AccessorNode an) {
        if (currNode == null)
            rootNode = currNode = an;
        else {
            currNode = currNode.setNextNode(an);
        }
    }


    private Object getBeanProperty(Object ctx, String property)
            throws IllegalAccessException, InvocationTargetException {

        Class cls = (ctx instanceof Class ? ((Class) ctx) : ctx != null ? ctx.getClass() : null);
        Member member = cls != null ? PropertyTools.getFieldOrAccessor(cls, property) : null;

        if (member instanceof Field) {
            FieldAccessor accessor = new FieldAccessor();
            accessor.setField((Field) member);

            addAccessorNode(accessor);

            return ((Field) member).get(ctx);
        }
        else if (member != null) {
            GetterAccessor accessor = new GetterAccessor((Method) member);
            addAccessorNode(accessor);

            return ((Method) member).invoke(ctx, EMPTYARG);
        }
        else if (ctx instanceof Map && ((Map) ctx).containsKey(property)) {
            MapAccessor accessor = new MapAccessor();
            accessor.setProperty(property);

            addAccessorNode(accessor);

            return ((Map) ctx).get(property);
        }
        else if ("this".equals(property)) {
            ThisValueAccessor accessor = new ThisValueAccessor();

            addAccessorNode(accessor);

            return this.ctx;
        }
        else if (variableFactory != null && variableFactory.isResolveable(property)) {
            VariableAccessor accessor = new VariableAccessor(property, variableFactory);

            addAccessorNode(accessor);

            return variableFactory.getVariableResolver(property).getValue();
        }
        else if (Token.LITERALS.containsKey(property)) {
            StaticReferenceAccessor accessor = new StaticReferenceAccessor();
            accessor.setLiteral(Token.LITERALS.get(property));

            addAccessorNode(accessor);

            return accessor.getLiteral();
        }
        else {
            Class tryStaticMethodRef = tryStaticAccess();

            if (tryStaticMethodRef != null) {
                StaticReferenceAccessor accessor = new StaticReferenceAccessor();
                accessor.setLiteral(tryStaticMethodRef);

                addAccessorNode(accessor);

                return tryStaticMethodRef;
            }
            else
                throw new PropertyAccessException("could not access property (" + property + ")");
        }
    }

    private void whiteSpaceSkip() {
        if (cursor < length)
            //noinspection StatementWithEmptyBody
            while (Character.isWhitespace(property[cursor]) && ++cursor < length) ;
    }

    private boolean scanTo(char c) {
        for (; cursor < length; cursor++) {
            if (property[cursor] == c) {
                return true;
            }
        }
        return false;
    }

    private int containsStringLiteralTermination() {
        int pos = cursor;
        for (pos--; pos > 0; pos--) {
            if (property[pos] == '\'' || property[pos] == '"') return pos;
            else if (!Character.isWhitespace(property[pos])) return pos;
        }
        return -1;
    }


    /**
     * Handle accessing a property embedded in a collection, map, or array
     *
     * @param ctx  -
     * @param prop -
     * @return -
     * @throws Exception -
     */
    private Object getCollectionProperty(Object ctx, String prop) throws Exception {
        if (prop.length() > 0) ctx = getBeanProperty(ctx, prop);

        int start = ++cursor;

        whiteSpaceSkip();

        if (cursor == length)
            throw new PropertyAccessException("unterminated '['");

        String item;

        if (property[cursor] == '\'' || property[cursor] == '"') {
            start++;

            int end;

            if (!scanTo(']'))
                throw new PropertyAccessException("unterminated '['");
            if ((end = containsStringLiteralTermination()) == -1)
                throw new PropertyAccessException("unterminated string literal in collection accessor");

            item = new String(property, start, end - start);
        }
        else {
            if (!scanTo(']'))
                throw new PropertyAccessException("unterminated '['");

            item = new String(property, start, cursor - start);
        }

        ++cursor;

        if (ctx instanceof Map) {
            MapAccessor accessor = new MapAccessor();
            accessor.setProperty(item);

            addAccessorNode(accessor);

            return ((Map) ctx).get(item);
        }
        else if (ctx instanceof List) {
            ListAccessor accessor = new ListAccessor();
            accessor.setIndex(parseInt(item));

            addAccessorNode(accessor);

            return ((List) ctx).get(accessor.getIndex());
        }
        else if (ctx instanceof Collection) {
            int count = parseInt(item);
            if (count > ((Collection) ctx).size())
                throw new PropertyAccessException("index [" + count + "] out of bounds on collection");

            Iterator iter = ((Collection) ctx).iterator();
            for (int i = 0; i < count; i++) iter.next();
            return iter.next();
        }
        else if (ctx instanceof Object[]) {
            ArrayAccessor accessor = new ArrayAccessor();
            accessor.setIndex(parseInt(item));

            addAccessorNode(accessor);

            return ((Object[]) ctx)[accessor.getIndex()];
        }
        else if (ctx instanceof CharSequence) {
            IndexedCharSeqAccessor accessor = new IndexedCharSeqAccessor();
            accessor.setIndex(parseInt(item));

            addAccessorNode(accessor);

            return ((CharSequence) ctx).charAt(accessor.getIndex());
        }
        else {
            throw new PropertyAccessException("illegal use of []: unknown type: " + (ctx == null ? null : ctx.getClass().getName()));
        }
    }

    private static final Map<String, Serializable[]> SUBEXPRESSION_CACHE = new WeakHashMap<String, Serializable[]>();

    /**
     * Find an appropriate method, execute it, and return it's response.
     *
     * @param ctx  -
     * @param name -
     * @return -
     * @throws Exception -
     */
    @SuppressWarnings({"unchecked"})
    private Object getMethod(Object ctx, String name) throws Exception {
        int st = cursor;

        int depth = 1;

        while (cursor++ < length - 1 && depth != 0) {
            switch (property[cursor]) {
                case'(':
                    depth++;
                    continue;
                case')':
                    depth--;

            }
        }
        cursor--;

        String tk = (cursor - st) > 1 ? new String(property, st + 1, cursor - st - 1) : "";

        cursor++;

        Object[] args;
        Serializable[] es;

        if (tk.length() == 0) {
            args = new Object[0];
            es = null;
        }
        else {
            if (SUBEXPRESSION_CACHE.containsKey(tk)) {
                es = SUBEXPRESSION_CACHE.get(tk);
                args = new Object[es.length];
                for (int i = 0; i < es.length; i++) {
                    args[i] = executeExpression(es[i], ctx, variableFactory);
                }

            }
            else {
                String[] subtokens = parseParameterList(tk.toCharArray(), 0, -1);

                es = new Serializable[subtokens.length];
                args = new Object[subtokens.length];
                for (int i = 0; i < subtokens.length; i++) {
                    es[i] = compileExpression(subtokens[i]);
                    args[i] = executeExpression(es[i], this.ctx, variableFactory);
                    ((CompiledExpression) es[i]).setKnownEgressType(args[i] != null ? args[i].getClass() : null);
                }

                SUBEXPRESSION_CACHE.put(tk, es);
            }

        }

        /**
         * If the target object is an instance of java.lang.Class itself then do not
         * adjust the Class scope target.
         */
        Class cls = ctx instanceof Class ? (Class) ctx : ctx.getClass();

        //    Integer signature = ;

        Method m;
        Class[] parameterTypes = null;

        /**
         * If we have not cached the method then we need to go ahead and try to resolve it.
         */
        /**
         * Try to find an instance method from the class target.
         */

        if ((m = ParseTools.getBestCanadidate(args, name, cls.getMethods())) != null) {
            parameterTypes = m.getParameterTypes();
        }

        if (m == null) {
            /**
             * If we didn't find anything, maybe we're looking for the actual java.lang.Class methods.
             */
            if ((m = ParseTools.getBestCanadidate(args, name, cls.getClass().getDeclaredMethods())) != null) {
                parameterTypes = m.getParameterTypes();
            }
        }


        if (m == null) {
            StringBuilder errorBuild = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                errorBuild.append(args[i] != null ? args[i].getClass().getName() : null);
                if (i < args.length - 1) errorBuild.append(", ");
            }

            throw new PropertyAccessException("unable to resolve method: " + cls.getName() + "." + name + "(" + errorBuild.toString() + ") [arglength=" + args.length + "]");
        }
        else {
            if (es != null) {
                CompiledExpression cExpr;
                for (int i = 0; i < es.length; i++) {
                    cExpr = ((CompiledExpression) es[i]);
                    if (cExpr.getKnownIngressType() == null) {
                        cExpr.setKnownIngressType(parameterTypes[i]);
                        cExpr.pack();
                    }
                    if (!cExpr.isConvertableIngressEgress()) {
                        args[i] = DataConversion.convert(args[i], parameterTypes[i]);
                    }
                }
            }
            else {
                /**
                 * Coerce any types if required.
                 */
                for (int i = 0; i < args.length; i++)
                    args[i] = DataConversion.convert(args[i], parameterTypes[i]);
            }


            MethodAccessor access = new MethodAccessor();
            access.setMethod(m);
            access.setCompiledParameters(es);

            addAccessorNode(access);

            /**
             * Invoke the target method and return the response.
             */
            return m.invoke(ctx, args);
        }
    }


    public Object getValue(Object ctx, Object elCtx, VariableResolverFactory variableFactory) throws Exception {
        return rootNode.getValue(ctx, elCtx, variableFactory);
    }

    private Class tryStaticAccess() {
        try {
            /**
             * Try to resolve this *smartly* as a static class reference.
             *
             * This starts at the end of the token and starts to step backwards to figure out whether
             * or not this may be a static class reference.  We search for method calls simply by
             * inspecting for ()'s.  The first union area we come to where no brackets are present is our
             * test-point for a class reference.  If we find a class, we pass the reference to the
             * property accessor along  with trailing methods (if any).
             *
             */
            boolean meth = false;
            int depth = 0;
            int last = property.length;
            for (int i = property.length - 1; i > 0; i--) {
                switch (property[i]) {
                    case'.':
                        if (!meth) {
                            return forName(new String(property, 0, last));
                        }

                        meth = false;
                        last = i;
                        break;
                    case')':
                        if (depth++ == 0)
                            meth = true;
                        break;
                    case'(':
                        depth--;
                        break;
                }
            }
        }
        catch (Exception cnfe) {
            // do nothing.
        }

        return null;
    }

}
