package io.github.ianw11.hivecarbon.engines;

import io.github.ianw11.hivecarbon.Player.Player;
import io.github.ianw11.hivecarbon.graph.Coordinate;

public class MoveTurnAction extends TurnAction {
   
   public final Coordinate oldCoordinate;
   
   public MoveTurnAction(Coordinate oldCoordinate, Coordinate targetCoordinate, Player player) {
      super(Action.MOVE, targetCoordinate, player);
      this.oldCoordinate = oldCoordinate;
   }

}
