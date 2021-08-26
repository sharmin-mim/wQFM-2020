package wqfm.algo;

import wqfm.configs.Config;

import wqfm.ds.Taxa;
import wqfm.utils.TaxaUtils;
import java.util.ArrayList;

import java.util.Comparator;

import java.util.List;
import java.util.Map;

import wqfm.bip.Bipartition_8_values;
import wqfm.ds.CustomDSPerLevel;
import wqfm.ds.FMResultObject;
import wqfm.ds.Quartet;
import wqfm.utils.Helper;
import wqfm.bip.WeightedPartitionScores;


/**
 *
 * @author mahim
 */
public class FMComputer {

    public int level;
    private Bipartition_8_values initialBipartition_8_values;
    private final CustomDSPerLevel customDS;
   // private Map<Integer, Integer> bipartitionMap;
    //private final Map<Integer, Boolean> lockedTaxaBooleanMap; //true: LOCKED, false:FREE
    //private Map<Double, List<Integer>> mapCandidateGainsPerListTax; // Map of hypothetical gain vs list of taxa
    //private Map<Integer, Bipartition_8_values> mapCandidateTax_vs_8vals; //after hypothetical swap [i.e. IF this is taken as snapshot, no need to recalculate]
   //private final List<StatsPerPass> listOfPerPassStatistics;
    private final List<Integer> listOfPerPassStatistics;

    // for not going up in endless loop condition.
    private double prevCumulativeMax;
    private boolean isFirstTime = true;
    //private Map<Integer, Integer> prevMap;
    
    private Boolean no_new_gain_calculation;
    private int sum_of_bipartition;

    public FMComputer(CustomDSPerLevel customDS, Bipartition_8_values initialBip_8_vals, int level) {

        // Regular values initialized.
        this.level = level;
        this.customDS = customDS;

        //this.bipartitionMap = mapInitialBipartition;
        this.initialBipartition_8_values = initialBip_8_vals;

        //for one-box/one-pass
        //this.mapCandidateGainsPerListTax = new TreeMap<>(Collections.reverseOrder());
        //this.mapCandidateTax_vs_8vals = new HashMap<>();

        this.listOfPerPassStatistics = new ArrayList<>(customDS.map_of_int_vs_tax_property.size()); //for one-iteration/all boxes

        //this.lockedTaxaBooleanMap = new HashMap<>(); //initialise the lockMap
        
        //will delete this portion because reset_taxa function will do the work //change
//        this.customDS.map_of_int_vs_tax_property.values().forEach((tax) -> { // initially all taxa are FREE
//            //this.lockedTaxaBooleanMap.put(tax, Boolean.FALSE);
//            tax.locked = Boolean.FALSE;
//        });

        // normal initialization
        //this.prevMap = new HashMap<>();
    }

//    public void run_FM_singlepass_hypothetical_swap() {//per pass or step [per num taxa of steps].
//        //Test hypothetically ...
//    	this.no_new_gain_calculation = Boolean.TRUE;
//        for (int taxToConsider : this.customDS.map_of_int_vs_tax_property.keySet()) {//change//after changing partition code, value set will work
//
//            Taxa taxonToConsider = this.customDS.map_of_int_vs_tax_property.get(taxToConsider);
//            taxonToConsider.participated_in_swap = false;
//        	if (taxonToConsider.locked == true) {
//                continue; // This is not a free taxon, so continue the loop
//            }
//
//            int taxPartValBeforeHypoSwap = this.bipartitionMap.get(taxToConsider);
//
//            //First check IF moving this will lead to a singleton bipartition by doing a hypothetical swap.
//            Map<Integer, Integer> newMap = new HashMap<>(this.bipartitionMap);
//            newMap.put(taxToConsider, TaxaUtils.getOppositePartition(taxPartValBeforeHypoSwap)); //hypothetically make the swap.
//            if (TaxaUtils.isThisSingletonBipartition(newMap) == true) {
//                continue; //THIS hypothetical movement of taxToConsider leads to singleton bipartition so, continue loop.
//            }
//            if (this.no_new_gain_calculation) {
//				
//        		this.no_new_gain_calculation = Boolean.FALSE;
//
//			}
//            taxonToConsider.participated_in_swap = true;
//
//            /*For each quartet, find status, compute previous-hypothetical-swap-values. 
//                Use short-cuts (excluding deferred), and compute after-hypothetical-swap-values*/
//            List<Integer> relevantQuartetsBeforeHypoMoving = taxonToConsider.relevant_quartet_indices;
//            Bipartition_8_values _8_vals_THIS_TAX_before_hypo_swap = new Bipartition_8_values(); // all initialized to 0
//            Bipartition_8_values _8_vals_THIS_TAX_after_hypo_swap = new Bipartition_8_values(); // all initialized to 0
//
//            List<Integer> deferredQuartetsBeforeHypoMoving = new ArrayList<>(); //keep deferred quartets for later checking ...
//
//            for (int itr = 0; itr < relevantQuartetsBeforeHypoMoving.size(); itr++) {
//                int idx_relevant_qrt = relevantQuartetsBeforeHypoMoving.get(itr);
//                //No need explicit checking as customDS will be changed after every level
//                Quartet quartet = customDS.initial_table1_of_list_of_quartets.get(idx_relevant_qrt);
//
//                int statusQuartetBeforeHypoSwap = TaxaUtils.findQuartetStatus(
//                        bipartitionMap.get(quartet.taxa_sisters_left[0]),
//                        bipartitionMap.get(quartet.taxa_sisters_left[1]),
//                        bipartitionMap.get(quartet.taxa_sisters_right[0]),
//                        bipartitionMap.get(quartet.taxa_sisters_right[1]));
//
//                int statusQuartetAfterHypoSwap = TaxaUtils.findQuartetStatusUsingShortcut(statusQuartetBeforeHypoSwap); //_8values include ns, nv, nd, nb, ws, wv, wd, wb
//
//                _8_vals_THIS_TAX_before_hypo_swap.addRespectiveValue(quartet.weight, statusQuartetBeforeHypoSwap); //_8values include ns, nv, nd, nb, ws, wv, wd, wb
//                _8_vals_THIS_TAX_after_hypo_swap.addRespectiveValue(quartet.weight, statusQuartetAfterHypoSwap); //If status.UNKNOWN, then don't add anything.
//
//                if (statusQuartetBeforeHypoSwap == DefaultValues.DEFERRED) {
//                    deferredQuartetsBeforeHypoMoving.add(idx_relevant_qrt);
//                }
//
//            } // end for [relevant-quartets-iteration]
//            for (int itr_deferred_qrts = 0; itr_deferred_qrts < deferredQuartetsBeforeHypoMoving.size(); itr_deferred_qrts++) {
//                int qrt_idx_deferred_relevant_quartets_after_hypo_swap = deferredQuartetsBeforeHypoMoving.get(itr_deferred_qrts);
//                Quartet quartet = customDS.initial_table1_of_list_of_quartets.get(qrt_idx_deferred_relevant_quartets_after_hypo_swap);
//                int status_after_hypothetical_swap = TaxaUtils.findQuartetStatus(newMap.get(quartet.taxa_sisters_left[0]),
//                        newMap.get(quartet.taxa_sisters_left[1]), newMap.get(quartet.taxa_sisters_right[0]), newMap.get(quartet.taxa_sisters_right[1]));
//                _8_vals_THIS_TAX_after_hypo_swap.addRespectiveValue(quartet.weight, status_after_hypothetical_swap);
//            }
//            double ps_before_reduced = WeightedPartitionScores.calculatePartitionScoreReduced(_8_vals_THIS_TAX_before_hypo_swap);
//            double ps_after_reduced = WeightedPartitionScores.calculatePartitionScoreReduced(_8_vals_THIS_TAX_after_hypo_swap);
//            double gainOfThisTax = ps_after_reduced - ps_before_reduced; //correct calculation
//
//            Bipartition_8_values _8_values_whole_considering_thisTax_swap = new Bipartition_8_values();
//            /*AfterHypoSwap.Whole_8Vals - BeforeHypoSwap.Whole_8Vals = AfterHypoSwap.OneTax.8Vals - BeforeHypoSwap.OneTax.8vals //vector rules of distance addition*/
//            //So, AfterHypoSwap.Whole_8Vals = BeforeHypoSwap.Whole_8Vals + AfterHypoSwap.OneTax.8Vals - BeforeHypoSwap.OneTax.8vals
//            _8_values_whole_considering_thisTax_swap.addObject(this.initialBipartition_8_values);
//            _8_values_whole_considering_thisTax_swap.addObject(_8_vals_THIS_TAX_after_hypo_swap);
//            _8_values_whole_considering_thisTax_swap.subtractObject(_8_vals_THIS_TAX_before_hypo_swap);
//
////            if (this.mapCandidateGainsPerListTax.containsKey(gainOfThisTax) == false) { // this gain was not contained
////                //initialize the taxon(for this gain-val) list.
////                this.mapCandidateGainsPerListTax.put(gainOfThisTax, new ArrayList<>());
////            }//else: simply append to the list.
////            this.mapCandidateGainsPerListTax.get(gainOfThisTax).add(taxToConsider); //add gain to map
//            
//            taxonToConsider.gain = gainOfThisTax;
//            
//            taxonToConsider.bipartition_8_values = _8_values_whole_considering_thisTax_swap;
//            //this.mapCandidateTax_vs_8vals.put(taxToConsider, _8_values_whole_considering_thisTax_swap);
//
//            /*    System.out.println("FMComputer L219. taxToConsider = " + taxToConsider + " , " + Helper.getStringMappedName(taxToConsider)
//                            + "\n _8_before = " + _8_vals_THIS_TAX_before_hypo_swap
//                            + "\n _8_after = " + _8_vals_THIS_TAX_AFTER_hypo_swap
//                            + "\n ps_before = " + ps_before_reduced
//                            + " , ps_after = " + ps_after_reduced
//                            + ", gainOfThisTax = " + gainOfThisTax);
//             */
//        }
//
//    }

