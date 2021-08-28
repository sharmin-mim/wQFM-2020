package wqfm.algo;

import wqfm.configs.Config;
import wqfm.bip.InitialBipartition;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import wqfm.bip.Bipartition_8_values;
import wqfm.ds.CustomDSPerLevel;
import wqfm.ds.FMResultObject;
import wqfm.ds.InitialTable;
import wqfm.ds.Quartet;
import wqfm.ds.Taxa;
import wqfm.utils.Helper;
import wqfm.utils.TreeHandler;
import wqfm.bip.WeightedPartitionScores;
import wqfm.configs.DefaultValues;

/**
 *
 * @author mahim
 */
public class FMRunner {

    //Main method to run all functions ... [ABSTRACT everything from Main class]
    public static String runFunctions() {
        FMRunner runner = new FMRunner();
        InitialTable initialTable = new InitialTable();
        CustomDSPerLevel customDS = new CustomDSPerLevel();
        runner.readFileAndPopulateInitialTables(Config.INPUT_FILE_NAME, customDS, initialTable);
        System.out.println("Reading from file <" + Config.INPUT_FILE_NAME + "> done."
                + "\nInitial-Num-Quartets = " + initialTable.sizeTable());
        System.out.println("Running with partition score " + WeightedPartitionScores.GET_PARTITION_SCORE_PRINT());
        int level = 0;
        customDS.level = level; //for debugging issues.

        System.out.println(InitialTable.TAXA_COUNTER);

        System.out.println(InitialTable.map_of_str_vs_int_tax_list);
        InitialTable.map_of_str_vs_int_tax_list.clear();
        //System.out.println(InitialTable.map_of_int_vs_str_tax_list);
        
        //no need to show. will comment out
//        for (int i = 0; i < InitialTable.TAXA_COUNTER; i++) {
//			System.out.print(i +"="+InitialTable.array_of_int_vs_str_tax_list[i]+" ");
//		}
//        System.out.println();
        customDS.map_of_int_vs_tax_property = InitialTable.initial_map_of_int_vs_tax_property;
        //customDS.fillUpTaxaList(InitialTable.TAXA_COUNTER);

        String final_tree = runner.recursiveDivideAndConquer(customDS, level, initialTable); //customDS will have (P, Q, Q_relevant etc) all the params needed.
        System.out.println("\n\n[L 49.] FMRunner: final tree return");

//        System.out.println(final_tree);
        //String final_tree_decoded = Helper.getFinalTreeFromMap(final_tree, InitialTable.map_of_int_vs_str_tax_list);
        String final_tree_decoded = Helper.getFinalTreeFromArrayOfIntVsStr2(final_tree, InitialTable.initial_map_of_int_vs_tax_property);
        System.out.println(final_tree_decoded);
        Helper.writeToFile(final_tree_decoded, Config.OUTPUT_FILE_NAME);
        
        //System.out.println(" Length of initial map of str vs int = "+InitialTable.map_of_int_vs_str_tax_list.size());
        //System.out.println(" Length of initial array of int vs str = "+InitialTable.array_of_int_vs_str_tax_list.length);
        System.out.println(" Length of initial taxa map = "+InitialTable.initial_map_of_int_vs_tax_property.size());
        //System.out.println("Final-Num-Quartets = " + initialTable.sizeTable());
        
        return final_tree_decoded;
    }

