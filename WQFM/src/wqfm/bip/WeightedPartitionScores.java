package wqfm.bip;

import wqfm.configs.Config;
import wqfm.configs.DefaultValues;

/**
 *
 * @author mahim
 */
public class WeightedPartitionScores {

    public static double ALPHA_PARTITION_SCORE = 1;
    public static double BETA_PARTITION_SCORE = 1;

    private static double calculatePScore0(Bipartition_8_values bip_8_vals) {
        return (bip_8_vals.wtSatisfied - bip_8_vals.wtViolated);
    }

    private static double calculatePScore1(Bipartition_8_values bip_8_vals) {
        return (bip_8_vals.wtSatisfied - 0.5 * bip_8_vals.wtViolated);
    }

    private static double calculatePScore2(Bipartition_8_values bip_8_vals) {
        return (bip_8_vals.wtSatisfied - bip_8_vals.wtViolated - bip_8_vals.wtDeferred);
    }

    private static double calculatePScore3(Bipartition_8_values bip_8_vals) {
        return (3 * bip_8_vals.wtSatisfied - 2 * bip_8_vals.wtViolated);
    }

    private static double calculatePScore4(Bipartition_8_values bip_8_vals) {
        return (5 * bip_8_vals.wtSatisfied - 4 * bip_8_vals.wtViolated);
    }

    private static double calculatePScore5(Bipartition_8_values bip_8_vals) {
        return bip_8_vals.wtSatisfied;
    }

    private static double calculatePScore6(Bipartition_8_values bip_8_vals) {
        return (bip_8_vals.wtSatisfied - 0.5 * bip_8_vals.wtViolated - 0.25 * bip_8_vals.wtDeferred);
    }

    private static double calculatePScore7(Bipartition_8_values bip_8_vals) {
        return (11 * bip_8_vals.wtSatisfied - 1 * bip_8_vals.wtViolated);
    }

    private static double calculatePScoreCommandLine(Bipartition_8_values bip_8_vals) {//creates problem while parallelizing left and right fmobject in fmrunner 
        //   System.out.println("ALPHA: "+WeightedPartitionScores.ALPHA_PARTITION_SCORE + "BETA: "+WeightedPartitionScores.BETA_PARTITION_SCORE);
        return (WeightedPartitionScores.ALPHA_PARTITION_SCORE * bip_8_vals.wtSatisfied - WeightedPartitionScores.BETA_PARTITION_SCORE * bip_8_vals.wtViolated);
    }

    private static double calculatePScoreFullDynamic(Bipartition_8_values bip_8_vals) {//creates problem while parallelizing left and right fmobject in fmrunner
        return (WeightedPartitionScores.ALPHA_PARTITION_SCORE * bip_8_vals.wtSatisfied - WeightedPartitionScores.BETA_PARTITION_SCORE * bip_8_vals.wtViolated);
    }
//    private static double calculatePScoreCommandLine(Bipartition_8_values bip_8_vals, double ALPHA_PARTITION_SCORE, double BETA_PARTITION_SCORE) {//creates problem while parallelizing left and right fmobject in fmrunner 
//        //   System.out.println("ALPHA: "+WeightedPartitionScores.ALPHA_PARTITION_SCORE + "BETA: "+WeightedPartitionScores.BETA_PARTITION_SCORE);
//        return (ALPHA_PARTITION_SCORE * bip_8_vals.wtSatisfied - BETA_PARTITION_SCORE * bip_8_vals.wtViolated);
//    }
//
//    private static double calculatePScoreFullDynamic(Bipartition_8_values bip_8_vals, double ALPHA_PARTITION_SCORE, double BETA_PARTITION_SCORE) {//creates problem while parallelizing left and right fmobject in fmrunner
//        return (ALPHA_PARTITION_SCORE * bip_8_vals.wtSatisfied - BETA_PARTITION_SCORE * bip_8_vals.wtViolated);
//    }


