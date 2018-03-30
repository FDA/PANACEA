package com.eng.cber.na.util;

import java.util.Collections;
import java.util.List;

import com.eng.cber.na.NetworkAnalysisVisualization;

public class CheckNameConflict {
	List<String> strArray;
	String name;
	public CheckNameConflict(List<String> strArray, String name){
		this.strArray = strArray;
		this.name = name;
	}
	public String check(){
		String newName = name; 
		if(strArray.contains(name))
		{
			Collections.reverse(strArray);
			Boolean found = false;
			for(String strName:strArray){
				if(strName.startsWith(name + "_")){
					newName = strName.substring(0, strName.indexOf("_")+1) + (Integer.valueOf(strName.substring(name.length()+1, strName.length()))+1);
					NetworkAnalysisVisualization.NALog(newName);
					found= true;
					break;
				}
			}
			if (!found){
				newName = name + "_1";
			}
		}		
		return newName;
	}
}
