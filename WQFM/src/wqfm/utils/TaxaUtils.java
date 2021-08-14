package wqfm.utils;

import java.util.List;
import java.util.Map;
import wqfm.ds.InitialTable;
import wqfm.ds.Quartet;
import wqfm.ds.Taxa;
import wqfm.configs.DefaultValues;

/**
 *
 * @author mahim
 */
public class TaxaUtils {
//    public static String getDummyTaxonName(int level) {
//        if (Main.DEBUG_DUMMY_NAME == true) {
//            String dummyTax = "X" + String.valueOf(level); //debug
//            return dummyTax;
//        } else {
//            String dummyTax = "DUMMY_MZCR_" + String.valueOf(level); //arbitrary names so as to not get mixed up with actual names
//            return dummyTax;
//        }
//    }
    

    public static int getDummyTaxonName(int level) {
        return (InitialTable.TAXA_COUNTER + level); //0->47 [original tax], then 48 and above ar DUMMY taxa
    }

    //Returns true if there is 1 taxa on either side, OR zero taxa on either side.[for pairwise swapping maybe needed]
    public static boolean isThisSingletonBipartition(List<Integer> logical_bipartition) { //true if this bipartition is a singleton bipartition
        int len = logical_bipartition.size();
        int sum = Helper.sumList(logical_bipartition);
        return (Math.abs(sum) == (len - 2)) || (Math.abs(sum) == len);
        //eg. -1,+1, +1,+1,+1,+1  --> so, two terms will lead to 0, rest sum will be length - 2
    }

    public static boolean isThisSingletonBipartition(Map<Integer, Integer> mapInitialBip) {
        int len = mapInitialBip.keySet().size();
        int sum = Helper.sumMapValuesInteger(mapInitialBip);

        return (Math.abs(sum) == (len - 2)) || (Math.abs(sum) == len);
    }

