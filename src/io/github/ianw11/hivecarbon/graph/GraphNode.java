package io.github.ianw11.hivecarbon.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import io.github.ianw11.hivecarbon.piece.Piece;
import io.github.ianw11.hivecarbon.piece.Piece.Location;

public class GraphNode {

   private final Coordinate mCoordinate;
   private Stack<Piece> mPieceStack;
   private final GraphNode[] mAdjacency;

   private int mCurrentController;

   public GraphNode(Coordinate coordinate) {
      mCoordinate = coordinate;
      mAdjacency = new GraphNode[6];
   }

   /**
    * 
    * @param location The location of THIS node's connection
    * @param neighbor
    */
   public void setAdjacency(Location location, GraphNode neighbor) {
      //GraphNode old = mAdjacency[location.ordinal()];
      mAdjacency[location.ordinal()] = neighbor;
      /*
      if (mPiece != null && old != neighbor) {
         if (old == null) {
            mPiece.addNeighbor();
         } else {
            mPiece.removeNeighbor();
         }
      }
       */
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

   public void setPiece(Piece piece, boolean canGoNextToOtherColor) {
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
         piece.removeAllNeighbors();
      }

      for (GraphNode neighbor : mAdjacency) {
         if (neighbor != null && neighbor.isActive()) {
            if (mPieceStack == null) {
               neighbor.byeNeighbor();
            } else {
               neighbor.newNeighbor();
               newNeighbor();
            }
         }
      }
   }

   public void byeNeighbor() {
      if (!isActive()) {
         throw new IllegalStateException("Node is not active");
      }
      
      for (Piece piece : mPieceStack) {
         piece.removeNeighbor();
      }
   }

   public void newNeighbor() {
      if (!isActive()) {
         throw new IllegalStateException("Node is not active");
      }
      
      for (Piece piece : mPieceStack) {
         piece.addNeighbor();
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
      if (mPieceStack == null || piece.equals(mPieceStack.peek())) {
         return;
      }
      
      visited.add(this);
      
      for (GraphNode node : mAdjacency) {
         if (node != null && !visited.contains(node)) {
            node.connectedNodesInternal(visited, piece);
         }
      }
   }

}
