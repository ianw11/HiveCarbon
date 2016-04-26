package io.github.ianw11.hivecarbon.engines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.ianw11.hivecarbon.Player.Player;
import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.graph.GraphBounds;
import io.github.ianw11.hivecarbon.graph.GraphBounds.Builder;
import io.github.ianw11.hivecarbon.graph.GraphNode;
import io.github.ianw11.hivecarbon.graph.HexGraph;
import io.github.ianw11.hivecarbon.piece.Piece;

public class RulesEngine {
   
   private static final int NUM_PLAYERS = 2;

   private final ArrayList<Player> mPlayers;
   private final HexGraph mGraph;
   
   private int mPlayerTurn = 0;
   private int mGameTurn = 1;

   public RulesEngine(final String[] playerNames) {
      if (playerNames.length != NUM_PLAYERS) {
         throw new IllegalStateException(NUM_PLAYERS + " required to play");
      }
      
      mPlayers = new ArrayList<Player>(NUM_PLAYERS);
      for (int i = 0; i < NUM_PLAYERS; ++i) {
         mPlayers.add(new Player(playerNames[i], i));
      }
      
      mGraph = new HexGraph();
   }

   // TURN is the main method here
   /**
    * Take a game turn, performing the TurnAction
    * @param turnAction The action to perform this turn
    * @return True if legal turnAction
    */
   public boolean turn(final TurnAction turnAction) {
      boolean ret = false;
      
      if (!verifyPlayerTurn(turnAction)) {
         return false;
      }

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
   
   public int getGameTurnNumber() {
      return mGameTurn;
   }
   
   public Player getCurrentPlayer() {
      return mPlayers.get(mPlayerTurn);
   }
   
   // Verifies it's the turn of the player taking the action
   private boolean verifyPlayerTurn(final TurnAction turnAction) {
      return turnAction.mPlayer.getId() == mPlayerTurn;
   }
   
   /**
    * Returns if the game is completed.
    * @return True if the game is completed.
    */
   public boolean isGameFinished() {
      boolean ret = false;
      for (final Player player : mPlayers) {
         ret |= player.isQueenSurrounded();
      }
      return ret;
   }
   
   public List<Coordinate> getActiveCoordinates(final Player player) {
      final List<Coordinate> pieces = new ArrayList<Coordinate>();
      
      for (final GraphNode node : mGraph.getActiveGraphNodes()) {
         if (node.isActive() && node.getPiece().getOwner() == player) {
            pieces.add(node.getCoordinate());
         }
      }
      
      return pieces;
   }
   
   /**
    * Get the coordinates on the board that can accept a piece
    * @return The list of empty coordinates
    */
   public List<Coordinate> getLegalPlacementCoordinates(final boolean isMove) {
      System.out.println("getLegalPlacementCoordinates()");
      // A piece can be set next to another colored piece during a MOVE
      final boolean canGoNextToOtherColor = isMove || mGraph.numActiveNodes() < 2;
      final List<GraphNode> inactiveNodes = Arrays.asList(mGraph.getInactiveGraphNodes());
      final Player currentPlayer = mPlayers.get(mPlayerTurn);
      
      final List<Coordinate> legalCoordinates = new ArrayList<Coordinate>();
      
      for (final GraphNode node : inactiveNodes) {
         if (!node.isActive() && (canGoNextToOtherColor || node.verifyNeighborColors(currentPlayer)) ) {
            legalCoordinates.add(node.getCoordinate());
         }
      }
      
      return legalCoordinates;
   }
   
   public Piece getPieceAtCoordinate(final Coordinate coordinate) {
      final GraphNode node = mGraph.getGraphNode(coordinate);
      return node.getPiece();
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
   private boolean performMove(final MoveTurnAction action) {
      final Coordinate oldCoordinate = action.oldCoordinate;
      final Coordinate coordinate = action.mCoordinate;
      final Piece piece = getPieceAtCoordinate(oldCoordinate);
      
      System.out.println("Moving piece " + piece + " to " + coordinate + " from " + oldCoordinate);
      
      if (!isDestinationOk(coordinate, piece, true) || !pieceCanMove(coordinate, piece) || !mGraph.isConnected(piece)) {
         return false;
      }
      
      mGraph.movePiece(oldCoordinate, coordinate);
      
      return true;
   }
   
   private boolean pieceCanMove(Coordinate coordinate, Piece piece) {
      //final GraphNode destination = mGraph.findGraphNode(coordinate);
      return true;
   }
   
   // If the player wants to PLAY a piece
   private boolean performPlay(final PlaceTurnAction action) {
      final Coordinate coordinate = action.mCoordinate;
      final Piece piece = action.mPiece;
      
      System.out.println("Playing piece " + piece + " at " + coordinate);

      boolean canGoNextToOtherColor = mGraph.numActiveNodes() < 2;
      if (!isDestinationOk(coordinate, piece, canGoNextToOtherColor) || piece.isPlaced()) {
         return false;
      }
      
      mGraph.playPiece(piece, coordinate);
      piece.setPlaced();

      return true;
   }
   
   private boolean isDestinationOk(Coordinate coordinate, Piece piece, boolean canGoNextToOtherColor) {
      GraphNode target = mGraph.getGraphNode(coordinate);
      if (target == null) {
         return true;
      }

      // Make sure that the player going has placed their queen
      if (mGameTurn == 4 && !mPlayers.get(mPlayerTurn).isQueenPlayed() && !piece.isQueen()) {
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
   
   public void renderGraphToConsole() {
      mGraph.renderToConsole();
   }
   
   public HexGraph getGraph() {
      return mGraph;
   }
   

}
