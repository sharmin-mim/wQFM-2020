package wqfm.ds;

import java.util.Arrays;
import java.util.Comparator;

import wqfm.utils.Helper;

/**
 *
 * @author mahim
 */
public class Quartet {

    public static int NUM_TAXA_PER_PARTITION = 2;
    public static int TEMP_TAX_TO_SWAP;

    public Taxa[] taxa_sisters_left;// = new String[NUM_TAXA_PER_PARTITION];
    public Taxa[] taxa_sisters_right;// = new String[NUM_TAXA_PER_PARTITION];
    public double weight;
    public int quartet_status;//mim

    public Quartet() {
        this.weight = 1.0;
    }


    public String getNamedQuartet() {
        StringBuilder builder = new StringBuilder();
        builder.append("((");
        builder.append(Helper.getStringMappedName(this.taxa_sisters_left[0].taxa_int_name));
        builder.append(",");
        builder.append(Helper.getStringMappedName(this.taxa_sisters_left[1].taxa_int_name));
        builder.append("),(");
        builder.append(Helper.getStringMappedName(this.taxa_sisters_right[0].taxa_int_name));
        builder.append(",");
        builder.append(Helper.getStringMappedName(this.taxa_sisters_right[1].taxa_int_name));
        builder.append(")); ");
        builder.append(Double.toString(this.weight));
        return builder.toString();
    }

    /*
        Keep a,b|c,d as the quartet.
        Keep the minimum of (a,b) in taxa_sisters_left i.e. taxa_sisters_left[0] = min, taxa_sisters_left[1] = max
        Same with (c,d) for taxa_sisters_right.
        FOR NOW, NOT DOING ABOVE THING
     */
    public final void initialiseQuartet(Taxa a, Taxa b, Taxa c, Taxa d, double w) {
        //sorting.

        this.taxa_sisters_left = new Taxa[NUM_TAXA_PER_PARTITION];
        this.taxa_sisters_right = new Taxa[NUM_TAXA_PER_PARTITION];

        this.taxa_sisters_left[0] = a;
        this.taxa_sisters_left[1] = b;
        this.taxa_sisters_right[0] = c;
        this.taxa_sisters_right[1] = d;
        this.weight = w;

        this.sort_quartet_taxa_names();

    }

    public Quartet(Quartet q) {
        initialiseQuartet(q.taxa_sisters_left[0], q.taxa_sisters_left[1],
                q.taxa_sisters_right[0], q.taxa_sisters_right[1], q.weight);
    }

    public Quartet(Taxa a, Taxa b, Taxa c, Taxa d, double w) {
        initialiseQuartet(a, b, c, d, w);
    }

    public Quartet(String s) {
        //ADDITIONALLY append to the map and reverse map.
//    	System.out.println(s);
//    	s = s.replace(" ", "");
//    	String[] qq = s.split("\\(\\(|,|\\),\\(|\\)\\);");
//    	for (String string : qq) {
//			System.out.println(string);
//		}
//    	System.out.println("............ "+qq.length);
        s = s.replace(" ", "");
        s = s.replace(";", ",");
        s = s.replace("(", "");
        s = s.replace(")", ""); // Finally end up with A,B,C,D,41.0
        String[] arr = s.split(",");
                
        //mim
        int[] a = new int[4];
        Taxa[] taxon = new Taxa[4];
        for (int i = 0; i < 4; i++) {
            if (InitialTable.map_of_str_vs_int_tax_list.containsKey(arr[i]) == true) {
                a[i] = InitialTable.map_of_str_vs_int_tax_list.get(arr[i]);
                taxon[i] = InitialTable.initial_map_of_int_vs_tax_property.get(a[i]);
            } else { //THIS taxon doesn't exist.
                a[i] = InitialTable.TAXA_COUNTER;
                InitialTable.TAXA_COUNTER++;
                InitialTable.map_of_str_vs_int_tax_list.put(arr[i], a[i]);
                taxon[i] = new Taxa(arr[i], a[i]);
                InitialTable.initial_map_of_int_vs_tax_property.put(a[i], taxon[i]);
               // InitialTable.map_of_int_vs_str_tax_list.put(a[i], arr[i]);
            }
		}
        initialiseQuartet(taxon[0], taxon[1], taxon[2], taxon[3], Double.parseDouble(arr[4]));
        //

//        initialiseQuartet(arr[0], arr[1], arr[2], arr[3], Double.parseDouble(arr[4]));
    }

    
    public double getWeight() {
		return weight;
	}
    
    


