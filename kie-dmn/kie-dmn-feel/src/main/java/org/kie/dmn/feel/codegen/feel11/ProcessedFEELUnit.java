package org.kie.dmn.feel.codegen.feel11;

import java.util.List;
import java.util.UUID;

import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;

public abstract class ProcessedFEELUnit implements CompiledFEELExpression {

    public enum DefaultMode {
        Compiled,
        Interpreted;

        public static DefaultMode of(boolean doCompile) {
            return doCompile ? Compiled : Interpreted;
        }

    }

    protected final String packageName;
    protected final String expression;
    protected final CompiledFEELSupport.SyntaxErrorListener errorListener =
            new CompiledFEELSupport.SyntaxErrorListener();
    protected final CompilerBytecodeLoader compiler =
            new CompilerBytecodeLoader();

    ProcessedFEELUnit(String expression,
                      CompilerContext ctx,
                      List<FEELProfile> profiles) {

        this.expression = expression;
        this.packageName = generateRandomPackage();
    }

    protected FEEL_1_1Parser getFEELParser(String expression, CompilerContext ctx, List<FEELProfile> profiles) {
        FEELEventListenersManager eventsManager =
                new FEELEventListenersManager();

        eventsManager.addListeners(ctx.getListeners());
        eventsManager.addListener(errorListener);

        return FEELParser.parse(
                eventsManager,
                expression,
                ctx.getInputVariableTypes(),
                ctx.getInputVariables(),
                ctx.getFEELFunctions(),
                profiles,
                ctx.getFEELFeelTypeRegistry());
    }

    private String generateRandomPackage() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return this.getClass().getPackage().getName() + ".gen" + uuid;
    }
}