    public static int findQuartetStatus(int left_sis1_bip, int left_sis2_bip, int right_sis1_bip, int right_sis2_bip) {
        int[] four_bipartitions = {left_sis1_bip, left_sis2_bip, right_sis1_bip, right_sis2_bip};

        int sum_four_bipartitions = Helper.sumArray(four_bipartitions);
        //Blank check: Easier to check if blank quartet (all four are same) [priority wise first]
//        if ((left_sisters_bip[0] == left_sisters_bip[1]) && (right_sisters_bip[0] == right_sisters_bip[1]) && (left_sisters_bip[0] == right_sisters_bip[0])) {

        if (Math.abs(sum_four_bipartitions) == 4) { // -1,-1,-1,-1 or +1,+1,+1,+1 all will lead to sum == 4
            return DefaultValues.BLANK;
        }
        //Deferred Check: sum == 2 check [otherwise, permutations will be huge]
        if (Math.abs(sum_four_bipartitions) == 2) { //-1,+1 ,+1,+1  => +2 or +1,-1 , -1,-1 => -2 
            return DefaultValues.DEFERRED;
        }
        //Satisfied check: left are equal, right are equal AND left(any one) != right(any one)
        if ((left_sis1_bip == left_sis2_bip) && (right_sis1_bip == right_sis2_bip) && (left_sis1_bip != right_sis1_bip)) {
            return DefaultValues.SATISFIED;
        }
        //All check fails, Violated quartet
        return DefaultValues.VIOLATED;
    }
    public static void findQuartetHypoStatus(Quartet quartet) {
    	Taxa[] sisters = new Taxa[4]; 
    	sisters[0] = quartet.taxa_sisters_left[0];//Taxa sisTaxa 
    	sisters[1] = quartet.taxa_sisters_left[1];//Taxa left_sis2 
    	sisters[2] = quartet.taxa_sisters_right[0];//Taxa right_sis1 = quartet.taxa_sisters_right[0];
    	sisters[3] = quartet.taxa_sisters_right[1];//Taxa right_sis2 = quartet.taxa_sisters_right[1];
//    	int[] sisters_partition = new int[4];
//    	sisters_partition[0] = sisters[0].partition;
//    	sisters_partition[1] = sisters[1].partition;
//    	sisters_partition[2] = sisters[2].partition;
//    	sisters_partition[3] = sisters[3].partition;
    	quartet.quartet_status = findQuartetStatus(sisters[0].partition , sisters[1].partition , sisters[2].partition , sisters[3].partition);
    	//System.out.println(quartet.quartet_status+"  "+findQuartetStatus(sisters[0].partition , sisters[1].partition , sisters[2].partition , sisters[3].partition));
    	int[] quartet_status_after_hypo_swap = {DefaultValues.DEFERRED, DefaultValues.DEFERRED, DefaultValues.DEFERRED, DefaultValues.DEFERRED};
    	if (quartet.quartet_status == DefaultValues.DEFERRED) {
//			System.out.println("......................sisters_partition[i] =" + sisters[0].partition+":"+ sisters[1].partition
//					+":"+sisters[2].partition+ ":"+sisters[3].partition);
    		for (int i = 0; i < 4; i++) {
    			int[] sisters_hypo_partition = {sisters[0].partition , sisters[1].partition , sisters[2].partition , sisters[3].partition };
    			sisters_hypo_partition[i] = getOppositePartition(sisters[i].partition);
//    			System.out.println("sisters_hypo_partition =" + sisters_hypo_partition[0]+":"+sisters_hypo_partition[1]+
//    					":"+sisters_hypo_partition[2]+":"+sisters_hypo_partition[3]);
//    			System.out.println("sisters_partition[i] =" + sisters[0].partition+":"+ sisters[1].partition
//    					+":"+sisters[2].partition+ ":"+sisters[3].partition+"\n");
    			quartet_status_after_hypo_swap [i] = findQuartetStatus(sisters_hypo_partition);
			}
		}
    	for (int i = 0; i < 4; i++) {
    		 sisters[i]._8_vals_THIS_TAX_before_hypo_swap.addRespectiveValue(quartet.weight, quartet.quartet_status); //_8values include ns, nv, nd, nb, ws, wv, wd, wb
    		 sisters[i]._8_vals_THIS_TAX_AFTER_hypo_swap.addRespectiveValue(quartet.weight, quartet_status_after_hypo_swap[i]); //If status.UNKNOWN, then don't add anything.
            
		}

    }
    public static void findQuartetHypoStatusAfterFindingBestTaxa(Quartet quartet, Taxa best_taxa) {

    	Taxa[] sisters = new Taxa[4]; 
    	sisters[0] = quartet.taxa_sisters_left[0];//Taxa sisTaxa 
    	sisters[1] = quartet.taxa_sisters_left[1];//Taxa left_sis2 
    	sisters[2] = quartet.taxa_sisters_right[0];//Taxa right_sis1 = quartet.taxa_sisters_right[0];
    	sisters[3] = quartet.taxa_sisters_right[1];//Taxa right_sis2 = quartet.taxa_sisters_right[1];
//    	int[] sisters_partition = new int[4];
//    	sisters_partition[0] = sisters[0].partition;
//    	sisters_partition[1] = sisters[1].partition;
//    	sisters_partition[2] = sisters[2].partition;
//    	sisters_partition[3] = sisters[3].partition;
    	
    	int quartet_previous_status = quartet.quartet_status;
    	int quartet_present_status = DefaultValues.DEFERRED;//
    	//System.out.println(quartet.quartet_status+"  "+findQuartetStatus(sisters[0].partition , sisters[1].partition , sisters[2].partition , sisters[3].partition));
    	
    	int[] quartet_previous_status_after_hypo_swap = {DefaultValues.DEFERRED, DefaultValues.DEFERRED, DefaultValues.DEFERRED, DefaultValues.DEFERRED};
    	int[] quartet_present_status_after_hypo_swap = {DefaultValues.DEFERRED, DefaultValues.DEFERRED, DefaultValues.DEFERRED, DefaultValues.DEFERRED};
    	
    	if (quartet_previous_status == DefaultValues.DEFERRED) {
    		quartet_present_status = findQuartetStatus(sisters[0].partition , sisters[1].partition , sisters[2].partition , sisters[3].partition);
    		int index_of_best_taxa = 0;

    		for (int i = 0; i < 4; i++) {
				if (sisters[i]==best_taxa) {
					index_of_best_taxa = i;
					break;
				}
			}
    		int partition_of_best_taxa = getOppositePartition(sisters[index_of_best_taxa].partition);; 

    		for (int i = 0; i < 4; i++) {
    			if (!sisters[i].locked) {
        			int[] sisters_hypo_partition = {sisters[0].partition , sisters[1].partition , sisters[2].partition , sisters[3].partition };
        			sisters_hypo_partition[i] = getOppositePartition(sisters[i].partition);
        			sisters_hypo_partition[index_of_best_taxa] = partition_of_best_taxa;
        			quartet_previous_status_after_hypo_swap [i] = findQuartetStatus(sisters_hypo_partition);
				}

			}
		}
    	
    	
    	if (quartet_present_status == DefaultValues.DEFERRED) {
//			System.out.println("......................sisters_partition[i] =" + sisters[0].partition+":"+ sisters[1].partition
//					+":"+sisters[2].partition+ ":"+sisters[3].partition);
    		for (int i = 0; i < 4; i++) {
    			if (!sisters[i].locked) {
        			int[] sisters_hypo_partition = {sisters[0].partition , sisters[1].partition , sisters[2].partition , sisters[3].partition };
        			sisters_hypo_partition[i] = getOppositePartition(sisters[i].partition);
        			quartet_present_status_after_hypo_swap [i] = findQuartetStatus(sisters_hypo_partition);
				}

			}
		}
    	for (int i = 0; i < 4; i++) {
    		if (!sisters[i].locked) {
	       		 sisters[i]._8_vals_THIS_TAX_before_hypo_swap.subtractRespectiveValue(quartet.weight, quartet_previous_status);
	       		 sisters[i]._8_vals_THIS_TAX_before_hypo_swap.addRespectiveValue(quartet.weight, quartet_present_status); //_8values include ns, nv, nd, nb, ws, wv, wd, wb
	       		 sisters[i]._8_vals_THIS_TAX_AFTER_hypo_swap.subtractRespectiveValue(quartet.weight, quartet_previous_status_after_hypo_swap[i]);
	       		 sisters[i]._8_vals_THIS_TAX_AFTER_hypo_swap.addRespectiveValue(quartet.weight, quartet_present_status_after_hypo_swap[i]);  
			}
           
		}
    	quartet.quartet_status = quartet_present_status;

    }

