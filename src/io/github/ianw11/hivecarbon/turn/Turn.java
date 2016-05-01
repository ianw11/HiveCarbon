package io.github.ianw11.hivecarbon.turn;

import io.github.ianw11.hivecarbon.engines.RulesEngine.Action;
import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.piece.Piece;

public abstract class Turn {
   
   // Resulting data
   private Action mTurnAction;
   private Piece mPieceToPlay; // Used for PLACING a piece
   private Coordinate mSourceCoordinate; // Used for MOVING a piece
   private Coordinate mDestinationCoordinate; // Used for PLACING *AND* MOVING
   
   
   /*
    * ABSTRACT METHOD THAT MUST BE OVERRIDDEN
    */
   
   protected abstract boolean isReady();
   
   
   /*
    * PUBLIC GETTERS
    */
   
   public final Action getTurnAction() {
      return mTurnAction;
   }
   
   public final Piece getPieceToPlay() {
      if (!isReady()) {
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
   
   public final Coordinate getSourceCoordinate() {
      if (!isReady()) {
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
   
   public final Coordinate getDestinationCoordinate() {
      if (!isReady()) {
         throw new IllegalStateException("Turn not ready");
      }
      if (mDestinationCoordinate == null) {
         throw new IllegalStateException("Destination coordinate is NULL");
      }
      
      return mDestinationCoordinate;
   }
   
   
   /*
    * PROTECTED SETTERS
    */
   
   protected final void setTurnAction(Action action) {
      mTurnAction = action;
   }
   
   protected final void setPieceToPlay(Piece piece) {
      mPieceToPlay = piece;
   }
   
   protected final void setSourceCoordinate(Coordinate coordinate) {
      mSourceCoordinate = coordinate;
   }
   
   protected final void setDestinationCoordinate(Coordinate coordinate) {
      mDestinationCoordinate = coordinate;
   }
}