    public void changeParameterValuesForNextPass() {
    	// Get previous step's stats and bipartition.
        int previousPassStats = this.listOfPerPassStatistics.get(this.listOfPerPassStatistics.size() - 1);
        Taxa chosen_taxa_for_this_pass = this.customDS.map_of_int_vs_tax_property.get(previousPassStats);
        int opposite_partition = TaxaUtils.getOppositePartition(chosen_taxa_for_this_pass.partition);
        
    	this.sum_of_bipartition -= chosen_taxa_for_this_pass.partition;
    	this.sum_of_bipartition += opposite_partition;
        chosen_taxa_for_this_pass.partition = opposite_partition;


        //Previous step's chosen-bipartition is THIS step's intial-bipartition.
        //System.out.println("\n map = "+ this.bipartitionMap);
//        Integer opposite_partition = this.bipartitionMap.get(chosen_taxa_for_this_pass.taxa_int_name);
//        opposite_partition = chosen_taxa_for_this_pass.partition;
        //this.bipartitionMap.put(previousPassStats, chosen_taxa_for_this_pass.partition);
//        System.out.println(" map = "+ this.bipartitionMap);
//        this.bipartitionMap.clear();
//        this.bipartitionMap = new HashMap<>(chosen_taxa_for_this_pass.map_final_bipartition); //NEED TO COPY here.
//        System.out.println(" map = "+ this.bipartitionMap+"\n");
//        System.out.println("chosen taxa for this pass = "+ chosen_taxa_for_this_pass.taxa_int_name+" partition = "+chosen_taxa_for_this_pass.partition);
        
        //Taxa chosen_taxa_for_this_pass = this.customDS.map_of_int_vs_tax_property.get(previousPassStats.whichTaxaWasPassed);
        //chosen_taxa_for_this_pass.bipartition_8_values.addObject(this.initialBipartition_8_values);//mim
        this.initialBipartition_8_values = new Bipartition_8_values(chosen_taxa_for_this_pass.bipartition_8_values);
        //System.out.println(".........this.initialBipartition_8_values .."+this.initialBipartition_8_values);
        
        //this.mapCandidateTax_vs_8vals = new HashMap<>();//mim
        
        this.customDS.map_of_int_vs_tax_property.values().forEach((taxon) -> {
        	if (!taxon.locked) {
        		taxon.bipartition_8_values = new Bipartition_8_values();
			}
        	
        });

        //Previous step's chosen-8Values will be THIS step's chosen-8Values
        //this.initialBipartition_8_values = new Bipartition_8_values(previousPassStats._8_values_chosen_for_this_pass);

        //System.out.println("this.mapCandidateGainsPerListTax size = "+this.mapCandidateGainsPerListTax.size());
        //Clear all the per-pass maps
        //this.mapCandidateGainsPerListTax = new TreeMap<>(Collections.reverseOrder());
//        this.mapCandidateTax_vs_8vals = new HashMap<>();
        //List<Integer> relevantQuartetsBeforeHypoMoving = chosen_taxa_for_this_pass.relevant_quartet_indices;
       /////////////////////////////////////////////// this.bestTaxaRelaventQuartetID_vs_previousStatus = new HashMap<>();
        //Map<Integer, Integer> quartetIDvsStatusAfterHypoSwap = this.taxaVSquartetIDandStatusAfterHypoSwap.get(previousPassStats.whichTaxaWasPassed);
   
//        for (int quartetIndex : relevantQuartetsBeforeHypoMoving) {
//    		Quartet quartet = customDS.initial_table1_of_list_of_quartets.get(quartetIndex);
//    		//int status = quartet.quartet_status;
//    	///////////////////////////////////	bestTaxaRelaventQuartetID_vs_previousStatus.put(quartetIndex, status);
//    		//quartet.quartetStatus = quartetIDvsStatusAfterHypoSwap.get(quartetIndex);
//    		//System.out.println(quartet);
//    		TaxaUtils.findQuartetHypoStatusAfterFindingBestTaxa(quartet, chosen_taxa_for_this_pass);
//    		if (quartet.quartet_status == DefaultValues.DEFERRED) {
//    			quartet.quartet_status = TaxaUtils.findQuartetStatus(bipartitionMap.get(quartet.taxa_sisters_left[0].taxa_int_name),
//                        bipartitionMap.get(quartet.taxa_sisters_left[1].taxa_int_name), bipartitionMap.get(quartet.taxa_sisters_right[0].taxa_int_name), 
//                        bipartitionMap.get(quartet.taxa_sisters_right[1].taxa_int_name));
//			} else {
//				quartet.quartet_status = DefaultValues.DEFERRED;
//
//			}
//    		
//		}
        if (Config.MEMORY_CONSTRAINT == 1) {
        	//System.out.println("Memory is constrained");
        	customDS.initial_table1_of_list_of_quartets.list_quartets.parallelStream().forEach((quartet) -> {
        		if (quartet.taxa_sisters_left[0] == previousPassStats|| quartet.taxa_sisters_left[1] == previousPassStats ||
        			quartet.taxa_sisters_right[0] == previousPassStats || quartet.taxa_sisters_right[1] == previousPassStats) {
		        	passing_relavent_quartet_for_next_hypo_moving(quartet,
		        			chosen_taxa_for_this_pass);
				}
        	});
		} else {
			//System.out.println("Memory is not constrained");
			 List<Integer> relevantQuartetsBeforeHypoMoving = chosen_taxa_for_this_pass.relevant_quartet_indices;
		        relevantQuartetsBeforeHypoMoving.parallelStream().forEach((quartetIndex) -> {
		        	passing_relavent_quartet_for_next_hypo_moving(customDS.initial_table1_of_list_of_quartets.get(quartetIndex),
		        			chosen_taxa_for_this_pass);
		        });
		}
       
       // this.taxa_vs_hypo_swap_store.remove(previousPassStats.whichTaxaWasPassed);
       // System.out.println("one pass complete");
    }
    public void passing_relavent_quartet_for_next_hypo_moving(Quartet quartet, Taxa chosen_taxa_for_this_pass) {
    	quartet.quartet_status = TaxaUtils.findQuartetHypoStatusAfterFindingBestTaxa(
    			quartet.quartet_status, quartet.weight,
    			this.customDS.map_of_int_vs_tax_property.get(quartet.taxa_sisters_left[0]),
    			this.customDS.map_of_int_vs_tax_property.get(quartet.taxa_sisters_left[1]),
    			this.customDS.map_of_int_vs_tax_property.get(quartet.taxa_sisters_right[0]),
    			this.customDS.map_of_int_vs_tax_property.get(quartet.taxa_sisters_right[1]),
    			chosen_taxa_for_this_pass);
	}

