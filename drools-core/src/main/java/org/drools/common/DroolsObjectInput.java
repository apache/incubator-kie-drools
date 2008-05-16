package org.drools.common;

import org.drools.rule.*;
import org.drools.rule.Package;
import org.drools.base.ClassFieldAccessorCache;

import java.io.ObjectInput;

/**
 * Created by IntelliJ IDEA. User: SG0521861 Date: Mar 4, 2008 Time: 7:24:07 AM To change this template use File |
 * Settings | File Templates.
 */
public interface DroolsObjectInput extends ObjectInput {
    ClassLoader getClassLoader();
    void setClassLoader(ClassLoader classLoader);
    InternalRuleBase getRuleBase();
    void setRuleBase(InternalRuleBase ruleBase);
    void setWorkingMemory(InternalWorkingMemory workingMemory);
    InternalWorkingMemory getWorkingMemory();
    Package getPackage();
    void setPackage(Package pkg);
    DialectDatas getDialectDatas();
    void setDialectDatas(DialectDatas dialectDatas);
    ClassFieldAccessorCache getExtractorFactory();
    void setExtractorFactory(ClassFieldAccessorCache extractorFactory);
}
