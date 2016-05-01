package io.github.ianw11.hivecarbon.turn;

import java.util.List;

import io.github.ianw11.hivecarbon.Player.Player;
import io.github.ianw11.hivecarbon.engines.RulesEngine;
import io.github.ianw11.hivecarbon.engines.RulesEngine.Action;
import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.io.InputMethod;
import io.github.ianw11.hivecarbon.piece.Piece;

public class GameTurn extends Turn {
   
   /**
    * Represents the state of the turn object as a user inputs choices
    * 
    *                               2a
    *                    ------ QUERY_PIECE --------
    *          1        /                           \              3
    *    SELECT_ACTION-<                             >- QUERY_DESTINATION_COORDINATE
    *                   \           2b              /
    *                    - QUERY_SOURCE_COORDINATE -
    */
   private enum TurnState {
      READY(null, null), // Lock in the turn and send action to rules engine
      SELECT_ACTION(null, null), // Move or Play.  Next is unknown
      QUERY_DESTINATION_COORDINATE(null, READY), // Which coordinate to move TO *OR* place piece on
      QUERY_PIECE(SELECT_ACTION, QUERY_DESTINATION_COORDINATE), // Which piece to play
      QUERY_SOURCE_COORDINATE(SELECT_ACTION, QUERY_DESTINATION_COORDINATE); // Which coordinate to move FROM
      
      
      public final TurnState NEXT;
      public final TurnState PREV;
      
      private TurnState(final TurnState prev, final TurnState next) {
         PREV = prev;
         NEXT = next;
      }
   }
   
   private enum UpdateTurnState {
      FAILURE,
      GO_BACK,
      SUCCESS;
   }
   
   // Static variables representing actions in the game
   protected static final Action[] ACTIONS = Action.values();
   protected static final String mOptionString;
   static {
      final StringBuilder sb = new StringBuilder();
      
      sb.append("\nOPTIONS:\n");
      for (int i = 0; i < ACTIONS.length; ++i) {
         sb.append(i + ": " + ACTIONS[i].toString() + "\n");
      }
      sb.append("\n");
      
      mOptionString = sb.toString();
   }
   private static final int GO_BACK_INPUT = -1;
   
   // The actual current state of the turn building process
   private TurnState mTurnState = TurnState.SELECT_ACTION;
   // The previous state. Used because the TurnState splits for PLAY vs MOVE
   private TurnState mPrevTurnState;
   
   
   // Member variables to build the object
   private final Player mCurrentPlayer;
   private final RulesEngine mRulesEngine;
   private final InputMethod mInputMethod;
   
   
   public GameTurn(final Player player, final RulesEngine rulesEngine, final InputMethod inputMethod) {
      mCurrentPlayer = player;
      mRulesEngine = rulesEngine;
      mInputMethod = inputMethod;
      
      // Build the turn
      while (mTurnState != TurnState.READY) {
         switch (mTurnState) {
         case SELECT_ACTION:
            mTurnState = selectAction();
            break;
         case QUERY_PIECE:
            mTurnState = queryPiece();
            break;
         case QUERY_SOURCE_COORDINATE:
            mTurnState = querySourceCoordinate();
            break;
         case QUERY_DESTINATION_COORDINATE:
            mTurnState = queryDestinationCoordinate(
                  mPrevTurnState == TurnState.QUERY_SOURCE_COORDINATE);
            break;
         default:
            throw new IllegalStateException("Illegal state when performing turn: " + mTurnState);
         }
      }
   }
   
   /**
    * Override Turn method
    */
   @Override
   protected boolean isReady() {
      return mTurnState == TurnState.READY;
   }
   
   /*
    * Private methods to set data as detailed in constructor
    */
   
   private TurnState selectAction() {
      System.out.println(mOptionString);
      
      try {
         setTurnAction(ACTIONS[mInputMethod.readInt()]);
      } catch (NumberFormatException e) {
         return updateTurnState(UpdateTurnState.FAILURE);
      } catch (IndexOutOfBoundsException e) {
         return updateTurnState(UpdateTurnState.FAILURE);
      }
      
      return updateTurnState(UpdateTurnState.SUCCESS);
   }

