package noodles;
import noodles.NoodlePanel;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public abstract class PanelStrategy{
   private int rows, cols, segment;
   private Layout layout;
   private Grid grid;
   private Puzzle puzzle;
   private BasicStroke noodleStroke, baseStroke;
   private String themeName;
   private Color[] theme;
   private Point2D.Double clickPoint, mousePoint;
   private Noodle draggedNoodle;
   
   public PanelStrategy(int rows, int cols, Layout layout, Grid grid, String themeName){
      this.rows = rows;
      this.cols = cols;
      this.segment = -1;
      this.layout = layout;
      this.grid = grid;
      this.baseStroke = new BasicStroke();
      this.setTheme(themeName);
   }
   
   //get methods
   public int getRows(){return this.rows;}
   public int getCols(){return this.cols;}
   public Layout getLayout(){return this.layout;}
   public Grid getGrid(){return this.grid;}
   public Puzzle getPuzzle(){return this.puzzle;}
   public String getThemeName(){return this.themeName;}
   public Color[] getTheme(){return this.theme;}
   
   //set methods
   public void setLayout(Layout layout){this.layout = layout;}
   public void setGrid(Grid grid){this.grid = grid;}
   public void setPuzzle(Puzzle puzzle){this.puzzle = puzzle;}
   public void setNoodleStroke(BasicStroke noodleStroke){this.noodleStroke = noodleStroke;}
   public void setRows(int rows){
      this.rows = rows;
      this.grid.initNoodles(this.rows, this.cols);
      this.regenerate();
   }
   public void setCols(int cols){
      this.cols = cols;
      this.grid.initNoodles(this.rows, this.cols);
      this.regenerate();
   }
   
   public void setTheme(String themeName){
      this.themeName = themeName;
      this.theme = ColorTheme.getTheme(themeName);
   }
   
   public void regenerate(){
      this.puzzle.initPuzzle(this.grid);
   }//end regenerate
   
   public int getSegment(Point2D.Double p1, Point2D.Double p2){
      int segments = this.layout.getSegments();
      double angle = Math.toDegrees(Math.atan2(p1.getY() - p2.getY(), p1.getX() - p2.getX()));
      angle += (360 / segments) * this.layout.getCornerAngle();
      angle %= 360;
      if(angle < 0)
         angle +=360;
      return (int)((angle / (360 / segments)) % segments);
   }
   
   public void mousePressed(MouseEvent e, NoodlePanel panel){
      if(this.puzzle.isSolved()) {
         this.puzzle.initPuzzle(this.grid);
      	 panel.massageSolved();
      }
      else{
         Point2D.Double point = new Point2D.Double(e.getX(), e.getY());
         Noodle noodle = this.layout.pixelToNoodle(point);
         int row = this.grid.getRow(noodle);
         int col = this.grid.getCol(noodle);
         if(row >= 0 && row < this.rows && col >= 0 && col < this.cols){
            noodle = this.grid.getNoodle(row, col);
            if(this.layout.getBase(noodle).contains(point)){
               if(e.getButton() == 1){
                  if(this.layout.getNoodleCenter(noodle, .2).contains(point))
                     noodle.rotate(-1);
                  else{
                     this.clickPoint = point;
                     this.draggedNoodle = noodle;
                  }
               }
               else if(e.getButton() == 2)
                  noodle.rotate(-1 * noodle.getRotation());
               else if(e.getButton() == 3)
                  noodle.rotate(1);
            }
            this.puzzle.testPowered(this.grid);
         }
      }
   }//end mousePressed
   
   public void mouseReleased(){
      if(this.draggedNoodle != null){
         this.segment = -1;
         this.draggedNoodle = null;
         this.clickPoint = null;
         this.mousePoint = null;
      }
      this.puzzle.testPowered(this.grid);
   }//end mouseReleased
   
   public void mouseDragged(MouseEvent e){
      if(clickPoint != null){
         if(this.segment < 0)
            this.segment = this.getSegment(this.clickPoint, this.layout.noodleToPixel(draggedNoodle));
         this.mousePoint = new Point2D.Double(e.getX(), e.getY());
         int difference = this.getSegment(this.mousePoint, this.layout.noodleToPixel(draggedNoodle)) - this.segment;
         this.draggedNoodle.rotate(difference);
         this.segment += difference;
      }
   }//end mouseDragged
   
   public void paintComponent(Graphics2D g2){
      this.noodleStroke = new BasicStroke(this.layout.getStroke(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
      g2.setStroke(this.baseStroke);
      g2.setColor(this.theme[2]);
      Noodle[][] noodles = this.grid.getNoodles();
      Noodle cur;
      for(int row = 0; row < this.rows; row++){
         for(int col = 0; col < this.cols; col++){
            cur = noodles[row][col];
            this.paintNoodle(g2, cur, row, col);
         }
      }
   }//end paintComponent
   
   public void paintNoodle(Graphics2D g2, Noodle noodle, int row, int col){
      g2.fill(this.layout.getBase(noodle));
      g2.setColor(noodle.getPowered() ? this.theme[0] : this.theme[1]);
      g2.setStroke(this.noodleStroke);
      boolean[] activeSides = noodle.getActiveSides();
      for(int i = 0; i < activeSides.length; i++){
         if(activeSides[i]){
            g2.draw(this.layout.getNoodleSide(noodle, i));
         }
      }
      g2.fill(this.layout.getNoodleCenter(noodle, .2));
      if(this.drawSource(noodle, row, col)){
         g2.setColor(this.theme[2]);
         g2.fill(this.layout.getNoodleCenter(noodle, -.1));
      }
      g2.setColor(this.theme[2]);
   }//end paintNoodle
   
   public boolean drawSource(Noodle noodle, int row, int col){///?????
      if(noodle.equals(this.puzzle.getSource()))
         return true;
      if(row == this.grid.getRow(this.puzzle.getSource()) && (col == this.grid.getNoodles()[0].length && this.grid.getCol(puzzle.getSource()) == 0))
         return true;
      if(col == this.grid.getCol(this.puzzle.getSource()) && (row == this.grid.getNoodles().length && this.grid.getRow(puzzle.getSource()) == 0))
         return true;
      return false;
   }
   
   public void setCustomSize(Dimension newSize){
      Dimension oldSize = this.getPreferredSize();
      double newRatio = newSize.getWidth() / newSize.getHeight();
      double oldRatio = oldSize.getWidth() / oldSize.getHeight();
      
      if(newRatio < oldRatio){
         this.layout.setSize(getCustomWidth(newSize.getWidth()));
         this.layout.setMarginX(0);
         this.layout.setMarginY((newSize.getHeight() - this.getPreferredSize().getHeight()) / 2);
      }
      else{
         this.layout.setSize(getCustomHeight(newSize.getHeight()));
         this.layout.setMarginX((newSize.getWidth() - this.getPreferredSize().getWidth()) / 2);
         this.layout.setMarginY(0);
      }
   }//end setCustomSize
   public abstract double getCustomWidth(double newWidth);
   public abstract double getCustomHeight(double newHeight);
   public abstract Dimension getPreferredSize();
}//end PanelStrategy class