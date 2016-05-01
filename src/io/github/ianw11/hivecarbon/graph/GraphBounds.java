package io.github.ianw11.hivecarbon.graph;

public class GraphBounds {
   
   public final int MIN_X, MAX_X, MIN_Y, MAX_Y;
   
   /*
    * Private constructor as this class may only be instantiated by the Builder
    */
   private GraphBounds(int minx, int maxx, int miny, int maxy) {
      MIN_X = minx;
      MAX_X = maxx;
      MIN_Y = miny;
      MAX_Y = maxy;
   }
   
   @Override
   public String toString() {
      return "Bounds -- MIN_X: " + MIN_X + " MAX_X: " + MAX_X + " MIN_Y: " + MIN_Y + " MAX_Y: " + MAX_Y;
   }
   
   /**
    * Returns whether a provided coordinate lies within (inclusive) this boundary
    * @param coordinate The coordinate in question
    * @return True if the parameter coordinate exists within the boundary 
    */
   public boolean isPointInside(Coordinate coordinate) {
      return   coordinate.x >= MIN_X &&
               coordinate.x <= MAX_X &&
               coordinate.y >= MIN_Y &&
               coordinate.y <= MAX_Y;
   }
   
   
   /**
    * The class responsible for constructing GraphBounds objects
    */
   public static class Builder {
      private int minX = 0,
                  maxX = 0,
                  minY = 0,
                  maxY = 0;
      
      public void addX(int x) {
         if (x < minX) {
            this.minX = x;
         }
         
         if (x > maxX) {
            this.maxX = x;
         }
      }
      
      public void addY(int y) {
         if (y < minY) {
            this.minY = y;
         }
         
         if (y > maxY) {
            this.maxY = y;
         }
      }
      
      public void addCoordinate(Coordinate coordinate) {
         addX(coordinate.x);
         addY(coordinate.y);
      }
      
      @Override
      public String toString() {
         return "Bounds -- MIN_X: " + minX + " MAX_X: " + maxX + " MIN_Y: " + minY + " MAX_Y: " + maxY;
      }
      
      public GraphBounds build() {
         return new GraphBounds(minX, maxX, minY, maxY);
      }
   }

}
