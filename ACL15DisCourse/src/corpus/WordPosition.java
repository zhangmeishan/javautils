package corpus;

public class WordPosition {

	int startSentId;
	int startWordId;
	
	public WordPosition()
	{
		startSentId = 0;
		startWordId = 0;
	}
	
	public WordPosition(int sentId, int wordId)
	{
		startSentId = sentId;
		startWordId = wordId;
	}
}
