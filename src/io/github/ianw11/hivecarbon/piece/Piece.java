package io.github.ianw11.hivecarbon.piece;

import io.github.ianw11.hivecarbon.engines.RulesEngine.Type;

public class Piece {
   
   public enum Location {
      TOP (new int[] {0,-1}),
      TOP_RIGHT (new int[] {1,-1}),
      BOTTOM_RIGHT (new int[] {1,0}),
      BOTTOM (new int[] {0, 1}),
      BOTTOM_LEFT (new int[] {-1,0}),
      TOP_LEFT (new int[] {-1,-1});
      
      private final int[] movementMatrix;
      private Location(int[] matrix) {
         movementMatrix = matrix;
      }
      
      public int[] getMovementMatrix(int xCoord) {
         if (xCoord % 2 == 0) {
            return evenXMatrix();
         } else {
            return oddXMatrix();
         }
      }
      
      public Location opposite() {
         switch(this) {
         case TOP:
            return BOTTOM;
         case TOP_RIGHT:
            return BOTTOM_LEFT;
         case BOTTOM_RIGHT:
            return TOP_LEFT;
         case BOTTOM:
            return TOP;
         case BOTTOM_LEFT:
            return TOP_RIGHT;
         case TOP_LEFT:
            return BOTTOM_RIGHT;
         default:
            throw new UnsupportedOperationException();
         }
      }
      
      private int[] evenXMatrix() {
         return movementMatrix;
      }
      
      private int[] oddXMatrix() {
         switch(this) {
         case TOP:
         case BOTTOM:
            return movementMatrix;
         default:
            return new int[] {movementMatrix[0], movementMatrix[1] + 1};
         }
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
      return "P" + mOwnerNumber + " - " + mType.name();
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
   
   /**
    * Test function
    */
   public int getNumNeighbors() {
      return mNumNeighbors;
   }
   
   public boolean isSurrounded() {
      return mNumNeighbors == 6;
   }

}
