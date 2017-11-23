   package org.drools.model.functions.accumulate;

   import java.io.Serializable;
   import java.util.Optional;

   import org.drools.model.AccumulateFunction;
   import org.drools.model.Variable;
   import org.drools.model.functions.Function1;

   public class Count<T> extends AbstractAccumulateFunction<T,Count.Context<T>,Integer> {

       public Count(Optional<Variable<T>> source) {
           super(source);
       }

       @Override
       public Context<T> init() {
           return new Context<T>();
       }

       @Override
       public void action(Context<T> acc, T obj) {
           acc.increment();
       }

       @Override
       public void reverse(Context<T> acc, T obj) {
           acc.subtract();
       }

       @Override
       public Integer result(Context<T> acc) {
           return acc.result();
       }

       @Override
       public Optional<Variable<T>> getOptSource() {
           return optSource;
       }

       public static class Context<Int> implements Serializable {
           private int total;

           public Context() {
               this.total = 0;
           }

           private void increment() {
               this.total++;
           }

           private void subtract() {
               this.total--;
           }

           private int result() {
               return total;
           }
       }
   }