package org.drools;

import java.util.Random;

public class RandomNumber
{
   private int randomNumber;
   
   public void begin()
   {
      randomNumber = new Random().nextInt(100);
   }
   
   public void setValue( int value ) {
       this.randomNumber = value;
   }
   
   public int getValue() 
   {
      return randomNumber;
   }
   
}
