package org.drools.model.consequences;

import org.drools.model.Consequence;
import org.drools.model.Drools;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.functions.Block0;
import org.drools.model.functions.Block1;
import org.drools.model.functions.Block10;
import org.drools.model.functions.Block11;
import org.drools.model.functions.Block12;
import org.drools.model.functions.Block13;
import org.drools.model.functions.Block14;
import org.drools.model.functions.Block2;
import org.drools.model.functions.Block3;
import org.drools.model.functions.Block4;
import org.drools.model.functions.Block5;
import org.drools.model.functions.Block6;
import org.drools.model.functions.Block7;
import org.drools.model.functions.Block8;
import org.drools.model.functions.Block9;
import org.drools.model.functions.BlockN;
import org.drools.model.functions.ScriptBlock;

public class ConsequenceBuilder {

    public _0 execute(Block0 block) {
        return new _0(block);
    }

    public _0 execute(Block1<Drools> block) {
        return new _0(block);
    }

    public <A> _1<A> on(Variable<A> dec1) {
        return new _1(dec1);
    }

    public <A, B> _2<A, B> on(Variable<A> decl1, Variable<B> decl2) {
        return new _2(decl1, decl2);
    }

    public interface ValidBuilder extends RuleItemBuilder<Consequence> { }

    public static abstract class AbstractValidBuilder<T extends AbstractValidBuilder> implements ValidBuilder {
        private final Variable[] declarations;
        protected BlockN block;
        protected boolean usingDrools = false;
        protected boolean breaking = false;
        protected String language = "java";

        protected AbstractValidBuilder(Variable... declarations) {
            this.declarations = declarations;
        }

        @Override
        public Consequence get() {
            return new ConsequenceImpl( block,
                                        declarations,
                                        usingDrools,
                                        breaking,
                                        language);
        }

        public T breaking() {
            breaking = true;
            return (T) this;
        }
    }

    public static class _0 extends AbstractValidBuilder<_0> {
        public _0(final Block0 block) {
            super(new Variable[0]);
            this.block = block.asBlockN();
        }

        public _0(final Block1<Drools> block) {
            super();
            this.usingDrools = true;
            this.block = block.asBlockN();
        }

        public _0(String language, Class<?> ruleClass, String script) {
            super();
            this.usingDrools = true;
            this.language = language;
            this.block = new ScriptBlock(ruleClass, script);
        }
    }

    public static class _1<A> extends AbstractValidBuilder<_1<A>> {
        public _1(Variable<A> declaration) {
            super(declaration);
        }

        public _1<A> execute(final Block1<A> block) {
            this.block = block.asBlockN();
            return this;
        }

        public _1<A> execute(final Block2<Drools, A> block) {
            this.usingDrools = true;
            this.block = block.asBlockN();
            return this;
        }

        public _1<A> executeScript(String language, Class<?> ruleClass, String script) {
            this.usingDrools = true;
            this.language = language;
            this.block = new ScriptBlock(ruleClass, script);
            return this;
        }
    }

    public static class _2<A, B> extends AbstractValidBuilder<_2<A,B>> {
        public _2(Variable<A> decl1, Variable<B> decl2) {
            super(decl1, decl2);
        }

        public _2<A, B> execute(final Block2<A, B> block) {
            this.block = block.asBlockN();
            return this;
        }

        public _2<A, B> execute(final Block3<Drools, A, B> block) {
            this.usingDrools = true;
            this.block = block.asBlockN();
            return this;
        }

        public _2<A, B> executeScript(String language, Class<?> ruleClass, String script) {
            this.usingDrools = true;
            this.language = language;
            this.block = new ScriptBlock(ruleClass, script);
            return this;
        }
    }

    public static class _3<A, B, C> extends AbstractValidBuilder<_3<A,B,C>> {
        public _3(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3) {
            super(decl1, decl2, decl3);
        }

