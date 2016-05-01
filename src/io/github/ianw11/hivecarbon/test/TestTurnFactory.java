package io.github.ianw11.hivecarbon.test;

import java.util.LinkedList;
import java.util.Queue;

import io.github.ianw11.hivecarbon.Player.Player;
import io.github.ianw11.hivecarbon.engines.RulesEngine;
import io.github.ianw11.hivecarbon.engines.RulesEngine.Action;
import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.piece.Piece;
import io.github.ianw11.hivecarbon.turn.Turn;
import io.github.ianw11.hivecarbon.turn.TurnFactory;

public class TestTurnFactory extends TurnFactory {
   
   /**
    * The Turn object used in tests.
    * 
    * All data needs to be mocked ahead of time for the engine to process
    *
    */
   private class TestTurn extends Turn {
      
      private TestTurn(Action action, Piece piece, Coordinate source, Coordinate dest) {
         setTurnAction(action);
         setPieceToPlay(piece);
         setSourceCoordinate(source);
         setDestinationCoordinate(dest);
      }
      
      @Override
      protected boolean isReady() {
         /*
          * Always return true during tests
          */
         return true;
      }
   }
   
   // The Turn queue that feeds turns into the game engine
   private Queue<TestTurn> mQueue = new LinkedList<TestTurn>();
   
   /*
    * Method to add mocked data to the turn queue.
    */
   public void addTurn(Action action, Piece piece, Coordinate source, Coordinate dest) {
      mQueue.add(new TestTurn(action, piece, source, dest));
   }
   
   @Override
   public Turn getTurn(Player player, RulesEngine engine) {
      /*
       * Both parameters are ignored because each Turn object
       * should already have been constructed
       */
      
      return mQueue.remove();
   }

}
