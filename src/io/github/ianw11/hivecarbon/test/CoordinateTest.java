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
      Coordinate base = reset();
      
      Coordinate c = Coordinate.sum(base, HexDirection.TOP, 0);
      expectEqual(c, new Coordinate(0, -1));
      
      c = Coordinate.sum(base, HexDirection.TOP_RIGHT, 0);
      expectEqual(c, new Coordinate(1, -1));
      
      c = Coordinate.sum(base, HexDirection.BOTTOM_RIGHT, 0);
      expectEqual(c, new Coordinate(1, 0));
      
      c = Coordinate.sum(base, HexDirection.BOTTOM, 0);
      expectEqual(c, new Coordinate(0, 1));
      
      c = Coordinate.sum(base, HexDirection.BOTTOM_LEFT, 0);
      expectEqual(c, new Coordinate(-1, 0));
      
      c = Coordinate.sum(base, HexDirection.TOP_LEFT, 0);
      expectEqual(c, new Coordinate(-1, -1));
      
      
      c = Coordinate.sum(base, HexDirection.TOP, 1);
      expectEqual(c, new Coordinate(0, -1));
      
      c = Coordinate.sum(base, HexDirection.TOP_RIGHT, 1);
      expectEqual(c, new Coordinate(1, 0));
      
      c = Coordinate.sum(base, HexDirection.BOTTOM_RIGHT, 1);
      expectEqual(c, new Coordinate(1, 1));
      
      c = Coordinate.sum(base, HexDirection.BOTTOM, 1);
      expectEqual(c, new Coordinate(0, 1));
      
      c = Coordinate.sum(base, HexDirection.BOTTOM_LEFT, 1);
      expectEqual(c, new Coordinate(-1, 1));
      
      c = Coordinate.sum(base, HexDirection.TOP_LEFT, 1);
      expectEqual(c, new Coordinate(-1, 0));
      
      
      Coordinate hashC = new Coordinate(1, 1);
      c = new Coordinate(1,1);
      expectEqual(c.hashCode(), hashC.hashCode());
      
      return true;
   }
   
   private Coordinate reset() {
      return new Coordinate(0, 0);
   }

}
