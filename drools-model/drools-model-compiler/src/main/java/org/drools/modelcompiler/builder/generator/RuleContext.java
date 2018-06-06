package org.drools.modelcompiler.builder.generator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.BaseKnowledgeBuilderResultImpl;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.ruleunit.RuleUnitDescr;
import org.drools.core.util.Bag;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.modelcompiler.builder.PackageModel;
import org.kie.api.definition.type.ClassReactive;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

public class RuleContext {

    private static final Map<Class<? extends RuleUnit>, RuleUnitDescr> ruleUnitDescrCache = new HashMap<>();

    private final KnowledgeBuilderImpl kbuilder;
    private final PackageModel packageModel;
    private final TypeResolver typeResolver;
    private DRLIdGenerator idGenerator;
    private final RuleDescr descr;
    private final boolean generatePatternDSL;

    private List<DeclarationSpec> declarations = new ArrayList<>();
    private List<DeclarationSpec> ooPathDeclarations = new ArrayList<>();
    private Deque<Consumer<Expression>> exprPointer = new LinkedList<>();
    private List<Expression> expressions = new ArrayList<>();
    private Map<String, String> namedConsequences = new HashMap<>();

    private List<QueryParameter> queryParameters = new ArrayList<>();
    private Optional<String> queryName = empty();

    private RuleUnitDescr ruleUnitDescr;

    private Map<String, String> aggregatePatternMap = new HashMap<>();

    /* These are used to check if some binding used in an OR expression is used in every branch */
    private Boolean isNestedInsideOr = false;
    private Bag<String> bindingOr = new Bag<>();
    private Set<String> unusableOrBinding = new HashSet<>();

    private RuleDialect ruleDialect = RuleDialect.JAVA; // assumed is java by default as per Drools manual.
    public enum RuleDialect {
        JAVA,
        MVEL;
    }

    public BaseDescr parentDesc = null;

    public RuleContext(KnowledgeBuilderImpl kbuilder, PackageModel packageModel, RuleDescr ruleDescr, TypeResolver typeResolver, boolean generatePatternDSL) {
        this.kbuilder = kbuilder;
        this.packageModel = packageModel;
        this.idGenerator = packageModel.getExprIdGenerator();
        this.descr = ruleDescr;
        exprPointer.push( this.expressions::add );
        this.typeResolver = typeResolver;
        this.generatePatternDSL = generatePatternDSL;
        findUnitClass();
    }

    private void findUnitClass() {
        if (descr == null) {
            return;
        }

        String unitName = null;
        AnnotationDescr unitAnn = descr.getAnnotation( "Unit" );
        if (unitAnn != null) {
            unitName = ( String ) unitAnn.getValue();
            unitName = unitName.substring( 0, unitName.length() - ".class".length() );
        } else if (descr.getUnit() != null) {
            unitName = descr.getUnit().getTarget();
        }

        if (unitName != null) {
            try {
                Class<? extends RuleUnit> unitClass = ( Class<? extends RuleUnit> ) getTypeResolver().resolveType( unitName );
                ruleUnitDescr = ruleUnitDescrCache.computeIfAbsent( unitClass, RuleUnitDescr::new );
            } catch (ClassNotFoundException e) {
                throw new RuntimeException( e );
            }
        }
    }

    public boolean isPatternDSL() {
        return generatePatternDSL;
    }

    public RuleUnitDescr getRuleUnitDescr() {
        return ruleUnitDescr;
    }

    public KnowledgeBuilderImpl getKbuilder() {
        return kbuilder;
    }

    public void addCompilationError( KnowledgeBuilderResult error ) {
        if ( error instanceof BaseKnowledgeBuilderResultImpl ) {
            (( BaseKnowledgeBuilderResultImpl ) error).setResource( descr.getResource() );
        }
        kbuilder.addBuilderResult( error );
    }

    public boolean hasErrors() {
        return kbuilder.hasResults( ResultSeverity.ERROR );
    }

    public Optional<DeclarationSpec> getDeclarationById(String id) {
        return declarations.stream().filter(d -> d.getBindingId().equals(id)).findFirst();
    }

    public void removeDeclarationById(String id) {
        final Optional<DeclarationSpec> declarationById = getDeclarationById(id);
        declarationById.map(declarations::remove).orElseThrow(() -> new RuntimeException("Cannot find id: " + id));
    }


    public boolean hasDeclaration(String id) {
        return getDeclarationById(id).isPresent();
    }

    public void addGlobalDeclarations(Map<String, Class<?>> globals) {
        for(Map.Entry<String, Class<?>> ks : globals.entrySet()) {
            addDeclaration(new DeclarationSpec(ks.getKey(), ks.getValue()));
        }
    }

    public Collection<String> getAvailableBindings() {
        return declarations.stream().map( DeclarationSpec::getBindingId ).collect( toList() );
    }

