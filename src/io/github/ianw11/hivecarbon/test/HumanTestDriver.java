package io.github.ianw11.hivecarbon.test;

import io.github.ianw11.hivecarbon.engines.RulesEngine;

public class HumanTestDriver {
   
   private static final String[] mPlayerNames = new String[] { "Ian", "Robby" };
   private static final RulesEngine mRulesEngine = new RulesEngine(mPlayerNames);

   public static void main(String[] args) {
      System.out.println("Welcome to Hive!  Play a test game to verify functionality!");
      
      //initializeToPredefinedState();
      
      boolean isDone = false;
      while (!isDone) {
         printGameStatus();
         
         isDone = mRulesEngine.turn();
      }
      
      System.out.println("Bye!");
   }

   private static void printGameStatus() {
      System.out.println("\n---------------------------\n---------------------------");
      System.out.println("Game turn number: " + mRulesEngine.getGameTurnNumber());
      System.out.println(mRulesEngine.getCurrentPlayer().getName() + "'s turn");
      System.out.println("---------------------------");
      
      mRulesEngine.renderGraphToConsole();
   }
   
   
   private static void initializeToPredefinedState() {
      /*
      final Player playerOne = mRulesEngine.getCurrentPlayer();
      final Piece[] playerOneUnplayedPieces = playerOne.getUnusedPieces();
      final Piece playerOneFirstPiece = playerOneUnplayedPieces[1];
      final Coordinate playerOneFirstCoordinate = mRulesEngine.getLegalPlacementCoordinates(false).get(0);
      final TurnAction playerOneAction = new PlaceTurnAction(playerOneFirstPiece, playerOneFirstCoordinate, playerOne);
      
      mRulesEngine.turn(playerOneAction);
      
      final Player playerTwo = mRulesEngine.getCurrentPlayer();
      final Piece[] playerTwoUnplayedPieces = playerTwo.getUnusedPieces();
      final Piece playerTwoFirstPiece = playerTwoUnplayedPieces[1];
      final Coordinate playerTwoFirstCoordinate = mRulesEngine.getLegalPlacementCoordinates(false).get(1);
      final TurnAction playerTwoAction = new PlaceTurnAction(playerTwoFirstPiece, playerTwoFirstCoordinate, playerTwo);
      
      mRulesEngine.turn(playerTwoAction);
      */
   }

   
}
