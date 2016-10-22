package is2.data;


public  class PipeGen {

	public static final String SENSE = "SENSE",POS = "POS",DIST = "DIST",WORD = "WORD",PRED = "PRED",ARG = "ARG",FEAT = "F", REL = "REL",TYPE = "TYPE" ,CHAR = "C";
	static public int outValue(int num1, int del) {
		String out = ""+num1;
		StringBuffer delS=new StringBuffer();
		for(int k =0;k< del;k++) delS.append('\b');
		del=out.length();
		System.out.print(delS+out);
		return del;
	}
	
	static public int outValue(int num1, int del, long last) {
		String out = ""+num1+" ("+(System.currentTimeMillis()-last)/(num1+1)+" ms/instance)";
		StringBuffer delS=new StringBuffer();
		for(int k =0;k< del;k++) delS.append('\b');
		del=out.length();
		System.out.print(delS+out);
		return del;
	}
	
	static public int outValueErr(int num1, float err, float f1, int del, long last) {
	
		String out = ""+num1+" ("+(System.currentTimeMillis()-last)/(num1+1)+" ms/instance "+(err/num1)+" err/instance f1="+
			f1 +") ";
		StringBuffer delS=new StringBuffer();
		for(int k =0;k< del;k++) delS.append('\b');
		del=out.length();
		System.out.print(delS+out);
		return del;
	}
	

	static public int outValueErr(int num1, float err, float f1, int del, long last, double upd) {
		String out = ""+num1+" ("+(System.currentTimeMillis()-last)/(num1+1)+" ms/instance "+(err/num1)+" err/instance f1="+
			f1 +") upd "+upd;
		StringBuffer delS=new StringBuffer();
		for(int k =0;k< del;k++) delS.append('\b');
		del=out.length();
		System.out.print(delS+out);
		return del;
	}

	static public int outValueErr(int num1, float err, float f1, int del, long last, double upd, String info) {
		String out = ""+num1+" ("+(System.currentTimeMillis()-last)/(num1+1)+" ms/instance "+(err/num1)+" err/instance f1="+
			f1 +") upd "+upd+" "+info;
		StringBuffer delS=new StringBuffer();
		for(int k =0;k< del;k++) delS.append('\b');
		del=out.length();
		System.out.print(delS+out);
		return del;
	}
	
	
	/**
	 * @param cnt
	 * @param l
	 * @return
	 */
	public static String getSecondsPerInstnace(int cnt, long l) {
		return " "+((float)l/(cnt*1000f))+" seconds/sentnece ";
	}

	/**
	 * @param l
	 * @return
	 */
	public static String getUsedTime(long l) {
		return "Used time " + (((float)l)/1000f)+" seconds ";
	}
	
	

}
