package org.drools.rule.builder;

import org.drools.compiler.Dialect;
import org.drools.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.PackageBuilder;
import org.drools.definition.process.Process;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.rule.Package;

public class ProcessBuildContext extends PackageBuildContext {
	
    private Process      process;
    private ProcessDescr processDescr;
    private DialectCompiletimeRegistry dialectRegistry;

    public ProcessBuildContext(final PackageBuilder pkgBuilder,
                               final Package pkg,
                               final Process process,
                               final BaseDescr processDescr,
                               final DialectCompiletimeRegistry dialectRegistry,
                               final Dialect defaultDialect) {
        this.process = process;
        this.processDescr = (ProcessDescr) processDescr;
        this.dialectRegistry = dialectRegistry;
        init( pkgBuilder,
              pkg,
              processDescr,
              dialectRegistry,
              defaultDialect,
              null );

    }

    public ProcessDescr getProcessDescr() {
        return processDescr;
    }

    public void setProcessDescr(ProcessDescr processDescr) {
        this.processDescr = processDescr;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
    
    public DialectCompiletimeRegistry getDialectRegistry() {
    	return dialectRegistry;
    }

}
