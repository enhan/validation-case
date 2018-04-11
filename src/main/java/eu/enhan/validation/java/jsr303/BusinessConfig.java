package eu.enhan.validation.java.jsr303;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@ThresholdConstraint
public final class BusinessConfig {

    @Min(0)
    public int thresholdA;


    public int thresholdB;

    @Max(10000)
    public int thresholdC;

    public BusinessConfig(int thresholdA, int thresholdB, int thresholdC) {
        this.thresholdA = thresholdA;
        this.thresholdB = thresholdB;
        this.thresholdC = thresholdC;
    }

    public int getThresholdA() {
        return thresholdA;
    }

    public void setThresholdA(int thresholdA) {
        this.thresholdA = thresholdA;
    }

    public int getThresholdB() {
        return thresholdB;
    }

    public void setThresholdB(int thresholdB) {
        this.thresholdB = thresholdB;
    }

    public int getThresholdC() {
        return thresholdC;
    }

    public void setThresholdC(int thresholdC) {
        this.thresholdC = thresholdC;
    }
}
