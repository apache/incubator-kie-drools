package org.kie.dmn.feel.lang.ast.infixexecutors;

public class EvaluatedParameters {

        private final Object left;
        private final Object right;

        public EvaluatedParameters(Object leftObject, Object rightObject) {
            this.left = leftObject;
            this.right = rightObject;
        }

        public Object getLeft() {
            return left;
        }

        public Object getRight() {
            return right;
        }
    }