package io.github.ianw11.hivecarbon.engines;

import io.github.ianw11.hivecarbon.Player.Player;

public abstract class TurnFactory {

   public abstract Turn getTurn(Player player, RulesEngine engine);
   
}
