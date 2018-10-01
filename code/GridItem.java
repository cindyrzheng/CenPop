
import java.util.concurrent.locks.*;
//used in ver. 5- a 2-d array of GridItem objects are used instead of a 2-d in array to hold the 
//population in a smart grid so that multiple threads can add to this grid and use lock methods 
//to prevent thread interference. 
public class GridItem
{
	private int pop;
	private ReentrantLock l;
	public GridItem(int p)
	{
		pop = p;
		l = new ReentrantLock();

	}

	public void addPop(int add)
	{
		pop += add;
	}

	public void subPop(int sub)
	{
		pop -= sub;
	}

	public int getPop()
	{
		return pop;
	}

	public Lock getLock()
	{
		return l;
	}
}