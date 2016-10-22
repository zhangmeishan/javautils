package corpus;

public class PhraseSplit {

	/**
	 * @param args
	 */
	public int start = -1;;
	public int end = -1;
	
	public PhraseSplit(int s, int e)
	{
		start = s;
		end = e;
	}
	
	public int compare(PhraseSplit target)
	{
		if(start > target.start || (start == target.start && end > target.end)) return 1;
		else if(start < target.start || (start == target.start && end < target.end)) return -1;
		else return 0;
	}
}
