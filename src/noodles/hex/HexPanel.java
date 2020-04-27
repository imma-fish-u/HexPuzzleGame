package noodles.hex;
import noodles.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class HexPanel extends PanelStrategy{
   
   public HexPanel(int rows, int cols, String themeName){
      super(rows, cols, new HexLayout(), new HexGrid(rows, cols), themeName);
      super.setPuzzle(new HexPuzzle(super.getGrid()));
   }
   
   public double getCustomWidth(double newWidth){
      double size = newWidth / ((((3/4d) * (super.getCols() + 1))) * 2);
      return size / (Layout.PADDINGRATIO + 1);
   }//end getCustomWidth
   
   public double getCustomHeight(double newHeight){
      double size = newHeight / (Math.sqrt(3) * (super.getRows() + 1/2d));
      return size / (Layout.PADDINGRATIO + 1);
   }//end getCustomHeight
   
   public Dimension getPreferredSize(){
      double width = (super.getLayout().getSize() + super.getLayout().getPadding())*2;
      double height = (Math.sqrt(3)/2d) * width;
      int x = (int)(width * (((3/4d) * (super.getCols()))) + 1);
      int y = (int)((height * super.getRows()) + (height / 2));
      return new Dimension(x, y);
   }//end getPreferredSize
}//end HexPanel class