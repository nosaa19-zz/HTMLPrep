package Domain;


public class BaseConstants {

    public static final byte START_SEGMENT = 1;
    public static final byte END_SEGMENT = 2;
    public static final byte NOT_SEGMENT = 3;

    public static final byte COLUMN_CLASS_MT = 1;
    public static final byte COLUMN_CLASS_OT = 2;
    public static final byte COLUMN_CLASS_MD = 3;
    public static final byte COLUMN_CLASS_OD = 4;
    public static final byte COLUMN_CLASS_MR = 5;
    public static final byte COLUMN_CLASS_OR = 6;
    public static final byte COLUMN_CLASS_MC = 7;
    public static final byte COLUMN_CLASS_OC = 8;

    public static final double WP = 0.5;
    public static final double WI = 0.25;
    public static final double WC = 0.25;
    public static final double THR_OT = 0.3;//0.3
    public static final double THR_MERGE = 0.3;//0.3
    public static final double THR_DEC = 0.7; //before 0.7, 0.8
    public static final double THR_LEC = 0.7; //before 0.7, 0.8
    public static final double THR_REC = 0.5; //0.5
    public static final int THR_REP = 3; //before 3, we consider the repretitive pattern if repetitive more than 3
    public static final double THR_DENSITY = 0.7; //before 0.7
}