        public _3<A, B, C> execute(final Block3<A, B, C> block) {
            this.block = block.asBlockN();
            return this;
        }

        public _3<A, B, C> execute(final Block4<Drools, A, B, C> block ) {
            this.usingDrools = true;
            this.block = block.asBlockN();
            return this;
        }

        public _3<A, B, C> executeScript(String language, Class<?> ruleClass, String script) {
            this.usingDrools = true;
            this.language = language;
            this.block = new ScriptBlock(ruleClass, script);
            return this;
        }
    }

    public static class _4<A, B, C, D> extends AbstractValidBuilder<_4<A,B,C,D>> {
        public _4(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3, Variable<D> decl4) {
            super(decl1, decl2, decl3, decl4);
        }

        public _4<A, B, C, D> execute(final Block4<A, B, C, D> block) {
            this.block = block.asBlockN();
            return this;
        }

        public _4<A, B, C, D> execute(final Block5<Drools, A, B, C, D> block ) {
            this.usingDrools = true;
            this.block = block.asBlockN();
            return this;
        }

        public _4<A, B, C, D> executeScript(String language, Class<?> ruleClass, String script) {
            this.usingDrools = true;
            this.language = language;
            this.block = new ScriptBlock(ruleClass, script);
            return this;
        }
    }

    public static class _5<A, B, C, D, E> extends AbstractValidBuilder<_5<A,B,C,D,E>> {
        public _5(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3, Variable<D> decl4, Variable<E> decl5) {
            super(decl1, decl2, decl3, decl4, decl5);
        }

        public _5<A, B, C, D, E> execute(final Block5<A, B, C, D, E> block) {
            this.block = block.asBlockN();
            return this;
        }

        public _5<A, B, C, D, E> execute(final Block6<Drools, A, B, C, D, E> block ) {
            this.usingDrools = true;
            this.block = block.asBlockN();
            return this;
        }

        public _5<A, B, C, D, E> executeScript(String language, Class<?> ruleClass, String script) {
            this.usingDrools = true;
            this.language = language;
            this.block = new ScriptBlock(ruleClass, script);
            return this;
        }
    }

    public static class _6<A, B, C, D, E, F> extends AbstractValidBuilder<_6<A,B,C,D,E,F>> {
        public _6(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3, Variable<D> decl4, Variable<E> decl5, Variable<F> decl6) {
            super(decl1, decl2, decl3, decl4, decl5, decl6);
        }

        public _6<A, B, C, D, E, F> execute(final Block6<A, B, C, D, E, F> block) {
            this.block = block.asBlockN();
            return this;
        }

        public _6<A, B, C, D, E, F> execute(final Block7<Drools, A, B, C, D, E, F> block ) {
            this.usingDrools = true;
            this.block = block.asBlockN();
            return this;
        }

        public _6<A, B, C, D, E, F> executeScript(String language, Class<?> ruleClass, String script) {
            this.usingDrools = true;
            this.language = language;
            this.block = new ScriptBlock(ruleClass, script);
            return this;
        }
    }

    public static class _7<A, B, C, D, E, F, G> extends AbstractValidBuilder<_7<A,B,C,D,E,F,G>> {
        public _7(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3, Variable<D> decl4, Variable<E> decl5, Variable<F> decl6,
                   Variable<G> decl7) {
            super(decl1, decl2, decl3, decl4, decl5, decl6, decl7);
        }

        public _7<A, B, C, D, E, F, G> execute(final Block7<A, B, C, D, E, F, G> block) {
            this.block = block.asBlockN();
            return this;
        }

        public _7<A, B, C, D, E, F, G> execute(final Block8<Drools, A, B, C, D, E, F, G> block ) {
            this.usingDrools = true;
            this.block = block.asBlockN();
            return this;
        }

        public _7<A, B, C, D, E, F, G> executeScript(String language, Class<?> ruleClass, String script) {
            this.usingDrools = true;
            this.language = language;
            this.block = new ScriptBlock(ruleClass, script);
            return this;
        }
    }

