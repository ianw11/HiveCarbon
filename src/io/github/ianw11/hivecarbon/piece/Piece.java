package io.github.ianw11.hivecarbon.piece;

import io.github.ianw11.hivecarbon.Player.Player;
import io.github.ianw11.hivecarbon.graph.HexGraph;

public class Piece {
   
   public enum Type {
      QUEEN_BEE(1, "QNB"),    // Index 0
      BEETLE(2, "BTL"),       // Index 1, 2
      GRASSHOPPER(3, "GHP"),  // Index 3, 4, 5
      SPIDER(2, "SPR"),       // Index 6, 7
      SOLDIER_ANT(3, "ANT");  // Index 8, 9, 10

      private final int mNumInGame;
      private final String mShortName;
      private Type(final int numInGame, final String shortName) {
         mNumInGame = numInGame;
         mShortName = shortName;
      }

      public int getNumInGame() {
         return mNumInGame;
      }
      public String getShortName() {
         return mShortName;
      }
   };
   
   private static final int MAX_NUM_NEIGHBORS = HexGraph.MAX_NUM_NEIGHBORS;
   private static final int MIN_NUM_NEIGHBORS = 0;
   
   
   private final Player mOwner;
   private final Type mType;
   
   private boolean mIsPlaced = false;
   private int mNumNeighbors = MIN_NUM_NEIGHBORS;
   
   public Piece(final Player owner, final Type type) {
      mOwner = owner;
      mType = type;
   }
   
   public String toString() {
      return "[PIECE P" + mOwner.getId() + " - " + mType.name() + "]";
   }
   
   public Player getOwner() {
      return mOwner;
   }
   
   public Type getType() {
      return mType;
   }
   
   
   public void setPlaced() {
      mIsPlaced = true;
   }
   
   public boolean isPlaced() {
      return mIsPlaced;
   }
   
   
   public boolean isQueen() {
      return mType.equals(Type.QUEEN_BEE);
   }
   
   
   public void addNeighbor() {
      if (++mNumNeighbors > MAX_NUM_NEIGHBORS) {
         throw new IllegalStateException("More than " + MAX_NUM_NEIGHBORS + " neighbors for tile");
      }
   }
   
   public void removeNeighbor() {
      if (--mNumNeighbors < 0) {
         throw new IllegalStateException("Less than " + MIN_NUM_NEIGHBORS + " neighbors for tile");
      }
   }
   
   public void removeAllNeighbors() {
      mNumNeighbors = MIN_NUM_NEIGHBORS;
   }
   
   public boolean isSurrounded() {
      return mNumNeighbors == MAX_NUM_NEIGHBORS;
   }
   
   
   public int getNumNeighbors() {
      return mNumNeighbors;
   }
}
