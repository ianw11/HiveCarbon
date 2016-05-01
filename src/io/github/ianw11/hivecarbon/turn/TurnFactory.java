package io.github.ianw11.hivecarbon.turn;

import io.github.ianw11.hivecarbon.Player.Player;
import io.github.ianw11.hivecarbon.engines.RulesEngine;

public abstract class TurnFactory {

   public abstract Turn getTurn(Player player, RulesEngine engine);
   
}
