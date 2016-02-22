package io.github.ianw11.hivecarbon.ui.tilepool;

import javax.swing.JList;

import io.github.ianw11.hivecarbon.piece.Piece;

public class TilePoolView {
   
   private final Piece mPiece;
   
   //private JLabe
   
   public TilePoolView(Piece piece) {
      mPiece = piece;
   }
   
   public String getDisplayName(){
      return mPiece.getType().name();
   }

}
