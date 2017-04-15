package org.kie.dmn.core.compiler;

import org.antlr.v4.runtime.CommonToken;
import org.kie.dmn.api.core.DMNContext;
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
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.events.SyntaxErrorEvent;
import org.kie.dmn.feel.runtime.events.UnknownVariableErrorEvent;
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
    
    public static boolean valueMatchesInUnaryTests(List<UnaryTest> unaryTests, Object value, DMNContext dmnContext) {
        EvaluationContextImpl ctx = new EvaluationContextImpl( null );
        try {
            ctx.enterFrame();
            // need to set the values for in context variables...
            for ( Map.Entry<String,Object> entry : dmnContext.getAll().entrySet() ) {
                ctx.setValue( entry.getKey(), entry.getValue() );
            }
    
            for ( UnaryTest t : unaryTests ) {
                try {
                    Boolean applyT = t.apply(ctx, value);
                    // the unary test above can actually return null, so we have to handle it here
                    if ( applyT == null ) {
                        return false;
                    } else if ( applyT ) {
                        return true;
                    }
                } catch ( Throwable e ) {
                    // if an unaryTest fail for any treason, is simply considered FALSE.
                    logger.warn("A non-critical error happened while evaluating a unary test. Execution will continue.", e);
                }
            }
        } finally {
            ctx.exitFrame();
        }
        return false;
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
        List<UnaryTest> result = Collections.emptyList();
        try {
            Map<String, Type> variableTypes = new HashMap<>();
            for ( Map.Entry<String, DMNType> entry : ctx.getVariables().entrySet() ) {
                // TODO: need to properly resolve types here
                variableTypes.put( entry.getKey(), BuiltInType.UNKNOWN );
            }
            result = feel.evaluateUnaryTests( unaryTests, variableTypes );
        } catch( Throwable t ) {
            logger.error( "Error evaluating unary tests. Error will be reported in the model.", t );
        }
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
                    } else if ( msg instanceof Msg.Message4 ) {
                        String message = null;
                        if( event.getOffendingSymbol() == null ) {
                            message = "";
                        } else if( event instanceof UnknownVariableErrorEvent ) {
                            message = event.getMessage();
                        } else if( event.getOffendingSymbol() instanceof CommonToken ) {
                            message = "syntax error near '" + ((CommonToken)event.getOffendingSymbol()).getText() + "'";
                        } else {
                            message = "syntax error near '" + event.getOffendingSymbol() + "'";
                        }
                        MsgUtil.reportMessage(
                                logger,
                                DMNMessage.Severity.ERROR,
                                element,
                                model,
                                null,
                                event,
                                (Msg.Message4) msg,
                                msgParams[0],
                                msgParams[1],
                                msgParams[2],
                                message );
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
