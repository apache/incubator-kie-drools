package org.drools.semantics.java;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.AngleBracketTemplateLexer;
import org.drools.RuntimeDroolsException;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.EvaluatorFactory;
import org.drools.base.FieldFactory;
import org.drools.base.FieldImpl;
import org.drools.compiler.RuleError;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.BoundVariableDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.LiteralDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.ReturnValueDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.And;
import org.drools.rule.BoundVariableConstraint;
import org.drools.rule.Column;
import org.drools.rule.GroupElement;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.Exists;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Not;
import org.drools.rule.Or;
import org.drools.rule.Package;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.Query;
import org.drools.rule.ReturnValueConstraint;
import org.drools.rule.Rule;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;
import org.drools.spi.TypeResolver;

public class RuleBuilder {
    private Package                     pkg;
    private Rule                        rule;
    private RuleDescr                   ruleDescr;

    public String                       ruleClass;
    public List                         methods;
    public Map                          invokers;

    private Map                         invokerLookups;

    private Map                         descrLookups;

    private Map                         declarations;

    private int                         counter;

    private int                         columnCounter;

    private int                         columnOffset;

    private List                        errors;

    private TypeResolver                typeResolver;    
    
    private Map                         notDeclarations;

    private static StringTemplateGroup  ruleGroup    = new StringTemplateGroup( new InputStreamReader( RuleBuilder.class.getResourceAsStream( "javaRule.stg" ) ),
                                                                                AngleBracketTemplateLexer.class );

    private static StringTemplateGroup  invokerGroup = new StringTemplateGroup( new InputStreamReader( RuleBuilder.class.getResourceAsStream( "javaInvokers.stg" ) ),
                                                                                AngleBracketTemplateLexer.class );

    private static KnowledgeHelperFixer knowledgeHelperFixer        = new KnowledgeHelperFixer();
    private static FunctionFixer functionFixer                      = new FunctionFixer();

    // @todo move to an interface so it can work as a decorator
    private JavaExprAnalyzer            analyzer     = new JavaExprAnalyzer();

    public RuleBuilder() {
        this.errors = new ArrayList();
    }

    public Map getInvokers() {
        return this.invokers;
    }

    public List getMethods() {
        return this.methods;
    }

    public Map getDescrLookups() {
        return this.descrLookups;
    }

    public String getRuleClass() {
        return this.ruleClass;
    }

    public Map getInvokerLookups() {
        return this.invokerLookups;
    }

    public List getErrors() {
        return this.errors;
    }

    public Rule getRule() {
        if (!this.errors.isEmpty()) {
            this.rule.setSemanticallyValid( false );
        }
        return this.rule;
    }

    public Package getPackage() {
        return this.pkg;
    }

    public synchronized Rule build(Package pkg,
                                   RuleDescr ruleDescr) {
        this.pkg = pkg;
        this.methods = new ArrayList();
        this.invokers = new HashMap();
        this.invokerLookups = new HashMap();
        this.declarations = new HashMap();
        this.descrLookups = new HashMap();

        this.typeResolver = new ClassTypeResolver( pkg.getImports(),
                                                   pkg.getPackageCompilationData().getClassLoader() );

        this.ruleDescr = ruleDescr;
        
        if ( ruleDescr instanceof QueryDescr ) {
            this.rule = new Query( ruleDescr.getName() );
        } else {
            this.rule = new Rule( ruleDescr.getName() );            
        }
        

        // Assign attributes
        setAttributes( rule,
                       ruleDescr.getAttributes() );

        // Build the left hand side
        // generate invoker, methods
        build( ruleDescr );
        
        return rule;
    }

