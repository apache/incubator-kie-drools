package org.kie.dmn.core.compiler.alphanetbased;

import javax.xml.namespace.QName;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.NamedElement;

public class DTQNameToTypeResolver {

    private final DMNCompilerImpl compiler;
    private final DMNModelImpl model;
    private final NamedElement node;
    private final DecisionTable decisionTable;

    public DTQNameToTypeResolver(DMNCompilerImpl compiler, DMNModelImpl model, NamedElement node, DecisionTable decisionTable) {
        this.compiler = compiler;
        this.model = model;
        this.node = node;
        this.decisionTable = decisionTable;
    }

    public Type resolve(QName qname) {
        DMNType resolveTypeRef = compiler.resolveTypeRef(model, node, decisionTable, qname);
        return ((BaseDMNTypeImpl) resolveTypeRef).getFeelType();
    }
}
