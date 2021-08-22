package wqfm.ds;


import java.util.Arrays;
import wqfm.configs.Config;
import java.util.HashMap;
import java.util.Map;
import wqfm.utils.CustomPair;
import wqfm.utils.TaxaUtils;
import wqfm.configs.DefaultValues;

/**
 *
 * @author mahim
 */
public class FMResultObject {

    public CustomDSPerLevel customDS_left_partition;
    public CustomDSPerLevel customDS_right_partition;

    public final int dummyTaxonThisLevel;
    private final CustomDSPerLevel customDS_initial_this_level;

    private final Map<Quartet, CustomPair> map_quartet_of_dummy_with_added_weights_and_partition;
    private final Map<Quartet, Integer> map_quartet_dummy_with_counts;

    public FMResultObject(CustomDSPerLevel customDS_this_level, int level) {
        this.customDS_initial_this_level = customDS_this_level;
        //pass the reference of initial table to both left & right partitions.
        this.customDS_left_partition = new CustomDSPerLevel(); //do not initialize tables YET
        this.customDS_right_partition = new CustomDSPerLevel(); //do not initialize tables YET
        this.dummyTaxonThisLevel = TaxaUtils.getDummyTaxonName(level); //obtain the dummy node for this level

        this.customDS_left_partition.initial_table1_of_list_of_quartets = new InitialTable();
        this.customDS_right_partition.initial_table1_of_list_of_quartets = new InitialTable();

        // ------- map for quartets with dummy node ---> will be formed to one node ----------
        this.map_quartet_of_dummy_with_added_weights_and_partition = new HashMap<>();

        this.map_quartet_dummy_with_counts = new HashMap<>();
    }