    public void find_best_taxa_of_single_pass() {
        /*
        1.  Check if mapCandidateGainsPerListTax.size == 0 (any of the two maps) THEN all are singleton ... LOCK all taxaToMove
        2.  OTHERWISE, Use the two maps to find bestTaxaToMove [maxGain OR highestGain_with_max_num_satisfied_qrts]
        3.  LOCK the bestTaxaToMove and put corresponding stats in map
         */
        if (this.no_new_gain_calculation == true) {
//            this.lockedTaxaBooleanMap.keySet().forEach((key) -> {
//                this.lockedTaxaBooleanMap.put(key, Boolean.TRUE); //ALL LEAD TO SINGLETON BIPARTITION .... [LOCK ALL THE TAXA]
//            });
            this.customDS.map_of_int_vs_tax_property.values().forEach((taxon) -> {
            	taxon.locked = Boolean.TRUE; //ALL LEAD TO SINGLETON BIPARTITION .... [LOCK ALL THE TAXA]
            });
        }//do not add the prospective steps thing.
        else {

//            Map.Entry<Double, List<Integer>> firstKeyEntry = this.mapCandidateGainsPerListTax.entrySet().iterator().next();
//            double highest_gain_value = firstKeyEntry.getKey();
//            List<Integer> list_taxaWithHighestGainValues = firstKeyEntry.getValue();
//            int taxonWithTheHighestGainInThisPass;
//            // exactly ONE taxon has the highest gain value... choose this
//            if (list_taxaWithHighestGainValues.size() == 1) {
//                //lock this taxon and put stats values for this taxon.
//                taxonWithTheHighestGainInThisPass = list_taxaWithHighestGainValues.get(0);
//
//            } else { // MORE than one taxon with same GAIN value .. select MAX count-satisfied-quartets one
//                //create TreeMap<ns,tax> in descending order and take the first one.
//                TreeMap<Integer, Integer> treeMap = new TreeMap<>(Collections.reverseOrder());
//                for (int i = 0; i < list_taxaWithHighestGainValues.size(); i++) {
//                    int taxChecking = list_taxaWithHighestGainValues.get(i);
//                    treeMap.put(this.customDS.map_of_int_vs_tax_property
//                    		.get(taxChecking).bipartition_8_values.numSatisfied, taxChecking);
//                }
//                Map.Entry<Integer, Integer> highestNumSatTaxEntry = treeMap.entrySet().iterator().next();
//                taxonWithTheHighestGainInThisPass = highestNumSatTaxEntry.getValue();
//            }
//            System.out.println("taxonWithTheHighestGainInThisPass = "+ taxonWithTheHighestGainInThisPass);
            //this.customDS.map_of_int_vs_tax_property.values().stream().filter(x -> x.locked == Boolean.TRUE);
            //this.customDS.map_of_int_vs_tax_property.keySet().stream().max(Comparator.comparing(Taxa::getGain).thenComparing(Taxa::getnumSatisfied)).get();
            Taxa taxaWithTheHighestGainInThisPass =this.customDS.map_of_int_vs_tax_property.values().stream()
            		.filter(x -> x.participated_in_swap == Boolean.TRUE)
            		.max(Comparator.comparing(Taxa::getGain).thenComparing(Taxa::getnumSatisfied)).get();
            //int taxonWithTheHighestGainInThisPass = taxaWithTheHighestGainInThisPass.taxa_int_name;
            int taxonWithTheHighestGainInThisPass = this.customDS.map_of_int_vs_tax_property.entrySet().stream()
            		.filter(entry -> entry.getValue().equals(taxaWithTheHighestGainInThisPass)).iterator().next().getKey();
            //System.out.println("taxonWithTheHighestGainInThisPass = "+ taxonWithTheHighestGainInThisPass);
       
            //maxGainTaxaPartA = partA.stream().max(Comparator.comparing(Taxa::getVal).thenComparing(Taxa::getSat)).get();

            //lock and put stats values for this taxon in corresponding maps
            //this.lockedTaxaBooleanMap.put(taxonWithTheHighestGainInThisPass, Boolean.TRUE);
            this.customDS.map_of_int_vs_tax_property.get(taxonWithTheHighestGainInThisPass).locked = Boolean.TRUE;

//            Map<Integer, Integer> mapAfterMovement = new HashMap<>(this.bipartitionMap); //create new map to not maintain references
//
//            //reverse the bipartition for THIS taxon
//            mapAfterMovement.put(taxonWithTheHighestGainInThisPass, TaxaUtils.getOppositePartition(mapAfterMovement.get(taxonWithTheHighestGainInThisPass)));
//
//            taxaWithTheHighestGainInThisPass.map_final_bipartition = mapAfterMovement;
//            StatsPerPass statsForThisPass = new StatsPerPass(taxonWithTheHighestGainInThisPass, taxaWithTheHighestGainInThisPass.gain,
//                    this.customDS.map_of_int_vs_tax_property.get(taxonWithTheHighestGainInThisPass).bipartition_8_values, mapAfterMovement);

            this.listOfPerPassStatistics.add(taxonWithTheHighestGainInThisPass);
        }
    }

