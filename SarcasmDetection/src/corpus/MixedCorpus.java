package corpus;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;


public class MixedCorpus {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String sLine = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));

        int multi = 9;
        if(args.length > 2) multi = Integer.parseInt(args[2]);
        Map<List<String>, Integer> mapTrueExamples = new HashMap<List<String>, Integer>();
        Map<List<String>, Integer> mapFalseExamples = new HashMap<List<String>, Integer>();

		List<String> curExample = new ArrayList<String>();
		int tweets = 0;
		while ((sLine = in.readLine()) != null) {
			sLine = sLine.trim();
			if(sLine.equals(""))
			{
				if(curExample.size() > 0)
				{
					boolean bValid = true;
					List<String> curNewExample = new ArrayList<String>();
					for(int idx = 0; idx < curExample.size(); idx++)
					{
						String[] smallunits = curExample.get(idx).split("\\s+");
						if(smallunits.length == 1 && idx == curExample.size()-1)
						{
							bValid = false;
						}
						if(smallunits.length > 1)
						{
							String curline = smallunits[0];
							for(int idy = 1; idy < smallunits.length; idy++)
							{
								curline = curline + " " + smallunits[idy];
							}
							curNewExample.add(curline);
						}
					}
					if(bValid)
					{
						String lastLine = curNewExample.get(curNewExample.size()-1);
						String[] smallunits = lastLine.split("\\s+");
						if(smallunits[0].equalsIgnoreCase("true"))
						{
							mapTrueExamples.put(curNewExample, curNewExample.size());
							tweets = tweets + curNewExample.size();
						}
						else if(smallunits[0].equalsIgnoreCase("false"))
						{
							mapFalseExamples.put(curNewExample, curNewExample.size());
							tweets = tweets + curNewExample.size();
						}
						else
						{
							System.out.println(lastLine);
						}
					}
				}
				curExample = new ArrayList<String>();
			}
			else
			{
				curExample.add(sLine);
			}
		}
		
		int truenum = mapTrueExamples.size();
		if(args.length > 3) truenum = Integer.parseInt(args[3]);
		
		System.out.println(String.format("Total: True: %d,  False: %d",  mapTrueExamples.size(), mapFalseExamples.size()));
		double avghis = (tweets * 1.0) / (mapTrueExamples.size() + mapFalseExamples.size());
		System.out.println(String.format("Average history tweet num: %f",  avghis));
		
		List<Entry<List<String>, Integer>> entTrueExamples = MapIntegerSort(mapTrueExamples);
		List<Entry<List<String>, Integer>> entFalseExamples = MapIntegerSort(mapFalseExamples);
		
		tweets = 0;
		int tweettrue = 0;
		int tweetfalse = 0;
		List<List<String>> trueExamples = new ArrayList<List<String>>();
		for(int idx = 0; idx < entTrueExamples.size() && idx < truenum; idx++)
		{
			trueExamples.add(entTrueExamples.get(idx).getKey());
			tweets += entTrueExamples.get(idx).getValue();
			tweettrue += entTrueExamples.get(idx).getValue();;
		}
		truenum = trueExamples.size();
		List<List<String>> falseExamples = new ArrayList<List<String>>();
		for(int idx = 0; idx < entFalseExamples.size() && idx < truenum*multi; idx++)
		{
			falseExamples.add(entFalseExamples.get(idx).getKey());
			tweets += entFalseExamples.get(idx).getValue();
			tweetfalse += entFalseExamples.get(idx).getValue();
		}
		
		Collections.shuffle(trueExamples, new Random(0));
		Collections.shuffle(falseExamples, new Random(1));
		System.out.println(String.format("Total: True: %d,  False: %d",  trueExamples.size(), falseExamples.size()));
		avghis = (tweets * 1.0) / (trueExamples.size() + falseExamples.size());
		System.out.println(String.format("Average history tweet num: %f",  avghis));
		avghis = (tweettrue * 1.0) / trueExamples.size();
		System.out.println(String.format("Average true history tweet num: %f",  avghis));
		avghis = (tweetfalse * 1.0) / falseExamples.size();
		System.out.println(String.format("Average false history tweet num: %f",  avghis));
		List<List<String>>  allExamples = new ArrayList<List<String>>();
		for(int idx = 0; idx < truenum; idx++)
		{
			allExamples.add(trueExamples.get(idx));
		}
		
		int falsenum = multi * truenum;
		if(falsenum > falseExamples.size()) falsenum = falseExamples.size();
		
		for(int idx = 0; idx < falsenum; idx++)
		{
			allExamples.add(falseExamples.get(idx));
		}
		
		System.out.println(String.format("Selected: True: %d,  False: %d",  truenum, falsenum));
		
		Collections.shuffle(allExamples, new Random(2));
		
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		for(int idx = 0; idx < allExamples.size(); idx++)
		{
			curExample = allExamples.get(idx);
			for(String oneline : curExample)
			{
				out.println(oneline);
			}
			out.println();
		}
		out.close();
		in.close();
	}
	
	public static List<Entry<List<String>, Integer>> MapIntegerSort(Map<List<String>, Integer> input)
	{
		List<Entry<List<String>, Integer>> mapintsort = new ArrayList<Entry<List<String>, Integer>>(input.entrySet());
		
		Collections.sort(mapintsort, new Comparator(){   
			public int compare(Object o1, Object o2) {    
				Map.Entry obj1 = (Map.Entry) o1;
				Map.Entry obj2 = (Map.Entry) o2;
				
				Integer a1 = (Integer)obj1.getValue();
				Integer a2 = (Integer)obj2.getValue();
				
				return a2.compareTo(a1);				
            }   
		}); 
		
		return mapintsort;
	}
}
