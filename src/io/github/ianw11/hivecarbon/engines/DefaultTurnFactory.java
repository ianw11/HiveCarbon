package io.github.ianw11.hivecarbon.engines;

import java.util.Scanner;

import io.github.ianw11.hivecarbon.Player.Player;

public class DefaultTurnFactory extends TurnFactory {
   
   Scanner scanner = new Scanner(System.in);

   @Override
   public Turn getTurn(Player player, RulesEngine rulesEngine) {
      return new GameTurn(player, rulesEngine, scanner);
   }
   
}
