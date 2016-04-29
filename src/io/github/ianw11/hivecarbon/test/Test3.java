package io.github.ianw11.hivecarbon.test;

import io.github.ianw11.hivecarbon.engines.RulesEngine.Action;
import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.piece.Piece.Type;
import io.github.ianw11.hivecarbon.test.TestDriver.TestObject;

public class Test3 extends TestObject {

   @Override
   public void printTestInfo() {
      System.out.println("TEST3");
   }

   @Override
   public boolean run() {
      setup();
      
      System.out.println("1ST");
      // Moving P0-QNB from 0,0 to 1,-1 should fail
      feedTurn(Action.MOVE, null, new Coordinate(0, 0), new Coordinate(1,-1));
      turnResult = engine.turn();
      {
         assert(turnResult == false);
         verifyBounds(-1, 1, 0, 1);
         assert(playerOnePieces.get(1).isPlaced());
         check(new Coordinate(0, 0), players.get(0), Type.QUEEN_BEE);
         check(new Coordinate(1, 0), players.get(0), Type.BEETLE);
         check(new Coordinate(-1, 0), players.get(1), Type.QUEEN_BEE);
         check(new Coordinate(-1, 1), players.get(1), Type.BEETLE);
      }
      
      System.out.println("2ND");
      // Moving P1-QNB from -1,0 to -2,1 should fail
      feedTurn(Action.MOVE, null, new Coordinate(-1, 0), new Coordinate(-2,-1));
      turnResult = engine.turn();
      {
         assert(turnResult == false);
         verifyBounds(-1, 1, 0, 1);
         assert(playerOnePieces.get(1).isPlaced());
         check(new Coordinate(0, 0), players.get(0), Type.QUEEN_BEE);
         check(new Coordinate(1, 0), players.get(0), Type.BEETLE);
         check(new Coordinate(-1, 0), players.get(1), Type.QUEEN_BEE);
         check(new Coordinate(-1, 1), players.get(1), Type.BEETLE);
      }
      
      hexGraph.renderToConsole();
      
      return true;
   }

   
   private void setup() {
      // Adding P1-QNB to 0,0 should succeed
      placePieceExpectSuccess(playerOnePieces.get(0), new Coordinate(0,0), players.get(0), new int[] {0,0,0,0});
      
      // Adding P2-QNB to -1,0 should succeed
      placePieceExpectSuccess(playerTwoPieces.get(0), new Coordinate(-1,0), players.get(1), new int[] {-1,0,0,0});
      
      // Adding P1-BTL to 1,0 should succeed
      placePieceExpectSuccess(playerOnePieces.get(1), new Coordinate(1,0), players.get(0), new int[] {-1,1,0,0});
      
      // Adding P2-BTL to -1,1 should succeed
      placePieceExpectSuccess(playerTwoPieces.get(1), new Coordinate(-1,1), players.get(1), new int[] {-1,1,0,1});
   }
}
