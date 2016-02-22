package io.github.ianw11.hivecarbon.engines;

import java.util.ArrayList;
import java.util.List;

import io.github.ianw11.hivecarbon.Player.Player;
import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.graph.Graph;
import io.github.ianw11.hivecarbon.graph.GraphNode;
import io.github.ianw11.hivecarbon.piece.Piece;

public class RulesEngine {

   public enum Type {
      QUEEN_BEE(1, "QNB"),
      BEETLE(2, "BTL"),
      GRASSHOPPER(3, "GHP"),
      SPIDER(2, "SPR"),
      SOLDIER_ANT(3, "ANT");

      private final int mNumInGame;
      private final String mShortName;
      private Type(int numInGame, String shortName) {
         mNumInGame = numInGame;
         mShortName = shortName;
      }

      public int getNumInGame() {
         return mNumInGame;
      }
      public String getShortName() {
         return mShortName;
      }
   };

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
   
   public Piece[] getUnusedPieces(int playerNum) {
      return mPlayers.get(playerNum).getUnusedPieces();
   }

   // TURN is the main method here
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
      
      if (!isDestinationOk(coordinate, piece, true) || !pieceCanMove(coordinate, piece) || !mGraph.isConnected(piece)) {
         return false;
      }
      
      GraphNode newNode = mGraph.findGraphNode(coordinate);
      if (newNode == null) {
         newNode = mGraph.createGraphNode(coordinate);
      }
      
      oldNode.setPiece(null, true);
      newNode.setPiece(piece, true);
      
      return true;
   }
   
   /**
    * Verifies a piece can actually move to the coordinate
    * @param coordinate Destination
    * @param piece The piece that wants to move
    * @return If the move is legal
    */
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
      
      if (mGameTurn == 1 && mPlayerTurn == 0 && !coordinate.equals(new Coordinate(0, 0))) {
         return false;
      }

      boolean canGoNextToOtherColor = mGameTurn < 3 || mGraph.numActiveNodes() < 3;

      if (!isDestinationOk(coordinate, piece, canGoNextToOtherColor) || piece.isPlaced()) {
         return false;
      }

      GraphNode node = mGraph.findGraphNode(coordinate);

      if (node == null) {
         node = mGraph.createGraphNode(coordinate);
      }

      node.setPiece(piece, canGoNextToOtherColor);
      piece.setPlaced();

      return true;
   }
   
   private boolean isDestinationOk(Coordinate coordinate, Piece piece, boolean canGoNextToOtherColor) {
      GraphNode target = mGraph.findGraphNode(coordinate);
      if (target == null) {
         return true;
      }

      if (mGameTurn == 4 && !mPlayers.get(mPlayerTurn).isQueenPlayed()) {
         System.out.println("Queen not yet played");
         return false;
      }

      return target.canSetPiece(piece, canGoNextToOtherColor);
   }

   
   
   public boolean isGameFinished() {
      boolean ret = false;
      for (Player player : mPlayers) {
         ret |= player.isQueenSurrounded();
      }
      return ret;
   }

   
   /**
    * UI Methods
    */
   public int[] getShift() {
      int[] temp = mGraph.getMapBounds();
      int[] ret = new int[] {
            -temp[0],
            -temp[2]
      };
      return ret;
   }
   
   public int[] getNormalizedBounds() {
      int[] temp = mGraph.getMapBounds();
      int[] ret = new int[4];
      int[] shift = getShift();
      
      ret[0] = temp[0] + shift[0];
      ret[1] = temp[1] + shift[0];
      
      ret[2] = temp[2] + shift[1];
      ret[3] = temp[3] + shift[1];
      
      return ret;
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
