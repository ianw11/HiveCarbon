package io.github.ianw11.hivecarbon.test;

import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.test.TestDriver.TestObject;

public class Test2 extends TestObject {

   @Override
   public void printTestInfo() {
      System.out.println("TEST 2 - MOVE TEST");

   }

   @Override
   public boolean run() {
      setup();
      
      System.out.println("1ST");
      // Moving P1-BTL from 1,0 to 0,-1 should succeed
      movePieceExpectSuccess(playerOnePieces.get(1), new Coordinate(1,0), new Coordinate(0, -1), players.get(0), false, new int[] {-1,0,-1,1});
      
      System.out.println("2ND");
      // Adding P2-BTL to -2,1 should succeed
      placePieceExpectSuccess(playerTwoPieces.get(2), new Coordinate(-2,1), players.get(1), false, new int[] {-2,0,-1,1});
      
      System.out.println("3RD");
      // Moving P1-BTL from 0,-1 to -1,-1 should succeed
      movePieceExpectSuccess(playerOnePieces.get(1), new Coordinate(0,-1), new Coordinate(-1, -1), players.get(0), false, new int[] {-2,0,-1,1});
      expectEqual(playerOnePieces.get(0).getNumNeighbors(), 2);
      expectEqual(playerOnePieces.get(1).getNumNeighbors(), 2);
      expectEqual(playerTwoPieces.get(0).getNumNeighbors(), 4);
      expectEqual(playerTwoPieces.get(1).getNumNeighbors(), 2);
      expectEqual(playerTwoPieces.get(2).getNumNeighbors(), 2);
      
      System.out.println("4TH");
      // Moving P2-BTL from -1,1 to 0,1 should succeed
      movePieceExpectSuccess(playerTwoPieces.get(1), new Coordinate(-1,1), new Coordinate(0, 1), players.get(1), false, new int[] {-2,0,-1,1});
      expectEqual(playerOnePieces.get(0).getNumNeighbors(), 3);
      expectEqual(playerOnePieces.get(1).getNumNeighbors(), 2);
      expectEqual(playerTwoPieces.get(0).getNumNeighbors(), 4);
      expectEqual(playerTwoPieces.get(1).getNumNeighbors(), 2);
      expectEqual(playerTwoPieces.get(2).getNumNeighbors(), 1);
      
      hexGraph.renderToConsole();
      
      return true;
   }
   
   private void setup() {
      // Adding P1-QNB to 0,0 should succeed
      placePieceExpectSuccess(playerOnePieces.get(0), new Coordinate(0,0), players.get(0), false, new int[] {0,0,0,0});
      
      // Adding P2-QNB to -1,0 should succeed
      placePieceExpectSuccess(playerTwoPieces.get(0), new Coordinate(-1,0), players.get(1), false, new int[] {-1,0,0,0});
      
      // Adding P1-BTL to 1,0 should succeed
      placePieceExpectSuccess(playerOnePieces.get(1), new Coordinate(1,0), players.get(0), false, new int[] {-1,1,0,0});
      
      // Adding P2-BTL to -1,1 should succeed
      placePieceExpectSuccess(playerTwoPieces.get(1), new Coordinate(-1,1), players.get(1), false, new int[] {-1,1,0,1});
   }

}
