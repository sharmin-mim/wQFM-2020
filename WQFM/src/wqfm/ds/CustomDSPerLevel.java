package wqfm.ds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import wqfm.bip.WeightedPartitionScores;

/**
 *
 * @author mahim
 */
public class CustomDSPerLevel {

    public InitialTable initial_table1_of_list_of_quartets; //immutable [doesn't change, only as reference, is passed]

    public int level;


    //Will mutate per level
    //public List<Integer> quartet_indices_list_unsorted;
    //public Map<Integer, List<Integer>> map_taxa_relevant_quartet_indices; //releveant quartets map, key: taxa & val:list<indices>
    //public Map<Double, List<Integer>> sorted_quartets_weight_list_indices_map;
    
    public Map<Integer, Taxa> map_of_int_vs_tax_property;
    
    public double ALPHA_PARTITION_SCORE;// = WeightedPartitionScores.ALPHA_PARTITION_SCORE;//this variable is added for paralllizing fmobject call in fmrunner
    public double BETA_PARTITION_SCORE;// = WeightedPartitionScores.BETA_PARTITION_SCORE;//this variable is added for paralllizing fmobject call in fmrunner

    //public List<Integer> taxa_list_int;

    public void setInitialTableReference(InitialTable initTable) {
        this.initial_table1_of_list_of_quartets = initTable;
    }

    public CustomDSPerLevel() {
        //this.quartet_indices_list_unsorted = new ArrayList<>();
        //this.map_taxa_relevant_quartet_indices = new LinkedHashMap<>();//mim . made it LinkedHashMap instead of HashMap
        //this.sorted_quartets_weight_list_indices_map = new TreeMap<>(Collections.reverseOrder());
       // this.taxa_list_int = new ArrayList<>();
        //this.initial_table1_of_list_of_quartets = new InitialTable();
    	this(WeightedPartitionScores.ALPHA_PARTITION_SCORE, WeightedPartitionScores.BETA_PARTITION_SCORE);
//        this.map_of_int_vs_tax_property = new LinkedHashMap<>();
    }
    public CustomDSPerLevel(double alpha, double beta) {
        this.ALPHA_PARTITION_SCORE = alpha;
        this.BETA_PARTITION_SCORE = beta;
        this.map_of_int_vs_tax_property = new LinkedHashMap<>();
    }
    public void printTable1() {
        System.out.println("----------- Table1 [SINGLE list of quartets indices] ------------------");
        
        for (Quartet quartet : this.initial_table1_of_list_of_quartets.list_quartets) {
			System.out.println(quartet.toString());
		}

//        System.out.println(this.quartet_indices_list_unsorted.stream()
//                .map(x -> this.initial_table1_of_list_of_quartets.get(x))
//                .map(x -> String.valueOf(x))
//                .collect(Collectors.joining("\n")));

    }

    private void printMap_RelevantQuartetsIndicesPerTaxa() {
        System.out.println("----------- Printing Map <Taxa,RelevantQrtIndex> ---------");
        map_of_int_vs_tax_property.keySet().stream().map((key_taxa) -> {
            List<Integer> list_relevant_qrts_indices = map_of_int_vs_tax_property.get(key_taxa).relevant_quartet_indices;
            System.out.print("Taxa:<" + key_taxa + ">: Length: " + list_relevant_qrts_indices.size() + "  ==>> ");
            return list_relevant_qrts_indices;
        }).map((list_relevant_qrts_indices) -> {
            for (int i = 0; i < list_relevant_qrts_indices.size(); i++) {
                System.out.print(list_relevant_qrts_indices.get(i) + ",");
            }
            return list_relevant_qrts_indices;
        }).forEachOrdered((_item) -> {
            System.out.println("");
        });
    }

    private void printMap_RelevantQuartetsPeraTaxa() {
        System.out.println("----------- Printing Map <Taxa,RelevantQrts> ---------");
        for (int key_taxa : map_of_int_vs_tax_property.keySet()) {
            List<Integer> list_relevant_qrts_indices = map_of_int_vs_tax_property.get(key_taxa).relevant_quartet_indices;
            System.out.print("Taxa:<" + key_taxa + ">: Length: " + list_relevant_qrts_indices.size() + "  ==>> ");
            for (int i = 0; i < list_relevant_qrts_indices.size(); i++) {
                System.out.print(this.initial_table1_of_list_of_quartets.get(list_relevant_qrts_indices.get(i)) + ",");
            }
            System.out.println("");
        }

    }

