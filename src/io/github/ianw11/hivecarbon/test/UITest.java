package io.github.ianw11.hivecarbon.test;

import io.github.ianw11.hivecarbon.Driver;
import io.github.ianw11.hivecarbon.engines.PlaceTurnAction;
import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.test.TestDriver.TestObject;

public class UITest extends TestObject {

   @Override
   public void printTestInfo() {
      System.out.println("UI TEST");
   }

   @Override
   public boolean run() {
      
      Driver driver = new Driver();
      
      driver.doAction(new PlaceTurnAction(playerOnePieces.get(0), new Coordinate(0,0), players.get(0)));
      
      driver.close();
      
      return true;
   }

}
