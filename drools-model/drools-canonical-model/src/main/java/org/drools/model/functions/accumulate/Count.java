package org.drools.model.functions.accumulate;

import java.io.Serializable;

import org.drools.model.Variable;
import org.drools.model.impl.ModelComponent;

public class Count<T> extends AbstractAccumulateFunction<T,Count.Context<T>,Integer> implements ModelComponent {

   public Count(Variable<T> source) {
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
   public Variable<T> getSource() {
       return source;
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

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof Count) ) return false;

        Count<?> that = ( Count<?> ) o;

        return ModelComponent.areEqualInModel( getVariable(), that.getVariable() );
    }
}