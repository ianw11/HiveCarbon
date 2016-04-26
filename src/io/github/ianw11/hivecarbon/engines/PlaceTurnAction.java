package io.github.ianw11.hivecarbon.engines;

import io.github.ianw11.hivecarbon.Player.Player;
import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.piece.Piece;

public class PlaceTurnAction extends TurnAction {
   
   public final Piece mPiece;
   
   public PlaceTurnAction(Piece piece, Coordinate coordinate, Player player) {
      super(Action.PLAY, coordinate, player);
      
      mPiece = piece;
   }

}
