package io.github.ianw11.hivecarbon.engines;

import io.github.ianw11.hivecarbon.engines.RulesEngine.Action;
import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.piece.Piece;

public abstract class Turn {
   
   // Resulting data
   protected Action mTurnAction;
   protected Piece mPieceToPlay; // Used for PLACING a piece
   protected Coordinate mSourceCoordinate; // Used for MOVING a piece
   protected Coordinate mDestinationCoordinate; // Used for PLACING *AND* MOVING
   
   public Action getTurnAction() {
      return mTurnAction;
   }
   
   public Piece getPieceToPlay() {
      if (!verifyTurnState()) {
         throw new IllegalStateException("Turn not ready");
      }
      if (mTurnAction != Action.PLAY) {
         throw new IllegalStateException("Attempting to get piece when not a PLAY action");
      }
      if (mPieceToPlay == null) {
         throw new IllegalStateException("Piece to play is NULL");
      }
      
      return mPieceToPlay;
   }
   
   public Coordinate getSourceCoordinate() {
      if (!verifyTurnState()) {
         throw new IllegalStateException("Turn not ready");
      }
      if (mTurnAction != Action.MOVE) {
         throw new IllegalStateException("Attempting to move piece when not a MOVE action");
      }
      if (mSourceCoordinate == null) {
         throw new IllegalStateException("Source coordinate is NULL");
      }
      
      return mSourceCoordinate;
   }
   
   public Coordinate getDestinationCoordinate() {
      if (!verifyTurnState()) {
         throw new IllegalStateException("Turn not ready");
      }
      if (mDestinationCoordinate == null) {
         throw new IllegalStateException("Destination coordinate is NULL");
      }
      
      return mDestinationCoordinate;
   }
   
   protected abstract boolean verifyTurnState();

}