    public void createFMResultObjects(Map<Integer, Integer> mapOfBipartition) {
        //Initially just transfer all to P_left and P_right. [Then for quartets-with-dummy, just pass the dummy node]
        mapOfBipartition.keySet().forEach((key_taxon) -> {
        	Taxa taxa = this.customDS_initial_this_level.map_of_int_vs_tax_property.get(key_taxon);
            if (mapOfBipartition.get(key_taxon) == DefaultValues.LEFT_PARTITION) {
                //this.customDS_left_partition.taxa_list_int.add(key_taxon);
            	this.customDS_left_partition.map_of_int_vs_tax_property.put(key_taxon, taxa);
                //this.customDS_left_partition.map_taxa_relevant_quartet_indices.put(key_taxon, new ArrayList<>());
            } else if (mapOfBipartition.get(key_taxon) == DefaultValues.RIGHT_PARTITION) {
                //this.customDS_right_partition.taxa_list_int.add(key_taxon);
                //this.customDS_right_partition.map_taxa_relevant_quartet_indices.put(key_taxon, new ArrayList<>());
                this.customDS_right_partition.map_of_int_vs_tax_property.put(key_taxon, taxa);
            }
            taxa.reset_tax_property();
            //taxa.relevant_quartet_indices.clear();
        });
        if (Config.MEMORY_CONSTRAINT == 0) {
            this.customDS_initial_this_level.map_of_int_vs_tax_property.values().forEach((taxon) -> {
            	taxon.relevant_quartet_indices.clear();
            });
		}
        Config.MEMORY_CONSTRAINT = 0;

        
        //Taxa dummy_taxon = new Taxa();
//        Taxa left_dummy_taxon = new Taxa(dummyTaxonThisLevel);
//        Taxa right_dummy_taxon = new Taxa(dummyTaxonThisLevel);//change needed//later I will treat them as different taxa because this will create problem during multithrading.

        //Add dummy taxon to both partitions.
        this.customDS_left_partition.map_of_int_vs_tax_property.put(dummyTaxonThisLevel, new Taxa());
        this.customDS_right_partition.map_of_int_vs_tax_property.put(dummyTaxonThisLevel, new Taxa());
//        this.customDS_left_partition.taxa_list_int.add(dummyTaxonThisLevel);
//        this.customDS_right_partition.taxa_list_int.add(dummyTaxonThisLevel);
        
//        System.out.println("Left partition");
//        System.out.println(this.customDS_left_partition.map_of_int_vs_tax_property.keySet());
//        System.out.println("Right partition");
//        System.out.println(this.customDS_right_partition.map_of_int_vs_tax_property.keySet());
        
//        for (int itr = 0; itr < this.customDS_initial_this_level.initial_table1_of_list_of_quartets.sizeTable(); itr++) {
//            // int qrt_idx = this.customDS_initial_this_level.quartet_indices_list_unsorted.get(itr); //add to new lists of customDS
//         	
//             Quartet quartet_parent = this.customDS_initial_this_level.initial_table1_of_list_of_quartets.get(itr);
//         	System.out.println(quartet_parent.taxa_sisters_left[0].get_taxa_int_name()+","+quartet_parent.taxa_sisters_left[1].get_taxa_int_name()+"|"+
//        			quartet_parent.taxa_sisters_right[0].get_taxa_int_name()+","+quartet_parent.taxa_sisters_right[1].get_taxa_int_name());
//         }
        //System.out.println(".................................Before size of initial table ="+this.customDS_initial_this_level.initial_table1_of_list_of_quartets.sizeTable());
        this.customDS_initial_this_level.initial_table1_of_list_of_quartets.list_quartets
        	.removeIf(quartet-> (quartet.quartet_status==DefaultValues.SATISFIED || quartet.quartet_status==DefaultValues.VIOLATED));
        //System.out.println(".................................After size of initial table ="+this.customDS_initial_this_level.initial_table1_of_list_of_quartets.sizeTable());
      
//        for (int itr = 0; itr < this.customDS_initial_this_level.initial_table1_of_list_of_quartets.sizeTable(); itr++) {
//            // int qrt_idx = this.customDS_initial_this_level.quartet_indices_list_unsorted.get(itr); //add to new lists of customDS
//         	
//             Quartet quartet_parent = this.customDS_initial_this_level.initial_table1_of_list_of_quartets.get(itr);
//         	System.out.println("............"+quartet_parent.taxa_sisters_left[0].get_taxa_int_name()+","+quartet_parent.taxa_sisters_left[1].get_taxa_int_name()+"|"+
//        			quartet_parent.taxa_sisters_right[0].get_taxa_int_name()+","+quartet_parent.taxa_sisters_right[1].get_taxa_int_name());
//         }
//        
        //1. Traverse each quartet, find the deferred and blank quartets and pass to next.
        //System.out.println(".................List of quartet........................");
        for (int itr = 0; itr < this.customDS_initial_this_level.initial_table1_of_list_of_quartets.sizeTable(); itr++) {
           // int qrt_idx = this.customDS_initial_this_level.quartet_indices_list_unsorted.get(itr); //add to new lists of customDS
        	
            Quartet quartet_parent = this.customDS_initial_this_level.initial_table1_of_list_of_quartets.get(itr);
            // find quartet's status.
//        	System.out.println(quartet_parent.taxa_sisters_left[0].get_taxa_int_name()+","+quartet_parent.taxa_sisters_left[1].get_taxa_int_name()+"|"+
//        			quartet_parent.taxa_sisters_right[0].get_taxa_int_name()+","+quartet_parent.taxa_sisters_right[1].get_taxa_int_name());
//            System.out.println("quartet_parent.taxa_sisters_left[0].taxa_int_name = "+quartet_parent.taxa_sisters_left[0].taxa_int_name);
            int left_1_partition = mapOfBipartition.get(quartet_parent.taxa_sisters_left[0]);
            int left_2_partition = mapOfBipartition.get(quartet_parent.taxa_sisters_left[1]);
            int right_1_partition = mapOfBipartition.get(quartet_parent.taxa_sisters_right[0]);
            int right_2_partition = mapOfBipartition.get(quartet_parent.taxa_sisters_right[1]);

            //int quartet_status = TaxaUtils.findQuartetStatus(left_1_partition, left_2_partition, right_1_partition, right_2_partition);
            int quartet_status = quartet_parent.quartet_status;//mim..........does work

            //check if quartet is blank or deferred and only keep those, add dummy taxon ... [add quartet-indices and taxa-set]
            if (quartet_status == DefaultValues.BLANK) { // pass THIS quartet, no need to add dummy [all 4 are on same side]
            	//System.out.println(quartet_parent.toString()+"......BLANK");
                if (left_1_partition == DefaultValues.LEFT_PARTITION) { //all four tax of the parent quartet are in left partition
                	this.customDS_left_partition.initial_table1_of_list_of_quartets.list_quartets.add(quartet_parent);
//                    this.customDS_left_partition.quartet_indices_list_unsorted
//                    	.add(this.customDS_left_partition.initial_table1_of_list_of_quartets.sizeTable() - 1); //add old quartet's index in Q_left

                } else if (left_1_partition == DefaultValues.RIGHT_PARTITION) { //all four tax of the parent quartet are in right partition
                	this.customDS_right_partition.initial_table1_of_list_of_quartets.addToListOfQuartets(quartet_parent);
//                    this.customDS_right_partition.quartet_indices_list_unsorted
//                    	.add(this.customDS_right_partition.initial_table1_of_list_of_quartets.sizeTable() - 1);
                	//this.customDS_right_partition.quartet_indices_list_unsorted.add(qrt_idx);  //add old quartet's index in Q_right

                }
            } else if (quartet_status == DefaultValues.DEFERRED) {//direct else should work now
                int[] arr_bipartition = {left_1_partition, left_2_partition, right_1_partition, right_2_partition};
                int commonBipartitionValue = findCommonBipartition(arr_bipartition); //find the common bipartition [i.e. whether q goes to Q_left or Q_right]
//                System.out.println(">> FMResultObject (line 64) parent qrt = " + quartet_parent + " bip = " + mapOfBipartition);
//             	System.out.println("............"+quartet_parent.taxa_sisters_left[0].get_taxa_int_name()+","+quartet_parent.taxa_sisters_left[1].get_taxa_int_name()+"|"+
//    			quartet_parent.taxa_sisters_right[0].get_taxa_int_name()+","+quartet_parent.taxa_sisters_right[1].get_taxa_int_name());
                Quartet newQuartetWithDummy = replaceExistingQuartetWithDummyNode(quartet_parent, arr_bipartition, commonBipartitionValue); //Find the new quartet WITH dummy node [replaces uncommon tax]

                // do not add yet, first put to map with added weight eg. 1,2|5,11 and 1,2|5,15 will be 1,2|5,X with weight = w1+w2
                if (this.map_quartet_of_dummy_with_added_weights_and_partition.containsKey(newQuartetWithDummy) == false) { //this quartet-of-dummy DOESN't exist.
                    this.map_quartet_of_dummy_with_added_weights_and_partition.put(newQuartetWithDummy, new CustomPair(newQuartetWithDummy.weight, commonBipartitionValue)); //initialize with 0 so that next step doesn't have to be if-else
                    this.map_quartet_dummy_with_counts.put(newQuartetWithDummy, 1); // current count = 1
                } else {
                    // else we will add weights for the Pair (value of the map_quartet_of_dummy_with_added_weights_and_partition)
                    CustomPair pair_value_from_map = this.map_quartet_of_dummy_with_added_weights_and_partition.get(newQuartetWithDummy);
                    int count = this.map_quartet_dummy_with_counts.get(newQuartetWithDummy);

                    double newWeight;
                    if (Config.NORMALIZE_DUMMY_QUARTETS == true) {
                        //// averaging
                        newWeight = (double) ((count * pair_value_from_map.weight_double) + newQuartetWithDummy.weight) / (double) (count + 1);
                    } else {
                        //// summing
                        newWeight = pair_value_from_map.weight_double + newQuartetWithDummy.weight;
                    }

                    CustomPair new_pair = new CustomPair(newWeight, pair_value_from_map.partition_int);
                    //this will update the added weights while maintaining the same bipartition.
                    this.map_quartet_of_dummy_with_added_weights_and_partition.put(newQuartetWithDummy, new_pair);

                    // update the count map
                    this.map_quartet_dummy_with_counts.put(newQuartetWithDummy, count + 1);

                }

                /// for some reason, pair doesn't seem to work hence custom-class [is there a way to do it more efficiently?]
            }

        }
        this.customDS_initial_this_level.initial_table1_of_list_of_quartets.list_quartets.clear();
        
//        System.out.println("............................................................Dummy_____________________________");
//        this.map_quartet_dummy_with_counts.keySet().forEach((new_quartet) -> {
//        	 System.out.println(new_quartet.taxa_sisters_left[0].get_taxa_int_name()+","+new_quartet.taxa_sisters_left[1].get_taxa_int_name()+"|"+
//             		new_quartet.taxa_sisters_right[0].get_taxa_int_name()+","+new_quartet.taxa_sisters_right[1].get_taxa_int_name()+":"+new_quartet.weight+" -> "+
//             		this.map_quartet_dummy_with_counts.get(new_quartet));
//        });
//        System.out.println("............................................................Dummy_____________________________");

        //2. Now keep adding the corrected-weighted-quartets to initial-table
        this.map_quartet_of_dummy_with_added_weights_and_partition.keySet().forEach((q_with_dummy) -> {
            CustomPair pair_val = this.map_quartet_of_dummy_with_added_weights_and_partition.get(q_with_dummy);
            Quartet new_quartet = new Quartet(q_with_dummy.taxa_sisters_left[0],
                    q_with_dummy.taxa_sisters_left[1],
                    q_with_dummy.taxa_sisters_right[0],
                    q_with_dummy.taxa_sisters_right[1],
                    pair_val.weight_double); //update the weight now.
            
            //System.out.println(new_quartet.toString()+".............newQ from deffered");

            //push to initial table.
           // this.customDS_initial_this_level.initial_table1_of_list_of_quartets.addToListOfQuartets(new_quartet);

            //obtain the index i.e. size - 1
            //int idx_quartet_newly_added = this.customDS_initial_this_level.initial_table1_of_list_of_quartets.sizeTable() - 1;

            //push to which partition depending on the pair_value's bipartition stored.
            if (pair_val.partition_int == DefaultValues.LEFT_PARTITION) {
            	this.customDS_left_partition.initial_table1_of_list_of_quartets.addToListOfQuartets(new_quartet);
//            	this.customDS_left_partition.quartet_indices_list_unsorted
//            		.add(this.customDS_left_partition.initial_table1_of_list_of_quartets.sizeTable() - 1);
               // this.customDS_left_partition.quartet_indices_list_unsorted.add(idx_quartet_newly_added);
//                System.out.println("Left"+new_quartet.taxa_sisters_left[0].get_taxa_int_name()+","+new_quartet.taxa_sisters_left[1].get_taxa_int_name()+"|"+
//                		new_quartet.taxa_sisters_right[0].get_taxa_int_name()+","+new_quartet.taxa_sisters_right[1].get_taxa_int_name()+":"+new_quartet.weight);
            } else if (pair_val.partition_int == DefaultValues.RIGHT_PARTITION) {
            	this.customDS_right_partition.initial_table1_of_list_of_quartets.addToListOfQuartets(new_quartet);
//            	System.out.println("right"+new_quartet.taxa_sisters_left[0].get_taxa_int_name()+","+new_quartet.taxa_sisters_left[1].get_taxa_int_name()+"|"+
//                		new_quartet.taxa_sisters_right[0].get_taxa_int_name()+","+new_quartet.taxa_sisters_right[1].get_taxa_int_name()+":"+new_quartet.weight);
//                this.customDS_right_partition.quartet_indices_list_unsorted
//                	.add(this.customDS_right_partition.initial_table1_of_list_of_quartets.sizeTable() - 1);
               // this.customDS_right_partition.quartet_indices_list_unsorted.add(idx_quartet_newly_added);
            }
        });
        
        //finally add the references to left and right partitions.

        //this.customDS_left_partition.initial_table1_of_list_of_quartets.assignByReference(this.customDS_initial_this_level.initial_table1_of_list_of_quartets);
        //this.customDS_right_partition.initial_table1_of_list_of_quartets.assignByReference(this.customDS_initial_this_level.initial_table1_of_list_of_quartets);
        

    }