    public static class _8<A, B, C, D, E, F, G, H> extends AbstractValidBuilder<_8<A,B,C,D,E,F,G,H>> {
        public _8(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3, Variable<D> decl4, Variable<E> decl5, Variable<F> decl6,
                   Variable<G> decl7, Variable<H> decl8) {
            super(decl1, decl2, decl3, decl4, decl5, decl6, decl7, decl8);
        }

        public _8<A, B, C, D, E, F, G, H> execute(final Block8<A, B, C, D, E, F, G, H> block) {
            this.block = block.asBlockN();
            return this;
        }

        public _8<A, B, C, D, E, F, G, H> execute(final Block9<Drools, A, B, C, D, E, F, G, H> block ) {
            this.usingDrools = true;
            this.block = block.asBlockN();
            return this;
        }

        public _8<A, B, C, D, E, F, G, H> executeScript(String language, Class<?> ruleClass, String script) {
            this.usingDrools = true;
            this.language = language;
            this.block = new ScriptBlock(ruleClass, script);
            return this;
        }
    }

    public static class _9<A, B, C, D, E, F, G, H, I> extends AbstractValidBuilder<_9<A,B,C,D,E,F,G,H,I>> {
        public _9(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3, Variable<D> decl4, Variable<E> decl5, Variable<F> decl6,
                   Variable<G> decl7, Variable<H> decl8, Variable<I> decl9) {
            super(decl1, decl2, decl3, decl4, decl5, decl6, decl7, decl8, decl9);
        }

        public _9<A, B, C, D, E, F, G, H, I> execute(final Block9<A, B, C, D, E, F, G, H, I> block) {
            this.block = block.asBlockN();
            return this;
        }

        public _9<A, B, C, D, E, F, G, H, I> execute(final Block10<Drools, A, B, C, D, E, F, G, H, I> block ) {
            this.usingDrools = true;
            this.block = block.asBlockN();
            return this;
        }

        public _9<A, B, C, D, E, F, G, H, I> executeScript(String language, Class<?> ruleClass, String script) {
            this.usingDrools = true;
            this.language = language;
            this.block = new ScriptBlock(ruleClass, script);
            return this;
        }
    }

    public static class _10<A, B, C, D, E, F, G, H, I, J> extends AbstractValidBuilder<_10<A,B,C,D,E,F,G,H,I,J>> {
        public _10(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3, Variable<D> decl4, Variable<E> decl5, Variable<F> decl6,
                   Variable<G> decl7, Variable<H> decl8, Variable<I> decl9, Variable<J> decl10) {
            super(decl1, decl2, decl3, decl4, decl5, decl6, decl7, decl8, decl9, decl10);
        }

        public _10<A, B, C, D, E, F, G, H, I, J> execute(final Block10<A, B, C, D, E, F, G, H, I, J> block) {
            this.block = block.asBlockN();
            return this;
        }

        public _10<A, B, C, D, E, F, G, H, I, J> execute(final Block11<Drools, A, B, C, D, E, F, G, H, I, J> block ) {
            this.usingDrools = true;
            this.block = block.asBlockN();
            return this;
        }

        public _10<A, B, C, D, E, F, G, H, I, J> executeScript(String language, Class<?> ruleClass, String script) {
            this.usingDrools = true;
            this.language = language;
            this.block = new ScriptBlock(ruleClass, script);
            return this;
        }
    }

    public static class _11<A, B, C, D, E, F, G, H, I, J, K> extends AbstractValidBuilder<_11<A,B,C,D,E,F,G,H,I,J,K>> {
        public _11(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3, Variable<D> decl4, Variable<E> decl5, Variable<F> decl6,
                   Variable<G> decl7, Variable<H> decl8, Variable<I> decl9, Variable<J> decl10, Variable<K> decl11) {
            super(decl1, decl2, decl3, decl4, decl5, decl6, decl7, decl8, decl9, decl10, decl11);
        }

