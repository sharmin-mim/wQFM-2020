package wqfm.utils;

import wqfm.interfaces.Status;
import wqfm.bip.Bipartition_8_values;
import wqfm.main.Main;

/**
 *
 * @author mahim
 */
public class WeightedPartitionScores {

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
        return (bip_8_vals.wtSatisfied - 0.5*bip_8_vals.wtViolated - 0.25*bip_8_vals.wtDeferred);
    }

    public static double calculatePartitionScoreReduced(Bipartition_8_values bip_8_vals) {

        switch (Main.PARTITION_SCORE_MODE) {
            case Status.PARTITION_SCORE_MODE_0:
                return WeightedPartitionScores.calculatePScore0(bip_8_vals);
            case Status.PARTITION_SCORE_MODE_1:
                return WeightedPartitionScores.calculatePScore1(bip_8_vals);
            case Status.PARTITION_SCORE_MODE_2:
                return WeightedPartitionScores.calculatePScore2(bip_8_vals);
            case Status.PARTITION_SCORE_MODE_3:
                return WeightedPartitionScores.calculatePScore3(bip_8_vals);
            case Status.PARTITION_SCORE_MODE_4:
                return WeightedPartitionScores.calculatePScore4(bip_8_vals);
            case Status.PARTITION_SCORE_MODE_5:
                return WeightedPartitionScores.calculatePScore5(bip_8_vals);
            case Status.PARTITION_SCORE_MODE_6:
                return WeightedPartitionScores.calculatePScore6(bip_8_vals);
            default:
                return WeightedPartitionScores.calculatePScore0(bip_8_vals); //[s]-[v] is default.
        }

    }
}
