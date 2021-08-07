package wqfm.ds;

import java.util.ArrayList;
import java.util.List;

import wqfm.bip.Bipartition_8_values;
import wqfm.configs.DefaultValues;

public class Taxa {
	public String taxa_name;
	//public int partition;
	public boolean locked;
	public boolean participated_in_swap;
	public double gain;
	public Bipartition_8_values bipartition_8_values;
	
	public List<Integer> relevant_quartet_indices;
	
	public Taxa(String taxa_name) {
		this();
		this.taxa_name = taxa_name;
	}
	
	
	
	public Taxa() {
		//this.partition = DefaultValues.UNASSIGNED_PARTITION;
		this.locked = Boolean.FALSE;
//		this.bipartition_8_values = bipartition_8_values;
		this.relevant_quartet_indices = new ArrayList<Integer>();
	}
	
	public void reset_tax_property() {
		//this.partition = DefaultValues.UNASSIGNED_PARTITION;
		this.locked = Boolean.FALSE;
		this.relevant_quartet_indices = new ArrayList<Integer>();
	}



	public double getGain() {
		return gain;
	}
	
	public int getnumSatisfied() {
		return this.bipartition_8_values.numSatisfied;
	}



	public boolean isLocked() {
		return locked;
	}
	
	
	
	
	
	
	
	

}
