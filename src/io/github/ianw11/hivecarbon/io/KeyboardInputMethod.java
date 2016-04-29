package io.github.ianw11.hivecarbon.io;

import java.util.Scanner;

public class KeyboardInputMethod extends InputMethod {
   
   private final Scanner mScanner;
   
   public KeyboardInputMethod(Scanner scanner) {
      mScanner = scanner;
   }

   @Override
   public int readInt() throws NumberFormatException {
      System.out.print("Enter number> ");
      
      try {
         
         return Integer.parseInt(mScanner.nextLine());
         
      } catch (NumberFormatException e) {
         System.out.println("Enter a number");
         throw e;
      }
   }

}