	@Override
    public String toString() {
        String s = "((" + this.taxa_sisters_left[0].taxa_int_name + "," + this.taxa_sisters_left[1].taxa_int_name 
        		+ "),(" + this.taxa_sisters_right[0].taxa_int_name + "," + this.taxa_sisters_right[1].taxa_int_name
        		+ ")); " + String.valueOf(this.weight);
        return s;
    }

    public void printQuartet() {
        System.out.println(this.toString());

    }

    /*
    *********** DO NOT USE WEIGHTS IN equals() method **************
    [Only needed for map check with dummy taxon]
     */
 /*    public boolean equals(Object o) {
        // if()
        Quartet q = (Quartet) o;
        return this.taxa_sisters_left[0].equals(q.taxa_sisters_left[0])
                && this.taxa_sisters_left[1].equals(q.taxa_sisters_left[1])
                && this.taxa_sisters_right[0].equals(q.taxa_sisters_right[0])
                && this.taxa_sisters_right[1].equals(q.taxa_sisters_right[1]) && this.weight == q.weight;
    }
     */
    public void sort_quartet_taxa_names() {
//        String[] left = {this.taxa_sisters_left[0], this.taxa_sisters_left[1]};
//        String[] right = {this.taxa_sisters_right[0], this.taxa_sisters_right[1]};

//        Arrays.sort(this.taxa_sisters_left);
//        Arrays.sort(this.taxa_sisters_right);
//    	System.out.println(this.taxa_sisters_left[0].get_taxa_int_name()+","+this.taxa_sisters_left[1].get_taxa_int_name()+"|"+
//    			this.taxa_sisters_right[0].get_taxa_int_name()+","+this.taxa_sisters_right[1].get_taxa_int_name());

        Arrays.sort(this.taxa_sisters_left, Comparator.comparing(Taxa::get_taxa_int_name));
        Arrays.sort(this.taxa_sisters_right, Comparator.comparing(Taxa::get_taxa_int_name));
        

        if (this.taxa_sisters_left[0].taxa_int_name < this.taxa_sisters_right[0].taxa_int_name) { //don't swap two sides
            //no need to swap
        } else {  // swap two sides
            for (int i = 0; i < Quartet.NUM_TAXA_PER_PARTITION; i++) {
            	Taxa TEMP_TAX_TO_SWAP = this.taxa_sisters_left[i];
                //Quartet.TEMP_TAX_TO_SWAP = this.taxa_sisters_left[i].taxa_int_name;
                this.taxa_sisters_left[i] = this.taxa_sisters_right[i];
                this.taxa_sisters_right[i] = TEMP_TAX_TO_SWAP;
            }
        }
//    	System.out.println(this.taxa_sisters_left[0].get_taxa_int_name()+","+this.taxa_sisters_left[1].get_taxa_int_name()+"|"+
//    			this.taxa_sisters_right[0].get_taxa_int_name()+","+this.taxa_sisters_right[1].get_taxa_int_name());

    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Arrays.hashCode(this.taxa_sisters_left);
        hash = 97 * hash + Arrays.hashCode(this.taxa_sisters_right);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Quartet other = (Quartet) obj;
        other.sort_quartet_taxa_names();

        if (!Arrays.equals(this.taxa_sisters_left, other.taxa_sisters_left)) {
            return false;
        }
        if (!Arrays.equals(this.taxa_sisters_right, other.taxa_sisters_right)) {
            return false;
        }
        return true;
    }

}
