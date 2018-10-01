//holds many constant values needed to be passed in for the Fork Join framework in ver 4 and the threads in ver 5 
public class GridStarter
{
	private CensusGroup[] cg;
	private Rectangle rect;
	private float rowStep, colStep;
	public GridStarter(CensusGroup[] cg, Rectangle rect, float rs, float cs)
	{
		this.cg = cg;
		this.rect = rect;
		rowStep = rs;
		colStep = cs; 
	}
	public float getRowStep (){return rowStep;}
	public float getColStep (){return colStep;}
	public CensusGroup[] getCG(){return cg;}
	public Rectangle getRect(){return rect;}
}