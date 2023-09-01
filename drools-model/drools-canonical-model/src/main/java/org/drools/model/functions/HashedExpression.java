package org.drools.model.functions;

/**
 * Represents a lambda expression with a stable fingerprint.
 * This field is used by org.drools.model.functions.IntrospectableLambda to leverage some features
 * such as FromNode sharing.
 * See org.drools.compiler.integrationtests.operators.FromTest#testFromSharing
 * and org.drools.compiler.integrationtests.operators.FromOnlyExecModelTest#testFromSharingWithNativeImage
 */
public interface HashedExpression {

    String getExpressionHash();

}
