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
import io.github.ianw11.hivecarbon.turn.DefaultTurnFactory;
import io.github.ianw11.hivecarbon.turn.Turn;
import io.github.ianw11.hivecarbon.turn.TurnFactory;

public class RulesEngine {
   
   private static final int NUM_PLAYERS = 2;
   
   public enum Action {
      QUIT,
      PLAY,
      MOVE
   };

   private final ArrayList<Player> mPlayers;
   private final HexGraph mGraph;
   
   private TurnFactory mTurnFactory;
   
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
      mTurnFactory = new DefaultTurnFactory();
   }

   // TURN is the main method here
   /**
    * Take a game turn
    * @return True if game is completed
    */
   public boolean turn() {
      if (isGameFinished()) {
         throw new IllegalStateException("Game is finished, cannot take another turn");
      }
      
      final Turn currentTurn = mTurnFactory.getTurn(getCurrentPlayer(), this);
      
      final boolean isLegalAction;
      switch (currentTurn.getTurnAction()) {
      case MOVE:
         isLegalAction = performMove(currentTurn);
         break;
      case PLAY:
         isLegalAction = performPlay(currentTurn);
         break;
      default:
         return true;
      }
      
      if (isLegalAction) {
         incrementTurnNumbers();
      }

      return isGameFinished();
   }
   
   
   /*
    * Public methods offering game state information to display
    */
   
   public int getGameTurnNumber() {
      return mGameTurn;
   }
   
   public Player getCurrentPlayer() {
      return mPlayers.get(mPlayerTurn);
   }
   
   
   /*
    * Public methods to correctly build game state
    */
   
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
   
   private boolean isGameFinished() {
      boolean ret = false;
      for (final Player player : mPlayers) {
         ret |= player.isQueenSurrounded();
      }
      return ret;
   }
   
   private void incrementTurnNumbers() {
      if (++mPlayerTurn == mPlayers.size()) {
         mPlayerTurn = 0;
         ++mGameTurn;
      }
   }
   
   
   /*
    * Private methods to actually run the game
    */
   
   // If the player wants to MOVE a piece
   private boolean performMove(final Turn turn) {
      final Coordinate oldCoordinate = turn.getSourceCoordinate();
      final Coordinate coordinate = turn.getDestinationCoordinate();
      final Piece piece = getPieceAtCoordinate(oldCoordinate);
      
      System.out.println("Moving piece " + piece + " to " + coordinate + " from " + oldCoordinate);
      
      if (!isDestinationOk(coordinate, piece, true) || !pieceCanMove(coordinate, piece) || !mGraph.isConnected(piece)) {
         return false;
      }
      
      mGraph.movePiece(oldCoordinate, coordinate);
      
      return true;
   }
   
   // If the player wants to PLAY a piece
   private boolean performPlay(final Turn turn) {
      final Coordinate coordinate = turn.getDestinationCoordinate();
      final Piece piece = turn.getPieceToPlay();
      
      System.out.println("Playing piece " + piece + " at " + coordinate);

      boolean canGoNextToOtherColor = mGraph.numActiveNodes() < 2;
      if (!isDestinationOk(coordinate, piece, canGoNextToOtherColor) || piece.isPlaced()) {
         return false;
      }
      
      mGraph.playPiece(piece, coordinate);
      piece.setPlaced();

      return true;
   }
   
   
   private boolean pieceCanMove(final Coordinate coordinate, final Piece piece) {
      //final GraphNode destination = mGraph.findGraphNode(coordinate);
      return true;
   }
   
   private boolean isDestinationOk(final Coordinate coordinate, final Piece piece, boolean canGoNextToOtherColor) {
      final GraphNode target = mGraph.getGraphNode(coordinate);
      if (target == null) {
         return false;
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
   private int[] getShift() {
      GraphBounds temp = mGraph.getMapBounds();
      return new int[] {
            -temp.MIN_X,
            -temp.MIN_Y
      };
   }
   
   public GraphBounds getNormalizedBounds() {
      GraphBounds temp = mGraph.getMapBounds();
      int[] shift = getShift();
      
      Builder builder = new Builder();
      
      builder.addX(temp.MIN_X + shift[0]);
      builder.addX(temp.MAX_X + shift[0]);
      
      builder.addY(temp.MIN_Y + shift[1]);
      builder.addY(temp.MAX_Y + shift[1]);
      
      return builder.build();
   }

   
   /**
    * Testing methods
    */
   
   /**
    * TESTING METHOD ONLY
    * @return
    */
   public List<Player> getPlayers() {
      return mPlayers;
   }
   
   /**
    * TESTING METHOD ONLY
    */
   public void renderGraphToConsole() {
      mGraph.renderToConsole();
   }
   
   /**
    * TESTING METHOD ONLY
    * @return
    */
   public HexGraph getGraph() {
      return mGraph;
   }

   /**
    * TESTING METHOD ONLY
    */
   public void setTurnFactory(final TurnFactory factory) {
      mTurnFactory = factory;
   }
}