    public void run_FM_single_iteration() {
       // int pass = 0; //to print while debugging.
        boolean areAllTaxaLocked = false; //initially this condition is false.
        //Bipartition_8_values initial_bip_8 = new Bipartition_8_values();
//        System.out.println(isFirstTime);
        if (isFirstTime) {
        	//System.out.println(".............................true...................");
            customDS.initial_table1_of_list_of_quartets.list_quartets.parallelStream().forEach((quartet) -> {
            	TaxaUtils.findQuartetHypoStatus(quartet,
            			this.customDS.map_of_int_vs_tax_property.get(quartet.taxa_sisters_left[0]),
            			this.customDS.map_of_int_vs_tax_property.get(quartet.taxa_sisters_left[1]),
            			this.customDS.map_of_int_vs_tax_property.get(quartet.taxa_sisters_right[0]),
            			this.customDS.map_of_int_vs_tax_property.get(quartet.taxa_sisters_right[1]));
            	this.initialBipartition_8_values.addRespectiveValue(quartet.weight, quartet.quartet_status);
            });
		} else {
			//System.out.println(".............................false...................");
	        customDS.initial_table1_of_list_of_quartets.list_quartets.parallelStream().forEach((quartet) -> {
	        	TaxaUtils.findQuartetHypoStatus(quartet,
	        			this.customDS.map_of_int_vs_tax_property.get(quartet.taxa_sisters_left[0]),
            			this.customDS.map_of_int_vs_tax_property.get(quartet.taxa_sisters_left[1]),
            			this.customDS.map_of_int_vs_tax_property.get(quartet.taxa_sisters_right[0]),
            			this.customDS.map_of_int_vs_tax_property.get(quartet.taxa_sisters_right[1]));
	        });
		}
//		else {
//			System.out.println("hiiiiiii");
//		}

       // System.out.println("\n\nprev calculated = "+this.initialBipartition_8_values);
//        System.out.println("pres calculated = "+initial_bip_8+"\n");
//        for (Quartet quartet : customDS.initial_table1_of_list_of_quartets.list_quartets) {
//    		//Quartet quartet = customDS.initial_table1_of_list_of_quartets.get(quartetIndex);
//    		//System.out.println(quartet);
////    		quartet.quartet_status = TaxaUtils.findQuartetStatus(bipartitionMap.get(quartet.taxa_sisters_left[0].taxa_int_name),
////                    bipartitionMap.get(quartet.taxa_sisters_left[1].taxa_int_name), bipartitionMap.get(quartet.taxa_sisters_right[0].taxa_int_name), 
////                    bipartitionMap.get(quartet.taxa_sisters_right[1].taxa_int_name));
//    		TaxaUtils.findQuartetHypoStatus(quartet);
//		}//mim
        //this.mapCandidateTax_vs_8vals.clear();//mim
//        this.customDS.map_of_int_vs_tax_property.values().forEach((taxon) -> {
//        	taxon.bipartition_8_values = new Bipartition_8_values();
//        });
        this.sum_of_bipartition = 0;
        this.customDS.map_of_int_vs_tax_property.values().forEach((taxon) -> {
        	this.sum_of_bipartition += taxon.partition; 
        });
       // System.out.println("-----------------Starting Iteration------------------");
        while (areAllTaxaLocked == false) {
          //  pass++; //for debug printing....

            //Either do threaded or single-thread calculation for hypothetical gain calculation

//            if (DefaultValues.THREADED_GAIN_CALCULATION_MODE == false) { //for now it is false.
//                //run_FM_singlepass_hypothetical_swap();
//            	//run_FM_singlepass_hypothetical_swap_modified();// modified and non-threaded
//                run_FM_singlepass_hypothetical_swap_parallel();//threaded// will transfer in else block later
//            } else {
//                try {
//                    // Threaded version, not running.
//                    //run_FM_singlepass_hypothetical_swap_threaded_version();
//                	run_FM_singlepass_hypothetical_swap_parallel();//threaded//
//                } catch (ExecutionException ex) {
//                    System.out.println("-->>(L 305.) Exception while running threads in run_FM_single_iteration()");
//
//                }
//            }
            //run_FM_singlepass_hypothetical_swap_parallel();
            run_FM_singlepass_hypothetical_swap_modified();
            //run_FM_singlepass_hypothetical_swap();


            find_best_taxa_of_single_pass(); //Find the best-taxon for THIS swap

            if (listOfPerPassStatistics.isEmpty() == false) { //AT LEAST ONE per-pass val exists.

//                StatsPerPass last_pass_stat = this.listOfPerPassStatistics.get(this.listOfPerPassStatistics.size() - 1);//mim

//                if (Config.DEBUG_MODE_PRINTING_GAINS_BIPARTITIONS) {
//                    System.out.println("[FMComputer L 310]. FM-pass(box) = " + pass + " , best-taxon: "
//                            + Helper.getStringMappedName(last_pass_stat.whichTaxaWasPassed)
//                            + " , MaxGain = " + last_pass_stat.maxGainOfThisPass);
//                }


//                StatsPerPass last_pass_stat = this.listOfPerPassStatistics.get(this.listOfPerPassStatistics.size() - 1);

                changeParameterValuesForNextPass();//Change parameters to maintain consistency wrt next step/box/pass.
            }

           // areAllTaxaLocked = Helper.checkAllValuesIFSame(this.lockedTaxaBooleanMap, true); //if ALL are true, then stop.
            areAllTaxaLocked = this.customDS.map_of_int_vs_tax_property.values().stream().noneMatch((x) -> (x.locked != true)); //if at least one is different wrt val, then return false
        }
}

