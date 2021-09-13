## TODO

Sistema il test della DT grossa
Metti i test FEEL in un metodo statico invece che di istanza (così eviti il singleton)
Predicate information su constraint e Commenti
Rinomina DMNCompiledAlphaNetworkEvaluator
Aggiungi gli indici al constraint
Trace string

## Fatto

Non fare compilare UnaryTest di nodi sharati
Disabilita generazione di propagateModifyObject
Rivedi / separa i contesti
Rivedi Interfaccia DMNCompiledAlphaNetworkEvaluator (meno metodi?)
Rimuovi codice generato che crea la RETE
Creare nuova interfaccia per result che non dipenda da ObjectSink
Cambiare creazione output (l'output (In questo caso resultCollectorAlphaSink) non ha bisogno di InternalFactHandle factHandle, PropagationContext propagationContext, InternalWorkingMemory workingMemory)
Inline creazione constraint
Inline output
Trova un metodo per aggiungere campi arbitrari alla ANC
Rimetti il dummy alpha node (sembra non serva?)
Aggiungere il metodo di inizializzazione ad ANC
Parametrizzare ANC in modo da avere un costruttore senza setNetworkNodeReference
Rimuovi da ANC private org.drools.core.rule.ContextEntry contextEntry4;


Istanziare rete network chiamando i metodi di creazione delle RETE durante la generazione
Spostare

    private boolean evaluateAllTests(PropertyEvaluator propertyEvaluator, CompiledFEELUnaryTests instance, int index, String traceString) {
        return instance.getUnaryTests().stream().anyMatch(t -> {
            Object value = propertyEvaluator.getValue(index);
            Boolean result = t.apply(propertyEvaluator.getEvaluationContext(), value);
            if (logger.isTraceEnabled()) {
                logger.trace(traceString);
            }
            return result != null && result;
        });
    }

nella ANC invece che in ogni classe di test

Esempio creazione ANC

    private boolean setNetworkNode0(org.drools.core.common.NetworkNode node) {
        lambdaConstraint4 = ~~alphaNetworkCreation.createConstraint("Age_62_6118", p -> evaluateAllTests(p, UnaryTestR1C1.getInstance(), 0, "trace"), null).getLambdaConstraint();~~

        resultCollectorAlphaSink11 = alphaNetworkCreation.results(0, "", context -> R1C1FeelExpression.getInstance().apply(context));
    }
