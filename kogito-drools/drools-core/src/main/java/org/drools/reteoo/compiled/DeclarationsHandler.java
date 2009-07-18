package org.drools.reteoo.compiled;

import org.drools.base.ClassFieldReader;
import org.drools.reteoo.AlphaNode;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.Sink;
import org.drools.rule.ContextEntry;

import java.util.*;

/**
 * This handler is used to create the member declarations section of a generated subclass of a {@link CompiledNetwork}.
 * Currently we only create member variables for the following types of nodes:
 * <p/>
 * <li>Non-hashed {@link AlphaNode}s</li>
 * <li>{@link LeftInputAdapterNode}s</li>
 * <li>{@link BetaNode}s</li>
 * <li>A {@link Map} for each set of hashed {@link AlphaNode}s. The keys are the hashed values, and the values are
 * the IDs of the alphas</li>
 *
 * @author <a href="mailto:stampy88@yahoo.com">dave sinclair</a>
 */
class DeclarationsHandler extends AbstractCompilerHandler {
    private static final String PRIVATE_MODIFIER = "private";

    /**
     * This field keeps track of the current set of hashed AlphaNodes for a ClassReader.
     *
     * @see #startHashedAlphaNodes(org.drools.base.ClassFieldReader)
     * @see #startHashedAlphaNode(org.drools.reteoo.AlphaNode, Object)
     */
    private HashedAlphasDeclaration currentHashedAlpha;

    private final StringBuilder builder;

    /**
     * Keeps track of all the ClassFieldReaders for hashed alphas, and the maps that contain hashed values/node ids
     * for said alphas.
     */
    private final Collection<HashedAlphasDeclaration> hashedAlphaDeclarations;

    DeclarationsHandler(StringBuilder builder) {
        this.builder = builder;
        this.hashedAlphaDeclarations = new LinkedList<HashedAlphasDeclaration>();
    }

    private String getVariableDeclaration(AlphaNode alphaNode) {
        Class<?> variableType = getVariableType(alphaNode);
        String variableName = getVariableName(alphaNode);
        // comment for variable declaration is just the toString of the node
        String comment = alphaNode.toString();

        return PRIVATE_MODIFIER + " " + variableType.getName() + " " + variableName + "; // " + comment;
    }

    private String getContextVariableDeclaration(AlphaNode alphaNode){
        Class<?> variableType = ContextEntry.class;
        String variableName = getContextVariableName(alphaNode);

        return PRIVATE_MODIFIER + " " + variableType.getName() + " " + variableName + ";";
    }

    private String getVariableDeclaration(Sink sink) {
        Class<?> declarationType = getVariableType(sink);
        String variableName = getVariableName(sink);
        // comment for variable declaration is just the toString of the node
        String comment = sink.toString();

        return PRIVATE_MODIFIER + " " + declarationType.getName() + " " + variableName + "; // " + comment;
    }

    private String getVariableDeclaration(ClassFieldReader fieldReader) {
        Class<?> declarationType = Map.class;
        Class<?> createType = HashMap.class;
        String variableName = getVariableName(fieldReader);

        // todo JANINO doesn't support generics
        // return "private java.util.Map<Object,Integer> " + variableName + " = new java.util.HashMap<Object,Integer>();";
        return PRIVATE_MODIFIER + " " + declarationType.getName() + " " + variableName
                + " = new " + createType.getName() + "();";
    }

    Collection<HashedAlphasDeclaration> getHashedAlphaDeclarations() {
        return Collections.unmodifiableCollection(hashedAlphaDeclarations);
    }

    @Override
    public void startNonHashedAlphaNode(AlphaNode alphaNode) {
        builder.append(getVariableDeclaration(alphaNode)).append(NEWLINE);
        builder.append(getContextVariableDeclaration(alphaNode)).append(NEWLINE);        
    }

    @Override
    public void startBetaNode(BetaNode betaNode) {
        builder.append(getVariableDeclaration(betaNode)).append(NEWLINE);
    }

    @Override
    public void startLeftInputAdapterNode(LeftInputAdapterNode leftInputAdapterNode) {
        builder.append(getVariableDeclaration(leftInputAdapterNode)).append(NEWLINE);
    }

    @Override
    public void startHashedAlphaNodes(ClassFieldReader hashedFieldReader) {
        // we create a new hashed alpha that will be used to keep track of the hashes values to node ID for each
        // class field reader.
        currentHashedAlpha = new HashedAlphasDeclaration(getVariableName(hashedFieldReader),
                hashedFieldReader.getValueType());

        // add the new declaration
        hashedAlphaDeclarations.add(currentHashedAlpha);

        builder.append(getVariableDeclaration(hashedFieldReader)).append(NEWLINE);
    }

    @Override
    public void startHashedAlphaNode(AlphaNode hashedAlpha, Object hashedValue) {
        currentHashedAlpha.add(hashedValue, String.valueOf(hashedAlpha.getId()));
    }
}
