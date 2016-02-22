package io.github.ianw11.hivecarbon.engines;

import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.piece.Piece;

public abstract class TurnAction {
   
   public enum Action {
      PLAY,
      MOVE
   };
   
   public final Piece mPiece;
   public final Action mAction;
   
   public final Coordinate mCoordinate;
   
   public final int mPlayerNumber;
   
   public TurnAction(Piece piece, Action action, Coordinate coordinate, int playerNumber) {
      mPiece = piece;
      mAction = action;
      mCoordinate = coordinate;
      mPlayerNumber = playerNumber;
   }

}