    public Optional<DeclarationSpec> getOOPathDeclarationById(String id) {
        return ooPathDeclarations.stream().filter(d -> d.getBindingId().equals(id)).findFirst();
    }

    public void addDeclaration(DeclarationSpec d) {
        // It would be probably be better to avoid putting the same declaration multiple times
        // instead of using Set semantic here
        if(!getDeclarationById(d.getBindingId()).isPresent()) {
            this.declarations.add(d);
        }
    }

    public void addDeclarationReplacing(DeclarationSpec d) {
        // It would be probably be better to avoid putting the same declaration multiple times
        // instead of using Set semantic here
        final String bindingId = d.getBindingId();
        final Optional<DeclarationSpec> declarationById = getDeclarationById(bindingId);
        if(declarationById.isPresent()) {
            removeDeclarationById(bindingId);
        }
        this.declarations.add(d);
    }

    public void addOOPathDeclaration(DeclarationSpec d) {
        if(!getOOPathDeclarationById(d.getBindingId()).isPresent()) {
            this.ooPathDeclarations.add(d);
        }
    }

    public List<DeclarationSpec> getDeclarations() {
        return declarations;
    }

    public List<DeclarationSpec> getOOPathDeclarations() {
        return ooPathDeclarations;
    }

    public void addExpression(Expression e) {
        exprPointer.peek().accept(e);
    }
    public void pushExprPointer(Consumer<Expression> p) {
        exprPointer.push(p);
    }
    public Consumer<Expression> popExprPointer() {
        return exprPointer.pop();
    }
    public int getExprPointerLevel() {
        return exprPointer.size();
    }

    public String getExprId(Class<?> patternType, String drlConstraint) {
        return idGenerator.getExprId(patternType, drlConstraint);
    }

    public String getConditionId(Class<?> patternType, String drlConstraint) {
        return idGenerator.getCondId(patternType, drlConstraint);
    }

    public String getOOPathId(Class<?> patternType, String drlConstraint) {
        return idGenerator.getOOPathId(patternType, drlConstraint);
    }

    public String getOrCreateUnificationId(String drlConstraint) {
        return idGenerator.getOrCreateUnificationVariable(drlConstraint);
    }

    public Optional<String> getUnificationId(String drlConstraint) {
        return idGenerator.getUnificationVariable(drlConstraint);
    }

    public void addNamedConsequence(String key, String value) {
        namedConsequences.put(key, value);
    }

    public RuleDescr getRuleDescr() {
        return descr;
    }

    public RuleDialect getRuleDialect() {
        return ruleDialect;
    }

    public void setRuleDialect(RuleDialect ruleDialect) {
        this.ruleDialect = ruleDialect;
    }

    public Optional<QueryParameter> queryParameterWithName(Predicate<? super QueryParameter> predicate) {
        return queryParameters.stream().filter(predicate).findFirst();
    }

    public List<QueryParameter> getQueryParameters() {
        return queryParameters;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public Optional<String> getQueryName() {
        return queryName;
    }

    public void setQueryName(Optional<String> queryName) {
        this.queryName = queryName;
    }

    public Map<String, String> getNamedConsequences() {
        return namedConsequences;
    }

    public Map<String, String> getAggregatePatternMap() {
        return aggregatePatternMap;
    }

    public PackageModel getPackageModel() {
        return packageModel;
    }

    public TypeResolver getTypeResolver() {
        return typeResolver;
    }

    public boolean isPropertyReactive( Class<?> patternClass) {
        PropertySpecificOption propertySpecificOption = kbuilder.getBuilderConfiguration().getPropertySpecificOption();
        return propertySpecificOption.isPropSpecific( patternClass.getAnnotation( PropertyReactive.class ) != null,
                                                      patternClass.getAnnotation( ClassReactive.class ) != null );
    }

    public Optional<Class<?>> getFunctionType(String name) {
        Method m = packageModel.getStaticMethod(name);
        if (m != null) {
            return of(m.getReturnType());
        }

        return packageModel.getFunctions().stream()
                .filter( method -> method.getNameAsString().equals( name ) )
                .findFirst()
                .flatMap( method -> resolveType( method.getType().asString() ) );
    }

    public Optional<Class<?>> resolveType(String name) {
        try {
            return of( typeResolver.resolveType( name ) );
        } catch(ClassNotFoundException e) {
            return empty();
        }
    }
  
    public Boolean isNestedInsideOr() {
        return isNestedInsideOr;
    }

    public void setNestedInsideOr(Boolean nestedInsideOr) {
        isNestedInsideOr = nestedInsideOr;
    }

    public Bag<String> getBindingOr() {
        return bindingOr;
    }

    public Set<String> getUnusableOrBinding() {
        return unusableOrBinding;
    }
}

