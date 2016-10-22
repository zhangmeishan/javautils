package feat_extractor;

import java.io.*;
import java.util.*;




public class POSCorpus2NNJointST {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String sLine = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		int charlength;
		while ((sLine = in.readLine()) != null) {
			if(sLine.trim().equals(""))continue;
			String[] wordposs = sLine.trim().split("\\s+");
			String[] words = new String[wordposs.length];
			String[] poss = new String[wordposs.length];
			charlength = 0;
			for(int idx  = 0; idx < wordposs.length; idx++)
			{
				String wordpos = wordposs[idx];
				int splitIndex = wordpos.lastIndexOf("_");
				words[idx] = ZHUtil.toDBC(wordposs[idx].substring(0, splitIndex));
				poss[idx] =  wordposs[idx].substring(splitIndex+1);
				charlength = charlength + words[idx].length();
			}
			
			String[] chnchars = new String[charlength];
			String[] chncharlabel = new String[charlength];
						
			int offset = 0;
			int model = 1;
			if(args.length > 2) model = Integer.parseInt(args[2]);
			for(int idx = 0; idx < words.length; idx++)
			{
				int curWordLength = words[idx].length();
				for(int idy = 0; idy < curWordLength; idy++)
				{
					chnchars[offset] = words[idx].substring(idy, idy+1);
					if(curWordLength == 1)
					{
						chncharlabel[offset] = model == 1 ? "s-" + poss[idx] : "s-seg";
					}
					else {
						if(idy == 0)
						{
							chncharlabel[offset] = model == 1 ? "b-" + poss[idx] : "b-seg";
						}
						else if(idy == curWordLength-1)
						{
							chncharlabel[offset] = model == 1 ? "e-" + poss[idx] : "e-seg";
						}
						else
						{
							chncharlabel[offset] = model == 1 ? "m-" + poss[idx] : "m-seg";
						}
					}
					offset++;
				}
			}
			assert(charlength == offset);
			
			for(int idx = 0; idx < charlength; idx++)
			{	
				String prevChar = idx == 0 ? "<s>" : chnchars[idx-1];
				String curType = "[T1]" + prevChar + chnchars[idx];
				String curoutline = chnchars[idx] + " " + curType + " " + chncharlabel[idx];				
				out.println(curoutline);
			}
			out.println();
		}
		
		out.close();
		in.close();
	}

}
