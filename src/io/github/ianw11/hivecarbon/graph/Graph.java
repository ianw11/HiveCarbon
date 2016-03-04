package io.github.ianw11.hivecarbon.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.ianw11.hivecarbon.graph.GraphBounds.Builder;
import io.github.ianw11.hivecarbon.graph.GraphNode.HexDirection;
import io.github.ianw11.hivecarbon.piece.Piece;

public class Graph {
   
   private static final boolean VERBOSE = false;
   private final Map<Coordinate, GraphNode> mGameMap;
   
   public Graph() {
      mGameMap = new HashMap<Coordinate, GraphNode>();
      
      final Coordinate initialCoordinate = new Coordinate(0, 0);
      GraphNode node = new GraphNode(initialCoordinate);
      mGameMap.put(initialCoordinate, node);
      
      setAdjacency(node);
   }
   
   /**
    * Returns the GraphNode at the given coordinate
    * @param coordinate
    * @return The GraphNode if present, else null
    */
   public GraphNode findGraphNode(Coordinate coordinate) {
      return mGameMap.get(coordinate);
   }
   
   /**
    * Moves a piece from a GraphNode to a target Coordinate.
    * @param oldNode The node the piece is currently on
    * @param targetLocation The destination for the piece
    */
   public void movePiece(GraphNode oldNode, Coordinate targetLocation) {
      GraphNode newNode = findGraphNode(targetLocation);
      
      // Because of new changes, the desired GraphNode should ALWAYS exist
      if (newNode == null) {
         throw new IllegalStateException("Perimeter not being applied correctly");
      }
      
      setAdjacency(newNode);
      
      final Piece piece = oldNode.getPiece();
      
      // For some reason, you need to set null first.  Not sure why....
      oldNode.setPiece(null, true);
      newNode.setPiece(piece, true);
   }
   
   public void playPiece(Piece piece, Coordinate coordinate, boolean canGoNextToOtherColor) {
      GraphNode node = findGraphNode(coordinate);
      
      // Because of new changes, the desired GraphNode should ALWAYS exist
      if (node == null) {
         throw new IllegalStateException("Perimeter not being applied correctly");
      }

      setAdjacency(node);
      
      node.setPiece(piece, canGoNextToOtherColor);
      piece.setPlaced();
   }
   
   /**
    * See if all pieces are still connected after the parameter piece is removed
    * @param piece Piece to ignore
    * @return If all other pieces are still connected
    */
   public boolean isConnected(Piece piece) {
      final int targetSize = numActiveNodes() - 1;
      for (GraphNode node : mGameMap.values()) {
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
      for (GraphNode node : mGameMap.values()) {
         if (node.isActive()) {
            ++ret;
         }
      }
      return ret;
   }
   
   
   public GraphBounds getMapBounds() {
      Builder builder = new GraphBounds.Builder();

      for (GraphNode node : mGameMap.values()) {
         if (!node.isActive()) {
            continue;
         }
         
         Coordinate coordinate = node.getCoordinate();
         int x = coordinate.x;
         int y = coordinate.y;
         if (VERBOSE)
            System.out.println("x: " + x + " and y: " + y);
         
         builder.setX(x);
         builder.setY(y);
      }
      
      if (VERBOSE)
         System.out.println(builder.toString());
      
      return builder.build();
   }
   
   public Set<Coordinate> getPerimeter(Coordinate toExclude) {
      Set<Coordinate> ret = new HashSet<Coordinate>();
      
      for (GraphNode node : mGameMap.values()) {
         if (node.isActive() && !node.getCoordinate().equals(toExclude)) {
            ret.addAll(node.getEmptyNeighbors());
         }
      }
      
      return ret;
   }
   
   
   
   private void setAdjacency(GraphNode node) {
      final Coordinate coordinate = node.getCoordinate();
      for (final HexDirection location : HexDirection.values()) {
         final Coordinate neighborCoordinate = Coordinate.sum(coordinate, location.getMovementMatrix(coordinate.x));
         
         if (findGraphNode(neighborCoordinate) == null) {
            
            if (VERBOSE)
               System.out.println("Creating PERIMETER GraphNode at x: " + neighborCoordinate.x + " y: " + neighborCoordinate.y);
            
            GraphNode neighbor = new GraphNode(neighborCoordinate);
            mGameMap.put(neighborCoordinate, neighbor);
            applyPerimeter(neighbor);
         }
         
      }
   }
   
   private void applyPerimeter(GraphNode newNode) {
      final Coordinate coordinate = newNode.getCoordinate();
      
      for (final HexDirection location : HexDirection.values()) {
         GraphNode neighbor = findGraphNode(Coordinate.sum(coordinate, location.getMovementMatrix(coordinate.x)));
         if (neighbor == null) {
            continue;
         }
         
         if (VERBOSE)
            System.out.println("Linking " + newNode.getCoordinate() + " to " + neighbor.getCoordinate() + " via direction: " + location.toString());
         
         newNode.setAdjacency(location, neighbor);
         neighbor.setAdjacency(location.opposite(), newNode);
      }
   }
   
   
   
   

   public void renderToConsole() {
      StringBuilder builder = new StringBuilder();
      final GraphBounds bounds = getMapBounds();
      
      final String LINE_PREFIX = "  |";
      final String LINE_PREFIX_CORNER = "  +";
      final String HORIZONTAL_CELL_SEPARATOR = "=========+";
      final String MIDCELL_BLANK_SPACE = "         |";
      final String NEWLINE = "\n";
      
      // Print X labels
      builder.append("   ");
      for (int x = bounds.MIN_X; x <= bounds.MAX_X; ++x) {
         builder.append(String.format("    % 2d    ", x));
      }
      builder.append(NEWLINE);
      
      
      for (int y = bounds.MIN_Y; y <= bounds.MAX_Y; ++y) {
         // Build the (top) frame for each line
         builder.append(LINE_PREFIX_CORNER);
         for (int x = bounds.MIN_X; x <= bounds.MAX_X; ++x) {
            builder.append(HORIZONTAL_CELL_SEPARATOR);
         }
         builder.append(String.format("\n% 2d|", y));
         
         String secondXRow = LINE_PREFIX;
         // Row by row tile dump
         for (int x = bounds.MIN_X; x <= bounds.MAX_X; ++x) {
            GraphNode currNode = findGraphNode(new Coordinate(x, y));
            
            if (currNode != null && currNode.isActive()) {
               
               String[] potential = new String[2];
               potential[0] = " p" + currNode.getCurrentController() + ": " + currNode.getPiece().getType().getShortName() + " |";
               potential[1] = String.format("  % 2d,% 2d  |", currNode.getCoordinate().x, currNode.getCoordinate().y);
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
      
      // Finish the frame
      builder.append(LINE_PREFIX_CORNER);
      for (int x = bounds.MIN_X; x <= bounds.MAX_X; ++x) {
         builder.append(HORIZONTAL_CELL_SEPARATOR);
      }
      builder.append(NEWLINE);
      
      System.out.println(builder.toString());
   }

}
