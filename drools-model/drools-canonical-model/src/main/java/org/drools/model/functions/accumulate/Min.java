   package org.drools.model.functions.accumulate;

   import java.io.Serializable;
   import java.util.Optional;

   import org.drools.model.Variable;
   import org.drools.model.functions.Function1;

   public class Min<T> extends AbstractAccumulateFunction<T, Min.Context, Double> {

       private final Function1<T, ? extends Number> mapper;

       public Min(Optional<Variable<T>> source, Function1<T, Double> mapper) {
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
   }