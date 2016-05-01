package io.github.ianw11.hivecarbon.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.ianw11.hivecarbon.graph.GraphBounds.Builder;
import io.github.ianw11.hivecarbon.graph.GraphNode.HexDirection;
import io.github.ianw11.hivecarbon.piece.Piece;

public class HexGraph {
   
   private static final boolean VERBOSE = false;
   public static final int MAX_NUM_NEIGHBORS = 6;
   
   
   private final Map<Coordinate, GraphNode> mGameMap;
   
   public HexGraph() {
      mGameMap = new HashMap<Coordinate, GraphNode>();

      createNodeAndLinkToNeighbors(new Coordinate(0, 0));
   }
   
   /**
    * Returns the GraphNode at the given coordinate
    * @param coordinate
    * @return The GraphNode if present, else null
    */
   public GraphNode getGraphNode(final Coordinate coordinate) {
      return mGameMap.get(coordinate);
   }
   
   public GraphNode[] getActiveGraphNodes() {
      final List<GraphNode> activeNodes = new ArrayList<GraphNode>();
      for (final GraphNode n : mGameMap.values()) {
         if (n.isActive()) {
            activeNodes.add(n);
         }
      }
      
      final GraphNode[] ret = new GraphNode[activeNodes.size()];
      activeNodes.toArray(ret);
      return ret;
   }
   
   /**
    * Gets an array of inactive nodes
    * @return The array of inactive nodes
    */
   public GraphNode[] getInactiveGraphNodes() {
      final List<GraphNode> emptyNodes = new ArrayList<GraphNode>();
      for (final GraphNode n : mGameMap.values()) {
         if (!n.isActive()) {
            emptyNodes.add(n);
         }
      }
      
      final GraphNode[] ret = new GraphNode[emptyNodes.size()];
      emptyNodes.toArray(ret);
      return ret;
   }
   
   /**
    * Moves a piece from a Coordinate to a target Coordinate.
    * @param oldNode The Coordinate the piece is currently on
    * @param targetLocation The destination for the piece
    */
   public void movePiece(final Coordinate oldLocation, final Coordinate targetLocation) {
      final GraphNode newNode = getGraphNode(targetLocation);
      // Because of new changes, the desired GraphNode should ALWAYS exist
      if (newNode == null) {
         throw new IllegalStateException("Perimeter not being applied correctly");
      }
      
      verifyPerimeterNodes(newNode);
      
      final GraphNode oldNode = getGraphNode(oldLocation);
      final Piece piece = oldNode.getPiece();
      // oldNode needs to be dealt with first
      oldNode.setPiece(null);
      newNode.setPiece(piece);
   }
   
   /**
    * Plays a piece on a Coordinate
    * @param piece The piece to play
    * @param coordinate Where the piece should be played
    */
   public void playPiece(final Piece piece, final Coordinate coordinate) {
      final GraphNode node = getGraphNode(coordinate);
      
      // Because of new changes, the desired GraphNode should ALWAYS exist
      if (node == null) {
         throw new IllegalStateException("Perimeter not being applied correctly");
      }

      verifyPerimeterNodes(node);
      
      node.setPiece(piece);
   }
   
   /**
    * See if all pieces are still connected after the parameter piece is removed.
    * Aka if this piece doesn't exist, is the graph still connected
    * @param piece Piece to ignore
    * @return If all other pieces are still connected
    */
   public boolean isConnected(final Piece piece) {
      // The expected number of other active nodes (the number of pieces on the board minus this piece)
      final int targetSize = numActiveNodes() - 1;
      
      for (final GraphNode node : mGameMap.values()) {
         if (!node.isActive() || node.getPiece() == piece) {
            continue;
         }
         
         final int connectedNodes = node.connectedNodes(piece);
         if (connectedNodes != targetSize) {
            System.err.println("Connected Nodes: " + connectedNodes + " is not target: " + targetSize);
            return false;
         }
      }
      
      return true;
   }
   
