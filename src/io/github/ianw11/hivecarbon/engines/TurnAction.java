package io.github.ianw11.hivecarbon.engines;

import io.github.ianw11.hivecarbon.Player.Player;
import io.github.ianw11.hivecarbon.graph.Coordinate;

public abstract class TurnAction {
   
   public enum Action {
      PLAY,
      MOVE
   };
   
   public final Action mAction;
   public final Coordinate mCoordinate;
   public final Player mPlayer;
   
   public TurnAction(Action action, Coordinate coordinate, Player player) {
      mAction = action;
      mCoordinate = coordinate;
      mPlayer = player;
   }

}
