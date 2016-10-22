package mason;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class NLEBIBProcessing {
	
	public static void main(String[] args) throws Exception {
		
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF8"));		
		String sLine = null;
		
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"));
		
		while ((sLine = in.readLine()) != null) {
			sLine = sLine.trim();
			if(sLine.startsWith("\\bibitem["))
			{
				sLine = sLine.replace("\\bibitem[", "\\bibitem[\\protect\\citename{");
				sLine = sLine.replace(", ", " }");
			}
			else
			{
				sLine = sLine.replace("\\newblock", "");
			}
			out.println(sLine.trim());
		}
		
		
		in.close();
		out.close();
	}

}
