package org.drools.verifier.components;

public class InlineEvalDescr extends PatternComponent {

    public InlineEvalDescr(Pattern pattern) {
        super( pattern );
    }

    @Override
    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.INLINE_EVAL_DESCR;
    }

}
