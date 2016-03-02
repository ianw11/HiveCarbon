package io.github.ianw11.hivecarbon.test;

import io.github.ianw11.hivecarbon.engines.MoveTurnAction;
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
      turnResult = engine.turn(new MoveTurnAction(playerOnePieces.get(0), graph.findGraphNode(new Coordinate(0, 0)), new Coordinate(1,-1), 0));
      {
         assert(turnResult == false);
         assert(engine.isGameFinished() == false);
         verifyBounds(-1, 1, 0, 1);
         assert(playerOnePieces.get(1).isPlaced());
         check(new Coordinate(0, 0), 0, Type.QUEEN_BEE);
         check(new Coordinate(1, 0), 0, Type.BEETLE);
         check(new Coordinate(-1, 0), 1, Type.QUEEN_BEE);
         check(new Coordinate(-1, 1), 1, Type.BEETLE);
      }
      
      System.out.println("2ND");
      // Moving P1-QNB from -1,0 to -2,1 should fail
      turnResult = engine.turn(new MoveTurnAction(playerTwoPieces.get(0), graph.findGraphNode(new Coordinate(-1, 0)), new Coordinate(-2,-1), 1));
      {
         assert(turnResult == false);
         assert(engine.isGameFinished() == false);
         verifyBounds(-1, 1, 0, 1);
         assert(playerOnePieces.get(1).isPlaced());
         check(new Coordinate(0, 0), 0, Type.QUEEN_BEE);
         check(new Coordinate(1, 0), 0, Type.BEETLE);
         check(new Coordinate(-1, 0), 1, Type.QUEEN_BEE);
         check(new Coordinate(-1, 1), 1, Type.BEETLE);
      }
      
      graph.renderToConsole();
      
      return true;
   }

   
   private void setup() {
      // Adding P1-QNB to 0,0 should succeed
      placePieceExpectSuccess(playerOnePieces.get(0), new Coordinate(0,0), 0, false, new int[] {0,0,0,0});
      
      // Adding P2-QNB to -1,0 should succeed
      placePieceExpectSuccess(playerTwoPieces.get(0), new Coordinate(-1,0), 1, false, new int[] {-1,0,0,0});
      
      // Adding P1-BTL to 1,0 should succeed
      placePieceExpectSuccess(playerOnePieces.get(1), new Coordinate(1,0), 0, false, new int[] {-1,1,0,0});
      
      // Adding P2-BTL to -1,1 should succeed
      placePieceExpectSuccess(playerTwoPieces.get(1), new Coordinate(-1,1), 1, false, new int[] {-1,1,0,1});
   }
}
