package org.kie.dmn.core.compiler;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.events.SyntaxErrorEvent;
import org.kie.dmn.model.v1_1.DMNElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DMNFEELHelper
        implements FEELEventListener {

    private static final Logger logger = LoggerFactory.getLogger( DMNFEELHelper.class );

    private final FEEL             feel;
    private final Queue<FEELEvent> feelEvents;

    public DMNFEELHelper() {
        this.feelEvents = new LinkedList<>();
        this.feel = createFEELInstance();
    }

    private FEEL createFEELInstance() {
        FEEL feel = FEEL.newInstance();
        feel.addListener( this );
        return feel;
    }

    @Override
    public void onEvent(FEELEvent event) {
        feelEvents.add( event );
    }

    public CompiledExpression compileFeelExpression(DMNCompilerContext ctx, String expression, DMNModelImpl model, DMNElement element, Msg.Message errorMsg, Object... msgParams) {
        CompilerContext feelctx = feel.newCompilerContext();

        for ( Map.Entry<String, DMNType> entry : ctx.getVariables().entrySet() ) {
            feelctx.addInputVariableType( entry.getKey(), ((BaseDMNTypeImpl) entry.getValue()).getFeelType() );
        }
        CompiledExpression ce = feel.compile( expression, feelctx );
        processEvents( model, element, errorMsg, msgParams );
        return ce;
    }

    public List<UnaryTest> evaluateUnaryTests(DMNCompilerContext ctx, String unaryTests, DMNModelImpl model, DMNElement element, Msg.Message errorMsg, Object... msgParams) {
        Map<String, Type> variableTypes = new HashMap<>();
        for ( Map.Entry<String, DMNType> entry : ctx.getVariables().entrySet() ) {
            // TODO: need to properly resolve types here
            variableTypes.put( entry.getKey(), BuiltInType.UNKNOWN );
        }
        List<UnaryTest> result = feel.evaluateUnaryTests( unaryTests, variableTypes );
        processEvents( model, element, errorMsg, msgParams );
        return result;
    }

    public void processEvents(DMNModelImpl model, DMNElement element, Msg.Message msg, Object... msgParams) {
        while ( !feelEvents.isEmpty() ) {
            FEELEvent event = feelEvents.remove();
            if ( !isDuplicateEvent( model, msg, element ) ) {
                if ( event instanceof SyntaxErrorEvent || event.getSeverity() == FEELEvent.Severity.ERROR ) {
                    if ( msg instanceof Msg.Message2 ) {
                        MsgUtil.reportMessage(
                                logger,
                                DMNMessage.Severity.ERROR,
                                element,
                                model,
                                null,
                                event,
                                (Msg.Message2) msg,
                                msgParams[0],
                                msgParams[1] );
                    } else if ( msg instanceof Msg.Message3 ) {
                        MsgUtil.reportMessage(
                                logger,
                                DMNMessage.Severity.ERROR,
                                element,
                                model,
                                null,
                                event,
                                (Msg.Message3) msg,
                                msgParams[0],
                                msgParams[1],
                                msgParams[2] );
                    }
                }
            }
        }
    }

    private boolean isDuplicateEvent(DMNModelImpl model, Msg.Message error, DMNElement element) {
        return model.getMessages().stream().anyMatch( msg -> msg.getMessageType() == error.getType() &&
                                                             (msg.getSourceId() == element.getId() ||
                                                              (msg.getSourceId() != null &&
                                                               element.getId() != null &&
                                                               msg.getSourceId().equals( element.getId() ))) );
    }

}
