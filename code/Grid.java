//Class representing the smart grid in ver. 4 
public class Grid
{
	private int[][] grid;
	private int row;
	private int col;

	public Grid(int[][] grid, int x, int y)
	{
		this.grid = grid;
		row = x;
		col = y;
	}

	public int getRow()
	{
		return row;
	}

	public int getCol()
	{
		return col;
	}

	public int [][] getGrid()
	{
		return grid;
	}
}