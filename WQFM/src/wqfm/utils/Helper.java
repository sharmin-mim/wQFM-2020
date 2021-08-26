package wqfm.utils;

import java.io.BufferedReader;
import wqfm.configs.Config;
import wqfm.bip.WeightedPartitionScores;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import wqfm.ds.InitialTable;
import wqfm.ds.Taxa;
import wqfm.configs.DefaultValues;

/**
 *
 * @author mahim
 */
public class Helper {

    public static void removeFile(String fileName) {
        String cmd = "rm -f " + fileName;
        Helper.runSystemCommand(cmd);
    }

    public static boolean isOptionOn(String option) {
        return !option.toLowerCase().equals(DefaultValues.OFF);
    }

    public static String getTreeFromFile(String fileName) {
        try {
            Scanner sc = new Scanner(new File(fileName));
            if (sc.hasNextLine()) {
                return sc.nextLine();
            }
        } catch (FileNotFoundException ex) {
            return DefaultValues.NULL;
        }
        return DefaultValues.NULL;
    }

    public static void runSystemCommand(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String s;

            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException ex) {
            System.out.println("Exception while running system command <" + cmd + "> ... Exiting.");
            System.exit(-1);
        }
    }

    //https://www.journaldev.com/878/java-write-to-file#:~:text=FileWriter%3A%20FileWriter%20is%20the%20simplest,number%20of%20writes%20is%20less.
    //Use FileWriter when number of write operations are less
    public static void writeToFile(String tree, String outputfileName) {
        File file = new File(outputfileName);
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            fr.write(tree);
        } catch (IOException e) {
            System.out.println("Error in writingFile to "
                    + outputfileName + ", [Helper.writeToFile]. Exiting system.");
            System.out.println("Tree:\n" + tree);
            System.exit(-1);
        } finally {
            //close resources
            try {
                fr.close();
            } catch (IOException e) {
                System.out.println("Error in closing file resource in [Helper.writeToFile]. to outputfile = " + outputfileName);
            }
        }
        System.out.println(">-> Successfully written to output-file " + outputfileName);
    }

    public static void printUsageAndExitSystem() {
//        System.out.println("java -jar wQFM.jar -i <input-file-name> -o <output-file-name> [-p 0/1 <partition-score-mode>]");
        System.out.println("USAGE: java -jar wQFM.jar <input-file> <output-file> <partition-score-alpha> <partition-score-beta>\n"
                + "Or, java -jar wQFM.jar <input-file> <output-file> {this uses dynamic partitioning threhs=0.9, cut-off=0.1}\n");
        System.out.println("Exiting System (arguments not used according to usage)");
        System.exit(-1);
    }

    public static void findOptionsUsingCommandLineArgs(String[] args) {
        System.out.println("Command line args" + args.length + " are -> " + Arrays.toString(args));
        if (args.length == 0) {
            if (Config.DEBUG_MODE_TESTING == false) {
                Helper.printUsageAndExitSystem();
            }
            System.out.println("-->>Using default params. ");
            return;
        }
        if (args.length == 1) {
            System.out.println("No output file, using default output file <" + Config.OUTPUT_FILE_NAME + ">");
            Config.INPUT_FILE_NAME = args[0];
            return;
        }
        if (((args.length == 4) || (args.length == 2)) == false) {
            printUsageAndExitSystem();
        }

        Config.INPUT_FILE_NAME = args[0];
        Config.OUTPUT_FILE_NAME = args[1];
        if (args.length == 2) {
            Config.PARTITION_SCORE_MODE = DefaultValues.PARTITION_SCORE_FULL_DYNAMIC;
        } else if (args.length == 4) {
            //partition-score argument not given. [to do feature selection] //default is [s] - [v]
            double alpha = Double.parseDouble(args[2]);
            double beta = Double.parseDouble(args[3]);
            WeightedPartitionScores.ALPHA_PARTITION_SCORE = alpha;
            WeightedPartitionScores.BETA_PARTITION_SCORE = beta;
            Config.PARTITION_SCORE_MODE = DefaultValues.PARITTION_SCORE_COMMAND_LINE;
        }
        System.out.println("-->>Helper.end Main.PARTITION_SCORE_MODE = " + Config.PARTITION_SCORE_MODE);
    }

    public static int sumArray(int[] arr) {
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        return sum;
    }

    public static int sumList(List<Integer> list) {
        int sum = 0;
        sum = list.stream().map((x) -> x).reduce(sum, Integer::sum);
        return sum;
    }

    public static int sumMapValuesInteger(Map<Integer, Integer> mapInitialBip) {
        int sum = 0;
//        System.out.println("mapInitialBip = "+mapInitialBip);
        sum = mapInitialBip.keySet().stream().map((key) -> mapInitialBip.get(key)).reduce(sum, Integer::sum);
//        System.out.println("sum = "+sum);
        return sum;
    }

