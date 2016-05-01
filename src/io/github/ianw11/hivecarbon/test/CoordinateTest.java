package io.github.ianw11.hivecarbon.test;

import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.graph.GraphNode.HexDirection;
import io.github.ianw11.hivecarbon.test.TestDriver.TestObject;

public class CoordinateTest extends TestObject {

   @Override
   public void printTestInfo() {
      System.out.println("LOCATION TEST");
   }

   @Override
   public boolean run() {
      
      /*
       * Testing even x-coordinate
       */
      
      Coordinate base = new Coordinate(0, 0);
      
      Coordinate c = Coordinate.sum(base, HexDirection.TOP);
      expectEqual(c, new Coordinate(0, -1));
      
      c = Coordinate.sum(base, HexDirection.TOP_RIGHT);
      expectEqual(c, new Coordinate(1, -1));
      
      c = Coordinate.sum(base, HexDirection.BOTTOM_RIGHT);
      expectEqual(c, new Coordinate(1, 0));
      
      c = Coordinate.sum(base, HexDirection.BOTTOM);
      expectEqual(c, new Coordinate(0, 1));
      
      c = Coordinate.sum(base, HexDirection.BOTTOM_LEFT);
      expectEqual(c, new Coordinate(-1, 0));
      
      c = Coordinate.sum(base, HexDirection.TOP_LEFT);
      expectEqual(c, new Coordinate(-1, -1));
      
      
      /*
       * Testing odd x-coordinate
       */
      
      base = new Coordinate(1, 0);
      
      c = Coordinate.sum(base, HexDirection.TOP);
      expectEqual(c, new Coordinate(1, -1));
      
      c = Coordinate.sum(base, HexDirection.TOP_RIGHT);
      expectEqual(c, new Coordinate(2, 0));
      
      c = Coordinate.sum(base, HexDirection.BOTTOM_RIGHT);
      expectEqual(c, new Coordinate(2, 1));
      
      c = Coordinate.sum(base, HexDirection.BOTTOM);
      expectEqual(c, new Coordinate(1, 1));
      
      c = Coordinate.sum(base, HexDirection.BOTTOM_LEFT);
      expectEqual(c, new Coordinate(0, 1));
      
      c = Coordinate.sum(base, HexDirection.TOP_LEFT);
      expectEqual(c, new Coordinate(0, 0));
      
      
      Coordinate hashC = new Coordinate(1, 1);
      c = new Coordinate(1,1);
      expectEqual(c.hashCode(), hashC.hashCode());
      
      return true;
   }

}