    // ------>>>> Main RECURSIVE function ....
    private String recursiveDivideAndConquer(CustomDSPerLevel customDS_this_level, int level, InitialTable initialTable) {
        /*So that when customDS is passed subsequently, automatic sorting will be done. No need to do it somewhere else*/
        if (level == 0) { //only do this during level 0 [at the START]
            customDS_this_level.setInitialTableReference(initialTable); //change reference of initial table.
        }
        
//        long time_1 = System.currentTimeMillis(); //calculate starting time
        customDS_this_level.initial_table1_of_list_of_quartets.sortQuartetListBasedOnWeight();//mim
//        long time_del = System.currentTimeMillis() - time_1;
//        long minutes = (time_del / 1000) / 60;
//        long seconds = (time_del / 1000) % 60;
//        System.out.format("\nSorting Time = %d ms ==> %d minutes and %d seconds.\n", time_del, minutes, seconds);
//        System.out.println("================= **** ======================== **** ====================");
        
        
//        customDS_this_level.sortQuartetIndicesMap(); //sort the quartet-index map for initial-bipartition-computation [NOT set of quartets]
        
//        if (level == 0) { //only do it for the initial step, other levels will be passed as parameters
//            customDS_this_level.fillUpTaxaList(); //fill-up the taxa list
//            System.out.println("Total Num-Taxa = " + customDS_this_level.map_taxa_relevant_quartet_indices.size());
//        }
       // System.out.println("Total Num-Taxa = " + customDS_this_level.map_taxa_relevant_quartet_indices.size());
        
        
        /////////////////// TERMINATING CONDITIONS \\\\\\\\\\\\\\\\\\\\\\\\
        // |P| <= 3 OR |Q|.isEmpty() ... return star over taxa list{P}
        if ((customDS_this_level.map_of_int_vs_tax_property.size() <= 3)
                || (customDS_this_level.initial_table1_of_list_of_quartets.list_quartets.isEmpty())) {
            String starTree = TreeHandler.getStarTree(customDS_this_level.map_of_int_vs_tax_property.keySet()); //depth-one tree
            return starTree;
        }

        level++; // For dummy node finding.
        customDS_this_level.level = level; //for debugging issues.

        InitialBipartition initialBip = new InitialBipartition();
        //Map<Integer, Integer> mapInitialBipartition = 
        initialBip.getInitialBipartitionMap(customDS_this_level);
        
//        customDS_this_level.map_of_int_vs_tax_property.keySet().forEach((key) -> {
//        	Taxa taxon = customDS_this_level.map_of_int_vs_tax_property.get(key);
//        	System.out.print(key+"="+taxon.partition+", ");
//        });
//        System.out.println();


        if (Config.DEBUG_MODE_PRINTING_GAINS_BIPARTITIONS) {
            System.out.println("L 84. FMComputer. Printing initialBipartition.");
//            Helper.printPartition(mapInitialBipartition, DefaultValues.LEFT_PARTITION, DefaultValues.RIGHT_PARTITION, InitialTable.map_of_int_vs_str_tax_list);
            Helper.printPartition3(customDS_this_level.map_of_int_vs_tax_property, DefaultValues.LEFT_PARTITION, DefaultValues.RIGHT_PARTITION);
        }

        Bipartition_8_values initialBip_8_vals = new Bipartition_8_values();
        initialBip_8_vals.compute8ValuesUsingAllQuartets_this_level(customDS_this_level);//memory consuming step. try to change
        //System.out.println("-----------initial 8 vals--------\n"+initialBip_8_vals);
        System.out.println(WeightedPartitionScores.GET_PARTITION_SCORE_PRINT() + " LEVEL: " + level + ", ALPHA: " + customDS_this_level.ALPHA_PARTITION_SCORE + ", BETA: " + customDS_this_level.BETA_PARTITION_SCORE);

        //System.out.println(initialBip_8_vals.toString());
        if (Config.MEMORY_CONSTRAINT == 0) {
        	customDS_this_level.fillRelevantQuartetsMap(); //fill-up the relevant quartets per taxa map;//memory consuming step
		}
        FMComputer fmComputerObject = new FMComputer(customDS_this_level, initialBip_8_vals, level);
        FMResultObject fmResultObject = fmComputerObject.run_FM_Algorithm_Whole();

        CustomDSPerLevel customDS_left = fmResultObject.customDS_left_partition;
        CustomDSPerLevel customDS_right = fmResultObject.customDS_right_partition;

        //Debug printing begin
        //        System.out.println("-------------- After Level " + level + " LEFT Quartets -------------------- ");
        //        System.out.println(customDS_left.onlyQuartetIndices());
        //        System.out.println(customDS_left.taxa_list_int);
        //        System.out.println("============== After Level " + level + " RIGHT Quartets ==================== ");
        //        System.out.println(customDS_right.onlyQuartetIndices());
        //        System.out.println(customDS_right.taxa_list_int);
        //Debug printing end
        /////////////////// Beginning of Recursion \\\\\\\\\\\\\\\\\\\\\\\\\\\
        int dummyTaxon = fmResultObject.dummyTaxonThisLevel;
        
        final int final_level = level;
        String right_tree_unrooted = null;
        String left_tree_unrooted = null;

		
		if (customDS_this_level.initial_table1_of_list_of_quartets.list_quartets.size() > 10000) {
			CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> recursiveDivideAndConquer(customDS_left, final_level, initialTable));
	        right_tree_unrooted = recursiveDivideAndConquer(customDS_right, level, initialTable);
	        left_tree_unrooted = null;
				try {
					left_tree_unrooted = cf.get();
				} catch (InterruptedException e) {
					//e.printStackTrace();
				} catch (ExecutionException e) {
					//e.printStackTrace();
				}

		} else {
	        left_tree_unrooted = recursiveDivideAndConquer(customDS_left, level, initialTable);
	        right_tree_unrooted = recursiveDivideAndConquer(customDS_right, level, initialTable);
		}
        
