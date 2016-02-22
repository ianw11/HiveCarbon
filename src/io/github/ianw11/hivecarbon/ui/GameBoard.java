package io.github.ianw11.hivecarbon.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class GameBoard extends JPanel {
   
   final static Color COLOURBACK = Color.WHITE;
   final static Color COLOURGRID = Color.BLACK;
   
   final static Color COLOURONE = new Color(255, 255, 255, 200);
   final static Color COLOURONETXT = Color.BLUE;
   final static Color COLOURTWO = new Color(0, 0, 0, 200);
   final static Color COLOURTWOTXT = new Color(255, 100, 255);
   
   private final int mBSize;
   private Hex[][] board;
   
   public GameBoard(Hex[][] board, int dimension, MouseListener ml) {
      setBackground(COLOURBACK);

      addMouseListener(ml);
      
      mBSize = dimension;
      this.board = board;
   }

   @Override
   public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setFont(new Font("TimesRoman", Font.PLAIN, 12));
      super.paintComponent(g2);
      
      
      // draw full grid
      for (int i = 0; i < mBSize; i++) {
         for (int j = 0; j < mBSize; j++) {
            HexMech.drawHex(i, j, g2);
         }
      }
      // fill in hexes
      for (int i = 0; i < mBSize; i++) {
         for (int j = 0; j < mBSize; j++) {
            if (board[i][j] != null) {
               HexMech.fillHex(i, j, board[i][j], g2);
            }
         }
      }
   }

}
