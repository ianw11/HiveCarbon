package io.github.ianw11.hivecarbon.Player;

import java.util.ArrayList;
import java.util.List;

import io.github.ianw11.hivecarbon.piece.Piece;
import io.github.ianw11.hivecarbon.piece.Piece.Type;

public class Player {
   
   private final String mName;
   private final int mId;
   
   private final ArrayList<Piece> mPieces = new ArrayList<Piece>();
   
   
   public Player(String name, int id) {
      mName = name;
      mId = id;
      
      // Initialize the player's pieces
      for (final Type type : Type.values()) {
         for (int i = 0; i < type.getNumInGame(); ++i) {
            mPieces.add(new Piece(this, type));
         }
      }
      
   }
   
   public String getName() {
      return mName;
   }
   
   public int getId() {
      return mId;
   }
   
   public List<Piece> getPieces() {
      return mPieces;
   }
   
   public Piece[] getUnusedPieces() {
      final List<Piece> pieces = new ArrayList<Piece>();
      for (final Piece piece : mPieces) {
         if (!piece.isPlaced()) {
            pieces.add(piece);
         }
      }
      
      final Piece[] arr = new Piece[pieces.size()];
      pieces.toArray(arr);
      return arr;
   }
   
   public boolean isQueenPlayed() {
      for (final Piece piece : mPieces) {
         if (piece.isQueen()) {
            return piece.isPlaced();
         }
      }
      
      throw new IllegalStateException("Player doesn't have queen piece");
   }
   
   public boolean isQueenSurrounded() {
      for (final Piece piece : mPieces) {
         if (piece.isQueen()) {
            return piece.isSurrounded();
         }
      }
      throw new IllegalStateException("Player doesn't have queen piece");
   }

}
