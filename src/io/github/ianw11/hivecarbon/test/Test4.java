package io.github.ianw11.hivecarbon.test;

import java.util.Arrays;
import java.util.Set;

import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.test.TestDriver.TestObject;

public class Test4 extends TestObject {

   @Override
   public void printTestInfo() {
      System.out.println("TEST4");
   }

   @Override
   public boolean run() {
      setup();
      
      System.out.println("1ST");
      // First determine the perimeter
      Coordinate badCoordinate = (new Coordinate(1, 0));
      Set<Coordinate> perimeter = graph.getPerimeter(badCoordinate);
      assert(perimeter != null);
      assert(perimeter.size() == 9);
      
      // {(0,-1), (1,-1), (0,1), (0,2), (-1,2), (-2,2), (-2,1), (-2,0), (-1,-1)
      Coordinate[] coordinates = new Coordinate[] {
            new Coordinate(0, -1),
            new Coordinate(1, -1),
            new Coordinate(0, 1),
            new Coordinate(0, 2),
            new Coordinate(-1, 2),
            new Coordinate(-2, 2),
            new Coordinate(-2, 1),
            new Coordinate(-2, 0),
            new Coordinate(-1, -1),
      };
      
      boolean[] isPresent = new boolean[9];
      Arrays.fill(isPresent, false);
      for (Coordinate coordinate : perimeter) {
         assert(!coordinate.equals(badCoordinate));
         
         for (int i = 0; i < coordinates.length; ++i) {
            if (coordinate.equals(coordinates[i])) {
               isPresent[i] = true;
            }
         }
      }
      
      for (boolean bool : isPresent) {
         assert(bool);
      }
      
      
      return true;
   }
   
   
   private void setup() {
      // Adding P1-QNB to 0,0 should succeed
      placePieceExpectSuccess(playerOnePieces.get(0), new Coordinate(0,0), 0, false, new int[] {0,0,0,0});
      
      // Adding P2-QNB to -1,0 should succeed
      placePieceExpectSuccess(playerTwoPieces.get(0), new Coordinate(-1,0), 1, false, new int[] {-1,0,0,0});
      
      // Adding P1-ANT to 1,0 should succeed
      placePieceExpectSuccess(playerOnePieces.get(8), new Coordinate(1,0), 0, false, new int[] {-1,1,0,0});
      
      // Adding P2-BTL to -1,1 should succeed
      placePieceExpectSuccess(playerTwoPieces.get(1), new Coordinate(-1,1), 1, false, new int[] {-1,1,0,1});
   }

}
