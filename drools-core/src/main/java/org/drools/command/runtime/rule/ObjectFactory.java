package org.drools.command.runtime.rule;

import javax.xml.bind.annotation.XmlRegistry;

import org.drools.command.runtime.rule.FireAllRulesCommand;
import org.drools.command.runtime.rule.GetObjectsCommand;
import org.drools.command.runtime.rule.InsertElementsCommand;
import org.drools.command.runtime.rule.InsertObjectCommand;
import org.drools.command.runtime.rule.ModifyCommand;
import org.drools.command.runtime.rule.QueryCommand;
import org.drools.command.runtime.rule.RetractCommand;
import org.drools.command.runtime.rule.ModifyCommand.SetterImpl;

@XmlRegistry
public class ObjectFactory {
    public FireAllRulesCommand createFireAllRulesCommand() {
        return new FireAllRulesCommand();
    }
    
    public GetObjectsCommand createGetObjectsCommand() {
        return new GetObjectsCommand();
    }
    
    public InsertElementsCommand createInsertElementsCommand() {
        return new InsertElementsCommand();
    }
    
    public InsertObjectCommand createInsertObjectCommand() {
        return new InsertObjectCommand();
    }
    
    public ModifyCommand createModifyCommand() {
        return new ModifyCommand();
    }
    
    public QueryCommand createQueryCommand() {
        return new QueryCommand();
    }
    
    public RetractCommand createRetractCommand() {
        return new RetractCommand();
    }
    
    public SetterImpl createModifyCommand$SetterImpl() {
        return new SetterImpl();
    }
}
