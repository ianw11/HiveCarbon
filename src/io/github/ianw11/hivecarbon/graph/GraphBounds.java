package io.github.ianw11.hivecarbon.graph;

public class GraphBounds {
   
   public final int MIN_X, MAX_X, MIN_Y, MAX_Y;
   
   private GraphBounds(int minx, int maxx, int miny, int maxy) {
      MIN_X = minx;
      MAX_X = maxx;
      MIN_Y = miny;
      MAX_Y = maxy;
   }
   
   public String toString() {
      return "Bounds -- MIN_X: " + MIN_X + " MAX_X: " + MAX_X + " MIN_Y: " + MIN_Y + " MAX_Y: " + MAX_Y;
   }
   
   public boolean isPointInside(Coordinate coordinate) {
      return   coordinate.x >= MIN_X &&
               coordinate.x <= MAX_X &&
               coordinate.y >= MIN_Y &&
               coordinate.y <= MAX_Y;
   }
   
   
   
   public static class Builder {
      private int minX = 0,
                  maxX = 0,
                  minY = 0,
                  maxY = 0;
      
      public void setX(int x) {
         if (x < minX) {
            this.minX = x;
         }
         
         if (x > maxX) {
            this.maxX = x;
         }
      }
      
      public void setY(int y) {
         if (y < minY) {
            this.minY = y;
         }
         
         if (y > maxY) {
            this.maxY = y;
         }
      }
      
      public String toString() {
         return "Bounds -- MIN_X: " + minX + " MAX_X: " + maxX + " MIN_Y: " + minY + " MAX_Y: " + maxY;
      }
      
      public GraphBounds build() {
         
         return new GraphBounds(minX, maxX, minY, maxY);
      }
   }

}