    public boolean changeAndCheckAfterFMSingleIteration() {
        if (this.listOfPerPassStatistics.isEmpty()) {
        	//System.out.println(" list of perpass statistics is empty . hence willIterateMore is false");
            return false; // list is empty, return the initial bipartition & stats.
        }

        //iterate over statsPerPass list...
        double max_cumulative_gain_of_current_iteration = Integer.MIN_VALUE;
        double cumulative_gain = 0; //will keep on adding with max-gain from each pass.
        int pass_index_with_max_cumulative_gain = 0; // to store the MAX cumulative gain index
        Taxa taxon_with_max_cumulative_gain = new Taxa(); // to store the taxa with the MAX cumulative gain

        for (int i = 0; i < this.listOfPerPassStatistics.size(); i++) {
        	Taxa current_taxon = this.customDS.map_of_int_vs_tax_property.get(this.listOfPerPassStatistics.get(i));
        	//double currentPassMaxGain = this.listOfPerPassStatistics.get(i).maxGainOfThisPass;
            double currentPassMaxGain = current_taxon.gain;
            cumulative_gain += currentPassMaxGain;
            //System.out.println(i+"  -> "+currentPassMaxGain+" -> "+cumulative_gain);
            if (cumulative_gain > max_cumulative_gain_of_current_iteration) {
                max_cumulative_gain_of_current_iteration = cumulative_gain; //max_cumulative_gain stores the MAX CGain
                pass_index_with_max_cumulative_gain = i; //stores the pass ... i.e. THIS snapshot
                taxon_with_max_cumulative_gain = current_taxon;
            }
        }
        //Retrieve the stat's bipartition.
//        int statOfMaxCumulativeGainBox = this.listOfPerPassStatistics.get(pass_index_with_max_cumulative_gain);


        /*        System.out.println("[FMComputer L 341] Cumulative gain (max) = " + max_cumulative_gain_of_current_iteration
                + " , for pass = " + (pass_index_with_max_cumulative_gain + 1)
                + " , Tax Passed = " + Helper.getStringMappedName(statOfMaxCumulativeGainBox.whichTaxaWasPassed)
                + " map_final_bipartition = \n"
                + Helper.getPartition(statOfMaxCumulativeGainBox.map_final_bipartition,
                        DefaultValues.LEFT_PARTITION,
                        DefaultValues.RIGHT_PARTITION,
                        InitialTable.map_of_int_vs_str_tax_list));
         */
        //Only when max-cumulative-gain is GREATER than zero, we will change, otherwise return the initial bipartition of this iteration
        if (max_cumulative_gain_of_current_iteration > Config.SMALLEPSILON) {

//        	Map<Integer, Integer> map_of_taxon_with_max_cumulative_gain = new HashMap<>(this.bipartitionMap);
//            for (int i = 0; i <= pass_index_with_max_cumulative_gain; i++) {
//            	Taxa taxon = this.customDS.map_of_int_vs_tax_property.get(this.listOfPerPassStatistics.get(i));
//            	//taxon.partition = TaxaUtils.getOppositePartition(taxon.partition);
//            	map_of_taxon_with_max_cumulative_gain.put(this.listOfPerPassStatistics.get(i), taxon.partition);
//			}
//            for (int i = pass_index_with_max_cumulative_gain+1; i < this.listOfPerPassStatistics.size(); i++) {
//            	Taxa taxon = this.customDS.map_of_int_vs_tax_property.get(this.listOfPerPassStatistics.get(i));
//            	//taxon.partition = TaxaUtils.getOppositePartition(taxon.partition);
//            	map_of_taxon_with_max_cumulative_gain.put(this.listOfPerPassStatistics.get(i), TaxaUtils.getOppositePartition(taxon.partition));
//			}

            for (int i = pass_index_with_max_cumulative_gain+1; i < this.listOfPerPassStatistics.size(); i++) {
            	Taxa taxon = this.customDS.map_of_int_vs_tax_property.get(this.listOfPerPassStatistics.get(i));
            	taxon.partition = TaxaUtils.getOppositePartition(taxon.partition);
			}//will change
            
        	// Check if this is not first time, and previous was the same as this one.
            if ((this.isFirstTime == false) && (max_cumulative_gain_of_current_iteration == this.prevCumulativeMax)) {
            	if (Helper.areEqualBipartition(this.customDS.map_of_int_vs_tax_property)) {
//					System.out.println(">>>>>>>>>>>>>>>>>>>> EQUAL PARTITION <<<<<<<<<<<<<<<<<<<<");
//					System.out.println("max_cumulative_gain_of_current_iteration = "+max_cumulative_gain_of_current_iteration+" \n"
//                			+ " but got equal bipartition. Hence  willIterateMore is false");
					return false;
				}

//                if (Helper.areEqualBipartition(map_of_taxon_with_max_cumulative_gain,
//                        this.prevMap,
//                        DefaultValues.LEFT_PARTITION,
//                        DefaultValues.RIGHT_PARTITION,
//                        DefaultValues.UNASSIGNED_PARTITION) == true) {
////                	System.out.println(map_of_taxon_with_max_cumulative_gain);
////                	System.out.println(this.prevMap);
//                	System.out.println("max_cumulative_gain_of_current_iteration = "+max_cumulative_gain_of_current_iteration+" \n"
//                			+ " but got equal bipartition. Hence  willIterateMore is false");
//                	
////                	this.customDS.map_of_int_vs_tax_property.keySet().forEach((key) -> {
////                		Taxa taxon = this.customDS.map_of_int_vs_tax_property.get(key);
////                		System.out.println(key+" >> "+taxon.partition+" >> "+taxon.prev_partition);
////                	});
//                    return false;
//                }
            }

//            System.out.println("\n\n..................................................................");
//
//            System.out.println("chosen taxa = "+ taxon_with_max_cumulative_gain.taxa_int_name);
//            System.out.println(this.bipartitionMap);
            // will check on this map [left side will contain the prev. map]
           // this.bipartitionMap = new HashMap<>(map_of_taxon_with_max_cumulative_gain);
//           
//            System.out.println(this.bipartitionMap);
//            for (int i = 0; i < this.listOfPerPassStatistics.size(); i++) {
//            	Taxa current_taxon = this.customDS.map_of_int_vs_tax_property.get(this.listOfPerPassStatistics.get(i));
//            	System.out.print(current_taxon.taxa_int_name + "="+ current_taxon.partition+"   ");
//            }
           
//            for (int i = pass_index_with_max_cumulative_gain+1; i < this.listOfPerPassStatistics.size(); i++) {
//            	Taxa taxon = this.customDS.map_of_int_vs_tax_property.get(this.listOfPerPassStatistics.get(i));
//            	taxon.partition = TaxaUtils.getOppositePartition(taxon.partition);
//			}
//            System.out.println(".....................");
//            for (int i = 0; i < this.listOfPerPassStatistics.size(); i++) {
//            	Taxa current_taxon = this.customDS.map_of_int_vs_tax_property.get(this.listOfPerPassStatistics.get(i));
//            	System.out.print(current_taxon.taxa_int_name + "="+ current_taxon.partition+"   ");
//            }
//
//            
//            System.out.println("\n..................................................................\n\n\n");

            this.initialBipartition_8_values = taxon_with_max_cumulative_gain.bipartition_8_values;
//            System.out.println("taxon_with_max_cumulative_gain = "+taxon_with_max_cumulative_gain.taxa_int_name);
//            System.out.println(" this.initialBipartition_8_values = "+this.initialBipartition_8_values);
            //this.initialBipartition_8_values = statOfMaxCumulativeGainBox._8_values_chosen_for_this_pass;
            this.listOfPerPassStatistics.clear();
            //this.mapCandidateGainsPerListTax = new TreeMap<>(Collections.reverseOrder());
            //this.mapCandidateTax_vs_8vals = new HashMap<>();

            this.customDS.map_of_int_vs_tax_property.values().forEach((tax) -> {
            
                //this.lockedTaxaBooleanMap.put(tax, Boolean.FALSE);
                tax.locked = Boolean.FALSE;
                tax.bipartition_8_values = new Bipartition_8_values();
                tax._8_vals_THIS_TAX_before_hypo_swap = new Bipartition_8_values();
                tax._8_vals_THIS_TAX_AFTER_hypo_swap = new Bipartition_8_values();
                tax.prev_partition = tax.partition;
                
            });
            //System.out.println("------------------------------------------new Iteration -------------------------------------------");

            // change prev to this cumulative max
            this.prevCumulativeMax = max_cumulative_gain_of_current_iteration;
            //this.prevMap = new HashMap<>(this.bipartitionMap);//change mim
//            System.out.println("max_cumulative_gain_of_current_iteration = "+max_cumulative_gain_of_current_iteration+"   willIterateMore is true");
            
            
            return true;
        }

        /*        System.out.println("L 376. level = " + this.level + " , max_cumulative_gain_of_current_iteration = "
                + max_cumulative_gain_of_current_iteration + ", this.bipartitionMap = \n"
                + Helper.getPartition(bipartitionMap, DefaultValues.LEFT_PARTITION, DefaultValues.RIGHT_PARTITION, InitialTable.map_of_int_vs_str_tax_list)
                + " , small_epsilon = " + Config.SMALLEPSILON + " , return false.");
         */
        //System.out.println("max_cumulative_gain_of_current_iteration = "+max_cumulative_gain_of_current_iteration+"   willIterateMore is false");
        return false;
    }

