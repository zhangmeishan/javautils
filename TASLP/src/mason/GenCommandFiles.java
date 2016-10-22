package mason;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;

public class GenCommandFiles {
	public static void main(String[] args) throws Exception {
		
		List<Integer> rawnumbers = new ArrayList<Integer>();
		rawnumbers.add(10);
		rawnumbers.add(100);
		rawnumbers.add(1000);
		rawnumbers.add(10000);
		rawnumbers.add(50000);
		rawnumbers.add(100000);
		rawnumbers.add(150000);
		rawnumbers.add(200000);
		rawnumbers.add(250000);
		rawnumbers.add(300000);
		rawnumbers.add(350000);
		rawnumbers.add(400000);
		rawnumbers.add(450000);
		rawnumbers.add(500000);
		rawnumbers.add(550000);
		rawnumbers.add(600000);
		rawnumbers.add(650000);
		rawnumbers.add(700000);
		rawnumbers.add(750000);
		rawnumbers.add(800000);
		rawnumbers.add(850000);
		rawnumbers.add(900000);
		rawnumbers.add(950000);
		rawnumbers.add(-1);
		//int ctbnumber = 31000;
		
		String filename = "F:\\zhangmeishan\\ÂÛÎÄÐ´×÷\\nle-sty-coling12-extended\\midfiles\\";
		for(Integer curNum : rawnumbers)
		{
			String outputfile = filename + String.format("ctb51-upall.sh", curNum);
					
			
			String modelfile = "uptall";
			if(curNum > 0)
			{
				modelfile = String.format("upt%d", curNum);
				outputfile = filename + String.format("ctb51-up%d.sh", curNum);
			}
			
			PrintWriter out = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(outputfile), "UTF-8"));
			out.println("for i in `seq 1 50`;");
			
			out.println("do");
			out.println("    echo \"Test iteration $i\"");
			//out.println(String.format("    ./train  ../data/train.ctb51.txt %s/joint.model 1", modelfile));

			if(curNum > 0)
			{
				out.println(String.format("    ./train  ../data/train.ctb51-upall.conll %s/joint.model 1 -n%d", modelfile, curNum+16090));
			}
			else
			{
				out.println(String.format("    ./train  ../data/train.ctb51-upall.conll %s/joint.model 1", modelfile));
			}
			/*
			if(curNum>0 && curNum < ctbnumber)
			{
				out.println(String.format("    ./train  ../data/uptrain0.conll %s/joint.model 1 -n%d", modelfile, curNum));
			}
			else if(curNum<0)
			{
				for(int idx = 0; idx <= 20; idx++)
				{
					out.println(String.format("    ./train  ../data/uptrain%d.conll %s/joint.model 1", idx, modelfile));
				}
			}
			else
			{
				int newnum = (curNum - ctbnumber)/20;
				out.println(String.format("    ./train  ../data/uptrain0.conll %s/joint.model 1", modelfile));
				for(int idx = 1; idx <= 20; idx++)
				{
					out.println(String.format("    ./train  ../data/uptrain%d.conll %s/joint.model 1 -n%d", idx, modelfile, newnum));
				}				
			}
			*/
			out.println(String.format("    ./posdepparser ../data/dev.ctb51.seg %s/devo.txt.$i %s/joint.model", modelfile, modelfile));
			out.println(String.format("    python ../zpar-zms-final/scripts/dep/dep2conll.py  %s/devo.txt.$i > %s/devo.txt.$i.conll", modelfile, modelfile));
			out.println(String.format("    java -Xmx1G -jar ../zpar-zms-final/scripts/dep/EvaluateJointDP.jar ../data/dev.ctb51.conll  %s/devo.txt.$i.conll", modelfile));
			out.println(String.format("    ./posdepparser ../data/test.ctb51.seg %s/testo.txt.$i %s/joint.model", modelfile, modelfile));
			out.println(String.format("    python ../zpar-zms-final/scripts/dep/dep2conll.py %s/testo.txt.$i > %s/testo.txt.$i.conll", modelfile, modelfile));
			out.println(String.format("    java -Xmx1G -jar ../zpar-zms-final/scripts/dep/EvaluateJointDP.jar ../data/test.ctb51.conll  %s/testo.txt.$i.conll", modelfile));
			out.println("done");
			out.println(String.format("cp %s/train.log logs/%s.train.log", modelfile, modelfile));
			out.close();
		}
		
	}

}
