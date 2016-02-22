package io.github.ianw11.hivecarbon.graph;

import io.github.ianw11.hivecarbon.piece.Piece.Location;

public class Coordinate {
   
   public final int x;
   public final int y;
   
   public Coordinate(int x, int y) {
      this.x = x;
      this.y = y;
   }
   
   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (!(obj instanceof Coordinate)) {
         return false;
      }
      Coordinate that = (Coordinate)obj;
      
      return this.x == that.x && this.y == that.y;
   }
   
   @Override
   public String toString() {
      return "[Coordinate (" + x + ", " + y + ")]";
   }
   
   public static Coordinate sum(Coordinate c1, Coordinate c2) {
      return new Coordinate(c1.x + c2.x, c1.y + c2.y);
   }
   
   public static Coordinate sum(Coordinate c1, int[] arr) {
      assert(arr.length == 2);
      return new Coordinate(c1.x + arr[0], c1.y + arr[1]);
   }
   
   public static Coordinate sum(Coordinate c1, Location l, int xCoord) {
      int[] matrix = l.getMovementMatrix(xCoord);
      return new Coordinate(c1.x + matrix[0], c1.y + matrix[1]);
   }

}