    public static int findQuartetStatus(int[] arr) { //call the above function
        return findQuartetStatus(arr[0], arr[1], arr[2], arr[3]);
    }

    public static int getOppositePartition(int partition) {
        switch (partition) {
            case DefaultValues.LEFT_PARTITION:
                return DefaultValues.RIGHT_PARTITION;
            case DefaultValues.RIGHT_PARTITION:
                return DefaultValues.LEFT_PARTITION;
            default:
                return DefaultValues.UNASSIGNED_PARTITION;
        }
    }


    /*public static Bipartition_8_values obtain8ValsOfTaxonBeforeSwap(CustomDSPerLevel customDS, List<Pair<Integer, Integer>> relevantQuartetsBeforeHypoMoving, String taxToConsider, Map<String, Integer> mapInitialBip) {
        //Consider each quartet. Using that set accordingly.
        Bipartition_8_values _8_vals = new Bipartition_8_values();
        return _8_vals;
    }*/
    public static int findQuartetStatusUsingShortcut(int status_quartet_before_hyp_swap) {
        if (status_quartet_before_hyp_swap == DefaultValues.DEFERRED) {
            return DefaultValues.UNKNOWN; //only if deferred, next calculations are necessary
        }
        return DefaultValues.DEFERRED; //s->d, v->d, b->d
    }

}

//----------------------------------------------------------- NOT USED FOR NOW ---------------------------------------------------------
/*public static int findQuartetStatus(int[] left_sisters_bip, int[] right_sisters_bip) {
        int[] four_bipartitions = {left_sisters_bip[0], left_sisters_bip[1], right_sisters_bip[0], right_sisters_bip[1]};

        int sum_four_bipartitions = Helper.sumArray(four_bipartitions);
        //Blank check: Easier to check if blank quartet (all four are same) [priority wise first]
//        if ((left_sisters_bip[0] == left_sisters_bip[1]) && (right_sisters_bip[0] == right_sisters_bip[1]) && (left_sisters_bip[0] == right_sisters_bip[0])) {

        if (Math.abs(sum_four_bipartitions) == 4) { // -1,-1,-1,-1 or +1,+1,+1,+1 all will lead to sum == 4
            return Status.BLANK;
        }
        //Deferred Check: sum == 2 check [otherwise, permutations will be huge]
        if (Math.abs(sum_four_bipartitions) == 2) { //-1,+1 ,+1,+1  => +2 or +1,-1 , -1,-1 => -2 
            return Status.DEFERRED;
        }
        //Satisfied check: left are equal, right are equal AND left(any one) != right(any one)
        if ((left_sisters_bip[0] == left_sisters_bip[1]) && (right_sisters_bip[0] == right_sisters_bip[1]) && (left_sisters_bip[0] != right_sisters_bip[0])) {
            return Status.SATISFIED;
        }
        //All check fails, Violated quartet
        return Status.VIOLATED;
    }*/