    //Whole FM ALGORITHM
    public FMResultObject run_FM_Algorithm_Whole() {
        //Map<Integer, Integer> map_previous_iteration;//= new HashMap<>();
        this.customDS.map_of_int_vs_tax_property.values().forEach((taxon) -> {
        	taxon.prev_partition = taxon.partition;
        });
        boolean willIterateMore;
        int iterationsFM = 0; //can have stopping criterion for 10k iterations ?

        while (true) { //stopping condition
            if (iterationsFM > Config.MAX_ITERATIONS_LIMIT) { //another stopping criterion.
                System.out.println("[FMComputer L409.] Thread (" + Thread.currentThread().getName()
                        + ", " + Thread.currentThread().getId() + ") MAX_ITERATIONS_LIMIT = "
                        + Config.MAX_ITERATIONS_LIMIT + " is reached for level = " + this.level);
                break;
            }
            iterationsFM++;
            //System.out.println("---------------- LEVEL " + level + ", Iteration " + iterationsFM + " ----------------");
            //map_previous_iteration = new HashMap<>(this.bipartitionMap); // always store this
//            System.out.println(map_previous_iteration);
//            System.out.print("{");
//            this.customDS.map_of_int_vs_tax_property.keySet().forEach((key) -> {
//            	Taxa taxon = this.customDS.map_of_int_vs_tax_property.get(key);
//            	System.out.print(key+"="+taxon.prev_partition+", ");
//            });
//            System.out.print("}\n");
            run_FM_single_iteration();

            willIterateMore = changeAndCheckAfterFMSingleIteration();
            if (willIterateMore == false) {
                //this.bipartitionMap = map_previous_iteration; // just change as previous map
                break;
            }
//
//            if (willIterateMore == false) {
//            	//System.out.println("will iterate more is false");
//                break;
//            }
            this.isFirstTime = false;
        }

        // Create results and return
        FMResultObject object = new FMResultObject(this.customDS, this.level); //pass the parent's customDS as reference
        object.createFMResultObjects(); //pass THIS level's final-bipartition to get P_left,Q_left,P_right,Q_right //changed it 
        return object;
    }
	//////////////////////////ADDED BY MIM \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	    
    public void run_FM_singlepass_hypothetical_swap_parallel() {
    	//parallel version of mim1_run_FM_singlepass_hypothetical_swap()
//    	System.out.println("------------before swap bipartition map -------------\n"+this.bipartitionMap);
    	this.no_new_gain_calculation = Boolean.TRUE;
    	//int sum_of_bipartition = Helper.sumMapValuesInteger(this.bipartitionMap);
    	//int len_of_bipartition = this.customDS.map_of_int_vs_tax_property.keySet().size();
//    	this.customDS.map_of_int_vs_tax_property.keySet().parallelStream().forEach(taxToConsider -> {
//    		per_taxa_singlepass_hypothetical_swap(taxToConsider, this.customDS.map_of_int_vs_tax_property.keySet().size());
//    	});
    	this.customDS.map_of_int_vs_tax_property.values().parallelStream().forEach(taxToConsider -> {
    		per_taxa_singlepass_hypothetical_swap(taxToConsider, this.customDS.map_of_int_vs_tax_property.keySet().size());
    	});
//    	System.out.println("------------After swap bipartition map -------------\n"+this.bipartitionMap);
    	
    }
    public void per_taxa_singlepass_hypothetical_swap(Taxa taxonToConsider, int len_of_bipartition) {

		//Taxa taxonToConsider  = this.customDS.map_of_int_vs_tax_property.get(taxToConsider);
		taxonToConsider.participated_in_swap = false;
    	if (taxonToConsider.locked == false) { // this is a free taxon, hypothetically test it ....
			//Map<Integer, Integer> quartetIDvsStatusAfterHypoSwap = new HashMap<Integer, Integer>();//mim
			//System.out.println("Line 65. Inside runFMSinglePassHypoSwap() .. taxaToConsider = " + taxToConsider);
           // int taxPartValBeforeHypoSwap = this.bipartitionMap.get(taxToConsider);
            //First check IF moving this will lead to a singleton bipartition ....
            //Map<Integer, Integer> newMap = new HashMap<>(this.bipartitionMap);
            //System.out.println("=====================newMap Before hypo swap====================");
            //System.out.println(newMap);
           // newMap.put(taxToConsider, TaxaUtils.getOppositePartition(taxPartValBeforeHypoSwap)); //hypothetically make the swap.
            //System.out.println("=====================newMap after hypo swap====================");
           // System.out.println(newMap);
            if (TaxaUtils.isThisSingletonBipartition(len_of_bipartition, this.sum_of_bipartition, taxonToConsider.partition) == false) {
            	if (this.no_new_gain_calculation) {
					synchronized (this.no_new_gain_calculation) {
						this.no_new_gain_calculation = Boolean.FALSE;
					}
				}
            	taxonToConsider.participated_in_swap = true;
            	//List<Integer> relevantQuartetsBeforeHypoMoving = taxonToConsider.relevant_quartet_indices;
                //System.out.println("Relavent Quartet indices : "+relevantQuartetsBeforeHypoMoving);
//                Bipartition_8_values _8_vals_THIS_TAX_before_hypo_swap = new Bipartition_8_values(); // all initialized to 0
//                Bipartition_8_values _8_vals_THIS_TAX_AFTER_hypo_swap = new Bipartition_8_values(); // all initialized to 0

               // List<Integer> deferredQuartetsBeforeHypoMoving = new ArrayList<>(); //keep deferred quartets for later checking ...
                //For each quartet, find status, compute previous-hypothetical-swap-values, and using short-cuts (excluding deferred), 
                //compute after-hypothetical-swap-values
                
//                for (int itr = 0; itr < relevantQuartetsBeforeHypoMoving.size(); itr++) {
//                	int idx_relevant_qrt = relevantQuartetsBeforeHypoMoving.get(itr);
//                    //No need explicit checking as customDS will be changed after every level
//                    Quartet quartet = customDS.initial_table1_of_list_of_quartets.get(idx_relevant_qrt);
//                    int status_quartet_before_hyp_swap = quartet.quartet_status;
//                    int status_quartet_after_hyp_swap = DefaultValues.DEFERRED;// = Utils.findQuartetStatusUsingShortcut(status_quartet_before_hyp_swap); //_8values include ns, nv, nd, nb, ws, wv, wd, wb
////                    _8_vals_THIS_TAX_before_hypo_swap.addRespectiveValue(quartet.weight, status_quartet_before_hyp_swap); //_8values include ns, nv, nd, nb, ws, wv, wd, wb
////                    _8_vals_THIS_TAX_AFTER_hypo_swap.addRespectiveValue(quartet.weight, status_quartet_after_hyp_swap); //If status.UNKNOWN, then don't add anything.
//                    if (status_quartet_before_hyp_swap == DefaultValues.DEFERRED) {
//                        //deferredQuartetsBeforeHypoMoving.add(idx_relevant_qrt);
//                        status_quartet_after_hyp_swap = TaxaUtils.findQuartetStatus(newMap.get(quartet.taxa_sisters_left[0].taxa_int_name),
//                                newMap.get(quartet.taxa_sisters_left[1].taxa_int_name), newMap.get(quartet.taxa_sisters_right[0].taxa_int_name), newMap.get(quartet.taxa_sisters_right[1].taxa_int_name));
//                        
//                    }
////                    else {
////                    	status_quartet_after_hyp_swap = DefaultValues.DEFERRED;
////    				}//change 
//                    _8_vals_THIS_TAX_before_hypo_swap.addRespectiveValue(quartet.weight, status_quartet_before_hyp_swap); //_8values include ns, nv, nd, nb, ws, wv, wd, wb
//                    _8_vals_THIS_TAX_AFTER_hypo_swap.addRespectiveValue(quartet.weight, status_quartet_after_hyp_swap); //If status.UNKNOWN, then don't add anything.
//                    //quartetIDvsStatusAfterHypoSwap.put(idx_relevant_qrt, status_quartet_after_hyp_swap);//mim
//            
//                }// end for [relevant-quartets-iteration]
                
                
            	Bipartition_8_values _8_vals_THIS_TAX_before_hypo_swap = new Bipartition_8_values(taxonToConsider._8_vals_THIS_TAX_before_hypo_swap); // all initialized to 0
            	Bipartition_8_values _8_vals_THIS_TAX_AFTER_hypo_swap = new Bipartition_8_values(taxonToConsider._8_vals_THIS_TAX_AFTER_hypo_swap); // all initialized to 0

                double ps_before_reduced = WeightedPartitionScores.calculatePartitionScoreReduced(_8_vals_THIS_TAX_before_hypo_swap);
                double ps_after_reduced = WeightedPartitionScores.calculatePartitionScoreReduced(_8_vals_THIS_TAX_AFTER_hypo_swap);
                double gainOfThisTax = ps_after_reduced - ps_before_reduced; //correct calculation
                
                Bipartition_8_values _8_values_whole_considering_thisTax_swap = new Bipartition_8_values();
                /*AfterHypoSwap.Whole_8Vals - BeforeHypoSwap.Whole_8Vals = AfterHypoSwap.OneTax.8Vals - BeforeHypoSwap.OneTax.8vals 
                 * //vector rules of distance addition*/
                //So, AfterHypoSwap.Whole_8Vals = BeforeHypoSwap.Whole_8Vals + AfterHypoSwap.OneTax.8Vals - BeforeHypoSwap.OneTax.8vals
                //_8_values_whole_considering_thisTax_swap.addObject(this.initialBipartition_8_values);
                _8_values_whole_considering_thisTax_swap.addObject(_8_vals_THIS_TAX_AFTER_hypo_swap);
                _8_values_whole_considering_thisTax_swap.subtractObject(_8_vals_THIS_TAX_before_hypo_swap);
               // this.mapCandidateTax_vs_8vals.put(taxToConsider, _8_values_whole_considering_thisTax_swap);
               // System.out.println("Before mapCandidateTax_vs_8vals : "+mapCandidateTax_vs_8vals);
                //Bipartition_8_values _8_values_whole_considering_thisTax_swap1 = new Bipartition_8_values();
                _8_values_whole_considering_thisTax_swap.addObject(this.initialBipartition_8_values);
                //System.out.println("After mapCandidateTax_vs_8vals : "+mapCandidateTax_vs_8vals);
                
                //synchronized (this.mapCandidateGainsPerListTax) {
//            	if (this.mapCandidateGainsPerListTax.containsKey(gainOfThisTax) == false) { // this gain was not contained
//                    //initialize the taxon(for this gain-val) list.
//            		 synchronized (this.mapCandidateGainsPerListTax) {
//            			 this.mapCandidateGainsPerListTax.put(gainOfThisTax, new ArrayList<>());
//            		 }
//                }//else: simply append to the list.
//                synchronized (this.mapCandidateGainsPerListTax) {
//                    this.mapCandidateGainsPerListTax.get(gainOfThisTax).add(taxToConsider); //add gain to map
//				}
//                synchronized (this.mapCandidateTax_vs_8vals) {
//                	this.mapCandidateTax_vs_8vals.put(taxToConsider, _8_values_whole_considering_thisTax_swap);
//				}
                taxonToConsider.gain = gainOfThisTax;
                taxonToConsider.bipartition_8_values = _8_values_whole_considering_thisTax_swap;
                
                //System.out.println("Taxa : "+taxToConsider);
                //System.out.println("_8_values_:"+_8_values_whole_considering_thisTax_swap);
            } //ELSE: DOESN'T lead to singleton bipartition [add to map, and other datastructures]
            //Calculate hypothetical Gain ... [using discussed short-cut]
            
            
		}
	
    	
    }
    
