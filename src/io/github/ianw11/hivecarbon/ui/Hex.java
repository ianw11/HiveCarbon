package io.github.ianw11.hivecarbon.ui;

import java.awt.Color;

import io.github.ianw11.hivecarbon.engines.RulesEngine.Type;

public class Hex {
   
   public static Hex emptyHex() {
      Hex hex = new Hex(NO_PLAYER_COLOR, null);
      return hex;
   }
   
   public static final Color PLAYER_ONE_COLOR = Color.WHITE;
   public static final Color PLAYER_TWO_COLOR = Color.BLACK;
   public static final Color NO_PLAYER_COLOR = Color.GRAY;
   
   private final Color mPlayerColor;
   private final Type mPieceType;
   
   public Hex(Color playerColor, Type type) {
      mPlayerColor = playerColor;
      mPieceType = type;
   }
   
   public Color getColor() {
      return mPlayerColor;
   }
   
   public Type getType() {
      return mPieceType;
   }
   
   public boolean isActive() {
      return mPieceType != null;
   }

}
