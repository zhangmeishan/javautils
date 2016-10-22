package microblog;

import java.util.HashMap;

public class Feature {

	public String name="";   //������  ����ģ��+����ֵ  ���� "CharUnigram=��"
	public double weight=0;  //��ǰȨ��
	public double sum=0;     //��ʷ��Ȩ��
	public int lastUpdateIndex=0; //���һ�θ������
	public double aveWeight=0; //��׼��Ȩ��

	 public enum featureName{
	    	CharUnigram,CharBigram;}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashMap<String, Feature>  hm = new HashMap<String, Feature>();
		hm.put("aa", new Feature());
		Feature f = hm.get("aa");
		f.sum=100;
		System.out.println(hm.get("aa").sum);
		featureName a=featureName.CharUnigram;
		System.out.println(a.toString());
		
	}
	public  Feature(){	
	}
	public  Feature(String name, double weight, double sum, int index){	
		this.name=name;
		this.weight=weight;
		this.sum=weight;
		this.lastUpdateIndex=index;
	}
	
	public void AveWeight(int curUpdateIndex){
		this.sum += (curUpdateIndex-this.lastUpdateIndex)*this.weight;
		this.aveWeight=this.sum/curUpdateIndex;
		this.lastUpdateIndex = curUpdateIndex;
	}

}
