package io.github.ianw11.hivecarbon.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import io.github.ianw11.hivecarbon.Player.Player;
import io.github.ianw11.hivecarbon.piece.Piece;
import io.github.ianw11.hivecarbon.piece.Piece.Type;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class GraphNode {
   
   /*
    * The HexDirection directionality assumes that:
    *  - X gets more positive to the right and more negative to the left
    *  - Y gets more positive going down and more negative going up
    *  
    *             -y
    *              ^
    *              |
    *              |
    *     -x <---- o ----> +x
    *              |
    *              |
    *              V
    *             +y
    *  
    *                     >------<
    *                    /        \
    *            >------< ( 0, -1) >------<
    *           /        \        /        \
    *          < (-1, -1) >------< ( 1, -1) >------<
    *           \        /        \        /        \
    *            >------< ( 0,  0) >------< ( 2,  0) >
    *           /        \        /        \        /
    *          < (-1,  0) >------< ( 1,  0) >------<
    *           \        /        \        / 
    *            >------< ( 0,  1) >------<
    *                    \        /
    *                     >------<
    */
   public enum HexDirection {
      TOP (new int[] {0,-1}),
      TOP_RIGHT (new int[] {1,-1}),
      BOTTOM_RIGHT (new int[] {1,0}),
      BOTTOM (new int[] {0, 1}),
      BOTTOM_LEFT (new int[] {-1,0}),
      TOP_LEFT (new int[] {-1,-1});
      
      private final int[] movementMatrix;
      private HexDirection(int[] matrix) {
         movementMatrix = matrix;
      }
      
      public int[] getMovementMatrix(Coordinate coordinate) {
         if (coordinate.x % 2 == 0) {
            return evenXMatrix();
         } else {
            return oddXMatrix();
         }
      }
      
      public HexDirection opposite() {
         switch(this) {
         case TOP:
            return BOTTOM;
         case TOP_RIGHT:
            return BOTTOM_LEFT;
         case BOTTOM_RIGHT:
            return TOP_LEFT;
         case BOTTOM:
            return TOP;
         case BOTTOM_LEFT:
            return TOP_RIGHT;
         case TOP_LEFT:
            return BOTTOM_RIGHT;
         default:
            throw new UnsupportedOperationException();
         }
      }
      
      private int[] evenXMatrix() {
         return movementMatrix;
      }
      
      private int[] oddXMatrix() {
         switch(this) {
         case TOP:
         case BOTTOM:
            return movementMatrix;
         default:
            return new int[] {movementMatrix[0], movementMatrix[1] + 1};
         }
      }
   };

   private final Coordinate mCoordinate;
   private final GraphNode[] mAdjacency = new GraphNode[HexGraph.MAX_NUM_NEIGHBORS];

   private Stack<Piece> mPieceStack = null;
   private Player mCurrentController;
   
   // Used to hold visited nodes when performing the DFS.  Allocated once for ease.
   private final List<GraphNode> mRECURSION_DIRTY_LIST = new ArrayList<GraphNode>();

   public GraphNode(Coordinate coordinate) {
      mCoordinate = coordinate;
   }

   
   /*
    * PUBLIC METHODS (TO GET INFORMATION)
    */
   
   /**
    * Returns the coordinate of this node
    * @return
    */
   public Coordinate getCoordinate() {
      return mCoordinate;
   }
   
   /**
    * Returns if this node has a piece on it
    * @return True if there is at least one piece on this node
    */
   public boolean isActive() {
      return mPieceStack != null;
   }

   /**
    * Returns the piece on this node
    * @return This node's piece
    */
   public Piece getPiece() {
      if (!isActive()) {
         throw new IllegalStateException("Getting piece on inactive node");
      }
      return mPieceStack.peek();
   }

   /**
    * Gets the controller of the top piece on this node
    * @return The id of the controlling player
    */
   public Player getCurrentController() {
      if (!isActive()) {
         throw new IllegalStateException("Getting controller from inactive node");
      }
      return mCurrentController;
   }
   
   /**
    * Ensures each node around this node has the same controller as this node
    * @param currPlayer The player attempting to PLAY a piece on this node
    * @return True if this is a legal PLAY node
    */
   public boolean verifyNeighborColors(Player currPlayer) {
      for (final GraphNode node : mAdjacency) {
         if (node != null && node.isActive() && node.getCurrentController() != currPlayer) {
            return false;
         }
      }
      
      return true;
   }

   /**
    * Determines if a piece can go on this node.  Beetles use canStackPiece
    * @param piece The piece to be placed
    * @param canGoNextToOtherColor Used in early turns to allow the first couple pieces to be played
    * @return True if the piece can be placed here
    */
   public boolean canPlacePiece(Piece piece, boolean canGoNextToOtherColor) {
      // If this node already has a piece
      if (isActive()) {
         return false;
      }
      
      if (canGoNextToOtherColor) {
         return true;
      }

      for (final GraphNode node : mAdjacency) {
         /* 
          * A null check is required here because this node might be a perimeter node
          * that doesn't have all neighbors initialized
          */
         if (node != null && node.isActive() && node.getCurrentController() != piece.getOwner()) {
            System.err.println("Failed on color check");
            return false;
         }
      }

      return true;
   }
   
   /**
    * Determines if a piece can climb on top of this node.
    * @param piece The piece (beetle only) attempting to climb
    * @return True if the piece is a beetle and can climb on top
    */
   public boolean canStackPiece(Piece piece) {
      // If not active and piece is not a beetle
      if (!isActive() || piece.getType().compareTo(Type.BEETLE) != 0) {
         return false;
      }
      
      throw new NotImplementedException();
      //return true;
   }
   
   
   /*
    * PROTECTED METHODS FOR THE GRAPH
    */
   
   protected int connectedNodes(Piece piece) {
      mRECURSION_DIRTY_LIST.clear();
      connectedNodesInternal(mRECURSION_DIRTY_LIST, piece);
      return mRECURSION_DIRTY_LIST.size();
   }
   
   protected List<Coordinate> getEmptyNeighbors() {
      final List<Coordinate> ret = new ArrayList<Coordinate>();
      
      for (final GraphNode node : mAdjacency) {
         if (node.isActive()) {
            continue;
         }
         
         ret.add(node.getCoordinate());
      }
      
      return ret;
   }
   
   /**
    * When a new GraphNode is allocated, this adds that node to this node's adjacency.
    * @param direction The direction from *this* node to the new node.
    * @param neighbor The new neighbor
    */
   protected void setAdjacency(HexDirection direction, GraphNode neighbor) {
      mAdjacency[direction.ordinal()] = neighbor;
   }

   /**
    * Sets a piece on this node.
    * Updates state accordingly
    * @param piece The piece getting placed or null to remove
    */
   protected void setPiece(Piece piece) {
      if (piece == null) {
         mCurrentController = null;
         mPieceStack.removeAllElements();
         mPieceStack = null;
      } else {
         mPieceStack = new Stack<Piece>();
         mPieceStack.add(piece);
         mCurrentController = piece.getOwner();
         piece.removeAllNeighbors();
      }
      
      updateNeighborCount();
   }
   
   
   /*
    * PRIVATE METHODS TO UPDATE NODE STATE
    */
   
   private void updateNeighborCount() {
      for (final GraphNode neighbor : mAdjacency) {
         if (neighbor.isActive()) {
            if (isActive()) {
               // Increment each neighbor's count
               neighbor.newNeighbor();
               // And increment this piece's count for the neighbor
               newNeighbor();
            } else {
               // If this node is empty, decrement each neighbor's count
               neighbor.byeNeighbor();
            }
         }
      }
   }

   private void byeNeighbor() {
      if (!isActive()) {
         throw new IllegalStateException("Node is not active");
      }
      
      for (final Piece piece : mPieceStack) {
         piece.removeNeighbor();
      }
   }

   private void newNeighbor() {
      if (!isActive()) {
         throw new IllegalStateException("Node is not active");
      }
      
      for (final Piece piece : mPieceStack) {
         piece.addNeighbor();
      }
   }
   
   /**
    * Called by connectedNodes (defined above).  This is the recursive method for the BFS.
    * @param visited The list marking previously visited nodes.
    * @param piece The piece that a player is attempting to MOVE.
    */
   private void connectedNodesInternal(List<GraphNode> visited, Piece piece) {
      if (piece.equals(mPieceStack.peek())) {
         return;
      }
      
      visited.add(this);
      
      for (final GraphNode node : mAdjacency) {
         if (node.isActive() && !visited.contains(node)) {
            node.connectedNodesInternal(visited, piece);
         }
      }
   }
}