   private TurnState queryPiece() {
      final Piece[] unplayedPieces = mCurrentPlayer.getUnusedPieces();
      
      System.out.println("Choose an unplayed piece (" + GO_BACK_INPUT + " to go back)");
      for (int i = 0; i < unplayedPieces.length; ++i) {
         System.out.println(i + ": " + unplayedPieces[i].toString());
      }
      
      final int selection;
      try {
         
         selection = mInputMethod.readInt();
         
      } catch (NumberFormatException e) {
         return updateTurnState(UpdateTurnState.FAILURE);
      } catch (IndexOutOfBoundsException e) {
         System.out.println("Enter a valid index");
         return updateTurnState(UpdateTurnState.FAILURE);
      }
      
      if (selection == GO_BACK_INPUT) {
         return updateTurnState(UpdateTurnState.GO_BACK);
      }
      
      setPieceToPlay(unplayedPieces[selection]);
      return updateTurnState(UpdateTurnState.SUCCESS);
   }
   
   private TurnState querySourceCoordinate() {
      final List<Coordinate> activeCoordinates = mRulesEngine.getActiveCoordinates(mCurrentPlayer);
      
      if (activeCoordinates.size() == 0) {
         printerr("No valid coordinates to move");
         return updateTurnState(UpdateTurnState.GO_BACK);
      }
      
      System.out.println("Choose a piece to move (" + GO_BACK_INPUT + " to go back)");
      for (int i = 0; i < activeCoordinates.size(); ++i) {
         final Piece piece = mRulesEngine.getPieceAtCoordinate(activeCoordinates.get(i));
         System.out.println(i + ": " + piece);
      }
      
      final int selection;
      try {
         
         selection = mInputMethod.readInt();
         
      } catch (NumberFormatException e) {
         return updateTurnState(UpdateTurnState.FAILURE);
      } catch (IndexOutOfBoundsException e) {
         System.out.println("Enter a valid index");
         return updateTurnState(UpdateTurnState.FAILURE);
      }
      
      if (selection == GO_BACK_INPUT) {
         return updateTurnState(UpdateTurnState.GO_BACK);
      }
      
      setSourceCoordinate(activeCoordinates.get(selection));
      return updateTurnState(UpdateTurnState.SUCCESS);
   }
   
   private TurnState queryDestinationCoordinate(boolean isMove) {
      final List<Coordinate> emptyNodes = mRulesEngine.getLegalPlacementCoordinates(isMove);
      
      System.out.println("Choose an empty coordinate (-1 to go back)");
      for (int i = 0; i < emptyNodes.size(); ++i) {
         System.out.println(i + ": " + emptyNodes.get(i));
      }
      
      final int selection;
      try {
         
         selection = mInputMethod.readInt();
         
      } catch (NumberFormatException e) {
         return updateTurnState(UpdateTurnState.FAILURE);
      } catch (IndexOutOfBoundsException e) {
         System.out.println("Enter a valid index");
         return updateTurnState(UpdateTurnState.FAILURE);
      }
      
      if (selection == GO_BACK_INPUT) {
         return updateTurnState(UpdateTurnState.GO_BACK);
      }
      
      setDestinationCoordinate(emptyNodes.get(selection));
      return updateTurnState(UpdateTurnState.SUCCESS);
   }
   
   
   /*
    * Methods to update the state of this Turn.
    */
   
   /**
    * Update the internal state to determine what else is required from the player
    * @param success If the current action was a success.
    * @return TurnState The new TurnState to perform
    */
   private TurnState updateTurnState(UpdateTurnState updateState) {
      switch (updateState) {
      case FAILURE:
         // Retry current step
         return mTurnState;
      case GO_BACK:
         // Go back a step
         final TurnState ret = mPrevTurnState;
         mPrevTurnState = mPrevTurnState.PREV;
         return ret;
      case SUCCESS:
         // Advance to next step
         return advanceTurnState();
      default:
         throw new IllegalStateException("No UpdateTurnState provided");
      }
   }
   
   private TurnState advanceTurnState() {
      switch (mTurnState) {
      case SELECT_ACTION:
         mPrevTurnState = TurnState.SELECT_ACTION;
         switch (getTurnAction()) {
         case QUIT:
            return TurnState.READY;
         case MOVE:
            return TurnState.QUERY_SOURCE_COORDINATE;
         case PLAY:
            return TurnState.QUERY_PIECE;
         }
      
      case QUERY_PIECE:
      case QUERY_SOURCE_COORDINATE:
      case QUERY_DESTINATION_COORDINATE:
         mPrevTurnState = mTurnState;
         return mTurnState.NEXT;
      default:
         throw new IllegalStateException("Turn shouldn't update state here");
      }
   }
   
   private void printerr(String str) {
      System.out.flush();
      System.err.println(str);
   }
}
