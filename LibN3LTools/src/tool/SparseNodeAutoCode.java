package tool;

import java.io.*;
import java.util.*;


public class SparseNodeAutoCode {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		//input file should be XXXnNode  name;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0]), "UTF-8"));
		List<String> nodes = new ArrayList<String>();
		List<String> vars = new ArrayList<String>();
		String sLine = "";
		while ((sLine = in.readLine()) != null) {
			sLine = sLine.trim();
			
			if(sLine.isEmpty())
			{
				continue;
			}
			
			String[] smallunits = sLine.split("\\s+");
			
			if(smallunits.length != 2 || smallunits[0].startsWith("//") || !smallunits[0].endsWith("Node")){
				continue;
			}
			
			nodes.add(sLine);
			vars.add(smallunits[1]);
		}
		
		in.close();
		
		PrintWriter output = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(args[1]), "UTF-8"), false);
		
		//model parameters
		output.println("//Declare vars in model file");
		for(String node : nodes){
			output.println(node.replace("Node", "Params"));
		}
		output.println();
		output.println();
		output.println("//initial in model file");
		for(String var : vars){
			output.println(var.replace(";", ".initial();"));
		}
		
		output.println();
		output.println();
		output.println("//export in model file");
		for(String var : vars){
			output.println(var.replace(";", ".exportAdaParams(ada);"));
		}
		
		output.println();
		output.println();
		output.println("//setFixed in model file");
		for(String var : vars){
			output.println(var.replace(";", ".setFixed(base);"));
		}
		
		output.println();
		output.println();
		output.println("//initial in Action node file");
		for(String var : vars){
			String newvar = var.replace(";", "");
			output.println(String.format("%s.setParam(&params.%s);", newvar, newvar));
		}
		
		
		output.println();
		output.println();
		output.println("//forward in Action node file");
		for(String var : vars){
			String newvar = var.replace(";", "");
			String[] items = newvar.split("_");
			String ourstr = newvar + ".forward(cg";
			for(int idx = 1; idx < items.length; idx++){
				if(items[idx].equals("ac")){ // action code
					ourstr = ourstr + ", sid_" + items[idx];
				}
				else{
					ourstr = ourstr + ", atomFeat.sid_" + items[idx];
				}
			}
			ourstr = ourstr + ");";
			
			output.println(ourstr);
			output.println(String.format("sumNodes.push_back(&%s);", newvar));
			output.println();
		}
		
		output.close();
	}

}
