package io.github.ianw11.hivecarbon.turn;

import java.util.Scanner;

import io.github.ianw11.hivecarbon.Player.Player;
import io.github.ianw11.hivecarbon.engines.RulesEngine;
import io.github.ianw11.hivecarbon.io.InputMethod;
import io.github.ianw11.hivecarbon.io.KeyboardInputMethod;

public class DefaultTurnFactory extends TurnFactory {
   
   private final Scanner scanner = new Scanner(System.in);
   private final InputMethod inputMethod = new KeyboardInputMethod(scanner);

   @Override
   public Turn getTurn(Player player, RulesEngine rulesEngine) {
      return new GameTurn(player, rulesEngine, inputMethod);
   }
   
}