    public static double calculatePartitionScoreReduced(Bipartition_8_values bip_8_vals) {

        switch (Config.PARTITION_SCORE_MODE) {
            case DefaultValues.PARTITION_SCORE_MODE_0:
                return WeightedPartitionScores.calculatePScore0(bip_8_vals);
            case DefaultValues.PARTITION_SCORE_MODE_1:
                return WeightedPartitionScores.calculatePScore1(bip_8_vals);
            case DefaultValues.PARTITION_SCORE_MODE_2:
                return WeightedPartitionScores.calculatePScore2(bip_8_vals);
            case DefaultValues.PARTITION_SCORE_MODE_3:
                return WeightedPartitionScores.calculatePScore3(bip_8_vals);
            case DefaultValues.PARTITION_SCORE_MODE_4:
                return WeightedPartitionScores.calculatePScore4(bip_8_vals);
            case DefaultValues.PARTITION_SCORE_MODE_5:
                return WeightedPartitionScores.calculatePScore5(bip_8_vals);
            case DefaultValues.PARTITION_SCORE_MODE_6:
                return WeightedPartitionScores.calculatePScore6(bip_8_vals);
            case DefaultValues.PARTITION_SCORE_MODE_7:
                return WeightedPartitionScores.calculatePScore7(bip_8_vals);
            case DefaultValues.PARITTION_SCORE_COMMAND_LINE:
            	//System.out.println("command line");
                return WeightedPartitionScores.calculatePScoreCommandLine(bip_8_vals);
            case DefaultValues.PARTITION_SCORE_FULL_DYNAMIC:
            	//System.out.println("Full dynamic");
                return WeightedPartitionScores.calculatePScoreFullDynamic(bip_8_vals);
            default:
                return WeightedPartitionScores.calculatePScore0(bip_8_vals); //[s]-[v] is default.
        }

    }
//    public static double calculatePartitionScoreReduced(Bipartition_8_values bip_8_vals, double ALPHA_PARTITION_SCORE, double BETA_PARTITION_SCORE) {
//
//        switch (Config.PARTITION_SCORE_MODE) {
//            case DefaultValues.PARTITION_SCORE_MODE_0:
//                return WeightedPartitionScores.calculatePScore0(bip_8_vals);
//            case DefaultValues.PARTITION_SCORE_MODE_1:
//                return WeightedPartitionScores.calculatePScore1(bip_8_vals);
//            case DefaultValues.PARTITION_SCORE_MODE_2:
//                return WeightedPartitionScores.calculatePScore2(bip_8_vals);
//            case DefaultValues.PARTITION_SCORE_MODE_3:
//                return WeightedPartitionScores.calculatePScore3(bip_8_vals);
//            case DefaultValues.PARTITION_SCORE_MODE_4:
//                return WeightedPartitionScores.calculatePScore4(bip_8_vals);
//            case DefaultValues.PARTITION_SCORE_MODE_5:
//                return WeightedPartitionScores.calculatePScore5(bip_8_vals);
//            case DefaultValues.PARTITION_SCORE_MODE_6:
//                return WeightedPartitionScores.calculatePScore6(bip_8_vals);
//            case DefaultValues.PARTITION_SCORE_MODE_7:
//                return WeightedPartitionScores.calculatePScore7(bip_8_vals);
//            case DefaultValues.PARITTION_SCORE_COMMAND_LINE:
//            	//System.out.println("command line");
//                return WeightedPartitionScores.calculatePScoreCommandLine(bip_8_vals, ALPHA_PARTITION_SCORE, BETA_PARTITION_SCORE);
//            case DefaultValues.PARTITION_SCORE_FULL_DYNAMIC:
//            	//System.out.println("Full dynamic");
//                return WeightedPartitionScores.calculatePScoreFullDynamic(bip_8_vals, ALPHA_PARTITION_SCORE, BETA_PARTITION_SCORE);
//            default:
//                return WeightedPartitionScores.calculatePScore0(bip_8_vals); //[s]-[v] is default.
//        }
//
//    }

