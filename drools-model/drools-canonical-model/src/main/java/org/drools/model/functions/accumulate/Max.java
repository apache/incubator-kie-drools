package org.drools.model.functions.accumulate;

import java.io.Serializable;
import java.util.Optional;

import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.impl.ModelComponent;

public class Max<T> extends AbstractAccumulateFunction<T, Max.Context, Double> implements ModelComponent {

   private final Function1<T, ? extends Number> mapper;

   public Max(Variable<T> source, Function1<T, Double> mapper) {
       super(source);
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
   public Variable<T> getSource() {
       return source;
   }

   public static class Context implements Serializable {
       private double max = Double.MIN_VALUE;


       private void add(Number value) {
           max = Math.max(max, value.doubleValue());
       }

       private double result() {
           return max;
       }
   }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof Max) ) return false;

        Max<?> that = ( Max<?> ) o;

        if ( !ModelComponent.areEqualInModel( getVariable(), that.getVariable() ) ) return false;
        return mapper.equals( that.mapper );
    }
}