package wqfm.bip;

import wqfm.configs.Config;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import wqfm.ds.CustomDSPerLevel;
import wqfm.ds.Quartet;
import wqfm.ds.Taxa;
import wqfm.main.Main;
import wqfm.configs.DefaultValues;

/**
 *
 * @author mahim
 */
public class InitialBipartition {
    //Function to obtain initial (logical) bipartition 

    private static String getCommaSeparatedEqualValue(Map<String, Integer> map, int value) {
        return map.keySet()
                .stream()
                .map(x -> map.get(x))
                .filter(x -> (x == value))
                .map(x -> String.valueOf(x))
                .collect(Collectors.joining(", "));
    }

    public static void printBipartition(Map<String, Integer> map) {
        System.out.print("LEFT: ");
        System.out.println(InitialBipartition.getCommaSeparatedEqualValue(map, DefaultValues.LEFT_PARTITION));

        System.out.print("RIGHT: ");
        System.out.println(InitialBipartition.getCommaSeparatedEqualValue(map, DefaultValues.RIGHT_PARTITION));
    }

    public Map<Integer, Integer> getInitialBipartitionMap(CustomDSPerLevel customDS) {
        // Factory level choice.
        switch (Config.BIPARTITION_MODE) {

            case DefaultValues.BIPARTITION_GREEDY:
                return this.getInitialBipartitionGreedy(customDS);

            case DefaultValues.BIPARTITION_EXTREME:
                return this.getInitialBipartitionExtreme(customDS);

            case DefaultValues.BIPARTITION_RANDOM:
                return this.getInitialBipartitionRandom(customDS);

            default:
                return this.getInitialBipartitionGreedy(customDS); // by default

        }

    }

    private Map<Integer, Integer> getInitialBipartitionExtreme(CustomDSPerLevel customDS) {
        // one on the left, all the others on the right.
        Map<Integer, Integer> map_partition = new HashMap<>();

        // initially put all taxa to the right.
        customDS.map_of_int_vs_tax_property.keySet().forEach((t) -> {
            map_partition.put(t, DefaultValues.RIGHT_PARTITION);
        });

        // take the first taxa put into left.
        int firstTaxon =customDS.map_of_int_vs_tax_property.entrySet().stream().findFirst().get().getKey();
        map_partition.put(firstTaxon, DefaultValues.LEFT_PARTITION);
        //map_partition.put(customDS.taxa_list_int.get(0), DefaultValues.LEFT_PARTITION);

        return map_partition;
    }

    private Map<Integer, Integer> getInitialBipartitionRandom(CustomDSPerLevel customDS) {
        Map<Integer, Integer> map_partition = new HashMap<>();

        // randomly assign partitions.
        customDS.map_of_int_vs_tax_property.keySet().forEach((t) -> {
            int partition = (Math.random() > 0.5) ? DefaultValues.LEFT_PARTITION : DefaultValues.RIGHT_PARTITION;
            map_partition.put(t, partition);
        });

        // check if any side is empty, then place there.
        boolean has_left = false, has_right = false;

        for (int key : map_partition.keySet()) {
            int val = map_partition.get(key);
            if (val == DefaultValues.LEFT_PARTITION) {
                has_left = true;
            }
            if (val == DefaultValues.RIGHT_PARTITION) {
                has_right = true;
            }
        }

        if (!has_left) {
            // assign first element.
        	int firstTaxon =customDS.map_of_int_vs_tax_property.entrySet().stream().findFirst().get().getKey();
            map_partition.put(firstTaxon, DefaultValues.LEFT_PARTITION);
        }
        if (!has_right) {
            // assign first element.
        	int firstTaxon =customDS.map_of_int_vs_tax_property.entrySet().stream().findFirst().get().getKey();
            map_partition.put(firstTaxon, DefaultValues.RIGHT_PARTITION);
        }

        return map_partition;
    }

