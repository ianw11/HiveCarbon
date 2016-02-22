package io.github.ianw11.hivecarbon.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.sun.javafx.binding.StringFormatter;

import io.github.ianw11.hivecarbon.piece.Piece;
import io.github.ianw11.hivecarbon.piece.Piece.Location;

public class Graph {
   
   private static final boolean VERBOSE = false;
   
   private final GraphNode mHead;
   private final ArrayList<GraphNode> mGameMap = new ArrayList<GraphNode>();
   
   public Graph() {
      mHead = new GraphNode(new Coordinate(0,0));
      mGameMap.add(mHead);
   }
   
   public Iterator<GraphNode> getIterator() {
      return mGameMap.iterator();
   }
   
   public GraphNode findGraphNode(Coordinate coordinate) {
      return mHead.findGraphNode(coordinate);
   }
   
   public GraphNode createGraphNode(Coordinate coordinate) {
      GraphNode node = new GraphNode(coordinate);
      mGameMap.add(node);

      // Set adjacency
      for (final Location location : Location.values()) {
         final GraphNode neighbor = mHead.findGraphNode(Coordinate.sum(coordinate, location.getMovementMatrix(coordinate.x)));
         if (neighbor != null) {
            node.setAdjacency(location, neighbor);
            neighbor.setAdjacency(location.opposite(), node);
         }
      }
      
      return node;
   }
   
   /**
    * See if all pieces are still connected after the parameter piece is removed
    * @param piece Piece to ignore
    * @return If all other pieces are still connected
    */
   public boolean isConnected(Piece piece) {
      final int targetSize = numActiveNodes() - 1;
      for (GraphNode node : mGameMap) {
         if (!node.isActive() || node.getPiece() == piece) {
            continue;
         }
         
         int connectedNodes = node.connectedNodes(piece);
         if (connectedNodes != targetSize) {
            System.err.println("Connected Nodes: " + connectedNodes + " is not target: " + targetSize);
            return false;
         }
      }
      
      return true;
   }
   
   public int numActiveNodes() {
      int ret = 0;
      for (GraphNode node : mGameMap) {
         if (node.isActive()) {
            ++ret;
         }
      }
      return ret;
   }
   
   
   private static final int MIN_X = 0;
   private static final int MAX_X = 1 + MIN_X;
   private static final int MIN_Y = 2 + MIN_X;
   private static final int MAX_Y = 3 + MIN_X;
   public int[] getMapBounds() {
      int[] bounds = new int[4];
      Arrays.fill(bounds, 0);

      for (GraphNode node : mGameMap) {
         if (!node.isActive()) {
            continue;
         }
         
         Coordinate coordinate = node.getCoordinate();
         int x = coordinate.x;
         int y = coordinate.y;
         if (VERBOSE) {
            System.out.println("x: " + x + " and y: " + y);
         }

         if (x < bounds[MIN_X]) {
            bounds[MIN_X] = x;
         } else if (x > bounds[MAX_X]) {
            bounds[MAX_X] = x;
         }
         if (y < bounds[MIN_Y]) {
            bounds[MIN_Y] = y;
         } else if (y > bounds[MAX_Y]) {
            bounds[MAX_Y] = y;
         }
      }

      if (VERBOSE)
         System.out.println("Bounds -- MIN_X: " + bounds[MIN_X] + " MAX_X: " + bounds[MAX_X] + " MIN_Y: " + bounds[MIN_Y] + " MAX_Y: " + bounds[MAX_Y]);

      return bounds;
   }

   public void renderToConsole() {
      StringBuilder builder = new StringBuilder();
      final int[] bounds = getMapBounds();
      
      final String LINE_PREFIX = "  |";
      final String LINE_PREFIX_CORNER = "  +";
      final String HORIZONTAL_CELL_SEPARATOR = "=========+";
      final String MIDCELL_BLANK_SPACE = "         |";
      final String NEWLINE = "\n";
      
      // Print X labels
      builder.append("   ");
      for (int x = bounds[MIN_X]; x <= bounds[MAX_X]; ++x) {
         builder.append(String.format("    % 2d    ", x));
      }
      builder.append(NEWLINE);
      
      
      for (int y = bounds[MIN_Y]; y <= bounds[MAX_Y]; ++y) {
         // Y labels
         builder.append(LINE_PREFIX_CORNER);
         
         for (int x = bounds[MIN_X]; x <= bounds[MAX_X]; ++x) {
            builder.append(HORIZONTAL_CELL_SEPARATOR);
         }
         builder.append(String.format("\n% 2d|", y));
         
         String secondXRow = LINE_PREFIX;
         // Row by row tile dump
         for (int x = bounds[MIN_X]; x <= bounds[MAX_X]; ++x) {
            GraphNode currNode = mHead.findGraphNode(new Coordinate(x, y));
            
            if (currNode != null && currNode.isActive()) {
               
               String[] potential = new String[2];
               potential[0] = " p" + currNode.getCurrentController() + ": " + currNode.getPiece().getType().getShortName() + " |";
               potential[1] = StringFormatter.format("  % 2d,% 2d  |", currNode.getCoordinate().x, currNode.getCoordinate().y).get();
               String stringToDisplay = potential[0];
               
               if (x % 2 == 0) {
                  builder.append(stringToDisplay);
                  secondXRow += MIDCELL_BLANK_SPACE;
               } else {
                  builder.append(MIDCELL_BLANK_SPACE);
                  secondXRow += stringToDisplay;
               }
            } else {
               String stringToDisplay = "  EMPTY  |";
               if (x % 2 == 0) {
                  builder.append(stringToDisplay);
                  secondXRow += MIDCELL_BLANK_SPACE;
               } else {
                  builder.append(MIDCELL_BLANK_SPACE);
                  secondXRow += stringToDisplay;
               }
            }
         }
         builder.append(NEWLINE);
         builder.append(secondXRow);
         builder.append(NEWLINE);
      }
      
      builder.append(LINE_PREFIX_CORNER);
      for (int x = bounds[MIN_X]; x <= bounds[MAX_X]; ++x) {
         builder.append(HORIZONTAL_CELL_SEPARATOR);
      }
      builder.append(NEWLINE);
      
      System.out.println(builder.toString());

   }

}
