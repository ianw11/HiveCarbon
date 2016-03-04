package io.github.ianw11.hivecarbon.piece;

public class Piece {
   
   public enum Type {
      QUEEN_BEE(1, "QNB"),    // Index 0
      BEETLE(2, "BTL"),       // Index 1, 2
      GRASSHOPPER(3, "GHP"),  // Index 3, 4, 5
      SPIDER(2, "SPR"),       // Index 6, 7
      SOLDIER_ANT(3, "ANT");  // Index 8, 9, 10

      private final int mNumInGame;
      private final String mShortName;
      private Type(int numInGame, String shortName) {
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
   
   
   private final int mOwnerNumber;
   private final Type mType;
   
   
   private boolean mIsPlaced = false;
   private int mNumNeighbors = 0;
   
   public Piece(int ownerNumber, Type type) {
      mOwnerNumber = ownerNumber;
      mType = type;
   }
   
   public String toString() {
      return "[PIECE P" + mOwnerNumber + " - " + mType.name() + "]";
   }
   
   public int getOwnerNumber() {
      return mOwnerNumber;
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
      if (++mNumNeighbors > 6) {
         throw new IllegalStateException("More than 6 neighbors for tile");
      }
   }
   
   public void removeNeighbor() {
      if (--mNumNeighbors < 0) {
         throw new IllegalStateException("Less than 0 neighbors for tile");
      }
   }
   
   public void removeAllNeighbors() {
      mNumNeighbors = 0;
   }
   
   public boolean isSurrounded() {
      return mNumNeighbors == 6;
   }
   
   
   
   public int getNumNeighbors() {
      return mNumNeighbors;
   }
}