    public void run_FM_singlepass_hypothetical_swap_modified() {//per pass or step [per num taxa of steps].
        //Test hypothetically ...
    	this.no_new_gain_calculation = Boolean.TRUE;
    	int len_of_bipartition = this.customDS.map_of_int_vs_tax_property.keySet().size();
    	this.customDS.map_of_int_vs_tax_property.values().forEach(taxToConsider -> {
    		per_taxa_singlepass_hypothetical_swap(taxToConsider, len_of_bipartition);
    	});
//        for (int taxToConsider : this.customDS.map_of_int_vs_tax_property.keySet()) {
//        	Taxa taxonToConsider = this.customDS.map_of_int_vs_tax_property.get(taxToConsider);
//        	taxonToConsider.participated_in_swap = false;
//    		if (taxonToConsider.locked == false) { // this is a free taxon, hypothetically test it ....
//    			//Map<Integer, Integer> quartetIDvsStatusAfterHypoSwap = new HashMap<Integer, Integer>();//mim
//    			//System.out.println("Line 65. Inside runFMSinglePassHypoSwap() .. taxaToConsider = " + taxToConsider);
//                //int taxPartValBeforeHypoSwap = this.bipartitionMap.get(taxToConsider);
//                //First check IF moving this will lead to a singleton bipartition ....
//                //Map<Integer, Integer> newMap = new HashMap<>(this.bipartitionMap);
//                //System.out.println("=====================newMap Before hypo swap====================");
//                //System.out.println(newMap);
//                //newMap.put(taxToConsider, TaxaUtils.getOppositePartition(taxPartValBeforeHypoSwap)); //hypothetically make the swap.
//                //System.out.println("=====================newMap after hypo swap====================");
//               // System.out.println(newMap);
//                if (TaxaUtils.isThisSingletonBipartition(len_of_bipartition, this.sum_of_bipartition, taxonToConsider.partition) == true) {
//                    //THIS hypothetical movement of taxToConsider leads to singleton bipartition so, continue ...
//                    continue;
//                } //ELSE: DOESN'T lead to singleton bipartition [add to map, and other datastructures]
//                //Calculate hypothetical Gain ... [using discussed short-cut]
//            	if (this.no_new_gain_calculation) {
//				
//            		this.no_new_gain_calculation = Boolean.FALSE;
//
//				}
//            	taxonToConsider.participated_in_swap = true;
//                //List<Integer> relevantQuartetsBeforeHypoMoving = taxonToConsider.relevant_quartet_indices;
//                //System.out.println("Relavent Quartet indices : "+relevantQuartetsBeforeHypoMoving);
////                Bipartition_8_values _8_vals_THIS_TAX_before_hypo_swap = new Bipartition_8_values(); // all initialized to 0
////                Bipartition_8_values _8_vals_THIS_TAX_AFTER_hypo_swap = new Bipartition_8_values(); // all initialized to 0
//                
////                Bipartition_8_values _8_vals_THIS_TAX_before_hypo_swap1 = new Bipartition_8_values(); // all initialized to 0
////                Bipartition_8_values _8_vals_THIS_TAX_AFTER_hypo_swap1 = new Bipartition_8_values(); // all initialized to 0
//                
//                
//                
////                for (int itr = 0; itr < relevantQuartetsBeforeHypoMoving.size(); itr++) {
////                	int idx_relevant_qrt = relevantQuartetsBeforeHypoMoving.get(itr);
////                    //No need explicit checking as customDS will be changed after every level
////                    Quartet quartet = customDS.initial_table1_of_list_of_quartets.get(idx_relevant_qrt);
////                    int status_quartet_before_hyp_swap = quartet.quartet_status;
////                    int status_quartet_after_hyp_swap = DefaultValues.DEFERRED;// = Utils.findQuartetStatusUsingShortcut(status_quartet_before_hyp_swap); //_8values include ns, nv, nd, nb, ws, wv, wd, wb
////                    if (status_quartet_before_hyp_swap == DefaultValues.DEFERRED) {
////                        //deferredQuartetsBeforeHypoMoving.add(idx_relevant_qrt);
////                        status_quartet_after_hyp_swap = TaxaUtils.findQuartetStatus(newMap.get(quartet.taxa_sisters_left[0].taxa_int_name),
////	                            newMap.get(quartet.taxa_sisters_left[1].taxa_int_name), newMap.get(quartet.taxa_sisters_right[0].taxa_int_name), newMap.get(quartet.taxa_sisters_right[1].taxa_int_name));
////	                    
////                    }
//////                    else {
//////                    	status_quartet_after_hyp_swap = DefaultValues.DEFERRED;
//////					}
////                    _8_vals_THIS_TAX_before_hypo_swap1.addRespectiveValue(quartet.weight, status_quartet_before_hyp_swap); //_8values include ns, nv, nd, nb, ws, wv, wd, wb
////                    _8_vals_THIS_TAX_AFTER_hypo_swap1.addRespectiveValue(quartet.weight, status_quartet_after_hyp_swap); //If status.UNKNOWN, then don't add anything.
////                    //quartetIDvsStatusAfterHypoSwap.put(idx_relevant_qrt, status_quartet_after_hyp_swap);//mim
////            
////                }// end for [relevant-quartets-iteration]
//                
//                
//                
//                
////               System.out.println("\n initialBipartition_8_values= "+ this.initialBipartition_8_values);
////               System.out.println("_8_vals_THIS_TAX_before_hypo_swap1 = "+_8_vals_THIS_TAX_before_hypo_swap1);
////               System.out.println("taxa before ======================= "+ taxonToConsider._8_vals_THIS_TAX_before_hypo_swap+"\n");
////               System.out.println("_8_vals_THIS_TAX_AFTER_hypo_swap1 = "+_8_vals_THIS_TAX_AFTER_hypo_swap1);
////               System.out.println("taxa before ======================= "+ taxonToConsider._8_vals_THIS_TAX_AFTER_hypo_swap+"\n\n");
//                
//                Bipartition_8_values _8_vals_THIS_TAX_before_hypo_swap = new Bipartition_8_values(taxonToConsider._8_vals_THIS_TAX_before_hypo_swap);
//                Bipartition_8_values _8_vals_THIS_TAX_AFTER_hypo_swap = new Bipartition_8_values(taxonToConsider._8_vals_THIS_TAX_AFTER_hypo_swap);
//
//                double ps_before_reduced = WeightedPartitionScores.calculatePartitionScoreReduced(_8_vals_THIS_TAX_before_hypo_swap);
//                double ps_after_reduced = WeightedPartitionScores.calculatePartitionScoreReduced(_8_vals_THIS_TAX_AFTER_hypo_swap);
//                double gainOfThisTax = ps_after_reduced - ps_before_reduced; //correct calculation
//                
//                Bipartition_8_values _8_values_whole_considering_thisTax_swap = new Bipartition_8_values();
//                /*AfterHypoSwap.Whole_8Vals - BeforeHypoSwap.Whole_8Vals = AfterHypoSwap.OneTax.8Vals - BeforeHypoSwap.OneTax.8vals 
//                 * //vector rules of distance addition*/
//                //So, AfterHypoSwap.Whole_8Vals = BeforeHypoSwap.Whole_8Vals + AfterHypoSwap.OneTax.8Vals - BeforeHypoSwap.OneTax.8vals
//                //_8_values_whole_considering_thisTax_swap.addObject(this.initialBipartition_8_values);
//                _8_values_whole_considering_thisTax_swap.addObject(_8_vals_THIS_TAX_AFTER_hypo_swap);
//                _8_values_whole_considering_thisTax_swap.subtractObject(_8_vals_THIS_TAX_before_hypo_swap);
//                //this.mapCandidateTax_vs_8vals.put(taxToConsider, _8_values_whole_considering_thisTax_swap);
//
//               // System.out.println("Before mapCandidateTax_vs_8vals : "+mapCandidateTax_vs_8vals);
//                //Bipartition_8_values _8_values_whole_considering_thisTax_swap1 = new Bipartition_8_values();
//                _8_values_whole_considering_thisTax_swap.addObject(this.initialBipartition_8_values);
//                //System.out.println("After mapCandidateTax_vs_8vals : "+mapCandidateTax_vs_8vals);
//                taxonToConsider.gain = gainOfThisTax;
//                taxonToConsider.bipartition_8_values = _8_values_whole_considering_thisTax_swap;
//                
//                
////                System.out.println("\n again \n initialBipartition_8_values= "+ this.initialBipartition_8_values);
////                System.out.println("_8_vals_THIS_TAX_before_hypo_swap = "+_8_vals_THIS_TAX_before_hypo_swap);
////                System.out.println("taxa before ======================= "+ taxonToConsider._8_vals_THIS_TAX_before_hypo_swap+"\n");
////                System.out.println("_8_vals_THIS_TAX_AFTER_hypo_swap = "+_8_vals_THIS_TAX_AFTER_hypo_swap);
////                System.out.println("taxa after ======================= "+ taxonToConsider._8_vals_THIS_TAX_AFTER_hypo_swap+"\n\n");
//                
//    		}
//    	}//end outer for

    }

	
}