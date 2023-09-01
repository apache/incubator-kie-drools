package org.drools.compiler.compiler;

import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;

public class DuplicateRule extends ConfigurableSeverityResult {
    
    public static final String KEY = "duplicateRule";
    
    private String rule;
    
    private PackageDescr pkgDescr;
    
    private int[] line;
    
    public DuplicateRule(RuleDescr ruleDescr, PackageDescr pkg, KnowledgeBuilderConfiguration config) {
        super(ruleDescr.getResource(), config);
        rule = ruleDescr.getName();
        pkgDescr = pkg;
        line = new int[1];
        line[0] = ruleDescr.getLine();
    }

	@Override
	public String getMessage() {
		return "Rule name " + rule 
        + " already exists in package  " + pkgDescr.getName();
	}

	@Override
	public int[] getLines() {
		return line;
	}

    @Override
    protected String getOptionKey() {
        return KEY;
    }

}
