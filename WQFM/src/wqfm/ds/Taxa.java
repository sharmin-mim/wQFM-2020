package wqfm.ds;


import java.util.List;


import wqfm.bip.Bipartition_8_values;
import wqfm.configs.DefaultValues;


public class Taxa {
	//public int taxa_int_name;
	public String taxa_name;
	public int partition;
	public int prev_partition;
	public boolean locked;
	public boolean participated_in_swap;
	public double gain;
	public Bipartition_8_values bipartition_8_values;
	//public Map<Integer, Integer> map_final_bipartition;//will delete this map. 
	public List<Integer> relevant_quartet_indices;
	
	public Bipartition_8_values _8_vals_THIS_TAX_before_hypo_swap = new Bipartition_8_values();
    public Bipartition_8_values _8_vals_THIS_TAX_AFTER_hypo_swap = new Bipartition_8_values();
	
	public Taxa(String taxa_name) {
		this();
		this.taxa_name = taxa_name;
	}
	
	
	
//	public Taxa() {
//		this();
//		//this.taxa_int_name = taxa_int_name;
//		//this.partition = DefaultValues.UNASSIGNED_PARTITION;
//		this.locked = Boolean.FALSE;
////		this.bipartition_8_values = bipartition_8_values;
//		this.relevant_quartet_indices = new ArrayList<Integer>();
//	}

	public Taxa() {
		this.locked = Boolean.FALSE;
		this.partition = DefaultValues.UNASSIGNED_PARTITION;
		//this.relevant_quartet_indices = new ArrayList<Integer>();

	}
	
	public void reset_tax_property() {
		this.partition = DefaultValues.UNASSIGNED_PARTITION;
		this.locked = Boolean.FALSE;
		//this.relevant_quartet_indices = new ArrayList<Integer>();
		this._8_vals_THIS_TAX_AFTER_hypo_swap = new Bipartition_8_values();
		this._8_vals_THIS_TAX_before_hypo_swap = new Bipartition_8_values();
	}



	public double getGain() {
		return gain;
	}
	
	public int getnumSatisfied() {
		return this.bipartition_8_values.numSatisfied;
	}


//
//	public int get_taxa_int_name() {
//		return taxa_int_name;
//	}
	

	
	
	
	
	
	
	
	
	

}
