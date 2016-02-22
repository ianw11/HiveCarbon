package io.github.ianw11.hivecarbon.ui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import io.github.ianw11.hivecarbon.graph.Coordinate;

public class BasicMouseListener extends MouseAdapter {
   
   public interface MouseActionListener {
      public void onMouseAction(Coordinate coordinate);
   }
   private final MouseActionListener mListener;
   
   private final int mBSize;
   
   public BasicMouseListener(int boardSize, MouseActionListener listener) {
      mBSize = boardSize;
      mListener = listener;
   }

   @Override
   public void mouseClicked(MouseEvent e) {
      //System.out.println(e.toString());
      Point p = new Point(HexMech.pxtoHex(e.getX(), e.getY()));
      //System.out.println("Click at: " + p.x + ", " + p.y);

      if (p.x < 0 || p.y < 0 || p.x >= mBSize || p.y >= mBSize) {
         System.err.println("Mouse clicked out of bounds");
         return;
      }
      
      System.out.println("Successful mouse click at: " + p.x + ", " + p.y);
      
      mListener.onMouseAction(new Coordinate(p.x, p.y));
   }

}
