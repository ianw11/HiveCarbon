package io.github.ianw11.hivecarbon.Player;

import java.util.ArrayList;
import java.util.List;

import io.github.ianw11.hivecarbon.engines.RulesEngine.Type;
import io.github.ianw11.hivecarbon.piece.Piece;

public class Player {
   
   private final int mId;
   
   private final ArrayList<Piece> mPieces = new ArrayList<Piece>();
   
   
   public Player(int id) {
      mId = id;
      
      // Initialize the player's pieces
      for (Type type : Type.values()) {
         for (int i = 0; i < type.getNumInGame(); ++i) {
            mPieces.add(new Piece(mId, type));
         }
      }
      
   }
   
   public List<Piece> getPieces() {
      return mPieces;
   }
   
   public Piece[] getUnusedPieces() {
      List<Piece> pieces = new ArrayList<Piece>();
      for (Piece piece : mPieces) {
         if (!piece.isPlaced()) {
            pieces.add(piece);
         }
      }
      
      Piece[] arr = new Piece[pieces.size()];
      pieces.toArray(arr);
      return arr;
   }
   
   public boolean isQueenPlayed() {
      for (Piece piece : mPieces) {
         if (piece.isQueen() && piece.isPlaced()) {
            return true;
         }
      }
      return false;
   }
   
   public boolean isQueenSurrounded() {
      for (Piece piece : mPieces) {
         if (piece.isQueen() && piece.isSurrounded()) {
            return true;
         }
      }
      return false;
   }

}
