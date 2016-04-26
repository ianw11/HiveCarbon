package io.github.ianw11.hivecarbon.test;

import java.util.List;
import java.util.Scanner;

import io.github.ianw11.hivecarbon.Player.Player;
import io.github.ianw11.hivecarbon.engines.MoveTurnAction;
import io.github.ianw11.hivecarbon.engines.PlaceTurnAction;
import io.github.ianw11.hivecarbon.engines.RulesEngine;
import io.github.ianw11.hivecarbon.engines.TurnAction;
import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.piece.Piece;

public class HumanTestDriver {
   
   private enum TestOptions {
      QUIT,
      PLACE_PIECE,
      MOVE_PIECE;
   }
   
   private static final TestOptions[] OPTIONS = TestOptions.values();
   
   private static final String[] mPlayerNames = new String[] { "Ian", "Robby" };
   private static final RulesEngine mRulesEngine = new RulesEngine(mPlayerNames);
   
   private static Scanner mScanner;
   
   private static String mOptionString = null;

   public static void main(String[] args) {
      System.out.println("Welcome to Hive!  Play a test game to verify functionality!");
      
      mScanner = new Scanner(System.in);
      
      initializeToPredefinedState();
      
      boolean isDone = false;
      while (!isDone) {
         printGameStatus();
         printOptions();
         
         final TestOptions choice = getInput();
         
         isDone = processChoice(choice, mRulesEngine.getCurrentPlayer());
      }
      
      mScanner.close();
      mScanner = null;
      
      System.out.println("Bye!");
   }

   private static void printGameStatus() {
      System.out.println("\n---------------------------\n---------------------------");
      System.out.println("Game turn number: " + mRulesEngine.getGameTurnNumber());
      System.out.println(mRulesEngine.getCurrentPlayer().getName() + "'s turn");
      System.out.println("---------------------------");
      
      mRulesEngine.renderGraphToConsole();
   }
   
   private static void printOptions() {
      // Build once and reuse
      if (mOptionString == null) {
         StringBuilder sb = new StringBuilder();
         
         sb.append("\nOPTIONS:\n");
         
         int i = 0;
         for (TestOptions t : OPTIONS) {
            sb.append((i++) + ": " + t.toString() + "\n");
         }
         
         sb.append("\n");
         
         mOptionString = sb.toString();
      }
      
      System.out.println(mOptionString);
   }
   
   private static TestOptions getInput() {
      TestOptions choice;
      try {
         
         choice = OPTIONS[readIntFromStdIn()];
         
      } catch (NumberFormatException e) {
         choice = null;
      } catch (IndexOutOfBoundsException e) {
         System.out.println("Enter a valid index");
         choice = null;
      }
      
      return choice;
   }
   
   // Returns if the game is completed
   private static boolean processChoice(TestOptions choice, Player player) {
      if (choice == null) {
         return false;
      }
      
      // Determine the action to perform
      final TurnAction action;
      switch (choice) {
      case QUIT:
         return true;
      
      case PLACE_PIECE:
         final Piece placePiece = chooseUnplayedPiece(player);
         final Coordinate coordinate = chooseEmptyCoordinate(false);
         action = new PlaceTurnAction(placePiece, coordinate, player);
         break;
      
      case MOVE_PIECE:
         final Coordinate moveOldCoordinate = chooseActiveCoordinate(player);
         if (moveOldCoordinate == null) {
            return false;
         }
         final Coordinate moveTargetCoordinate = chooseEmptyCoordinate(true);
         action = new MoveTurnAction(moveOldCoordinate, moveTargetCoordinate, player);
         break;
      
      default:
         throw new IllegalStateException("No choice has been selected");
      }
      
      
      // Perform the action and update state as needed
      final boolean legalAction = mRulesEngine.turn(action);
      
      if (!legalAction) {
         System.out.println("Illegal action");
      }
      
      return mRulesEngine.isGameFinished();
   }
   
   
   private static Piece chooseUnplayedPiece(Player currPlayer) {
      final Piece[] unplayedPieces = currPlayer.getUnusedPieces();
      
      System.out.println("Choose an unplayed piece");
      for (int i = 0; i < unplayedPieces.length; ++i) {
         System.out.println(i + ": " + unplayedPieces[i].toString());
      }
      
      Piece selectedPiece = null;
      while (selectedPiece == null) {
         try {
            selectedPiece = unplayedPieces[readIntFromStdIn()];
         } catch (NumberFormatException e) {
         } catch (IndexOutOfBoundsException e) {
            System.out.println("Enter a valid index");
         }
      }
      
      return selectedPiece;
   }
   
   private static Coordinate chooseActiveCoordinate(Player currPlayer) {
      List<Coordinate> activeCoordinates = mRulesEngine.getActiveCoordinates(currPlayer);
      
      if (activeCoordinates.size() == 0) {
         printErr("No valid coordinates");
         return null;
      }
      
      System.out.println("Choose a piece to move");
      for (int i = 0; i < activeCoordinates.size(); ++i) {
         final Piece piece = mRulesEngine.getPieceAtCoordinate(activeCoordinates.get(i));
         System.out.println(i + ": " + piece);
      }
      
      Coordinate selectedCoordinate = null;
      while (selectedCoordinate == null) {
         try {
            selectedCoordinate = activeCoordinates.get(readIntFromStdIn());
         } catch (NumberFormatException e) {
         } catch (IndexOutOfBoundsException e) {
            System.out.println("Enter a valid index");
         }
      }
      
      return selectedCoordinate;
   }
   
   private static Coordinate chooseEmptyCoordinate(boolean isMove) {
      List<Coordinate> emptyNodes = mRulesEngine.getLegalPlacementCoordinates(isMove);
      
      System.out.println("Choose an empty coordinate");
      for (int i = 0; i < emptyNodes.size(); ++i) {
         System.out.println(i + ": " + emptyNodes.get(i));
      }
      
      Coordinate selectedCoordinate = null;
      while (selectedCoordinate == null) {
         try {
            selectedCoordinate = emptyNodes.get(readIntFromStdIn());
         } catch (NumberFormatException e) {
         } catch (IndexOutOfBoundsException e) {
            System.out.println("Enter a valid index");
         }
      }
      
      return selectedCoordinate;
   }
   
   
   private static void initializeToPredefinedState() {
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
   }
   
   
   private static int readIntFromStdIn() throws NumberFormatException {
      System.out.print("Enter number> ");
      
      try {
         
         return Integer.parseInt(mScanner.nextLine());
         
      } catch (NumberFormatException e) {
         System.out.println("Enter a number");
         throw e;
      }
   }

   private static void printErr(String str) {
      System.out.flush();
      System.err.println(str);
   }
}
