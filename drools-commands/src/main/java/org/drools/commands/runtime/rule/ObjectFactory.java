package org.drools.commands.runtime.rule;

import javax.xml.bind.annotation.XmlRegistry;

import org.drools.commands.runtime.rule.ModifyCommand.SetterImpl;

@XmlRegistry
public class ObjectFactory {

    public FireAllRulesCommand createFireAllRulesCommand() {
        return new FireAllRulesCommand();
    }
    
    public GetObjectCommand createGetObjectCommand() {
        return new GetObjectCommand();
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

    public InsertObjectInEntryPointCommand createInsertObjectInEntryPointCommand() {
        return new InsertObjectInEntryPointCommand();
    }
    
    public ModifyCommand createModifyCommand() {
        return new ModifyCommand();
    }

    public SetterImpl createModifyCommand$SetterImpl() {
        return new SetterImpl();
    }
    
    public QueryCommand createQueryCommand() {
        return new QueryCommand();
    }
    
    public DeleteCommand createRetractCommand() {
        return new DeleteCommand();
    }

}
