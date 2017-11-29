package org.drools.model.functions.accumulate;

import java.io.Serializable;
import java.util.Optional;

import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.impl.ModelComponent;

public class Min<T> extends AbstractAccumulateFunction<T, Min.Context, Double> implements ModelComponent {

   private final Function1<T, ? extends Number> mapper;

   public Min(Optional<Variable<T>> source, Function1<T, Double> mapper, Optional<String> paramName) {
       super(source, paramName);
       this.mapper = mapper;
   }

   @Override
   public Context init() {
       return new Context();
   }

   @Override
   public void action(Context acc, T obj) {
       acc.add(mapper.apply(obj));
   }

   @Override
   public void reverse(Context acc, T obj) {

   }


   @Override
   public Double result(Context acc) {
       return acc.result();
   }

   @Override
   public Optional<Variable<T>> getOptSource() {
       return optSource;
   }

   public static class Context implements Serializable {
       private double min = Double.MAX_VALUE;

       private void add(Number value) {
           min = Math.min(min, value.doubleValue());
       }

       private double result() {
           return min;
       }
   }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof Min) ) return false;

        Min<?> that = ( Min<?> ) o;

        if ( !ModelComponent.areEqualInModel( getVariable(), that.getVariable() ) ) return false;
        return mapper.equals( that.mapper );
    }
}