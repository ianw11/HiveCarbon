package io.github.ianw11.hivecarbon.engines;

import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.graph.GraphNode;
import io.github.ianw11.hivecarbon.piece.Piece;

public class MoveTurnAction extends TurnAction {
   
   public final GraphNode oldGraphNode;
   
   public MoveTurnAction(Piece piece, GraphNode oldGraphNode, Coordinate targetCoordinate, int playerNumber) {
      super(piece, Action.MOVE, targetCoordinate, playerNumber);
      this.oldGraphNode = oldGraphNode;
   }

}
