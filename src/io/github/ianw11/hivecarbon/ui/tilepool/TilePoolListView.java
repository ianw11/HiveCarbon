package io.github.ianw11.hivecarbon.ui.tilepool;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import io.github.ianw11.hivecarbon.piece.Piece;

public class TilePoolListView extends JList<Piece> {
   
   public TilePoolListView(Piece[] arr) {
      super(arr);
      
      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      setLayoutOrientation(JList.VERTICAL);
      deselectAll();
      
      JScrollPane listScroller = new JScrollPane(this);
   }
   
   
   
   public void deselectAll() {
      setVisibleRowCount(-1);
   }
   
   

}
