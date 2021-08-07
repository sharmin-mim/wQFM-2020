package wqfm.ds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mahim
 */
//Only store the List<Quartets> initially. [will be modified by individual threads/classes/objects]
public class InitialTable { //STORED separately to have the synchronized add function

    public static Map<String, Integer> map_of_str_vs_int_tax_list = new HashMap<>(); //for forward checking
   // public static Map<Integer, String> map_of_int_vs_str_tax_list = new HashMap<>(); //for back conversion
    //public static String[] array_of_int_vs_str_tax_list; //for back conversion (memory efficient)//mim
    public static int TAXA_COUNTER = 0;  //mim note: at the time of parallel execution of left and right partition, this static field will create problem.
    
    public List<Quartet> list_quartets;
    
    public static Map<Integer, Taxa> initial_map_of_int_vs_tax_property = new LinkedHashMap<>();

    public InitialTable(boolean flag) {
        // do not initialize. [to pass as reference]

    }

    public InitialTable() {
        this.list_quartets = new ArrayList<>();
    }

    public List<Quartet> get_QuartetList() {
        return list_quartets;
    }
    public void sortQuartetListBasedOnWeight() {
    	this.list_quartets.sort(Comparator.comparing(Quartet::getWeight, Collections.reverseOrder()));
	}

    @Override
    public String toString() {
        return "InitialTable{" + "list_quartets=" + list_quartets + '}';
    }

    public Quartet get(int idx) {
        return list_quartets.get(idx);
    }

    public int sizeTable() {
        return list_quartets.size();
    }

    public void addToListOfQuartets(Quartet q) {
        this.list_quartets.add(q);
    }

    public void printQuartetList() {
        for (int i = 0; i < this.list_quartets.size(); i++) {
            System.out.println(i + ":-> " + this.list_quartets.get(i).toString());
        }
    }

    public void assignByReference(InitialTable initialTable) {
        this.list_quartets = initialTable.list_quartets; //assign by reference
    }

}
