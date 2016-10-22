package corpus;

import java.io.*;
import java.util.*;

public class EvaluateMetrics {
	int edu_count_pred;
	int edu_count_gold;
	int edu_count_correct;
	
	int span_count_pred;
	int span_count_gold;
	int span_count_correct;
	
	int nuclear_count_pred;
	int nuclear_count_gold;
	int nuclear_count_correct;
	
	int dislabel_count_pred;
	int dislabel_count_gold;
	int dislabel_count_correct;
	
	int synlabel_count_pred;
	int synlabel_count_gold;
	int synlabel_count_correct;
	
	int poslabel_count_pred;
	int poslabel_count_gold;
	int poslabel_count_correct;

	/*
	int spanbin_count_pred;
	int spanbin_count_gold;
	int spanbin_count_correct;
	
	int nuclearbin_count_pred;
	int nuclearbin_count_gold;
	int nuclearbin_count_correct;
	
	int dislabelbin_count_pred;
	int dislabelbin_count_gold;
	int dislabelbin_count_correct;
*/	
	
	public EvaluateMetrics()
	{
		edu_count_pred = 0;
		edu_count_gold = 0;
		edu_count_correct = 0;
		
		span_count_pred = 0;
		span_count_gold = 0;
		span_count_correct = 0;
		
		nuclear_count_pred = 0;
		nuclear_count_gold = 0;
		nuclear_count_correct = 0;
		
		dislabel_count_pred = 0;
		dislabel_count_gold = 0;
		dislabel_count_correct = 0;
		
		synlabel_count_pred = 0;
		synlabel_count_gold = 0;
		synlabel_count_correct = 0;
		
		poslabel_count_pred = 0;
		poslabel_count_gold = 0;
		poslabel_count_correct = 0;

		/*
		spanbin_count_pred = 0;
		spanbin_count_gold = 0;
		spanbin_count_correct = 0;
		
		nuclearbin_count_pred = 0;
		nuclearbin_count_gold = 0;
		nuclearbin_count_correct = 0;
		
		dislabelbin_count_pred = 0;
		dislabelbin_count_gold = 0;
		dislabelbin_count_correct = 0;
		*/
	}
	
	
	public void print(PrintWriter out)
	{
		Formatter fout = new Formatter(out);
		if(poslabel_count_pred + poslabel_count_gold > 0)
			fout.format("POS:\tP=%d/%d=%.5f, R=%d/%d=%.5f, F=%.5f\n",
				poslabel_count_correct, poslabel_count_pred, poslabel_count_correct*1.0/poslabel_count_pred, 
				poslabel_count_correct, poslabel_count_gold, poslabel_count_correct*1.0/poslabel_count_gold, 
				poslabel_count_correct*2.0/(poslabel_count_pred+poslabel_count_gold));
		
		if(synlabel_count_pred+synlabel_count_gold > 0)
			fout.format("SYN:\tP=%d/%d=%.5f, R=%d/%d=%.5f, F=%.5f\n",
				synlabel_count_correct, synlabel_count_pred, synlabel_count_correct*1.0/synlabel_count_pred, 
				synlabel_count_correct, synlabel_count_gold, synlabel_count_correct*1.0/synlabel_count_gold, 
				synlabel_count_correct*2.0/(synlabel_count_pred+synlabel_count_gold));
		
		if(edu_count_pred+edu_count_gold > 0)fout.format("EDU:\tP=%d/%d=%.5f, R=%d/%d=%.5f, F=%.5f\n",
				edu_count_correct, edu_count_pred, edu_count_correct*1.0/edu_count_pred, 
				edu_count_correct, edu_count_gold, edu_count_correct*1.0/edu_count_gold, 
				edu_count_correct*2.0/(edu_count_pred+edu_count_gold));
		
		if(span_count_pred+span_count_gold > 0)fout.format("SPAN:\tP=%d/%d=%.5f, R=%d/%d=%.5f, F=%.5f\n",
				span_count_correct, span_count_pred, span_count_correct*1.0/span_count_pred, 
				span_count_correct, span_count_gold, span_count_correct*1.0/span_count_gold, 
				span_count_correct*2.0/(span_count_pred+span_count_gold));
				
		if(nuclear_count_pred+nuclear_count_gold > 0)fout.format("NUCLEAR:\tP=%d/%d=%.5f, R=%d/%d=%.5f, F=%.5f\n",
				nuclear_count_correct, nuclear_count_pred, nuclear_count_correct*1.0/nuclear_count_pred, 
				nuclear_count_correct, nuclear_count_gold, nuclear_count_correct*1.0/nuclear_count_gold, 
				nuclear_count_correct*2.0/(nuclear_count_pred+nuclear_count_gold));
		
		if(dislabel_count_pred+dislabel_count_gold > 0)fout.format("DIS:\tP=%d/%d=%.5f, R=%d/%d=%.5f, F=%.5f\n",
				dislabel_count_correct, dislabel_count_pred, dislabel_count_correct*1.0/dislabel_count_pred, 
				dislabel_count_correct, dislabel_count_gold, dislabel_count_correct*1.0/dislabel_count_gold, 
				dislabel_count_correct*2.0/(dislabel_count_pred+dislabel_count_gold));
		
/*		
		fout.format("SPAN_BIN:\tP=%d/%d=%.5f, R=%d/%d=%.5f, F=%.5f\n",
				spanbin_count_correct, spanbin_count_pred, spanbin_count_correct*1.0/spanbin_count_pred, 
				spanbin_count_correct, spanbin_count_gold, spanbin_count_correct*1.0/spanbin_count_gold, 
				spanbin_count_correct*2.0/(spanbin_count_pred+spanbin_count_gold));
				
		fout.format("NUCLEAR_BIN:\tP=%d/%d=%.5f, R=%d/%d=%.5f, F=%.5f\n",
				nuclearbin_count_correct, nuclearbin_count_pred, nuclearbin_count_correct*1.0/nuclearbin_count_pred, 
				nuclearbin_count_correct, nuclearbin_count_gold, nuclearbin_count_correct*1.0/nuclearbin_count_gold, 
				nuclearbin_count_correct*2.0/(nuclearbin_count_pred+nuclearbin_count_gold));
		
		fout.format("DIS_BIN:\tP=%d/%d=%.5f, R=%d/%d=%.5f, F=%.5f\n",
				dislabelbin_count_correct, dislabelbin_count_pred, dislabelbin_count_correct*1.0/dislabelbin_count_pred, 
				dislabelbin_count_correct, dislabelbin_count_gold, dislabelbin_count_correct*1.0/dislabelbin_count_gold, 
				dislabelbin_count_correct*2.0/(dislabelbin_count_pred+dislabelbin_count_gold));
*/
	}
}
