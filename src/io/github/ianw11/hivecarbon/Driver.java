package io.github.ianw11.hivecarbon;

import java.awt.Color;
import java.awt.Container;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import io.github.ianw11.hivecarbon.engines.PlaceTurnAction;
import io.github.ianw11.hivecarbon.engines.RulesEngine;
import io.github.ianw11.hivecarbon.engines.TurnAction;
import io.github.ianw11.hivecarbon.graph.Coordinate;
import io.github.ianw11.hivecarbon.graph.Graph;
import io.github.ianw11.hivecarbon.graph.GraphBounds;
import io.github.ianw11.hivecarbon.graph.GraphNode;
import io.github.ianw11.hivecarbon.piece.Piece;
import io.github.ianw11.hivecarbon.piece.Piece.Type;
import io.github.ianw11.hivecarbon.ui.BasicMouseListener;
import io.github.ianw11.hivecarbon.ui.BasicMouseListener.MouseActionListener;
import io.github.ianw11.hivecarbon.ui.GameBoard;
import io.github.ianw11.hivecarbon.ui.Hex;
import io.github.ianw11.hivecarbon.ui.HexMech;
import io.github.ianw11.hivecarbon.ui.tilepool.TilePoolListView;

/**********************************
 * This is the main class of a Java program to play a game based on hexagonal
 * tiles. The mechanism of handling hexes is in the file hexmech.java. Written
 * by: M.H. Date: December 2012
 ***********************************/

public class Driver implements MouseActionListener {

   final static int HEXSIZE = 100; // hex size in pixels
   final static int BORDERS = 15;

   private static final int TILEPOOL = 1;
   private static final int BOARD = 0;

   private RulesEngine mEngine;
   private JFrame mFrame;
   
   private JFrame mTilePoolFrame;

   private int boardX, boardY;
   private int tileX, tileY;

   public Driver() {
      initGUI();
      initGame();
   }

   public boolean doAction(TurnAction action) {
      boolean ret = mEngine.turn(action);
      if (ret) {
         buildBoard();
      }
      return ret;
   }

   public void close() {
      mFrame.setVisible(false);
      mFrame.dispose();
   }
   
   @Override
   public void onMouseAction(Coordinate coordinate) {
      boolean result = mEngine.turn(new PlaceTurnAction(mEngine.getPlayers().get(0).getPieces().get(0), Coordinate.sum(coordinate, new Coordinate(-1, -1)), 0));
      
      System.out.println("Result: " + result);
      
      if (result) {
         buildBoard();
      }
   }
   

   private void initGame() {
      mEngine = new RulesEngine(2);

      HexMech.setXYasVertex(false); // RECOMMENDED: leave this as FALSE.
      HexMech.setHeight(HEXSIZE); // Either setHeight or setSize must be run to initialize the hex
      HexMech.setBorders(BORDERS);

      buildBoard();
   }

   private void buildBoard() {
      GraphBounds bounds = mEngine.getNormalizedBounds();
      int[] shiftXY = mEngine.getShift();

      int x = bounds.MAX_X + 3;
      int y = bounds.MAX_Y + 3;
      Hex[][] board = new Hex[x][y];
      System.out.println("Building board with dimensions (" + x + ", " + y + ")");

      int boardSize = Math.max(x, y);

      // Initialize game board with empty hexes
      for (int i = bounds.MIN_X; i < bounds.MAX_X; ++i) {
         for (int j = bounds.MIN_Y; j < bounds.MAX_Y; ++j) {
            board[i][j] = Hex.emptyHex();
         }
      }

      // Place correct hexes
      Graph graph = mEngine.getGraph();
      Iterator<GraphNode> iterator = graph.getIterator();
      while (iterator.hasNext()) {
         GraphNode node = iterator.next();

         Color color;
         Type type;
         if (node.isActive()) {
            System.out.println("Active node");
            color = node.getCurrentController() == 0 ? Hex.PLAYER_ONE_COLOR : Hex.PLAYER_TWO_COLOR;
            type = node.getPiece().getType();
         } else {
            color = Color.GREEN;
            type = null;
         }

         Coordinate shifted = Coordinate.sum(node.getCoordinate(), shiftXY);

         board[shifted.x + 1][shifted.y + 1] = new Hex(color, type);
      }


      JPanel mPanel = new GameBoard(board, boardSize, new BasicMouseListener(boardSize, this));
      // for hexes in the FLAT orientation, the height of a 10x10 grid is 1.1764
      // * the width. (from h / (s+t))
      int SCRSIZE = HEXSIZE * (boardSize + 1) + BORDERS * 3;

      updateFrame(mPanel, BOARD, (int)(SCRSIZE/1.23), SCRSIZE);
      updateTilePoolFrame(0);
   }
   
   private void initGUI() {
      //JFrame.setDefaultLookAndFeelDecorated(true);

      mFrame = new JFrame("Hive");
      mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      boardX = boardY = tileX = tileY = 0;

      //buildTilePool();

      mFrame.setResizable(false);
      mFrame.setLocationRelativeTo(null);
      mFrame.setVisible(true);
      
      
      mTilePoolFrame = new JFrame("Tile Pool");
      mTilePoolFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      mTilePoolFrame.setResizable(true);
      mTilePoolFrame.setLocationRelativeTo(mFrame);
      mTilePoolFrame.setVisible(true);
      
   }

   private void buildTilePool() {
      JPanel panel = new JPanel();

      JTextArea text = new JTextArea("Test");
      panel.add(text);

      updateFrame(panel, TILEPOOL, 150, 150);
   }

   
   private void updateFrame(JPanel panel, int index, int dimX, int dimY) {
      if (index == BOARD) {
         boardX = dimX;
         boardY = dimY;
      } else if(index == TILEPOOL) {
         tileX = dimX;
         tileY = dimY;
      }

      mFrame.setSize(Math.max(boardX, tileX), boardY + tileY);
      
      Container pane = mFrame.getContentPane();
      if (pane.getComponents().length > index) {
         pane.remove(index);
      }
      pane.add(panel, index);
      
      // Force a draw
      panel.revalidate();
   }

   private void updateTilePoolFrame(int playerNum) {
      JList<Piece> list = new TilePoolListView(mEngine.getUnusedPieces(playerNum));
      
      Container pane = mTilePoolFrame.getContentPane();
      pane.add(list, 0);
      pane.revalidate();
   }

   // MAIN METHOD
   public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            new Driver();
         }
      });
   }
}