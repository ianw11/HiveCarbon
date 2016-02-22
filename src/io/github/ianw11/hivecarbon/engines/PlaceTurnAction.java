package io.github.ianw11.hivecarbon.engines;

import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.piece.Piece;

public class PlaceTurnAction extends TurnAction {
   
   public PlaceTurnAction(Piece piece, Coordinate coordinate, int playerNumber) {
      super(piece, Action.PLAY, coordinate, playerNumber);
   }

}
