package org.drools.compiler.compiler;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.drools.compiler.lang.descr.AccessorDescr;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.AccumulateImportDescr;
import org.drools.compiler.lang.descr.ActionDescr;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.AnnotatedBaseDescr;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.AtomicExprDescr;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.BindingDescr;
import org.drools.compiler.lang.descr.CollectDescr;
import org.drools.compiler.lang.descr.ConstraintConnectiveDescr;
import org.drools.compiler.lang.descr.DeclarativeInvokerDescr;
import org.drools.compiler.lang.descr.EntryPointDeclarationDescr;
import org.drools.compiler.lang.descr.EnumDeclarationDescr;
import org.drools.compiler.lang.descr.EvalDescr;
import org.drools.compiler.lang.descr.ExistsDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.FactTemplateDescr;
import org.drools.compiler.lang.descr.FieldConstraintDescr;
import org.drools.compiler.lang.descr.FieldTemplateDescr;
import org.drools.compiler.lang.descr.ForallDescr;
import org.drools.compiler.lang.descr.FromDescr;
import org.drools.compiler.lang.descr.FunctionDescr;
import org.drools.compiler.lang.descr.FunctionImportDescr;
import org.drools.compiler.lang.descr.GlobalDescr;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.compiler.lang.descr.LiteralRestrictionDescr;
import org.drools.compiler.lang.descr.MVELExprDescr;
import org.drools.compiler.lang.descr.NamedConsequenceDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.PredicateDescr;
import org.drools.compiler.lang.descr.QueryDescr;
import org.drools.compiler.lang.descr.RelationalExprDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.compiler.lang.descr.WindowDeclarationDescr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PackageDescrResourceVisitor {

    private static final Logger logger = LoggerFactory.getLogger(PackageDescrResourceVisitor.class);

    private static void checkResource( BaseDescr descr ) {
        if( descr != null ) {
            assertNotNull(descr.getClass().getSimpleName() + ".resource is null!", descr.getResource());
        }
    }

    private void visit( final Object descr ) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String lastMethodName = null;
        for( int i = 0; i < 5; ++i ) {
            String thisMethodName = stack[i].getMethodName() + ":" + stack[i].getLineNumber();
            if( thisMethodName.equals(lastMethodName) ) {
                fail("Infinite loop detected!");
            }
            lastMethodName = thisMethodName;
        }

        if( descr instanceof AccessorDescr ) {
            visit((AccessorDescr) descr);
        } else if( descr instanceof AccumulateDescr ) {
            visit((AccumulateDescr) descr);
        } else if( descr instanceof ActionDescr ) {
            visit((ActionDescr) descr);
        } else if( descr instanceof AndDescr ) {
            visit((AndDescr) descr);
        } else if( descr instanceof AnnotationDescr ) {
            visit((AnnotationDescr) descr);
        } else if( descr instanceof AtomicExprDescr ) {
            visit((AtomicExprDescr) descr);
        } else if( descr instanceof AttributeDescr ) {
            visit((AttributeDescr) descr);
        } else if( descr instanceof BindingDescr ) {
            visit((BindingDescr) descr);
        } else if( descr instanceof CollectDescr ) {
            visit((CollectDescr) descr);
        } else if( descr instanceof ConstraintConnectiveDescr ) {
            visit((ConstraintConnectiveDescr) descr);
        } else if( descr instanceof ExistsDescr ) {
            visit((ExistsDescr) descr);
        } else if( descr instanceof ExprConstraintDescr ) {
            visit((ExprConstraintDescr) descr);
        } else if( descr instanceof FactTemplateDescr ) {
            visit((FactTemplateDescr) descr);
        } else if( descr instanceof FieldConstraintDescr ) {
            visit((FieldConstraintDescr) descr);
        } else if( descr instanceof FieldTemplateDescr ) {
            visit((FieldTemplateDescr) descr);
        } else if( descr instanceof ForallDescr ) {
            visit((ForallDescr) descr);
        } else if( descr instanceof FromDescr ) {
            visit((FromDescr) descr);
        } else if( descr instanceof FunctionDescr ) {
            visit((FunctionDescr) descr);
        } else if( descr instanceof FunctionImportDescr ) {
            visit((FunctionImportDescr) descr);
        } else if( descr instanceof GlobalDescr ) {
            visit((GlobalDescr) descr);
        } else if( descr instanceof ImportDescr ) {
            visit((ImportDescr) descr);
        } else if( descr instanceof LiteralRestrictionDescr ) {
            visit((LiteralRestrictionDescr) descr);
        } else if( descr instanceof MVELExprDescr ) {
            visit((MVELExprDescr) descr);
        } else if( descr instanceof NotDescr ) {
            visit((NotDescr) descr);
        } else if( descr instanceof OrDescr ) {
            visit((OrDescr) descr);
        } else if( descr instanceof PackageDescr ) {
            visit((PackageDescr) descr);
        } else if( descr instanceof PatternDescr ) {
            visit((PatternDescr) descr);
        } else if( descr instanceof PredicateDescr ) {
            visit((PredicateDescr) descr);
        } else if( descr instanceof QueryDescr ) {
            visit((QueryDescr) descr);
        } else if( descr instanceof RelationalExprDescr ) {
            visit((RelationalExprDescr) descr);
        } else if( descr instanceof RuleDescr ) {
            visit((RuleDescr) descr);
        } else if( descr instanceof TypeDeclarationDescr ) {
            visit((TypeDeclarationDescr) descr);
        } else if( descr instanceof TypeFieldDescr ) {
            visit((TypeFieldDescr) descr);
        } else if( descr instanceof WindowDeclarationDescr ) {
            visit((WindowDeclarationDescr) descr);
        } else if( descr instanceof NamedConsequenceDescr  ) {
            visit((NamedConsequenceDescr) descr);
        } else if( descr instanceof EvalDescr  ) {
            visit((EvalDescr) descr);
        } else if( descr instanceof BehaviorDescr  ) {
            visit((BehaviorDescr) descr);
        } else {
            throw new RuntimeException("xx DID NOT VISIT: " + descr.getClass().getName());
        }
    }

    protected void visit( final AccessorDescr descr ) {
        checkResource(descr);
        for( DeclarativeInvokerDescr d : descr.getInvokersAsArray() ) {
            visit(d);
        }
    }

    protected void visit( final AccumulateDescr descr ) {
        checkResource(descr);
        visit(descr.getInputPattern());
        for( BaseDescr d : descr.getDescrs() ) {
            visit(d);
        }
    }

    protected void visit( final ActionDescr descr ) {
        checkResource(descr);
    }

    protected void visit( final AndDescr descr ) {
        checkResource(descr);
        for( BaseDescr baseDescr : descr.getDescrs() ) {
            visit(baseDescr);
        }
    }

    protected void visit( final AnnotatedBaseDescr descr ) {
        checkResource(descr);
        for( BaseDescr annoDescr : descr.getAnnotations() ) {
            visit(annoDescr);
        }
    }

    protected void visit( final AtomicExprDescr descr ) {
        checkResource(descr);
    }

    protected void visit( final AttributeDescr descr ) {

    }

    protected void visit( final BindingDescr descr ) {
        checkResource(descr);
    }

    protected void visit( final CollectDescr descr ) {
        checkResource(descr);
        visit(descr.getInputPattern());
        for( BaseDescr d : descr.getDescrs() ) {
            visit(d);
        }
    }

    protected void visit( final ConstraintConnectiveDescr descr ) {
        checkResource(descr);
        for( BaseDescr d : descr.getDescrs() ) {
            visit(d);
        }
    }

    protected void visit( final ExistsDescr descr ) {
        checkResource(descr);
        for( Object o : descr.getDescrs() ) {
            visit(o);
        }
    }

    protected void visit( final ExprConstraintDescr descr ) {
        checkResource(descr);
    }

    protected void visit( final FactTemplateDescr descr ) {
        checkResource(descr);
        for( FieldTemplateDescr d : descr.getFields() ) {
            visit(d);
        }
    }

    protected void visit( final FieldConstraintDescr descr ) {
        checkResource(descr);
        for( Object o : descr.getRestrictions() ) {
            visit(o);
        }
    }

    protected void visit( final FieldTemplateDescr descr ) {
        checkResource(descr);
    }

    protected void visit( final ForallDescr descr ) {
        checkResource(descr);
        visit(descr.getBasePattern());
        for( BaseDescr o : descr.getDescrs() ) {
            visit(o);
        }
    }

    protected void visit( final FromDescr descr ) {
        checkResource(descr);
        for( BaseDescr d : descr.getDescrs() ) {
            visit(d);
        }
    }

    protected void visit( final FunctionDescr descr ) {
        checkResource(descr);
    }

    protected void visit( final FunctionImportDescr descr ) {
        checkResource(descr);
    }

    protected void visit( final GlobalDescr descr ) {
        checkResource(descr);
    }

    protected void visit( final ImportDescr descr ) {
        checkResource(descr);
    }

    protected void visit( final LiteralRestrictionDescr descr ) {
        checkResource(descr);
    }

    protected void visit( final MVELExprDescr descr ) {
        checkResource(descr);
    }

    protected void visit( final NotDescr descr ) {
        checkResource(descr);
        // NotDescr isn't type-safe
        for( Object o : descr.getDescrs() ) {
            visit(o);
        }
    }

    protected void visit( final OrDescr descr ) {
        checkResource(descr);
        for( BaseDescr d : descr.getDescrs() ) {
            visit(d);
        }
    }

    protected void visit( final PackageDescr descr ) {
        if( descr == null ) { return; }
        checkResource(descr);
        for( ImportDescr importDescr : descr.getImports() ) {
            visit(importDescr);
        }
        for( FunctionImportDescr funcImportDescr : descr.getFunctionImports() ) {
            visit(funcImportDescr);
        }
        for( AccumulateImportDescr accImportDescr : descr.getAccumulateImports() ) {
            visit(accImportDescr);
        }
        for( AttributeDescr attrDescr : descr.getAttributes() ) {
            visit(attrDescr);
        }
        for( GlobalDescr globDesc : descr.getGlobals() ) {
            visit(globDesc);
        }
        for( FunctionDescr funcDescr : descr.getFunctions() ) {
            visit(funcDescr);
        }
        for( RuleDescr ruleDescr : descr.getRules() ) {
            visit(ruleDescr);
        }
        for( TypeDeclarationDescr typeDescr : descr.getTypeDeclarations() ) {
            visit(typeDescr);
        }
        for( EntryPointDeclarationDescr entryDescr : descr.getEntryPointDeclarations() ) {
            visit(entryDescr);
        }
        for( WindowDeclarationDescr windowDescr : descr.getWindowDeclarations() ) {
            visit(windowDescr);
        }
        for( EnumDeclarationDescr enumDescr : descr.getEnumDeclarations() ) {
            visit(enumDescr);
        }
    }

    protected void visit( final PatternDescr descr ) {
        checkResource(descr);
        visit(descr.getConstraint());
        for( BaseDescr behaDescr : descr.getBehaviors() ) {
            visit(behaDescr);
        }
    }

    protected void visit( final PredicateDescr descr ) {
        checkResource(descr);
    }

    protected void visit( final QueryDescr descr ) {
        checkResource(descr);
        visit(descr.getLhs());
        for( AttributeDescr attrDescr : descr.getAttributes().values() ) {
            visit(attrDescr);
        }
    }

    protected void visit( final RelationalExprDescr descr ) {
        checkResource(descr);
        visit(descr.getLeft());
        visit(descr.getRight());
    }

    protected void visit( final RuleDescr descr ) {
        checkResource(descr);
        for( AttributeDescr d : descr.getAttributes().values() ) {
            visit(d);
        }
        visit(descr.getLhs());
        visitConsequence(descr.getConsequence());
        for( Object o : descr.getNamedConsequences().values() ) {
            visitConsequence(o);
        }
    }

    protected void visitConsequence( final Object consequence ) {
        if( consequence instanceof BaseDescr ) {
            visit(consequence);
        }
    }

    protected void visit( final TypeDeclarationDescr descr ) {
        checkResource(descr);
        for( TypeFieldDescr fieldDescr : descr.getFields().values() ) {
            visit(fieldDescr);
        }
    }

    protected void visit( final TypeFieldDescr descr ) {
        if( descr == null ) {
            return;
        }
        checkResource(descr);
        visit(descr.getOverriding());
    }

    protected void visit( final WindowDeclarationDescr descr ) {
        checkResource(descr);
        visit(descr.getPattern());
    }

    protected void visit( final NamedConsequenceDescr descr ) {
        checkResource(descr);
    }

    protected void visit( final EvalDescr descr ) {
        checkResource(descr);
    }

    protected void visit( final BehaviorDescr descr ) {
        checkResource(descr);
    }
}
