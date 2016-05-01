package io.github.ianw11.hivecarbon.graph;

import io.github.ianw11.hivecarbon.graph.GraphNode.HexDirection;

public class Coordinate {
   
   public final int x;
   public final int y;
   
   public Coordinate(int x, int y) {
      this.x = x;
      this.y = y;
   }
   
   public static Coordinate sum(Coordinate c1, Coordinate c2) {
      return new Coordinate(c1.x + c2.x, c1.y + c2.y);
   }
   
   public static Coordinate sum(Coordinate c1, int[] arr) {
      assert(arr.length == 2);
      return new Coordinate(c1.x + arr[0], c1.y + arr[1]);
   }
   
   public static Coordinate sum(Coordinate c1, HexDirection l) {
      final int[] matrix = l.getMovementMatrix(c1);
      return new Coordinate(c1.x + matrix[0], c1.y + matrix[1]);
   }
   
   
   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (!(obj instanceof Coordinate)) {
         return false;
      }
      final Coordinate that = (Coordinate)obj;
      
      return this.x == that.x && this.y == that.y;
   }
   
   @Override
   public String toString() {
      return "[Coordinate (" + x + ", " + y + ")]";
   }
   
   @Override
   public int hashCode() {
      int hash = 17;
      hash = ((hash + x) << 5) - (hash + x);
      hash = ((hash + y) << 5) - (hash + y);
      return hash;
   }

}