    public static String GET_PARTITION_SCORE_PRINT() {
        int mode = Config.PARTITION_SCORE_MODE;
        switch (mode) {
            case DefaultValues.PARTITION_SCORE_MODE_0:
                return "mode = " + mode + ", [ws - wv]";
            case DefaultValues.PARTITION_SCORE_MODE_1:
                return "mode = " + mode + ", [ws - 0.5*wv]";
            case DefaultValues.PARTITION_SCORE_MODE_2:
                return "mode = " + mode + ", [ws - wv - wd]";
            case DefaultValues.PARTITION_SCORE_MODE_3:
                return "mode = " + mode + ", [3*ws - 2*wv]";
            case DefaultValues.PARTITION_SCORE_MODE_4:
                return "mode = " + mode + ", [5*ws - 4*wv]";
            case DefaultValues.PARTITION_SCORE_MODE_5:
                return "mode = " + mode + ", [ws]";
            case DefaultValues.PARTITION_SCORE_MODE_6:
                return "mode = " + mode + ", [ws - 0.5*wv - 0.25*wd]";
            case DefaultValues.PARTITION_SCORE_MODE_7:
                return "mode = " + mode + ", [11*ws - 1*wv]";
            case DefaultValues.PARITTION_SCORE_COMMAND_LINE:
                return "mode = Command line [" + WeightedPartitionScores.ALPHA_PARTITION_SCORE + "*ws - " + WeightedPartitionScores.BETA_PARTITION_SCORE + "*wv]";
            case DefaultValues.PARTITION_SCORE_FULL_DYNAMIC:
                return "mode: FULL DYNAMIC [Left-Bin-Right-Level-0-Stop-to-1]. Threshold = " + Config.THRESHOLD_BINNING + " , Cut-off = " + Config.CUT_OFF_LIMIT_BINNING;
            default:
                return "default partition score mode = " + mode + ", [ws - wv]";
        }
    }
//    public static String GET_PARTITION_SCORE_PRINT(double ALPHA_PARTITION_SCORE, double BETA_PARTITION_SCORE) {
//        int mode = Config.PARTITION_SCORE_MODE;
//        switch (mode) {
//            case DefaultValues.PARTITION_SCORE_MODE_0:
//                return "mode = " + mode + ", [ws - wv]";
//            case DefaultValues.PARTITION_SCORE_MODE_1:
//                return "mode = " + mode + ", [ws - 0.5*wv]";
//            case DefaultValues.PARTITION_SCORE_MODE_2:
//                return "mode = " + mode + ", [ws - wv - wd]";
//            case DefaultValues.PARTITION_SCORE_MODE_3:
//                return "mode = " + mode + ", [3*ws - 2*wv]";
//            case DefaultValues.PARTITION_SCORE_MODE_4:
//                return "mode = " + mode + ", [5*ws - 4*wv]";
//            case DefaultValues.PARTITION_SCORE_MODE_5:
//                return "mode = " + mode + ", [ws]";
//            case DefaultValues.PARTITION_SCORE_MODE_6:
//                return "mode = " + mode + ", [ws - 0.5*wv - 0.25*wd]";
//            case DefaultValues.PARTITION_SCORE_MODE_7:
//                return "mode = " + mode + ", [11*ws - 1*wv]";
//            case DefaultValues.PARITTION_SCORE_COMMAND_LINE:
//                return "mode = Command line [" + ALPHA_PARTITION_SCORE + "*ws - " + BETA_PARTITION_SCORE + "*wv]";
//            case DefaultValues.PARTITION_SCORE_FULL_DYNAMIC:
//                return "mode: FULL DYNAMIC [Left-Bin-Right-Level-0-Stop-to-1]. Threshold = " + Config.THRESHOLD_BINNING + " , Cut-off = " + Config.CUT_OFF_LIMIT_BINNING;
//            default:
//                return "default partition score mode = " + mode + ", [ws - wv]";
//        }
//    }
}
