package org.drools.mvel.asm;

public interface InvokerStub extends InvokerDataProvider {
    String getGeneratedInvokerClassName();
    String[] getExpectedDeclarationTypes();
    String[] getPackageImports();
}
