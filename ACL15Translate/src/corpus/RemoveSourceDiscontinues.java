package corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class RemoveSourceDiscontinues {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		String sLine = "";
		Map<String, Integer> target_word_map = new HashMap<String, Integer>();
		BufferedReader in_word_target = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(args[3]), "UTF-8"));
		
		while((sLine = in_word_target.readLine()) != null)
		{
			sLine = sLine.trim();
			if(sLine.trim().isEmpty())continue;
			String[] smallunits = sLine.split("\\s+");
			if(smallunits.length != 3)continue;
			target_word_map.put(smallunits[1], Integer.parseInt(smallunits[2]));
		}
		
		in_word_target.close();
		
		String source = "";
		String target = "";
		
		String align = "";
		
		BufferedReader in_target = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(args[0]), "UTF-8"));
		
		BufferedReader in_source = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(args[1]), "UTF-8"));
		
		BufferedReader in_align = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(args[2]), "UTF-8"));
		
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[4]), "UTF-8"));	
		
		while((target = in_target.readLine()) != null &&
				   (source = in_source.readLine()) != null &&
				   (align = in_align.readLine()) != null)
		{
			String[] source_words = source.trim().split("\\s+");
			String[] target_words = target.trim().split("\\s+");
			String[] align_result_str = align.trim().split("\\s+");
			
			int[] align_results = new int[align_result_str.length];
			for(int idx = 0; idx < align_results.length; idx++)align_results[idx]= Integer.parseInt(align_result_str[idx]);
			
			for(int idx = 0; idx < source_words.length; idx++)
			{
				int minfreq = Integer.MAX_VALUE;
				int minid = -1;
				int[] currentidalign = new int[target_words.length];
				for(int idy = 0; idy < target_words.length; idy++)currentidalign[idy] = -1;
				for(int idy = 0; idy<align_results.length; idy++ )
				{
					int target_id = align_results[idy];
					idy++;
					int source_id = align_results[idy];
					if(source_id == idx)
					{
						int freq = 1;
						if(target_word_map.containsKey(target_words[target_id]))freq = target_word_map.get(target_words[target_id]);
						if(freq < minfreq)
						{
							minid = target_id;
							minfreq = freq;
						}
						currentidalign[target_id]=1;
					}
				}
				
				int removecount = 0;
				
				for(int idy = 0; idy<align_results.length; idy++ )
				{
					int target_id = align_results[idy];
					idy++;
					int source_id = align_results[idy];
					if(source_id == idx)
					{
						boolean bConsecutiveWithMin = true;
						if(target_id > minid)
						{
							int step = 1;
							while(target_id-step>minid)
							{
								if(currentidalign[target_id-step] == -1)
								{
									bConsecutiveWithMin = false;
									break;
								}
								step++;
							}
						}
						else if(target_id < minid)
						{
							int step = 1;
							while(target_id+step<minid)
							{
								if(currentidalign[target_id+step] == -1)
								{
									bConsecutiveWithMin = false;
									break;
								}
								step++;
							}
						}
						
						if(!bConsecutiveWithMin)
						{	
							//System.out.print(String.format("%d ", align_results[idy-1]));
							align_results[idy-1] = -1;
							align_results[idy] = -1;
							removecount++;
							
						}
					}
				}
				/*
				if(removecount > 0)
				{
					System.out.println();
					System.out.print(String.format("remove count: %d, source: %d, minid: %d, target:[", removecount, idx, minid));
					for(int idy = 0; idy < target_words.length; idy++)
					{
						if(currentidalign[idy] == 1)
						{
							System.out.print(String.format("%d ", idy));
						}
					}
					System.out.println("]");				
				}
				
				System.out.flush();*/

			}
			
			String outalign = "";
			
			for(int idx = 0; idx < target_words.length; idx++)
			{
				int[] currentidalign = new int[source_words.length];
				for(int idy = 0; idy < source_words.length; idy++)currentidalign[idy] = -1;
				int aligncount = 0;
				for(int idy = 0; idy<align_results.length; idy++ )
				{
					int target_id = align_results[idy];
					idy++;
					int source_id = align_results[idy];
					if(target_id == idx)
					{
						currentidalign[source_id]=1;
						aligncount++;
					}
				}
				
				if(aligncount == 0)
				{
					outalign = outalign + " " + String.format("%d %d", idx, -1);
				}
				else
				{
					for(int idy = 0; idy < source_words.length; idy++)
					{
						if(currentidalign[idy]==1)
						{
							outalign = outalign + " " + String.format("%d %d", idx, idy);
						}
					}
				}
				
			}
			
			/*
			for(int idx = 0; idx < align_results.length; idx++)
			{
				if(align_results[idx] == -1 && align_results[idx+1] == -1)
				{
					idx++;
				}
				else
				{
					outalign = outalign + " " + String.format("%d %d", align_results[idx], align_results[idx+1]);
					idx++;
				}
			}*/
			writer.println(outalign.trim());
		
		}
		
		in_target.close();
		in_source.close();
		in_align.close();
		writer.close();

	}

}
