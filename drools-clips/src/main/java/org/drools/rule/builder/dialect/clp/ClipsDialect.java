package org.drools.rule.builder.dialect.clp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.TypeResolver;
import org.drools.compiler.Dialect;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.CollectDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.ForallDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;
import org.drools.rule.builder.AccumulateBuilder;
import org.drools.rule.builder.ActionBuilder;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.EntryPointBuilder;
import org.drools.rule.builder.FromBuilder;
import org.drools.rule.builder.GroupElementBuilder;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.rule.builder.PatternBuilder;
import org.drools.rule.builder.PredicateBuilder;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.rule.builder.ProcessClassBuilder;
import org.drools.rule.builder.QueryBuilder;
import org.drools.rule.builder.ReturnValueBuilder;
import org.drools.rule.builder.ReturnValueEvaluatorBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleClassBuilder;
import org.drools.rule.builder.RuleConditionBuilder;
import org.drools.rule.builder.SalienceBuilder;
import org.drools.rule.builder.dialect.mvel.MVELDialectConfiguration;
public class ClipsDialect implements Dialect {

    public final static String                ID                      = "clips";

    private final static String               EXPRESSION_DIALECT_NAME = "mvel";    
    
    private final PatternBuilder              pattern                 = new PatternBuilder();    
    private final ClpConsequenceBuilder      consequence             = new ClpConsequenceBuilder();    
    
    private Package                           pkg;
    private ClipsDialectConfiguration          configuration;
    private TypeResolver                      typeResolver;
    private ClassFieldExtractorCache          classFieldExtractorCache;

    // a map of registered builders
    private Map builders;    
    
    public void init(PackageBuilder builder) {
        this.pkg = builder.getPackage();
        this.configuration = (ClipsDialectConfiguration) builder.getPackageBuilderConfiguration().getDialectConfiguration( "clips" );
        this.typeResolver = builder.getTypeResolver();
        this.classFieldExtractorCache = builder.getClassFieldExtractorCache();           
        
        initBuilder();
    }
    
    public void initBuilder() {
        // statically adding all builders to the map
        // but in the future we can move that to a configuration
        // if we want to
        this.builders = new HashMap();

        final GroupElementBuilder gebuilder = new GroupElementBuilder();

        this.builders.put( AndDescr.class,
                           gebuilder );

        this.builders.put( OrDescr.class,
                           gebuilder );

        this.builders.put( NotDescr.class,
                           gebuilder );

        this.builders.put( ExistsDescr.class,
                           gebuilder );

        this.builders.put( PatternDescr.class,
                           getPatternBuilder() );

//        this.builders.put( FromDescr.class,
//                           getFromBuilder() );
//
//        this.builders.put( QueryDescr.class,
//                           getQueryBuilder() );
//
//        this.builders.put( AccumulateDescr.class,
//                           getAccumulateBuilder() );
//
//        this.builders.put( EvalDescr.class,
//                           getEvalBuilder() );
//        
//        this.builders.put( CollectDescr.class,
//                           collect );
//
//        this.builders.put( ForallDescr.class,
//                           forall );
//
//        this.builders.put( FunctionDescr.class,
//                           function );
   }    

    public void init(Package pkg) {
        // TODO Auto-generated method stub
        
    }

    public void init(RuleDescr ruleDescr) {
        // TODO Auto-generated method stub
        
    }    
    
    public void addFunction(FunctionDescr functionDescr,
                            TypeResolver typeResolver) {
        // TODO Auto-generated method stub        
    }

    public void addImport(String importEntry) {
        // TODO Auto-generated method stub
        
    }

    public void addRule(RuleBuildContext context) {
        // TODO Auto-generated method stub
        
    }

    public void addStaticImport(String importEntry) {
        // TODO Auto-generated method stub
        
    }

    public AnalysisResult analyzeBlock(RuleBuildContext context,
                                       BaseDescr descr,
                                       String text) {
        // TODO Auto-generated method stub
        return null;
    }

    public AnalysisResult analyzeExpression(RuleBuildContext context,
                                            BaseDescr descr,
                                            Object content) {
        // TODO Auto-generated method stub
        return null;
    }

    public void compileAll() {
        // TODO Auto-generated method stub
        
    }

    public AccumulateBuilder getAccumulateBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    public RuleConditionBuilder getBuilder(final Class clazz) {
        return (RuleConditionBuilder) this.builders.get( clazz );
    }

    public Map getBuilders() {
        return this.builders;
    }

    public ClassFieldExtractorCache getClassFieldExtractorCache() {
        return this.classFieldExtractorCache;
    }

    public ConsequenceBuilder getConsequenceBuilder() {
        return consequence;
    }

    public RuleConditionBuilder getEvalBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getExpressionDialectName() {
        // TODO Auto-generated method stub
        return null;
    }

    public FromBuilder getFromBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getId() {
        return ID;
    }

    public PatternBuilder getPatternBuilder() {
        return this.pattern;
    }

    public PredicateBuilder getPredicateBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    public QueryBuilder getQueryBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    public List getResults() {
        return new ArrayList();
    }

    public ReturnValueBuilder getReturnValueBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    public RuleClassBuilder getRuleClassBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    public SalienceBuilder getSalienceBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    public TypeResolver getTypeResolver() {
        return this.typeResolver;
    }

    public void addProcess(ProcessBuildContext context) {
        // TODO Auto-generated method stub
        
    }

    public AnalysisResult analyzeBlock(PackageBuildContext context,
                                       BaseDescr descr,
                                       String text,
                                       Set[] availableIdentifiers) {
        // TODO Auto-generated method stub
        return null;
    }

    public AnalysisResult analyzeExpression(PackageBuildContext context,
                                            BaseDescr descr,
                                            Object content,
                                            Set[] availableIdentifiers) {
        // TODO Auto-generated method stub
        return null;
    }

    public ActionBuilder getActionBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    public EntryPointBuilder getEntryPointBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    public ProcessClassBuilder getProcessClassBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    public ReturnValueEvaluatorBuilder getReturnValueEvaluatorBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    public void init(ProcessDescr processDescr) {
        // TODO Auto-generated method stub
        
    }

}
