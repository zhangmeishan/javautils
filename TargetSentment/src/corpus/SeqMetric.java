package corpus;

public class SeqMetric {

	public int overall_label_count;
	public int correct_label_count;
	public int predicated_label_count;
	
	public SeqMetric()
	{
		overall_label_count = 0;
		correct_label_count = 0;
		predicated_label_count = 0;
	}
	
	public void reset()
	{
		overall_label_count = 0;
		correct_label_count = 0;
		predicated_label_count = 0;
	}

	public boolean bIdentical()
	{
		if(predicated_label_count == 0)
		{
			if(overall_label_count == correct_label_count)
			{
				return true;
			}
			return false;
		}
		else
		{
			if(overall_label_count == correct_label_count && predicated_label_count == correct_label_count)
			{
				return true;
			}
			return false;
		}
	}
	
	public double getAccuracy()
	{
		if(predicated_label_count == 0)
		{
			return correct_label_count*1.0/overall_label_count;
		}
		else
		{
			return correct_label_count*2.0/(overall_label_count + predicated_label_count);
		}
	}
	
	
	public void print()
	{
		if(predicated_label_count == 0)
		{
			System.out.println(String.format("Accuracy:\tP=%d/%d=%.5f",
					correct_label_count, overall_label_count, correct_label_count*1.0/overall_label_count));
		}
		else
		{
			System.out.println(String.format("Recall:\tP=%d/%d=%.5f, Accuracy:\tP=%d/%d=%.5f, Fmeasure:\t%.5f",
					correct_label_count, overall_label_count, correct_label_count*1.0/overall_label_count,
					correct_label_count, predicated_label_count, correct_label_count*1.0/predicated_label_count,
					correct_label_count*2.0/(overall_label_count + predicated_label_count)));
		}
	}
}