    private Map<Integer, Integer> getInitialBipartitionGreedy(CustomDSPerLevel customDS) {

        Map<Integer, Integer> map_partition = new HashMap<>(); //return this map

        for (int tax : customDS.map_of_int_vs_tax_property .keySet()) { //initially assign all as 0/unassigned
            map_partition.put(tax, DefaultValues.UNASSIGNED_PARTITION);
        }
        //System.out.println(map_partition);
        customDS.map_of_int_vs_tax_property.values().forEach((taxon -> {
        	 taxon.partition = DefaultValues.UNASSIGNED_PARTITION;
        }));
        
        int count_unassigned_taxa = customDS.map_of_int_vs_tax_property.size();
        int count_assigned_taxa = 0;

        int count_taxa_left_partition = 0;
        int count_taxa_right_partition = 0;
        //int loop_count = 0;
        
        //System.out.println("============================ Quartet List==================================");
        for (Quartet quartet_under_consideration : customDS.initial_table1_of_list_of_quartets.get_QuartetList()) {
        	
        	//loop_count++;
        	//System.out.println(quartet_under_consideration.toString());
            int q1 = quartet_under_consideration.taxa_sisters_left[0].taxa_int_name;
            int q2 = quartet_under_consideration.taxa_sisters_left[1].taxa_int_name;
            int q3 = quartet_under_consideration.taxa_sisters_right[0].taxa_int_name;
            int q4 = quartet_under_consideration.taxa_sisters_right[1].taxa_int_name;
            
            Taxa t1 = quartet_under_consideration.taxa_sisters_left[0];
            Taxa t2 = quartet_under_consideration.taxa_sisters_left[1];
            Taxa t3 = quartet_under_consideration.taxa_sisters_right[0];
            Taxa t4 = quartet_under_consideration.taxa_sisters_right[1];
            
//        	System.out.println(quartet_under_consideration.taxa_sisters_left[0].get_taxa_int_name()+","
//        			+quartet_under_consideration.taxa_sisters_left[1].get_taxa_int_name()+"|"
//        			+quartet_under_consideration.taxa_sisters_right[0].get_taxa_int_name()+","
//        			+quartet_under_consideration.taxa_sisters_right[1].get_taxa_int_name()+":"
//        			+quartet_under_consideration.weight);

            int status_q1, status_q2, status_q3, status_q4; //status of q1,q2,q3,q4 respectively
            status_q1 = map_partition.get(q1);
            status_q2 = map_partition.get(q2);
            status_q3 = map_partition.get(q3);
            status_q4 = map_partition.get(q4);
            
            
            


            if (status_q1 == DefaultValues.UNASSIGNED_PARTITION && status_q2 == DefaultValues.UNASSIGNED_PARTITION /*all taxa of this quartet are unassigned to any bipartition*/
                    && status_q3 == DefaultValues.UNASSIGNED_PARTITION && status_q4 == DefaultValues.UNASSIGNED_PARTITION) { // assign q1,q2 to left and q3,q4 to right
                map_partition.put(q1, DefaultValues.LEFT_PARTITION);
                map_partition.put(q2, DefaultValues.LEFT_PARTITION);
                map_partition.put(q3, DefaultValues.RIGHT_PARTITION);
                map_partition.put(q4, DefaultValues.RIGHT_PARTITION);
                t1.partition = DefaultValues.LEFT_PARTITION;
                t2.partition = DefaultValues.LEFT_PARTITION;
                t3.partition = DefaultValues.RIGHT_PARTITION;
                t4.partition = DefaultValues.RIGHT_PARTITION;
                count_taxa_left_partition += 2;
                count_taxa_right_partition += 2;
                status_q1 = DefaultValues.LEFT_PARTITION;
                status_q2 = DefaultValues.LEFT_PARTITION;
                status_q3 = DefaultValues.RIGHT_PARTITION;
                status_q4 = DefaultValues.RIGHT_PARTITION;
                
                count_assigned_taxa +=4;
                
                //System.out.println(partition_list.get(idx_q1));
            } else if (status_q1 != DefaultValues.UNASSIGNED_PARTITION && status_q2 != DefaultValues.UNASSIGNED_PARTITION /*all taxa of this quartet are assigned to any bipartition*/
                    && status_q3 != DefaultValues.UNASSIGNED_PARTITION && status_q4 != DefaultValues.UNASSIGNED_PARTITION){
            	
            }else {
                if (status_q1 == DefaultValues.UNASSIGNED_PARTITION) //q1 not present in any partition
                {	//if status_q2 is assigned
                    if (status_q2 != DefaultValues.UNASSIGNED_PARTITION) { //look for q2's partition. put q1 in there
                        if (status_q2 == DefaultValues.LEFT_PARTITION) {
                            status_q1 = DefaultValues.LEFT_PARTITION;
                            map_partition.put(q1, DefaultValues.LEFT_PARTITION);
                            t1.partition = DefaultValues.LEFT_PARTITION;
                            count_taxa_left_partition++;
                        } else {
                            status_q1 = DefaultValues.RIGHT_PARTITION;
                            map_partition.put(q1, DefaultValues.RIGHT_PARTITION);
                            t1.partition = DefaultValues.RIGHT_PARTITION;
                            count_taxa_right_partition++;
                        }
                        count_assigned_taxa ++;
                    } //q3 is assgined
                    else if (status_q3 != DefaultValues.UNASSIGNED_PARTITION) {
                        // q3 in left, put q1 in right
                        if (status_q3 == DefaultValues.LEFT_PARTITION) {
                            status_q1 = DefaultValues.RIGHT_PARTITION;
                            map_partition.put(q1, DefaultValues.RIGHT_PARTITION);
                            t1.partition = DefaultValues.RIGHT_PARTITION;
                            status_q2 = DefaultValues.RIGHT_PARTITION;
                            map_partition.put(q2, DefaultValues.RIGHT_PARTITION);
                            t2.partition = DefaultValues.RIGHT_PARTITION;
                            count_taxa_right_partition +=2;
                        } // status_q3 in right,put status_q1 in left
                        else {
                            status_q1 = DefaultValues.LEFT_PARTITION;
                            map_partition.put(q1, DefaultValues.LEFT_PARTITION);
                            t1.partition = DefaultValues.LEFT_PARTITION;
                            status_q2 = DefaultValues.LEFT_PARTITION;
                            map_partition.put(q2, DefaultValues.LEFT_PARTITION);
                            t2.partition = DefaultValues.LEFT_PARTITION;
                            count_taxa_left_partition +=2;
                        }
                        count_assigned_taxa +=2;
                    } else if (status_q4 != DefaultValues.UNASSIGNED_PARTITION) {
                        // q4 in left, put q1 in right
                        if (status_q4 == DefaultValues.LEFT_PARTITION) {
                            status_q1 = DefaultValues.RIGHT_PARTITION;
                            map_partition.put(q1, DefaultValues.RIGHT_PARTITION);
                            t1.partition = DefaultValues.RIGHT_PARTITION;
                            status_q2 = DefaultValues.RIGHT_PARTITION;
                            map_partition.put(q2, DefaultValues.RIGHT_PARTITION);
                            t2.partition = DefaultValues.RIGHT_PARTITION;
                            count_taxa_right_partition +=2;
                            status_q3 = DefaultValues.LEFT_PARTITION;
                            map_partition.put(q3, DefaultValues.LEFT_PARTITION);
                            t3.partition = DefaultValues.LEFT_PARTITION;
                            count_taxa_left_partition++;
                        } //q4 in right,put q1 in left
                        else {
                            status_q1 = DefaultValues.LEFT_PARTITION;
                            map_partition.put(q1, DefaultValues.LEFT_PARTITION);
                            t1.partition = DefaultValues.LEFT_PARTITION;
                            status_q2 = DefaultValues.LEFT_PARTITION;
                            map_partition.put(q2, DefaultValues.LEFT_PARTITION);
                            t2.partition = DefaultValues.LEFT_PARTITION;
                            count_taxa_left_partition +=2;
                            status_q3 = DefaultValues.RIGHT_PARTITION;
                            map_partition.put(q3, DefaultValues.RIGHT_PARTITION);
                            t3.partition = DefaultValues.RIGHT_PARTITION;
                            count_taxa_right_partition ++;
                        }
                        count_assigned_taxa +=3;
                    }
                    

                }
                if (status_q2 == DefaultValues.UNASSIGNED_PARTITION) {
                    //look for q1's partition, put q2 in there
                    if (status_q1 == DefaultValues.LEFT_PARTITION) {
                        status_q2 = DefaultValues.LEFT_PARTITION;
                        map_partition.put(q2, DefaultValues.LEFT_PARTITION);
                        t2.partition = DefaultValues.LEFT_PARTITION;
                        count_taxa_left_partition++;
                    } else {
                        status_q2 = DefaultValues.RIGHT_PARTITION;
                        map_partition.put(q2, DefaultValues.RIGHT_PARTITION);
                        t2.partition = DefaultValues.RIGHT_PARTITION;
                        count_taxa_right_partition++;
                    }
                    count_assigned_taxa ++;

                }
                if (status_q3 == DefaultValues.UNASSIGNED_PARTITION) {
                    if (status_q4 != DefaultValues.UNASSIGNED_PARTITION) //q4 is assigned, look for q4 and put q3 in there
                    {
                        if (status_q4 == DefaultValues.RIGHT_PARTITION) {
                            status_q3 = DefaultValues.RIGHT_PARTITION;
                            map_partition.put(q3, DefaultValues.RIGHT_PARTITION);
                            t3.partition = DefaultValues.RIGHT_PARTITION;
                            count_taxa_right_partition++;
                        } else {
                            status_q3 = DefaultValues.LEFT_PARTITION;
                            map_partition.put(q3, DefaultValues.LEFT_PARTITION);
                            t3.partition = DefaultValues.LEFT_PARTITION;
                            count_taxa_left_partition++;
                        }
                        count_assigned_taxa ++;
                    } else {
                        if (status_q1 == DefaultValues.RIGHT_PARTITION) {
                            status_q3 = DefaultValues.LEFT_PARTITION;
                            map_partition.put(q3, DefaultValues.LEFT_PARTITION);
                            t3.partition = DefaultValues.LEFT_PARTITION;
                            status_q4 = DefaultValues.LEFT_PARTITION;
                            map_partition.put(q4, DefaultValues.LEFT_PARTITION);
                            t4.partition = DefaultValues.LEFT_PARTITION;
                            count_taxa_left_partition +=2;
                        } else {
                            status_q3 = DefaultValues.RIGHT_PARTITION;
                            map_partition.put(q3, DefaultValues.RIGHT_PARTITION);
                            t3.partition = DefaultValues.RIGHT_PARTITION;
                            status_q4 = DefaultValues.RIGHT_PARTITION;
                            map_partition.put(q4, DefaultValues.RIGHT_PARTITION);
                            t4.partition = DefaultValues.RIGHT_PARTITION;
                            count_taxa_right_partition +=2;
                        }
                        count_assigned_taxa +=2;
                    }
                    
                }
                if (status_q4 == DefaultValues.UNASSIGNED_PARTITION) {
                    if (status_q3 == DefaultValues.LEFT_PARTITION) {
                        status_q4 = DefaultValues.LEFT_PARTITION;
                        map_partition.put(q4, DefaultValues.LEFT_PARTITION);
                        t4.partition = DefaultValues.LEFT_PARTITION;
                        count_taxa_left_partition++;
                    } else {
                        status_q4 = DefaultValues.RIGHT_PARTITION;
                        map_partition.put(q4, DefaultValues.RIGHT_PARTITION);
                        t4.partition = DefaultValues.RIGHT_PARTITION;
                        count_taxa_right_partition++;
                    }
                    count_assigned_taxa ++;

                }
            }
            if (count_assigned_taxa == count_unassigned_taxa) {
//				System.out.println("count_assigned_taxa ="+count_assigned_taxa );
//				System.out.println("count_unassigned_taxa ="+count_unassigned_taxa );
				break;
			}
        
		}
		//System.out.println("loop count = "+loop_count);
        //now assign remaining taxa randomly step4
        int flag_for_random_assignment = 0;
        if (count_assigned_taxa != count_unassigned_taxa) {
            for (int key_tax : map_partition.keySet()) {
                if (map_partition.get(key_tax) == DefaultValues.UNASSIGNED_PARTITION) {
                    if (count_taxa_left_partition < count_taxa_right_partition) {
                        flag_for_random_assignment = 2;
                    } else if (count_taxa_left_partition > count_taxa_right_partition) {
                        flag_for_random_assignment = 1;
                    } else {
                        flag_for_random_assignment++;
                    }
                    if (flag_for_random_assignment % 2 == 0) {
                        map_partition.put(key_tax, DefaultValues.LEFT_PARTITION);
                        customDS.map_of_int_vs_tax_property.get(key_tax).partition = DefaultValues.LEFT_PARTITION;
                        count_taxa_left_partition++;
                        count_assigned_taxa++;
                    } else {
                        map_partition.put(key_tax, DefaultValues.RIGHT_PARTITION);
                        customDS.map_of_int_vs_tax_property.get(key_tax).partition = DefaultValues.RIGHT_PARTITION;
                        count_taxa_right_partition++;
                        count_assigned_taxa++;
                    }
                }
                if (count_assigned_taxa == count_unassigned_taxa) {
//    				System.out.println("count_assigned_taxa ="+count_assigned_taxa );
//    				System.out.println("count_unassigned_taxa ="+count_unassigned_taxa );
    				break;
    			}
            }
		}

        
//        System.out.println(".......................Initial map......................... \n"+map_partition);
//        customDS.map_of_int_vs_tax_property.values().forEach((taxon -> {
//       	 System.out.print(taxon.taxa_int_name +"="+taxon.partition+", ");
//       }));

        return map_partition;

    }

}