   public int numActiveNodes() {
      int ret = 0;
      for (final GraphNode node : mGameMap.values()) {
         if (node.isActive()) {
            ++ret;
         }
      }
      return ret;
   }
   
   
   public GraphBounds getMapBounds() {
      final Builder builder = new GraphBounds.Builder();

      for (final GraphNode node : mGameMap.values()) {
         if (!node.isActive()) {
            continue;
         }
         
         final Coordinate coordinate = node.getCoordinate();
         if (VERBOSE)
            System.out.println(coordinate);
         
         builder.addCoordinate(coordinate);
      }
      
      if (VERBOSE)
         System.out.println(builder.toString());
      
      return builder.build();
   }
   
   /**
    * Gets all empty nodes (the perimeter) in the Graph.
    * @param toExclude
    * @return The list of Coordinates that are inactive in the current Graph
    */
   public Set<Coordinate> getPerimeter(final Coordinate toExclude) {
      final Set<Coordinate> ret = new HashSet<Coordinate>();
      
      for (final GraphNode node : mGameMap.values()) {
         if (node.isActive() && !node.getCoordinate().equals(toExclude)) {
            ret.addAll(node.getEmptyNeighbors());
         }
      }
      
      return ret;
   }
   
   
   /**
    * Forces creation of perimeter nodes if they do not exist yet.
    * @param node
    */
   private void verifyPerimeterNodes(final GraphNode node) {
      final Coordinate coordinate = node.getCoordinate();
      for (final HexDirection location : HexDirection.values()) {
         // Apply each direction to the Node's coordinate
         final Coordinate neighborCoordinate = Coordinate.sum(coordinate, location.getMovementMatrix(coordinate));
         
         // If the neighbor does not exist, create the neighbor as a perimeter node
         if (getGraphNode(neighborCoordinate) == null) {
            if (VERBOSE)
               System.out.println("Creating PERIMETER GraphNode at x: " + neighborCoordinate.x + " y: " + neighborCoordinate.y);
            
            createNodeAndLinkToNeighbors(neighborCoordinate);
         }
      }
   }
   
   private void createNodeAndLinkToNeighbors(final Coordinate coordinate) {
      // Create node
      final GraphNode node = new GraphNode(coordinate);
      mGameMap.put(coordinate, node);
      
      // Then link to neighbors
      for (final HexDirection location : HexDirection.values()) {
         final Coordinate neighborCoordinate = Coordinate.sum(coordinate, location.getMovementMatrix(coordinate));
         final GraphNode neighbor = getGraphNode(neighborCoordinate);
         if (neighbor == null) {
            continue;
         }
         
         if (VERBOSE)
            System.out.println("Linking " + coordinate + " to " + neighborCoordinate + " via direction: " + location);
         
         node.setAdjacency(location, neighbor);
         neighbor.setAdjacency(location.opposite(), node);
      }
   }
   
   
   
   
   

   public void renderToConsole() {
      final StringBuilder builder = new StringBuilder();
      
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
            final GraphNode currNode = getGraphNode(new Coordinate(x, y));
            
            final String stringToDisplay;
            
            if (currNode != null && currNode.isActive()) {
               // 2 potential options for output
               final String[] potential = new String[2];
               potential[0] = " p" + currNode.getCurrentController().getId() + ": " + currNode.getPiece().getType().getShortName() + " |";
               potential[1] = String.format("  % 2d,% 2d  |", currNode.getCoordinate().x, currNode.getCoordinate().y);
               
               // Toggle this array to output indices or piece info
               stringToDisplay = potential[0];
            } else {
               stringToDisplay  = " <EMPTY> |";
            }
            
            if (x % 2 == 0) {
               builder.append(stringToDisplay);
               secondXRow += MIDCELL_BLANK_SPACE;
            } else {
               builder.append(MIDCELL_BLANK_SPACE);
               secondXRow += stringToDisplay;
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