//    private static String getKeysWithSpecifiedValue(Map<Integer, Integer> map, int val, Map<Integer, String> reverse_mapping) {
//        return map.keySet()
//                .stream()
//                .filter((t) -> {
//                    return map.get(t) == val;
//                })
//                .map(x -> (reverse_mapping.get(x) == null) ? Helper.getDummyName(x) : reverse_mapping.get(x)) // x -> reverse_mapping.get(x)
//                .collect(Collectors.joining(", "));
//    }
//    private static String getKeysWithSpecifiedValue2(Map<Integer, Integer> map, int val, Map<Integer, Taxa> reverse_mapping) {
//        return map.keySet()
//                .stream()
//                .filter((t) -> {
//                    return map.get(t) == val;
//                })
//                .map(x -> (reverse_mapping.get(x) == null) ? Helper.getDummyName(x) : reverse_mapping.get(x).taxa_name) // x -> reverse_mapping.get(x)// mim->check again
//                .collect(Collectors.joining(", "));
//    }


//
//    
//
//	public static String getPartition(Map<Integer, Integer> partition_map,
//            int left_partition, int right_partition,
//            Map<Integer, String> reverse_mapping) {
//
//        StringBuilder bld = new StringBuilder();
//
//        bld
//                .append("LEFT: ")
//                .append(getKeysWithSpecifiedValue(partition_map, left_partition, reverse_mapping))
//                .append("\n")
//                .append("RIGHT: ")
//                .append(getKeysWithSpecifiedValue(partition_map, right_partition, reverse_mapping))
//                .append("\n");
//
//        return bld.toString();
//    }
//    public static String getPartition2(Map<Integer, Integer> partition_map,
//            int left_partition, int right_partition,
//            Map<Integer, Taxa> reverse_mapping) {
//
//        StringBuilder bld = new StringBuilder();
//
//        bld
//                .append("LEFT: ")
//                .append(getKeysWithSpecifiedValue2(partition_map, left_partition, reverse_mapping))
//                .append("\n")
//                .append("RIGHT: ")
//                .append(getKeysWithSpecifiedValue2(partition_map, right_partition, reverse_mapping))
//                .append("\n");
//
//        return bld.toString();
//    }


//    public static void printPartition(Map<Integer, Integer> partition_map,
//            int left_partition, int right_partition,
//            Map<Integer, String> reverse_mapping) {
//
//        System.out.println(getPartition(partition_map, left_partition, right_partition, reverse_mapping));
//    }
//    public static void printPartition2(Map<Integer, Integer> partition_map,
//            int left_partition, int right_partition,
//            Map<Integer, Taxa> reverse_mapping) {
//
//        System.out.println(getPartition2(partition_map, left_partition, right_partition, reverse_mapping));
//    }
    private static String getKeysWithSpecifiedValue3(Map<Integer, Taxa> partition_map, int val) {
        return partition_map.keySet()
                .stream()
                .filter((t) -> {
                    return partition_map.get(t).partition == val;
                })//.forEach((t) -> {});
                .map(x -> (partition_map.get(x).taxa_name == null) ? Helper.getDummyName(x) : partition_map.get(x).taxa_name) // x -> reverse_mapping.get(x)// mim->check again
                .collect(Collectors.joining(", "));
    }
    public static String getPartition3(Map<Integer, Taxa> partition_map,
            int left_partition, int right_partition) {

        StringBuilder bld = new StringBuilder();

        bld
                .append("LEFT: ")
                .append(getKeysWithSpecifiedValue3(partition_map, left_partition))
                .append("\n")
                .append("RIGHT: ")
                .append(getKeysWithSpecifiedValue3(partition_map, right_partition))
                .append("\n");

        return bld.toString();
    }
    public static void printPartition3(Map<Integer, Taxa> partition_map, int left_partition, int right_partition) {
    	
    	 System.out.println(getPartition3(partition_map, left_partition, right_partition));
    }

    private static String getDummyName(int x) {
        return "DUM_" + Integer.toString(x);
    }