        public _11<A, B, C, D, E, F, G, H, I, J, K> execute(final Block11<A, B, C, D, E, F, G, H, I, J, K> block) {
            this.block = block.asBlockN();
            return this;
        }

        public _11<A, B, C, D, E, F, G, H, I, J, K> execute(final Block12<Drools, A, B, C, D, E, F, G, H, I, J, K> block ) {
            this.usingDrools = true;
            this.block = block.asBlockN();
            return this;
        }

        public _11<A, B, C, D, E, F, G, H, I, J, K> executeScript(String language, Class<?> ruleClass, String script) {
            this.usingDrools = true;
            this.language = language;
            this.block = new ScriptBlock(ruleClass, script);
            return this;
        }
    }

    public static class _12<A, B, C, D, E, F, G, H, I, J, K, L> extends AbstractValidBuilder<_12<A,B,C,D,E,F,G,H,I,J,K,L>> {
        public _12(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3, Variable<D> decl4, Variable<E> decl5, Variable<F> decl6,
                   Variable<G> decl7, Variable<H> decl8, Variable<I> decl9, Variable<J> decl10, Variable<K> decl11, Variable<L> decl12) {
            super(decl1, decl2, decl3, decl4, decl5, decl6, decl7, decl8, decl9, decl10, decl11, decl12);
        }

        public _12<A, B, C, D, E, F, G, H, I, J, K, L> execute(final Block12<A, B, C, D, E, F, G, H, I, J, K, L> block) {
            this.block = block.asBlockN();
            return this;
        }

        public _12<A, B, C, D, E, F, G, H, I, J, K, L> execute(final Block13<Drools, A, B, C, D, E, F, G, H, I, J, K, L> block ) {
            this.usingDrools = true;
            this.block = block.asBlockN();
            return this;
        }

        public _12<A, B, C, D, E, F, G, H, I, J, K, L> executeScript(String language, Class<?> ruleClass, String script) {
            this.usingDrools = true;
            this.language = language;
            this.block = new ScriptBlock(ruleClass, script);
            return this;
        }
    }

    public static class _13<A, B, C, D, E, F, G, H, I, J, K, L, M> extends AbstractValidBuilder<_13<A,B,C,D,E,F,G,H,I,J,K,L, M>> {
        public _13(Variable<A> decl1, Variable<B> decl2, Variable<C> decl3, Variable<D> decl4, Variable<E> decl5, Variable<F> decl6,
                   Variable<G> decl7, Variable<H> decl8, Variable<I> decl9, Variable<J> decl10, Variable<K> decl11, Variable<L> decl12, Variable<M> decl13) {
            super(decl1, decl2, decl3, decl4, decl5, decl6, decl7, decl8, decl9, decl10, decl11, decl12, decl13);
        }

        public _13<A, B, C, D, E, F, G, H, I, J, K, L, M> execute(final Block13<A, B, C, D, E, F, G, H, I, J, K, L, M> block) {
            this.block = block.asBlockN();
            return this;
        }

        public _13<A, B, C, D, E, F, G, H, I, J, K, L, M> execute(final Block14<Drools, A, B, C, D, E, F, G, H, I, J, K, L, M> block ) {
            this.usingDrools = true;
            this.block = block.asBlockN();
            return this;
        }

        public _13<A, B, C, D, E, F, G, H, I, J, K, L, M> executeScript(String language, Class<?> ruleClass, String script) {
            this.usingDrools = true;
            this.language = language;
            this.block = new ScriptBlock(ruleClass, script);
            return this;
        }
    }

    public static class _N extends AbstractValidBuilder<_N> {

        public _N(Variable... declarations) {
            super(declarations);
        }

        public _N executeScript(String language, Class<?> ruleClass, String script) {
            this.usingDrools = true;
            this.language = language;
            this.block = new ScriptBlock(ruleClass, script);
            return this;
        }
    }
}