    private int findCommonBipartition(int[] arr) {
        //Three will be same, one will be different
//        int sum = arr[0] + arr[1] + arr[2] + arr[3];
       // int sum = Arrays.stream(arr).sum();
        if (Arrays.stream(arr).sum() < 0) {
            return DefaultValues.LEFT_PARTITION;
        } else {
            return DefaultValues.RIGHT_PARTITION;
        }
    }

    private Quartet replaceExistingQuartetWithDummyNode(Quartet quartet, int[] arr, int commonBipartition) {
        Quartet q = new Quartet(quartet);
//    	System.out.println(quartet.taxa_sisters_left[0].get_taxa_int_name()+","+quartet.taxa_sisters_left[1].get_taxa_int_name()+"|"+
//		quartet.taxa_sisters_right[0].get_taxa_int_name()+","+quartet.taxa_sisters_right[1].get_taxa_int_name());

        int idx = -1;
        //finds which idx contains the uncommon bipartition
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != commonBipartition) {
                idx = i;
            }
        }
        //returns the taxon name of the uncommon bipartition
        switch (idx) {
        case DefaultValues.LEFT_SISTER_1_IDX:
            q.taxa_sisters_left[0] = this.dummyTaxonThisLevel;
            break;
        case DefaultValues.LEFT_SISTER_2_IDX:
            q.taxa_sisters_left[1] = this.dummyTaxonThisLevel;
            break;
        case DefaultValues.RIGHT_SISTER_1_IDX:
            q.taxa_sisters_right[0] = this.dummyTaxonThisLevel;
            break;
        case DefaultValues.RIGHT_SISTER_2_IDX:
            q.taxa_sisters_right[1] = this.dummyTaxonThisLevel;
            break;
        default:
            break;
    }
//        System.out.println(q.taxa_sisters_left[0].get_taxa_int_name()+","+q.taxa_sisters_left[1].get_taxa_int_name()+"|"+
//        		q.taxa_sisters_right[0].get_taxa_int_name()+","+q.taxa_sisters_right[1].get_taxa_int_name());
        q.sort_quartet_taxa_names();
        return q;
    }

}
