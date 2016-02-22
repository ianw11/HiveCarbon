package io.github.ianw11.hivecarbon.test;

import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.piece.Piece.Location;
import io.github.ianw11.hivecarbon.test.TestDriver.TestObject;

public class CoordinateTest extends TestObject {

   @Override
   public void printTestInfo() {
      System.out.println("LOCATION TEST");
   }

   @Override
   public boolean run() {
      Coordinate base = reset();
      
      Coordinate c = Coordinate.sum(base, Location.TOP, 0);
      expectEqual(c, new Coordinate(0, -1));
      
      c = Coordinate.sum(base, Location.TOP_RIGHT, 0);
      expectEqual(c, new Coordinate(1, -1));
      
      c = Coordinate.sum(base, Location.BOTTOM_RIGHT, 0);
      expectEqual(c, new Coordinate(1, 0));
      
      c = Coordinate.sum(base, Location.BOTTOM, 0);
      expectEqual(c, new Coordinate(0, 1));
      
      c = Coordinate.sum(base, Location.BOTTOM_LEFT, 0);
      expectEqual(c, new Coordinate(-1, 0));
      
      c = Coordinate.sum(base, Location.TOP_LEFT, 0);
      expectEqual(c, new Coordinate(-1, -1));
      
      
      c = Coordinate.sum(base, Location.TOP, 1);
      expectEqual(c, new Coordinate(0, -1));
      
      c = Coordinate.sum(base, Location.TOP_RIGHT, 1);
      expectEqual(c, new Coordinate(1, 0));
      
      c = Coordinate.sum(base, Location.BOTTOM_RIGHT, 1);
      expectEqual(c, new Coordinate(1, 1));
      
      c = Coordinate.sum(base, Location.BOTTOM, 1);
      expectEqual(c, new Coordinate(0, 1));
      
      c = Coordinate.sum(base, Location.BOTTOM_LEFT, 1);
      expectEqual(c, new Coordinate(-1, 1));
      
      c = Coordinate.sum(base, Location.TOP_LEFT, 1);
      expectEqual(c, new Coordinate(-1, 0));
      
      return true;
   }
   
   private Coordinate reset() {
      return new Coordinate(0, 0);
   }

}
