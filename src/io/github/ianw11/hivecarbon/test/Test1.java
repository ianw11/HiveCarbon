package io.github.ianw11.hivecarbon.test;

import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.test.TestDriver.TestObject;

public class Test1 extends TestObject {

   @Override
   public void printTestInfo() {
      System.out.println("TEST 1 - PLACE ONE PIECE");
   }

   @Override
   public boolean run() {
      
      // Adding P1-QNB to 0,0 should succeed
      placePieceExpectSuccess(playerOnePieces.get(0), new Coordinate(0,0), 0, false, new int[] {0,0,0,0});
      
      // Adding P2-QNB to -1,0 should succeed
      placePieceExpectSuccess(playerTwoPieces.get(0), new Coordinate(-1,0), 1, false, new int[] {-1,0,0,0});
      
      // Adding P1-QNB to -1,0 should fail
      placePieceExpectFail(playerOnePieces.get(0), new Coordinate(-1,0), 0, false, new int[] {-1,0,0,0});
      
      // Adding P1-QNB to 1,0 should fail
      placePieceExpectFail(playerOnePieces.get(0), new Coordinate(1,0), 0, false, new int[] {-1,0,0,0});
      
      // Adding P2-QNB to 1,0 should fail
      placePieceExpectFail(playerTwoPieces.get(0), new Coordinate(1,0), 1, false, new int[] {-1,0,0,0});
      
      // Adding P2-BTL to 1,0 should fail
      placePieceExpectFail(playerTwoPieces.get(1), new Coordinate(1,0), 1, false, new int[] {-1,0,0,0});
      
      // Adding P1-BTL to 1,0 should succeed
      placePieceExpectSuccess(playerOnePieces.get(1), new Coordinate(1,0), 0, false, new int[] {-1,1,0,0});
      
      // Adding P2-BTL to -1,1 should succeed
      placePieceExpectSuccess(playerTwoPieces.get(1), new Coordinate(-1,1), 1, false, new int[] {-1,1,0,1});
      
      graph.renderToConsole();
      
      return true;
   }

}
