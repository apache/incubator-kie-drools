package org.kie.dmn.core.compiler.alphanetbased;

import java.util.Collections;

import org.drools.base.base.ClassObjectType;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.model.Variable;

import static org.drools.model.DSL.declarationOf;

public class ReteBuilderContext {

    public InternalKnowledgeBase kBase;
    public BuildContext buildContext;
    public Variable<PropertyEvaluator> variable;
    public Declaration declaration;
    public ObjectTypeNode otn;

    public ReteBuilderContext() {
        kBase = KnowledgeBaseFactory.newKnowledgeBase();
        buildContext = new BuildContext(kBase, Collections.emptyList());
        EntryPointNode entryPoint = buildContext.getRuleBase().getRete().getEntryPointNodes().values().iterator().next();
        ClassObjectType objectType = new ClassObjectType(PropertyEvaluator.class);
        variable = declarationOf(PropertyEvaluator.class, "$ctx");

        Pattern pattern = new Pattern(1, objectType, "$ctx");
        declaration = pattern.getDeclaration();

        otn = new ObjectTypeNode(buildContext.getNextNodeId(), entryPoint, objectType, buildContext);
        buildContext.setObjectSource(otn);
    }
}
