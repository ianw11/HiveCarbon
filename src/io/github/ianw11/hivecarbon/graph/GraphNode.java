package io.github.ianw11.hivecarbon.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import io.github.ianw11.hivecarbon.piece.Piece;
import io.github.ianw11.hivecarbon.piece.Piece.Type;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class GraphNode {
   
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
      
      public int[] getMovementMatrix(int xCoord) {
         if (xCoord % 2 == 0) {
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
   private final GraphNode[] mAdjacency = new GraphNode[6];

   private Stack<Piece> mPieceStack = null;
   private int mCurrentController;
   
   // Used to hold visited nodes when performing the DFS.  Allocated once for ease.
   private final List<GraphNode> mRecursionDirtyList = new ArrayList<GraphNode>();

   public GraphNode(Coordinate coordinate) {
      mCoordinate = coordinate;
   }

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
   public int getCurrentController() {
      if (!isActive()) {
         throw new IllegalStateException("Getting controller from inactive node");
      }
      return mCurrentController;
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

      for (GraphNode node : mAdjacency) {
         /* 
          * A null check is required here because this node might be a perimeter node
          * that doesn't have all neighbors initialized
          */
         if (node != null && node.isActive() && node.getCurrentController() != piece.getOwnerNumber()) {
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
   
   
   protected int connectedNodes(Piece piece) {
      mRecursionDirtyList.clear();
      connectedNodesInternal(mRecursionDirtyList, piece);
      return mRecursionDirtyList.size();
   }
   
   protected List<Coordinate> getEmptyNeighbors() {
      List<Coordinate> ret = new ArrayList<Coordinate>();
      
      for (GraphNode node : mAdjacency) {
         if (node.isActive()) {
            continue;
         }
         
         ret.add(node.getCoordinate());
      }
      
      return ret;
   }
   
   protected void setAdjacency(HexDirection direction, GraphNode neighbor) {
      mAdjacency[direction.ordinal()] = neighbor;
   }

   protected void setPiece(Piece piece, boolean canGoNextToOtherColor) {
      if (piece != null && !canPlacePiece(piece, canGoNextToOtherColor)){
         throw new IllegalStateException("Null piece AND can't place piece -- Piece is null: " + (piece == null));
      }

      if (piece == null) {
         mCurrentController = -1;
         mPieceStack.removeAllElements();
         mPieceStack = null;
      } else {
         mPieceStack = new Stack<Piece>();
         mPieceStack.add(piece);
         mCurrentController = piece.getOwnerNumber();
         piece.removeAllNeighbors();
      }
      
      updateNeighborCount();
   }
   
   private void updateNeighborCount() {
      for (GraphNode neighbor : mAdjacency) {
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
      
      for (Piece piece : mPieceStack) {
         piece.removeNeighbor();
      }
   }

   private void newNeighbor() {
      if (!isActive()) {
         throw new IllegalStateException("Node is not active");
      }
      
      for (Piece piece : mPieceStack) {
         piece.addNeighbor();
      }
   }
   
   private void connectedNodesInternal(List<GraphNode> visited, Piece piece) {
      if (piece.equals(mPieceStack.peek())) {
         return;
      }
      
      visited.add(this);
      
      for (GraphNode node : mAdjacency) {
         if (node.isActive() && !visited.contains(node)) {
            node.connectedNodesInternal(visited, piece);
         }
      }
   }
}
