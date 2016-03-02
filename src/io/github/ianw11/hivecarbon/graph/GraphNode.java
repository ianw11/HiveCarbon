package io.github.ianw11.hivecarbon.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import io.github.ianw11.hivecarbon.piece.Piece;

public class GraphNode {
   
   public enum Location {
      TOP (new int[] {0,-1}),
      TOP_RIGHT (new int[] {1,-1}),
      BOTTOM_RIGHT (new int[] {1,0}),
      BOTTOM (new int[] {0, 1}),
      BOTTOM_LEFT (new int[] {-1,0}),
      TOP_LEFT (new int[] {-1,-1});
      
      private final int[] movementMatrix;
      private Location(int[] matrix) {
         movementMatrix = matrix;
      }
      
      public int[] getMovementMatrix(int xCoord) {
         if (xCoord % 2 == 0) {
            return evenXMatrix();
         } else {
            return oddXMatrix();
         }
      }
      
      public Location opposite() {
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
   private Stack<Piece> mPieceStack;
   private final GraphNode[] mAdjacency = new GraphNode[6];

   private int mCurrentController;

   public GraphNode(Coordinate coordinate) {
      mCoordinate = coordinate;
   }

   /**
    * @param location The location of THIS node's connection
    * @param neighbor
    */
   protected void setAdjacency(Location location, GraphNode neighbor) {
      mAdjacency[location.ordinal()] = neighbor;
   }

   public Coordinate getCoordinate() {
      return mCoordinate;
   }

   public boolean canSetPiece(Piece piece, boolean canGoNextToOtherColor) {
      // If this node already has a piece
      if (mPieceStack != null) {
         return false;
      }

      if (canGoNextToOtherColor) {
         return true;
      }

      for (GraphNode node : mAdjacency) {
         if (node != null && node.getCurrentController() != piece.getOwnerNumber()) {
            System.out.println("Failed on color check");
            return false;
         }
      }

      return true;
   }

   protected void setPiece(Piece piece, boolean canGoNextToOtherColor) {
      if (piece != null && !canSetPiece(piece, canGoNextToOtherColor)){
         throw new IllegalStateException();
      }

      if (piece == null) {
         mCurrentController = -1;
         mPieceStack.removeAllElements();
         mPieceStack = null;
      } else {
         mPieceStack = new Stack<Piece>();
         mPieceStack.add(piece);
         mCurrentController = piece.getOwnerNumber();
         //piece.removeAllNeighbors();
      }

      for (GraphNode neighbor : mAdjacency) {
         if (neighbor.isActive()) {
            if (mPieceStack == null) {
               neighbor.byeNeighbor();
            } else {
               neighbor.newNeighbor();
               newNeighbor();
            }
         }
      }
   }

   private void byeNeighbor() {
      if (!isActive()) {
         throw new IllegalStateException("Node is not active");
      }
      
      for (Piece piece : mPieceStack) {
         //piece.removeNeighbor();
      }
   }

   private void newNeighbor() {
      if (!isActive()) {
         throw new IllegalStateException("Node is not active");
      }
      
      for (Piece piece : mPieceStack) {
         //piece.addNeighbor();
      }
   }

   
   
   public boolean isActive() {
      return mPieceStack != null;
   }

   public Piece getPiece() {
      return mPieceStack.peek();
   }

   public int getCurrentController() {
      return mCurrentController;
   }

   public GraphNode findGraphNode(Coordinate coordinate) {
      return findGraphNodeInternal(coordinate, new ArrayList<GraphNode>());
   }

   private GraphNode findGraphNodeInternal(Coordinate coordinate, List<GraphNode> visited) {
      if (mCoordinate.equals(coordinate)) {
         return this;
      }
      
      visited.add(this);
      
      for (GraphNode node : mAdjacency) {
         if (node != null && !visited.contains(node)) {
            GraphNode ret = node.findGraphNodeInternal(coordinate, visited);
            if (ret != null) {
               return ret;
            }
         }
      }
      
      return null;
   }
   
   public int connectedNodes(Piece piece) {
      ArrayList<GraphNode> visited = new ArrayList<GraphNode>();
      connectedNodesInternal(visited, piece);
      return visited.size();
   }
   
   private void connectedNodesInternal(ArrayList<GraphNode> visited, Piece piece) {
      if (piece.equals(mPieceStack.peek())) {
         return;
      }
      
      System.out.println("Working on coordinate " + mCoordinate);
      visited.add(this);
      
      for (GraphNode node : mAdjacency) {
         System.out.println("Node: " + node.getCoordinate());
         if (node.isActive() && !visited.contains(node)) {
            node.connectedNodesInternal(visited, piece);
         }
      }
   }
   
   public List<Coordinate> getEmptyNeighbors(Coordinate toIgnore) {
      List<Coordinate> ret = new ArrayList<Coordinate>();
      
      for (GraphNode node : mAdjacency) {
         if (node.isActive()) {
            continue;
         }
         
         Coordinate coordinate = node.getCoordinate();
         if (!coordinate.equals(toIgnore)) {
            ret.add(coordinate);
         }
      }
      
      return ret;
   }

}
