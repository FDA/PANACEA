package com.eng.cber.na.util;

import org.apache.commons.math3.util.CombinatoricsUtils;

public class CombinationBig {
	private int n1, n2;
	public CombinationBig(){};
	
	public CombinationBig(int n1, int n2){
		this.n1 = n1;
		this.n2 = n2;
	};
	
	public void setValues(int n1, int n2){
		this.n1 = n1;
		this.n2 = n2;
	}
	public long exec(){
		int n3 = n1 - n2;
		long ret = 1;
		if (n3 < n2){
			//switch n2 and n3
			int temp = n2;
			n2 = n3;
			n3 = temp;
		}
		
		for(int i = n3+1; i <= n1; i++)
			ret = ret * i;
		ret = ret/CombinatoricsUtils.factorial(n2);

		return ret;
	}
}
