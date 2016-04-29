package io.github.ianw11.hivecarbon.test;

import java.util.LinkedList;
import java.util.Queue;

import io.github.ianw11.hivecarbon.Player.Player;
import io.github.ianw11.hivecarbon.engines.RulesEngine;
import io.github.ianw11.hivecarbon.engines.RulesEngine.Action;
import io.github.ianw11.hivecarbon.engines.Turn;
import io.github.ianw11.hivecarbon.engines.TurnFactory;
import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.piece.Piece;

public class TestTurnFactory extends TurnFactory {
   
   private class TestTurn extends Turn {
      private final Coordinate destCoordinate;
      private final Coordinate sourceCoordinate;
      private final Piece pieceToPlay;
      private final Action turnAction;
      
      public TestTurn(Action action, Piece piece, Coordinate source, Coordinate dest) {
         turnAction = action;
         pieceToPlay = piece;
         sourceCoordinate = source;
         destCoordinate = dest;
      }
      
      @Override
      public Coordinate getDestinationCoordinate() {
         return destCoordinate;
      }
      
      @Override
      public Piece getPieceToPlay() {
         return pieceToPlay;
      }
      
      @Override
      public Coordinate getSourceCoordinate() {
         return sourceCoordinate;
      }
      
      @Override
      public Action getTurnAction() {
         return turnAction;
      }
      
      @Override
      protected boolean verifyTurnState() {
         return true;
      }
   }
   
   private Queue<TestTurn> mQueue = new LinkedList<TestTurn>();
   
   public void addTurn(Action action, Piece piece, Coordinate source, Coordinate dest) {
      mQueue.add(new TestTurn(action, piece, source, dest));
   }
   
   @Override
   public Turn getTurn(Player player, RulesEngine engine) {
      return mQueue.remove();
   }

}