    public void printCustomDS() {
        this.initial_table1_of_list_of_quartets.printQuartetList();
        printMap_RelevantQuartetsIndicesPerTaxa();
        //System.out.println(this.sorted_quartets_weight_list_indices_map);
    }

//    public void sortQuartetIndicesMap() {
//        for (int i = 0; i < this.quartet_indices_list_unsorted.size(); i++) {
//            int qrt_index = this.quartet_indices_list_unsorted.get(i);
//            Quartet q = this.initial_table1_of_list_of_quartets.get(qrt_index);
//            if (this.sorted_quartets_weight_list_indices_map.containsKey(q.weight) == false) { //initialize the list [this weight doesn't exist]
//                this.sorted_quartets_weight_list_indices_map.put(q.weight, new ArrayList<>());
//            }
//            this.sorted_quartets_weight_list_indices_map.get(q.weight).add(qrt_index); //append to list in treeMap
//        }
//    }

    public void fillRelevantQuartetsMap() {
    	int size_of_quartet_table = this.initial_table1_of_list_of_quartets.sizeTable();
    	this.map_of_int_vs_tax_property.values().forEach((taxon) -> {
    		taxon.relevant_quartet_indices = new ArrayList<Integer>();
    	});
        for (int index_qrt = 0; index_qrt < size_of_quartet_table; index_qrt++) {
            Quartet q = this.initial_table1_of_list_of_quartets.get(index_qrt);

            for (int i = 0; i < Quartet.NUM_TAXA_PER_PARTITION; i++) { // Do for left-sisters ... push to map THIS quartet's row,col
                //int taxon = q.taxa_sisters_left[i];
//                if (this.map_taxa_relevant_quartet_indices.containsKey(taxon) == false) { //map doesn't have an entry yet for THIS taxon
//                    this.map_taxa_relevant_quartet_indices.put(taxon, new ArrayList<>()); // initialize for THIS taxon
//                }
                this.map_of_int_vs_tax_property.get(q.taxa_sisters_left[i]).relevant_quartet_indices.add(index_qrt);
            }
            for (int i = 0; i < Quartet.NUM_TAXA_PER_PARTITION; i++) { // Repeat the same for right-sisters
               // int taxon = q.taxa_sisters_right[i];
//                if (this.map_taxa_relevant_quartet_indices.containsKey(taxon) == false) { //map doesn't have an entry yet for THIS taxon
//                    this.map_taxa_relevant_quartet_indices.put(taxon, new ArrayList<>()); // initialize for THIS taxon
//                }
            	this.map_of_int_vs_tax_property.get(q.taxa_sisters_right[i]).relevant_quartet_indices.add(index_qrt);
            }
        }
    }

//    public void fillUpTaxaList(int tAXA_COUNTER) {
//    	 System.out.println("map_taxa_relevant_quartet_indices sixe = "+this.map_taxa_relevant_quartet_indices.size());
//    	for (int i = 0; i < tAXA_COUNTER; i++) {
//    		this.map_taxa_relevant_quartet_indices.put(i, new ArrayList<>());
//		}
//        System.out.println("map_taxa_relevant_quartet_indices sixe = "+this.map_taxa_relevant_quartet_indices.size());
//    }

//    public String onlyQuartetIndices() {
//        String s = "";
//        s = this.quartet_indices_list_unsorted
//                .stream()
//                .map((qrtIndex) -> (String.valueOf(qrtIndex) + ", "))
//                .reduce(s, String::concat);
//        return s;
//    }

    public void printSortedQuartetsTable() {

        /*for (double weight : this.sorted_quartets_weight_list_indices_map.keySet()) {
            List<Integer> quartet_indices = this.sorted_quartets_weight_list_indices_map.get(weight);
            for(int qrt_idx: quartet_indices){
                System.out.println(this.initial_table1_of_list_of_quartets.get(qrt_idx));
            }
        }*/
//    	for (Quartet quartet : this.initial_table1_of_list_of_quartets.get_QuartetList()) {
//			System.out.println(quartet.toString());
//		}
    	this.initial_table1_of_list_of_quartets.get_QuartetList().stream().forEach(quartet -> {System.out.println(quartet.toString());});
//        this.sorted_quartets_weight_list_indices_map.keySet()
//                .stream()
//                .map(weight -> this.sorted_quartets_weight_list_indices_map.get(weight))
//                .forEach(list_quartet_indices -> {
//                    System.out.println(
//                            list_quartet_indices.stream()
//                                    .map(qrt_idx -> this.initial_table1_of_list_of_quartets.get(qrt_idx))
//                                    .map(quartet -> quartet.getNamedQuartet())
//                                    .collect(Collectors.joining("\n"))
//                    );
//                });

    }

}