    private void setAttributes(Rule rule,
                               List attributes) {
        for ( Iterator it = attributes.iterator(); it.hasNext(); ) {
            AttributeDescr attributeDescr = (AttributeDescr) it.next();
            String name = attributeDescr.getName();
            if ( name.equals( "salience" ) ) {
                rule.setSalience( Integer.parseInt( attributeDescr.getValue() ) );
            } else if ( name.equals( "no-loop" ) ) {
                if ( attributeDescr.getValue() == null ) {
                    rule.setNoLoop( true );
                } else {
                    rule.setNoLoop( Boolean.valueOf( attributeDescr.getValue() ).booleanValue() );
                }
            } else if ( name.equals( "auto-focus" ) ) {
                if ( attributeDescr.getValue() == null ) {
                    rule.setAutoFocus( true );
                } else {
                    rule.setAutoFocus( Boolean.valueOf( attributeDescr.getValue() ).booleanValue() );
                }                
            } else if ( name.equals( "agenda-group" ) ) {
                rule.setAgendaGroup( attributeDescr.getValue() );
            } else if ( name.equals( "duration" ) ) {
                rule.setDuration( Long.parseLong( attributeDescr.getValue() ) );
                rule.setAgendaGroup( "" );
            } else if ( name.equals( "language" ) ) {
                //@todo: we don't currently  support multiple languages
            }
        }
    }

    private void build(RuleDescr ruleDescr) {
        
        for ( Iterator it = ruleDescr.getLhs().getDescrs().iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof ConditionalElementDescr ) {
                if ( object instanceof AndDescr ) {
                    And and = new And();                    
                    //rule.addChild( and );
                    build( rule,
                           (ConditionalElementDescr) object,
                           and,
                           false );
                    rule.addPattern( and );
                } else if ( object instanceof OrDescr ) {
                    Or or = new Or();
                    build( rule,
                           (ConditionalElementDescr) object,
                           or,
                           false );
                    rule.addPattern( or );
                } else if ( object instanceof NotDescr ) {
                    // We cannot have declarations created inside a not visible outside it, so track no declarations so they can be removed
                    this.notDeclarations = new HashMap();
                    Not not = new Not();
                    build( rule,
                           (ConditionalElementDescr) object,
                           not,
                           true );
                    rule.addPattern( not );
                    
                    // remove declarations bound inside not node
                    for ( Iterator notIt = this.notDeclarations.keySet().iterator(); notIt.hasNext(); ) {
                        this.declarations.remove( notIt.next() );
                    }
                    
                    this.notDeclarations = null;
                } else if ( object instanceof ExistsDescr ) {
                    // We cannot have declarations created inside a not visible outside it, so track no declarations so they can be removed
                    this.notDeclarations = new HashMap();                    
                    Exists exists = new Exists();
                    build( rule,
                           (ConditionalElementDescr) object,
                           exists,
                           true );
                    // remove declarations bound inside not node
                    for ( Iterator notIt = this.notDeclarations.keySet().iterator(); notIt.hasNext(); ) {
                        this.declarations.remove( notIt.next() );
                    }
                    
                    this.notDeclarations = null;                    
                    rule.addPattern( exists );                    
                } else if ( object instanceof EvalDescr ) {
                    EvalCondition eval = build( (EvalDescr) object );
                    if ( eval != null ) {
                        rule.addPattern( eval );                        
                    }                        
                }
            } else if ( object instanceof ColumnDescr ) {
                Column column = build( (ColumnDescr) object );
                if ( column != null ) {
                    rule.addPattern( column );
                }
            }
        }        
        