		String merged_tree = TreeHandler.mergeUnrootedTrees(left_tree_unrooted, right_tree_unrooted, String.valueOf(dummyTaxon));
//        String left_tree_unrooted = recursiveDivideAndConquer(customDS_left, level, initialTable);
//        String right_tree_unrooted = recursiveDivideAndConquer(customDS_right, level, initialTable);
//        String merged_tree = TreeHandler.mergeUnrootedTrees(left_tree_unrooted, right_tree_unrooted, String.valueOf(dummyTaxon));
        return merged_tree;
    }

    // https://stackoverflow.com/questions/6100712/simple-way-to-count-character-occurrences-in-a-string/23906674
    private int countChars_in_String(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    //------------------Initial Bipartition --------------------
    private void populatePerInputLine(CustomDSPerLevel customDS, InitialTable initialTable, String line, int line_cnt) {
        // Check if STAR is present 
        // ((1,2,49,57)); 6     is a STAR
        // ((0,1),(10,9)); 343  is normal // should have 3 right brackets and 3 left brackets

        int cnt_left_brackets = countChars_in_String(line, '(');
        int cnt_right_brackets = countChars_in_String(line, ')');

        if ((cnt_left_brackets != cnt_right_brackets) || (cnt_left_brackets != 3) || (cnt_right_brackets != 3)) {
            System.out.println("\n\n****** Found STAR, line num " + line_cnt + " :->" + line);
            System.out.println("\nCan't handle polytomy for now. Exiting System.\n\n");
            System.exit(-1);
        }

        // No issues with STAR.
        Quartet quartet = new Quartet(line);
        initialTable.addToListOfQuartets(quartet); //add to initial-quartets-single-list
//        int idx_qrt_in_table_1 = initialTable.sizeTable() - 1; //size - 1 is the last index
//        customDS.quartet_indices_list_unsorted.add(idx_qrt_in_table_1);
    }

    //BufferedReader is used here since BufferedReader is faster than Scanner.readLine
    public void readFileAndPopulateInitialTables(String inputFileName, CustomDSPerLevel customDS, InitialTable initialTable) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(inputFileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                int line_cnt = 1;
                while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                    populatePerInputLine(customDS, initialTable, line, line_cnt);
                    line_cnt++;
                }
            }
        } catch (IOException ex) {
            System.out.println("ERROR READING FILE <" + inputFileName + ">. EXITING SYSTEM");
            System.exit(-1);

        } finally {
            try {
                fileInputStream.close();
            } catch (IOException ex) {
                System.out.println("ERROR IN CLOSING fileInputStream while reading file. Exiting");
                System.exit(-1);
            }
        }
        //mim
       // initialTable.sortQuartetListBasedOnWeight();
//        initialTable.array_of_int_vs_str_tax_list = new String[initialTable.TAXA_COUNTER];
//        for (String tax_name : initialTable.map_of_str_vs_int_tax_list.keySet()) {
//        	//System.out.println(initialTable.map_of_str_vs_int_tax_list.get(tax_name)+" -> "+tax_name);
//        	initialTable.array_of_int_vs_str_tax_list[initialTable.map_of_str_vs_int_tax_list.get(tax_name)]=tax_name;
//        	//customDS.map_of_int_vs_tax_property.put(InitialTable.map_of_str_vs_int_tax_list.get(tax_name), new Taxa(tax_name));
//		}
        //mim
        //FeatureComputer.Compute_Feature(initialTable.get_QuartetList());

    }

    //-------------------------------------------------------------------------------------------------------------
}
