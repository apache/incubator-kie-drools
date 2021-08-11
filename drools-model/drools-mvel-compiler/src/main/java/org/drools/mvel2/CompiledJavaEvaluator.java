package org.drools.mvel2;

import java.io.Serializable;

public interface CompiledJavaEvaluator extends Serializable{

    Object eval(java.util.Map map);
}
