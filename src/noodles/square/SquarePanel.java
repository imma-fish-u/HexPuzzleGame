package noodles.square;
import noodles.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class SquarePanel extends PanelStrategy{
   
   public SquarePanel(int rows, int cols, String themeName){
      super(rows, cols, new SquareLayout(), new SquareGrid(rows, cols), themeName);
      super.setPuzzle(new SquarePuzzle(super.getGrid()));
   }
   
   public Noodle getGhost(int row, int col){
      return new Square(row, col, false, new boolean[]{false, false, false, false});
   }//end getGhostNoodle
   
   public Noodle getGhost(int row, int col, boolean powered, boolean[] activeSides){
      return new Square(row, col, powered, activeSides);
   }//end getGhostNoodle
   
   public double getCustomWidth(double newWidth){
      double size = newWidth / (super.getCols() * 2);
      return size / (Layout.PADDINGRATIO + 1);
   }//end getCustomWidth
   
   public double getCustomHeight(double newHeight){
      double size = newHeight / (super.getRows() * 2);
      return size / (Layout.PADDINGRATIO + 1);
   }//end getCustomHeight
   
   public Dimension getPreferredSize(){
      double square = (super.getLayout().getSize() + super.getLayout().getPadding()) * 2;
      int x = (int)(square * super.getCols());
      int y = (int)(square * super.getRows());
      return new Dimension(x, y);
   }//end getPreferredSize
}//end SquarePanel