package io.github.ianw11.hivecarbon.engines;

import java.util.ArrayList;
import java.util.List;

import io.github.ianw11.hivecarbon.Player.Player;
import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.graph.Graph;
import io.github.ianw11.hivecarbon.graph.GraphBounds;
import io.github.ianw11.hivecarbon.graph.GraphBounds.Builder;
import io.github.ianw11.hivecarbon.graph.GraphNode;
import io.github.ianw11.hivecarbon.piece.Piece;

public class RulesEngine {

   private final ArrayList<Player> mPlayers = new ArrayList<Player>();
   private int mPlayerTurn = 0;
   private int mGameTurn = 1;

   private final Graph mGraph;

   public RulesEngine(int numPlayers) {
      for (int i = 0; i < numPlayers; ++i) {
         mPlayers.add(new Player(i));
      }
      
      mGraph = new Graph();
   }

   // TURN is the main method here
   /**
    * Take a game turn, performing the TurnAction
    * @param turnAction The action to perform this turn
    * @return True if successful
    */
   public boolean turn(TurnAction turnAction) {
      boolean ret = false;

      switch (turnAction.mAction) {
      case MOVE:
         ret = performMove((MoveTurnAction)turnAction);
         break;
      case PLAY:
         ret = performPlay((PlaceTurnAction)turnAction);
         break;
      }

      if (ret) {
         incrementTurnNumbers();
      }

      return ret;
   }
   
   /**
    * Returns if the game is completed.
    * @return True if the game is completed.
    */
   public boolean isGameFinished() {
      boolean ret = false;
      for (Player player : mPlayers) {
         ret |= player.isQueenSurrounded();
      }
      return ret;
   }
   
   /*
    * Get an array of unused pieces from a Player
    * @param playerNum The desired player
    * @return An array of all unused pieces
    */
   /*
   private Piece[] getUnusedPieces(int playerNum) {
      return mPlayers.get(playerNum).getUnusedPieces();
   }
   */

   
   private void incrementTurnNumbers() {
      if (++mPlayerTurn == mPlayers.size()) {
         mPlayerTurn = 0;
         ++mGameTurn;
      }
   }
   
   // If the player wants to MOVE a piece
   private boolean performMove(MoveTurnAction action) {
      if (action.mPlayerNumber != mPlayerTurn) {
         return false;
      }
      
      final GraphNode oldNode = action.oldGraphNode;
      final Coordinate coordinate = action.mCoordinate;
      final Piece piece = action.mPiece;
      
      System.out.println("Moving piece " + piece + " to " + coordinate + " from " + oldNode.getCoordinate());
      
      if (!isDestinationOk(coordinate, piece, true) || !pieceCanMove(coordinate, piece) || !mGraph.isConnected(piece)) {
         return false;
      }
      
      mGraph.movePiece(oldNode, coordinate);
      
      return true;
   }
   
   private boolean pieceCanMove(Coordinate coordinate, Piece piece) {
      //final GraphNode destination = mGraph.findGraphNode(coordinate);
      return true;
   }
   
   // If the player wants to PLAY a piece
   private boolean performPlay(PlaceTurnAction action) {
      if (action.mPlayerNumber != mPlayerTurn) {
         return false;
      }

      final Coordinate coordinate = action.mCoordinate;
      final Piece piece = action.mPiece;
      
      System.out.println("Playing piece " + piece + " at " + coordinate);
      
      if (mGameTurn == 1 && mPlayerTurn == 0 && !coordinate.equals(new Coordinate(0, 0))) {
         return false;
      }

      boolean canGoNextToOtherColor = mGameTurn < 3 || mGraph.numActiveNodes() < 3;

      if (!isDestinationOk(coordinate, piece, canGoNextToOtherColor) || piece.isPlaced()) {
         return false;
      }
      
      mGraph.playPiece(piece, coordinate, canGoNextToOtherColor);

      return true;
   }
   
   private boolean isDestinationOk(Coordinate coordinate, Piece piece, boolean canGoNextToOtherColor) {
      GraphNode target = mGraph.findGraphNode(coordinate);
      if (target == null) {
         return true;
      }

      if (mGameTurn == 4 && !mPlayers.get(mPlayerTurn).isQueenPlayed()) {
         System.err.println("Queen not yet played");
         return false;
      }

      return target.canPlacePiece(piece, canGoNextToOtherColor);
   }

   
   
   

   
   /**
    * UI Methods
    */
   public int[] getShift() {
      GraphBounds temp = mGraph.getMapBounds();
      int[] ret = new int[] {
            -temp.MIN_X,
            -temp.MIN_Y
      };
      return ret;
   }
   
   public GraphBounds getNormalizedBounds() {
      GraphBounds temp = mGraph.getMapBounds();
      int[] shift = getShift();
      
      Builder builder = new Builder();
      
      builder.setX(temp.MIN_X + shift[0]);
      builder.setX(temp.MAX_X + shift[0]);
      
      builder.setY(temp.MIN_Y + shift[1]);
      builder.setY(temp.MAX_Y + shift[1]);
      
      return builder.build();
   }

   /**
    * Testing methods
    */
   
   public List<Player> getPlayers() {
      return mPlayers;
   }
   
   public Graph getGraph() {
      return mGraph;
   }
   

}
