package org.drools.core.rule.builder.dialect.asm;

public interface InvokerStub extends InvokerDataProvider {
    String getGeneratedInvokerClassName();
    String[] getExpectedDeclarationTypes();
    String[] getPackageImports();
}
