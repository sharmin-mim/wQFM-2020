/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqfm.bip;

import wqfm.configs.Config;

import java.util.HashMap;

import java.util.List;
import java.util.Map;
//import javafx.util.Pair;
import wqfm.ds.CustomDSPerLevel;
import wqfm.ds.Quartet;
import wqfm.feature.FeatureComputer;


import wqfm.configs.DefaultValues;

/**
 *
 * @author mahim
 */
public class Bipartition_8_values {

    public int numSatisfied;
    public int numViolated;
    public int numDeferred;
    public int numBlank;

    public double wtSatisfied;
    public double wtViolated;
    public double wtDeferred;
    public double wtBlank;

    public Bipartition_8_values(int numSatisfiedQuartets, int numViolatedQuartets, int numDeferredQuartets, int numBlankQuartets, double weightSatisfiedQuartets, double weightViolatedQuartets, double weightDeferredQuartets, double weightBlankQuartets) {
        this.numSatisfied = numSatisfiedQuartets;
        this.numViolated = numViolatedQuartets;
        this.numDeferred = numDeferredQuartets;
        this.numBlank = numBlankQuartets;
        this.wtSatisfied = weightSatisfiedQuartets;
        this.wtViolated = weightViolatedQuartets;
        this.wtDeferred = weightDeferredQuartets;
        this.wtBlank = weightBlankQuartets;
    }

    public Bipartition_8_values() {
        this.numSatisfied = 0;
        this.numViolated = 0;
        this.numDeferred = 0;
        this.numBlank = 0;
        this.wtSatisfied = 0.0;
        this.wtViolated = 0.0;
        this.wtDeferred = 0.0;
        this.wtBlank = 0.0;
    }

    public Bipartition_8_values(Bipartition_8_values obj) {
        this.numSatisfied = obj.numSatisfied;
        this.numViolated = obj.numViolated;
        this.numDeferred = obj.numDeferred;
        this.numBlank = obj.numBlank;
        this.wtSatisfied = obj.wtSatisfied;
        this.wtViolated = obj.wtViolated;
        this.wtDeferred = obj.wtDeferred;
        this.wtBlank = obj.wtBlank;
    }

    public void addRespectiveValue(double weight, int status) {
    	synchronized (this) {
    		switch (status) {
	            case DefaultValues.SATISFIED:
	                this.numSatisfied++;
	                this.wtSatisfied += weight;
	                break;
	            case DefaultValues.VIOLATED:
	                this.numViolated++;
	                this.wtViolated += weight;
	                break;
	            case DefaultValues.DEFERRED:
	                this.numDeferred++;
	                this.wtDeferred += weight;
	                break;
	            case DefaultValues.BLANK:
	                this.numBlank++;
	                this.wtBlank += weight;
	                break;
	            case DefaultValues.UNKNOWN: // do nothing for this case
	                break;
	            default:
	                break;
	        }
		}
        
    }
    public void subtractRespectiveValue(double weight, int status) {
    	synchronized (this) {
            switch (status) {
	            case DefaultValues.SATISFIED:
	                this.numSatisfied--;
	                this.wtSatisfied -= weight;
	                break;
	            case DefaultValues.VIOLATED:
	                this.numViolated--;
	                this.wtViolated -= weight;
	                break;
	            case DefaultValues.DEFERRED:
	                this.numDeferred--;
	                this.wtDeferred -= weight;
	                break;
	            case DefaultValues.BLANK:
	                this.numBlank--;
	                this.wtBlank -= weight;
	                break;
	            case DefaultValues.UNKNOWN: // do nothing for this case
	                break;
	            default:
	                break;
	        }
		}

    }

    private void addRespectiveValue(Quartet q, int status) { // not needed for now.
        addRespectiveValue(q.weight, status);
    }

