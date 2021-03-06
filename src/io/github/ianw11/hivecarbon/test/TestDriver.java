package io.github.ianw11.hivecarbon.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.ianw11.hivecarbon.Player.Player;
import io.github.ianw11.hivecarbon.engines.RulesEngine;
import io.github.ianw11.hivecarbon.engines.RulesEngine.Action;
import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.graph.GraphBounds;
import io.github.ianw11.hivecarbon.graph.GraphNode;
import io.github.ianw11.hivecarbon.graph.HexGraph;
import io.github.ianw11.hivecarbon.piece.Piece;
import io.github.ianw11.hivecarbon.piece.Piece.Type;

public class TestDriver {
   
   public static void main(String[] args) {
      
      ArrayList<TestObject> tests = new ArrayList<TestObject>();
      
      //////////////////////////
      // ADD TESTS BELOW HERE //
      //////////////////////////
      
      
      tests.add(new CoordinateTest());
      tests.add(new Test1());
      tests.add(new Test2());
      tests.add(new Test3());
      tests.add(new Test4());
      //tests.add(new UITest());
      
      
      //////////////////////////
      // ADD TESTS ABOVE HERE //
      //////////////////////////
      
      
      for (TestObject test : tests) {
         test.printTestInfo();
         System.out.println();
         System.out.flush();
         if (!test.run()) {
            System.err.println("FAILURE IN TEST");
            return;
         }  
      }
      
      System.out.println("TESTS RAN SUCCESSFULLY");
   }
   
   public static abstract class TestObject {
      private final String[] playerNames = new String[] { "TestPlayer1", "TestPlayer2" };
      protected final RulesEngine engine = new RulesEngine(playerNames);
      protected final HexGraph hexGraph = engine.getGraph();
      protected final List<Player> players = engine.getPlayers();
      
      protected final List<Piece> playerOnePieces = players.get(0).getPieces();
      protected final List<Piece> playerTwoPieces = players.get(1).getPieces();
      
      private final TestTurnFactory turnFactory = new TestTurnFactory();
      
      boolean turnResult;
      
      private GraphBounds bounds;
      private ArrayList<Piece> placedPieces = new ArrayList<Piece>();
      private Map<Piece, Coordinate> pieceLocations = new HashMap<Piece, Coordinate>();
      
      public abstract void printTestInfo();
      public abstract boolean run();
      
      public TestObject() {
         engine.setTurnFactory(turnFactory);
      }
      
      protected final void feedTurn(Action action, Piece piece, Coordinate source, Coordinate dest) {
         turnFactory.addTurn(action, piece, source, dest);
      }
      
      protected boolean check(Coordinate coordinate, Player player, Type type) {
         GraphNode node = hexGraph.getGraphNode(coordinate);
         assert(node != null);
         assert(node.getCurrentController() == player);
         assert(node.getPiece().getType().equals(type));
         return true;
      }
      
      protected boolean checkNull(Coordinate coordinate) {
         return hexGraph.getGraphNode(coordinate) == null;
      }
      
      protected void expectEqual(Object obtained, Object expected) {
         try {
            assert(obtained.equals(expected));
         } catch (AssertionError e) {
            System.err.println(obtained + " does not equal expected: " + expected);
            throw e;
         }
      }
      
      protected void verifyBounds(int minX, int maxX, int minY, int maxY) {
         bounds = hexGraph.getMapBounds();
         expectEqual(bounds.MIN_X, minX);
         expectEqual(bounds.MAX_X, maxX);
         expectEqual(bounds.MIN_Y, minY);
         expectEqual(bounds.MAX_Y, maxY);
      }
      
      
      /**
       * Place a piece and expect a success
       */
      protected void placePieceExpectSuccess(Piece piece, Coordinate coordinate, Player player, int[] expectedBounds) {
         assert(!placedPieces.contains(piece));
         placedPieces.add(piece);
         doActionSuccess(Action.PLAY, piece, null, coordinate, expectedBounds);
         
         for (Piece p : placedPieces) {
            assert(p.isPlaced());
         }
         
         pieceLocations.put(piece, coordinate);
         
         for (Piece p : pieceLocations.keySet()) {
            Coordinate c = pieceLocations.get(p);
            check(c, p.getOwner(), p.getType());
         }
      }
      
      /**
       * Try to place a piece and expect a failure
       */
      protected void placePieceExpectFail(Piece piece, Coordinate coordinate, Player player, int[] expectedBounds) {
         boolean pieceAlreadyAdded = placedPieces.contains(piece);
         GraphNode node = hexGraph.getGraphNode(coordinate);
         boolean nodeIsNull = node == null;
         
         doActionFail(Action.PLAY, piece, null, coordinate, expectedBounds);
         
         assert(pieceAlreadyAdded == placedPieces.contains(piece));
         if (nodeIsNull) {
            checkNull(coordinate);
         }
         
         for (Piece p : pieceLocations.keySet()) {
            Coordinate c = pieceLocations.get(p);
            check(c, p.getOwner(), p.getType());
         }
      }
      
      
      protected void movePieceExpectSuccess(Piece piece, Coordinate oldCoordinate, Coordinate targetCoordinate, Player player, int[] expectedBounds) {
         // Ensure piece exists on the board already
         assert(placedPieces.contains(piece));
         
         //GraphNode targetNode = graph.findGraphNode(targetCoordinate);
         // assert(targetNode == null || targetNode.getPiece() == null);
         
         doActionSuccess(Action.MOVE, null, oldCoordinate, targetCoordinate, expectedBounds);
         
         pieceLocations.remove(piece);
         pieceLocations.put(piece, targetCoordinate);
         
         for (Piece p : pieceLocations.keySet()) {
            Coordinate c = pieceLocations.get(p);
            check(c, p.getOwner(), p.getType());
         }
      }
      
      
      private void doActionSuccess(Action action, Piece piece, Coordinate source, Coordinate dest, int[] expectedBounds) {
         feedTurn(action, piece, source, dest);
         turnResult = engine.turn();
         genericChecks(expectedBounds);
      }
      
      private void doActionFail(Action action, Piece piece, Coordinate source, Coordinate dest, int[] expectedBounds) {
         feedTurn(action, piece, source, dest);
         turnResult = engine.turn();
         genericChecks(expectedBounds);
      }
      
      private void genericChecks(int[] expectedBounds) {
         verifyBounds(expectedBounds[0], expectedBounds[1], expectedBounds[2], expectedBounds[3]);
      }
   }

}