//    public static String getStringMappedName(int x) {
//        String s = InitialTable.map_of_int_vs_str_tax_list.get(x);
//        return (s != null) ? s : getDummyName(x);
//    }
    public static String getStringMappedName(int x) {

//        String s;
//    	if (x < InitialTable.array_of_int_vs_str_tax_list.length ) {
//			s = InitialTable.array_of_int_vs_str_tax_list[x];
//			return s;
//		} else {
//			return getDummyName(x);
//
//		}
        //return (s != null) ? s : getDummyName(x);
        String s = InitialTable.initial_map_of_int_vs_tax_property.get(x).taxa_name;
        return (s != null) ? s : getDummyName(x);
    }

    private static String getKeysWithSpecifiedValue(Map<Integer, Integer> map, int val) {
        return map.keySet()
                .stream()
                .filter((t) -> {
                    return map.get(t) == val;
                })
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    public static void printPartition(Map<Integer, Integer> partition_map, int left_partition, int right_partition) {
        System.out.print("LEFT:  ");
        System.out.println(getKeysWithSpecifiedValue(partition_map, left_partition));

        System.out.print("RIGHT: ");
        System.out.println(getKeysWithSpecifiedValue(partition_map, right_partition));

    }

//    public static int sumMapValuesInteger(Map<String, Integer> mapInitialBip) {
//        int sum = 0;
//        sum = mapInitialBip.keySet().stream().map((key) -> mapInitialBip.get(key)).reduce(sum, Integer::sum);
//        return sum;
//    }
    public static boolean checkAllValuesIFSame(List<Boolean> list, boolean val) {
        return list.stream().noneMatch((x) -> (x != val)); //if at least one is different wrt val, then return false
    }

    public static boolean checkAllValuesIFSame(Map<Integer, Boolean> map, boolean val) {
        if (map.isEmpty()) {
            return true;
        }
        return map.keySet().stream().noneMatch((key) -> (map.get(key) != val));
    }

    public static String getFinalTreeFromMap(String finalTree,
            Map<Integer, String> map_of_int_vs_str) {

        String decodedTree = "";
        for (int i = 0; i < finalTree.length(); i++) {
            char c = finalTree.charAt(i);
            if (c != '(' && c != ')' && c != ',' && c != ';') {
                String key = "";
                int j;
                for (j = i + 1; j < finalTree.length(); j++) {
                    char c1 = finalTree.charAt(j);
                    if (c1 == ')' || c1 == '(' || c1 == ',' || c1 == ';') {
                        break;
                    }
                }
                // System.out.println(j);
                key = finalTree.substring(i, j);
                // System.out.println("i: "+i+ " j: "+j);
                // System.out.println("Key: "+ key);
                String val = map_of_int_vs_str.get(Integer.parseInt(key.trim()));
                
               // String val = array_of_int_vs_str[Integer.parseInt(key.trim())];
                //System.out.println(val);
                decodedTree += val;
                i += (j - 1 - i);
            } else {
                decodedTree += c;
            }
            //  System.out.println(finalTree.charAt(i));

        }
//        for(int key: map_of_int_vs_str.keySet()){
//            System.out.println("<<REPLACING key=" + key + ", with val=" + map_of_int_vs_str.get(key) + ">>");
//            replaced = replaced.replace(String.valueOf(key), map_of_int_vs_str.get(key));
//        }
        return decodedTree;
    }
    public static String getFinalTreeFromArrayOfIntVsStr(String finalTree,
            String[] array_of_int_vs_str) {

        String decodedTree = "";
        for (int i = 0; i < finalTree.length(); i++) {
            char c = finalTree.charAt(i);
            if (c != '(' && c != ')' && c != ',' && c != ';') {
                String key = "";
                int j;
                for (j = i + 1; j < finalTree.length(); j++) {
                    char c1 = finalTree.charAt(j);
                    if (c1 == ')' || c1 == '(' || c1 == ',' || c1 == ';') {
                        break;
                    }
                }
                // System.out.println(j);
                key = finalTree.substring(i, j);
                // System.out.println("i: "+i+ " j: "+j);
                // System.out.println("Key: "+ key);
               // String val = map_of_int_vs_str.get(Integer.parseInt(key.trim()));
                
                String val = array_of_int_vs_str[Integer.parseInt(key.trim())];
                //System.out.println(val);
                decodedTree += val;
                i += (j - 1 - i);
            } else {
                decodedTree += c;
            }
            //  System.out.println(finalTree.charAt(i));

        }
//        for(int key: map_of_int_vs_str.keySet()){
//            System.out.println("<<REPLACING key=" + key + ", with val=" + map_of_int_vs_str.get(key) + ">>");
//            replaced = replaced.replace(String.valueOf(key), map_of_int_vs_str.get(key));
//        }
        return decodedTree;
    }

    public static boolean areEqualBipartition(Map<Integer, Integer> map1, Map<Integer, Integer> map2,
            int leftPartition, int rightPartition, int unassignedPartition) {

        // check if normally equal.
        if(map1.equals(map2)){
            return true;
        }

        Map<Integer, Integer> newFinalMap = new HashMap<>();
        map2.keySet().forEach((key) -> {
            newFinalMap.put(key, (map2.get(key) == unassignedPartition) ? unassignedPartition : ((map2.get(key) == leftPartition) ? rightPartition : leftPartition)); // only non-zeros will be flipped
        });
        
        
//        System.out.println("COMPARING two maps function\nmap1 = " + map1 + "\nmap2 = " + map2 + "\nnewFinalMap = " + newFinalMap);
        
        
        return (map1.equals(newFinalMap));
    }
    public static boolean areEqualBipartition(Map<Integer, Taxa> map_of_int_vs_tax_property) {
    	boolean equal_partition = true;
//    	map_of_int_vs_tax_property.values().forEach((taxon) -> {
//    		if(taxon.partition != taxon.prev_partition) {
//    			equal_partition = Boolean.FALSE;
//    			//break;
//    		}
//    	});
    	for (Taxa taxon : map_of_int_vs_tax_property.values()) {
    		if(taxon.partition != taxon.prev_partition) {
    			equal_partition = false;
    			break;
    		}
		}
    	if (equal_partition) {
			return true;
		} else {
			equal_partition = true;
		   	for (Taxa taxon : map_of_int_vs_tax_property.values()) {
	    		if(taxon.partition != TaxaUtils.getOppositePartition(taxon.prev_partition)) {
	    			equal_partition = false;
	    			break;
	    		}
			}

		}
        return equal_partition;
    }

	public static String getFinalTreeFromArrayOfIntVsStr2(String final_tree,
			Map<Integer, Taxa> initial_map_of_int_vs_tax_property) {


        String decodedTree = "";
        for (int i = 0; i < final_tree.length(); i++) {
            char c = final_tree.charAt(i);
            if (c != '(' && c != ')' && c != ',' && c != ';') {
                String key = "";
                int j;
                for (j = i + 1; j < final_tree.length(); j++) {
                    char c1 = final_tree.charAt(j);
                    if (c1 == ')' || c1 == '(' || c1 == ',' || c1 == ';') {
                        break;
                    }
                }
                // System.out.println(j);
                key = final_tree.substring(i, j);
                // System.out.println("i: "+i+ " j: "+j);
                // System.out.println("Key: "+ key);
               // String val = map_of_int_vs_str.get(Integer.parseInt(key.trim()));
                
                String val = initial_map_of_int_vs_tax_property.get(Integer.parseInt(key.trim())).taxa_name;
                //System.out.println(val);
                decodedTree += val;
                i += (j - 1 - i);
            } else {
                decodedTree += c;
            }
            //  System.out.println(finalTree.charAt(i));

        }
//        for(int key: map_of_int_vs_str.keySet()){
//            System.out.println("<<REPLACING key=" + key + ", with val=" + map_of_int_vs_str.get(key) + ">>");
//            replaced = replaced.replace(String.valueOf(key), map_of_int_vs_str.get(key));
//        }
        return decodedTree;
    
	}

}