    public void compute8ValuesUsingAllQuartets_this_level(CustomDSPerLevel customDS) {

        Map<List<Integer>, List<Double>> map_four_tax_seq_weights_list = new HashMap<>();
        //  System.out.println("L 100. Bipartition_8_vals: BIPARTITION size : " + map_bipartitions.keySet().size());
        //  System.out.println("Keyset size before populating: " + dictiory_4Tax_sequence.keySet().size());
        //for feature computation
        

//        for (Quartet quartet : customDS.initial_table1_of_list_of_quartets.list_quartets) {
//            //Quartet quartet = customDS.initial_table1_of_list_of_quartets.get(idx_quartet);
//
//            if (Config.PARTITION_SCORE_MODE == DefaultValues.PARTITION_SCORE_FULL_DYNAMIC) {
//                FeatureComputer.makeDictionary(quartet, map_four_tax_seq_weights_list);
//            }
//
//            //obtain the quartet's taxa's bipartitions
////            int left_sis_1_bip_val = map_bipartitions.get(quartet.taxa_sisters_left[0].taxa_int_name);
////            int left_sis_2_bip_val = map_bipartitions.get(quartet.taxa_sisters_left[1].taxa_int_name);
////            int right_sis_1_bip_val = map_bipartitions.get(quartet.taxa_sisters_right[0].taxa_int_name);
////            int right_sis_2_bip_val = map_bipartitions.get(quartet.taxa_sisters_right[1].taxa_int_name);
////
////            int status_quartet = TaxaUtils.findQuartetStatus(left_sis_1_bip_val, left_sis_2_bip_val, right_sis_1_bip_val, right_sis_2_bip_val); //obtain quartet status
////            //quartet.quartet_status = status_quartet;//mim// I dont understand why output tree changed. //less time needed//
////            //compute scores according to status.
////           // System.out.println(quartet.toString()+" -> "+status_quartet);
////            //String stat = "";
////            switch (status_quartet) {
////                case DefaultValues.SATISFIED:
////                	//stat = "s";
////                    this.numSatisfied++;
////                    //System.out.println("this.wtSatisfied = "+ this.wtSatisfied + ", quartet.weight ="+quartet.weight);
////                    //double m = (double) 4.835391 + (double)4.648647;
////                    //System.out.println("m ="+m);
////                    this.wtSatisfied = this.wtSatisfied+ quartet.weight;
////                    //this.wtSatisfied += quartet.weight;
////                   // System.out.println("this.wtSatisfied = "+ this.wtSatisfied);
////                   
////                    break;
////                case DefaultValues.VIOLATED:
////                	//stat = "v";
////                    this.numViolated++;
////                    this.wtViolated += quartet.weight;
////                    break;
////                case DefaultValues.DEFERRED:
////                	//stat = "d";
////                    this.numDeferred++;
////                    this.wtDeferred += quartet.weight;
////                    break;
////                case DefaultValues.BLANK:
////                	//stat = "b";
////                    this.numBlank++;
////                    this.wtBlank += quartet.weight;
////                    break;
////                default:
////                    break;
////            }
////            System.out.println(quartet.toString()+" -> "+stat);
////            System.out.println(this);
//
//        }
//        
//        
//        if (Config.PARTITION_SCORE_MODE == DefaultValues.PARTITION_SCORE_FULL_DYNAMIC) {
//            FeatureComputer.computeBinningFeature(map_four_tax_seq_weights_list, customDS.level);
//        }
        
        if (Config.PARTITION_SCORE_MODE == DefaultValues.PARTITION_SCORE_FULL_DYNAMIC) {
            for (Quartet quartet : customDS.initial_table1_of_list_of_quartets.list_quartets) {
            	 FeatureComputer.makeDictionary(quartet, map_four_tax_seq_weights_list);
            }
            FeatureComputer.computeBinningFeature(map_four_tax_seq_weights_list, customDS.level);
        }

    }

    @Override
    public String toString() {
        return "_8Vals{" + "ns=" + numSatisfied + ", nv=" + numViolated + ", nd=" + numDeferred + ", nb=" + numBlank + ", ws=" + wtSatisfied + ", wv=" + wtViolated + ", wd=" + wtDeferred + ", wb=" + wtBlank + '}';
    }

    public void addObject(Bipartition_8_values obj) {
        this.numSatisfied += obj.numSatisfied;
        this.numViolated += obj.numViolated;
        this.numDeferred += obj.numDeferred;
        this.numBlank += obj.numBlank;
        this.wtSatisfied += obj.wtSatisfied;
        this.wtViolated += obj.wtViolated;
        this.wtDeferred += obj.wtDeferred;
        this.wtBlank += obj.wtBlank;
    }

    public void subtractObject(Bipartition_8_values obj) {
        this.numSatisfied -= obj.numSatisfied;
        this.numViolated -= obj.numViolated;
        this.numDeferred -= obj.numDeferred;
        this.numBlank -= obj.numBlank;
        this.wtSatisfied -= obj.wtSatisfied;
        this.wtViolated -= obj.wtViolated;
        this.wtDeferred -= obj.wtDeferred;
        this.wtBlank -= obj.wtBlank;
    }

    public void assign(Bipartition_8_values obj) {
        this.numSatisfied = obj.numSatisfied;
        this.numViolated = obj.numViolated;
        this.numDeferred = obj.numDeferred;
        this.numBlank = obj.numBlank;
        this.wtSatisfied = obj.wtSatisfied;
        this.wtViolated = obj.wtViolated;
        this.wtDeferred = obj.wtDeferred;
        this.wtBlank = obj.wtBlank;
    }
}
