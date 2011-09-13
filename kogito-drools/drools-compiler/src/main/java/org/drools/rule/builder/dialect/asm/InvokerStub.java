package org.drools.rule.builder.dialect.asm;

public interface InvokerStub extends InvokerDataProvider {
    String getGeneratedInvokerClassName();
    String[] getPackageImports();
}
