package noodles.square;
import noodles.*;

public class SquarePuzzle extends Puzzle{

   public SquarePuzzle(Grid grid){
      super(grid, new int[]{0, 1, 2, 3}, 3);
   }
}//end SquarePuzzle