        // Build the consequence and generate it's invoker/methods
        // generate the main rule from the previously generated methods.
        if ( !( ruleDescr instanceof QueryDescr ) ) {
            // do not build the consequence if we have a query
            buildConsequence( ruleDescr );
        }
        buildRule( ruleDescr );        
    }    

    private void build(Rule rule,
                       ConditionalElementDescr descr,
                       GroupElement ce,
                       boolean decrementOffset) {
        for ( Iterator it = descr.getDescrs().iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof ConditionalElementDescr ) {
                if ( object instanceof AndDescr ) {
                    And and = new And();
                    ce.addChild( and );
                    build( rule,
                           (ConditionalElementDescr) object,
                           and,
                           decrementOffset );
                } else if ( object instanceof OrDescr ) {
                    Or or = new Or();
                    ce.addChild( or );
                    build( rule,
                           (ConditionalElementDescr) object,
                           or,
                           decrementOffset );
                } else if ( object instanceof NotDescr ) {
                    Not not = new Not();
                    ce.addChild( not );
                    build( rule,
                           (ConditionalElementDescr) object,
                           not,
                           true );
                } else if ( object instanceof ExistsDescr ) {
                    Exists exists = new Exists();
                    ce.addChild( exists );
                    build( rule,
                           (ConditionalElementDescr) object,
                           exists,
                           true );
                } else if ( object instanceof EvalDescr ) {
                    EvalCondition eval = build( (EvalDescr) object );
                    if ( eval != null ) {
                        ce.addChild( eval );
                    }
                }
            } else if ( object instanceof ColumnDescr ) {
                if( decrementOffset ) {
                    this.columnOffset--;
                }
                Column column = build( (ColumnDescr) object );
                if ( column != null ) {
                    ce.addChild( column );
                }
            }
        }
    }

    private Column build(ColumnDescr columnDescr) {
        if ( columnDescr.getObjectType() == null || columnDescr.getObjectType().equals( "" ) ) {
            this.errors.add( new RuleError( this.rule,
                                             columnDescr,
                                             null,
                                             "ObjectType not correctly defined" ) );
            return null;
        }

        Class clazz = null;

        try {
            //clazz = Class.forName( columnDescr.getObjectType() );
            clazz = typeResolver.resolveType( columnDescr.getObjectType() );
        } catch ( ClassNotFoundException e ) {
            this.errors.add( new RuleError( this.rule,
                                             columnDescr,
                                             null,
                                             "Unable to resolve ObjectType '" + columnDescr.getObjectType() + "'" ) );
            return null;
        }

        Column column;
        if ( columnDescr.getIdentifier() != null && !columnDescr.getIdentifier().equals( "" ) ) {
            column = new Column( columnCounter++,
                                 this.columnOffset,
                                 new ClassObjectType( clazz ),
                                 columnDescr.getIdentifier() );;
            this.declarations.put( column.getDeclaration().getIdentifier(),
                                   column.getDeclaration() );
            
            if ( this.notDeclarations != null ) {
                this.notDeclarations.put( column.getDeclaration().getIdentifier(), 
                                          column.getDeclaration() );
            }            
        } else {
            column = new Column( columnCounter++,
                                 this.columnOffset,
                                 new ClassObjectType( clazz ),
                                 null);
        }

        for ( Iterator it = columnDescr.getDescrs().iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof FieldBindingDescr ) {
                build( column,
                       (FieldBindingDescr) object );
            } else if ( object instanceof LiteralDescr ) {
                build( column,
                       (LiteralDescr) object );
            } else if ( object instanceof BoundVariableDescr ) {
                build( column,
                       (BoundVariableDescr) object );
            } else if ( object instanceof ReturnValueDescr ) {
                build( column,
                       (ReturnValueDescr) object );
            } else if ( object instanceof PredicateDescr ) {
                build( column,
                       (PredicateDescr) object );
            }
        }
        return column;
    }

    private void build(Column column,
                       FieldBindingDescr fieldBindingDescr) {
        Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        FieldExtractor extractor = getFieldExtractor( fieldBindingDescr,
                                                      clazz,
                                                      fieldBindingDescr.getFieldName() );
        if ( extractor == null ) {
            return;
        }

        Declaration declaration = column.addDeclaration( fieldBindingDescr.getIdentifier(),
                                                         extractor );

        this.declarations.put( declaration.getIdentifier(),
                               declaration );
        
        if ( this.notDeclarations != null ) {
            this.notDeclarations.put( declaration.getIdentifier(), 
                                      declaration );
        }
    }

    private void build(Column column,
                       BoundVariableDescr boundVariableDescr) {
        if ( boundVariableDescr.getIdentifier() == null || boundVariableDescr.getIdentifier().equals( "" ) ) {
            this.errors.add( new RuleError( this.rule,
                                             boundVariableDescr,
                                             null,
                                             "Identifier not defined for binding field '" + boundVariableDescr.getFieldName() + "'" )  );
            return;
        }

        Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        FieldExtractor extractor = getFieldExtractor( boundVariableDescr,
                                                      clazz,
                                                      boundVariableDescr.getFieldName() );
        if ( extractor == null ) {
            return;
        }

        Declaration declaration = (Declaration) this.declarations.get( boundVariableDescr.getIdentifier() );

        if ( declaration == null ) {
            this.errors.add( new RuleError( this.rule,
                                             boundVariableDescr,
                                             null,
                                             "Unable to return Declaration for identifier '" + boundVariableDescr.getIdentifier() + "'" ) );
            return;
        }

        Evaluator evaluator = getEvaluator( boundVariableDescr,
                                            extractor.getObjectType().getValueType(),
                                            boundVariableDescr.getEvaluator() );
        if ( evaluator == null ) {
            return;
        }

        column.addConstraint( new BoundVariableConstraint( extractor,
                                                           declaration,
                                                           evaluator ) );
    }

    private void build(Column column,
                       LiteralDescr literalDescr) {

        Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        FieldExtractor extractor = getFieldExtractor( literalDescr,
                                                      clazz,
                                                      literalDescr.getFieldName() );
        if ( extractor == null ) {
            return;
        }

        FieldValue field = null;
        if ( literalDescr.isStaticFieldValue() ) {
            int lastDot = literalDescr.getText().lastIndexOf( '.' );
            String className = literalDescr.getText().substring( 0, lastDot );
            String fieldName = literalDescr.getText().substring( lastDot + 1 );
            try {
                Class staticClass = this.typeResolver.resolveType( className );
                field = new FieldImpl( staticClass.getField( fieldName ).get( null ) );
            } catch ( ClassNotFoundException e ) {
                this.errors.add( new RuleError( this.rule,
                                                literalDescr,
                                                e,
                                                e.getMessage() ) );                                               
            } catch ( Exception e ) {
                this.errors.add( new RuleError( this.rule,
                                                literalDescr,
                                                e,
                                                "Unable to create a Field value of type  '" + extractor.getObjectType().getValueType() + "' and value '" + literalDescr.getText() + "'" ) );                
            }
          
        } else {
            try {
                field = FieldFactory.getFieldValue( literalDescr.getText(),
                                                    extractor.getObjectType().getValueType() );
            } catch ( Exception e ) {
                this.errors.add( new RuleError( this.rule,
                                                 literalDescr,
                                                 e,
                                                 "Unable to create a Field value of type  '" + extractor.getObjectType().getValueType() + "' and value '" + literalDescr.getText() + "'" ) );
            }
        }
        
        Evaluator evaluator = getEvaluator( literalDescr,
                                            extractor.getObjectType().getValueType(),
                                            literalDescr.getEvaluator() );
        if ( evaluator == null ) {
            return;
        }

        column.addConstraint( new LiteralConstraint( field,
                                                     extractor,
                                                     evaluator ) );
    }

    private void build(Column column,
                       ReturnValueDescr returnValueDescr) {
        String classMethodName = "returnValue" + counter++;
        returnValueDescr.setClassMethodName( classMethodName );

        List usedDeclarations = getUsedDeclarations( returnValueDescr,
                                                     returnValueDescr.getText() );
        if ( usedDeclarations == null ) {
            return;
        }

        Declaration[] declarations = new Declaration[usedDeclarations.size()];
        for ( int i = 0, size = usedDeclarations.size(); i < size; i++ ) {
            declarations[i] = (Declaration) this.declarations.get( (String) usedDeclarations.get( i ) );
        }

        Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();
        FieldExtractor extractor = getFieldExtractor( returnValueDescr,
                                                      clazz,
                                                      returnValueDescr.getFieldName() );
        if ( extractor == null ) {
            return;
        }

        Evaluator evaluator = getEvaluator( returnValueDescr,
                                            extractor.getObjectType().getValueType(),
                                            returnValueDescr.getEvaluator() );
        if ( evaluator == null ) {
            return;
        }

        ReturnValueConstraint returnValueConstraint = new ReturnValueConstraint( extractor,
                                                                                 declarations,
                                                                                 evaluator );
        column.addConstraint( returnValueConstraint );

        StringTemplate st = ruleGroup.getInstanceOf( "returnValueMethod" );

        setStringTemplateAttributes( st,
                                     declarations,
                                     returnValueDescr.getText() );

        st.setAttribute( "methodName",
                         classMethodName );
        st.setAttribute( "text",
                         functionFixer.fix( returnValueDescr.getText() ) );

        this.methods.add( st.toString() );

        st = invokerGroup.getInstanceOf( "returnValueInvoker" );

        st.setAttribute( "package",
                         this.pkg.getName() );
        st.setAttribute( "ruleClassName",
                         ucFirst( this.ruleDescr.getClassName() ) );
        st.setAttribute( "invokerClassName",
                         ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker" );
        st.setAttribute( "methodName",
                         classMethodName );

        setStringTemplateAttributes( st,
                                     declarations,
                                     returnValueDescr.getText() );

        st.setAttribute( "text",
                         returnValueDescr.getText() );

        String invokerClassName = pkg.getName() + "." + ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker";
        this.invokers.put( invokerClassName,
                           st.toString() );
        this.invokerLookups.put( invokerClassName,
                                 returnValueConstraint );
        this.descrLookups.put( invokerClassName,
                               returnValueDescr );
    }

    private void build(Column column,
                       PredicateDescr predicateDescr) {
        // generate method
        // generate Invoker
        String classMethodName = "predicate" + counter++;
        predicateDescr.setClassMethodName( classMethodName );

        Class clazz = ((ClassObjectType) column.getObjectType()).getClassType();

        FieldExtractor extractor = getFieldExtractor( predicateDescr,
                                                      clazz,
                                                      predicateDescr.getFieldName() );
        if ( extractor == null ) {
            return;
        }

        Declaration declaration = column.addDeclaration( predicateDescr.getDeclaration(),
                                                         extractor );

        this.declarations.put( declaration.getIdentifier(),
                               declaration );
        
        if ( this.notDeclarations != null ) {
            this.notDeclarations.put( declaration.getIdentifier(), 
                                      declaration );
        }        

        List usedDeclarations = getUsedDeclarations( predicateDescr,
                                                     predicateDescr.getText() );
        if ( usedDeclarations == null ) {
            return;
        }

        // Don't include the focus declaration, that hasn't been merged into the tuple yet.
        usedDeclarations.remove( predicateDescr.getDeclaration() );

        Declaration[] declarations = new Declaration[usedDeclarations.size()];
        for ( int i = 0, size = usedDeclarations.size(); i < size; i++ ) {
            declarations[i] = (Declaration) this.declarations.get( (String) usedDeclarations.get( i ) );
        }

        PredicateConstraint predicateConstraint = new PredicateConstraint( declaration,
                                                                           declarations );
        column.addConstraint( predicateConstraint );

        StringTemplate st = ruleGroup.getInstanceOf( "predicateMethod" );

        st.setAttribute( "declaration",
                         declaration );
        st.setAttribute( "declarationType",
                         ((ClassObjectType) declaration.getObjectType()).getClassType().getName().replace( '$',
                                                                                                           '.' ) );

        setStringTemplateAttributes( st,
                                     declarations,
                                     predicateDescr.getText() );

        st.setAttribute( "methodName",
                         classMethodName );
        st.setAttribute( "text",
                         functionFixer.fix( predicateDescr.getText() ) );

        this.methods.add( st.toString() );

        st = invokerGroup.getInstanceOf( "predicateInvoker" );

        st.setAttribute( "package",
                         this.pkg.getName() );
        st.setAttribute( "ruleClassName",
                         ucFirst( this.ruleDescr.getClassName() ) );
        st.setAttribute( "invokerClassName",
                         ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker" );
        st.setAttribute( "methodName",
                         classMethodName );

        st.setAttribute( "declaration",
                         declaration );
        st.setAttribute( "declarationType",
                         ((ClassObjectType) declaration.getObjectType()).getClassType().getName().replace( '$',
                                                                                                           '.' ) );

        setStringTemplateAttributes( st,
                                     declarations,
                                     predicateDescr.getText() );

        st.setAttribute( "text",
                         predicateDescr.getText() );

        String invokerClassName = pkg.getName() + "." + ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker";
        this.invokers.put( invokerClassName,
                           st.toString() );
        this.invokerLookups.put( invokerClassName,
                                 predicateConstraint );
        this.descrLookups.put( invokerClassName,
                               predicateDescr );
    }

    private EvalCondition build(EvalDescr evalDescr) {

        String classMethodName = "eval" + counter++;
        evalDescr.setClassMethodName( classMethodName );

        List usedDeclarations = getUsedDeclarations( evalDescr,
                                                     evalDescr.getText() );

        Declaration[] declarations = new Declaration[usedDeclarations.size()];
        for ( int i = 0, size = usedDeclarations.size(); i < size; i++ ) {
            declarations[i] = (Declaration) this.declarations.get( (String) usedDeclarations.get( i ) );
        }

        EvalCondition eval = new EvalCondition( declarations );

        StringTemplate st = ruleGroup.getInstanceOf( "evalMethod" );

        setStringTemplateAttributes( st,
                                     declarations,
                                     evalDescr.getText() );

        st.setAttribute( "methodName",
                         classMethodName );
        st.setAttribute( "text",
                         functionFixer.fix( evalDescr.getText() ) );

        this.methods.add( st.toString() );

        st = invokerGroup.getInstanceOf( "evalInvoker" );

        st.setAttribute( "package",
                         this.pkg.getName() );
        st.setAttribute( "ruleClassName",
                         ucFirst( this.ruleDescr.getClassName() ) );
        st.setAttribute( "invokerClassName",
                         ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker" );
        st.setAttribute( "methodName",
                         classMethodName );

        setStringTemplateAttributes( st,
                                     declarations,
                                     evalDescr.getText() );

        st.setAttribute( "text",
                         evalDescr.getText() );

        String invokerClassName = pkg.getName() + "." + ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker";
        this.invokers.put( invokerClassName,
                           st.toString() );
        this.invokerLookups.put( invokerClassName,
                                 eval );
        this.descrLookups.put( invokerClassName,
                               evalDescr );
        return eval;
    }
    
    private void buildConsequence( RuleDescr ruleDescr ) {
        // generate method
        // generate Invoker
        String classMethodName = "consequence";

        StringTemplate st = ruleGroup.getInstanceOf( "consequenceMethod" );

        st.setAttribute( "methodName",
                         classMethodName );

        Declaration[] declarations = (Declaration[]) this.declarations.values().toArray( new Declaration[this.declarations.size()] );
        setStringTemplateAttributes( st,
                                     declarations,
                                     ruleDescr.getConsequence() );
        
        st.setAttribute( "text",
                         functionFixer.fix( knowledgeHelperFixer.fix( ruleDescr.getConsequence() ) ) );

        this.methods.add( st.toString() );

        st = invokerGroup.getInstanceOf( "consequenceInvoker" );

        st.setAttribute( "package",
                         this.pkg.getName() );
        st.setAttribute( "ruleClassName",
                         ucFirst( this.ruleDescr.getClassName() ) );
        st.setAttribute( "invokerClassName",
                         ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker" );
        st.setAttribute( "methodName",
                         classMethodName );

        setStringTemplateAttributes( st,
                                     declarations,
                                     ruleDescr.getConsequence() );

        st.setAttribute( "text",
                         ruleDescr.getConsequence() );

        String invokerClassName = this.pkg.getName() + "." + ruleDescr.getClassName() + ucFirst( classMethodName ) + "Invoker";
        this.invokers.put( invokerClassName,
                           st.toString() );
        this.invokerLookups.put( invokerClassName,
                                 this.rule );
        this.descrLookups.put( invokerClassName,
                               ruleDescr );        
    }

    private void buildRule(RuleDescr ruleDescr) {
        StringTemplate st = ruleGroup.getInstanceOf( "ruleClass" );

        st.setAttribute( "package",
                         this.pkg.getName() );
        st.setAttribute( "ruleClassName",
                         ucFirst( this.ruleDescr.getClassName() ) );
        st.setAttribute( "imports",
                         this.pkg.getImports() );
        st.setAttribute( "methods",
                         this.methods );

        this.ruleClass = st.toString();
    }

    private List getUsedGlobals(String text) {
        if ( text == null || text.trim().equals( "" ) ) {
            return new ArrayList( 0 );
        }
        List list = new ArrayList( 1 );
        Map globals = this.pkg.getGlobals();
        for ( Iterator it = globals.keySet().iterator(); it.hasNext(); ) {
            String key = (String) it.next();

            // poor mans check. Only add the application variable if it appears as text in the script
            if ( text.indexOf( key ) == -1 ) {
                continue;
            }
            Class clazz = (Class) globals.get( key );

            String type = clazz.getName();
            int nestedClassPosition = type.indexOf( '$' );

            if ( nestedClassPosition != -1 ) {
                type = type.substring( 0,
                                       nestedClassPosition );
            }

            if ( !list.contains( key ) ) {
                list.add( key );
            }
        }

        return list;
    }

    private void setStringTemplateAttributes(StringTemplate st,
                                             Declaration[] declarations,
                                             String text) {
        String[] declarationTypes = new String[declarations.length];
        for ( int i = 0, size = declarations.length; i < size; i++ ) {
            declarationTypes[i] = ((ClassObjectType) declarations[i].getObjectType()).getClassType().getName().replace( '$',
                                                                                                                        '.' );
        }

        List globals = getUsedGlobals( text );
        List globalTypes = new ArrayList( globals.size() );
        for ( Iterator it = globals.iterator(); it.hasNext(); ) {
            globalTypes.add( ((Class) this.pkg.getGlobals().get( it.next() )).getName().replace( '$',
                                                                                                 '.' ) );
        }

        st.setAttribute( "declarations",
                         declarations );
        st.setAttribute( "declarationTypes",
                         declarationTypes );

        st.setAttribute( "globals",
                         globals );
        st.setAttribute( "globalTypes",
                         globalTypes );
    }

    private String ucFirst(String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }

    private FieldExtractor getFieldExtractor(PatternDescr descr,
                                             Class clazz,
                                             String fieldName) {
        FieldExtractor extractor = null;
        try {
            extractor = new ClassFieldExtractor( clazz,
                                                 fieldName );
        } catch ( RuntimeDroolsException e ) {
            this.errors.add( new RuleError( this.rule,
                                             descr,
                                             e,
                                             "Unable to create Field Extractor for '" + fieldName + "'")  );
        }

        return extractor;
    }

    private Evaluator getEvaluator(PatternDescr descr,
                                   int valueType,
                                   String evaluatorString) {
        Evaluator evaluator = EvaluatorFactory.getEvaluator( valueType,
                                                             evaluatorString );

        if ( evaluator == null ) {
            this.errors.add( new RuleError( this.rule,
                                             descr,
                                             null,
                                             "Unable to determine the Evaluator for  '" + valueType + "' and '" + evaluatorString + "'" ) );
        }

        return evaluator;
    }

    private List getUsedDeclarations(PatternDescr descr,
                                     String text) {
        List usedDeclarations = null;
        try {
            usedDeclarations = this.analyzer.analyze( text,
                                                      this.declarations.keySet() );
        } catch ( Exception e ) {
            this.errors.add( new RuleError( this.rule,
                                             descr,
                                             null,
                                             "Unable to determine the used declarations" ) );
        }
        return usedDeclarations;
    }
}
