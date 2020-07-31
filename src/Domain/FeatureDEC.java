/*
 * FeaturesLEC is the data structure for storing features class of Path and Content.
 */
package Domain;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Oviliani
 */
public class FeatureDEC implements Comparable<FeatureDEC> {
    private String idxFDEC;
    private ArrayList<String> Path;
    private ArrayList<String> ClassSet;
    private ArrayList<String> IDSet;

    public FeatureDEC() {

    }

    public FeatureDEC(String idxFDEC) {
        this.idxFDEC = idxFDEC;
    }

    public FeatureDEC(String idxFDEC, ArrayList<String> Path, ArrayList<String> IDSet, ArrayList<String> ClassSet) {
        this.idxFDEC = idxFDEC;
        this.Path = Path;
        this.ClassSet = ClassSet;
        this.IDSet = IDSet;
    }

    public ArrayList<String> getPath() {
        return this.Path;
    }

    public ArrayList<String> getIDSet() {
        return this.IDSet;
    }

    public ArrayList<String> getClassSet() {
        return this.ClassSet;
    }

    public String getidxFDEC() {
        return this.idxFDEC;
    }

    /**
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        return this.idxFDEC.equals(((FeatureDEC) o).idxFDEC);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.idxFDEC);
        return hash;
    }

    @Override
    public int compareTo(FeatureDEC t) {
        return extractInt(this.idxFDEC).compareTo(extractInt(t.getidxFDEC()));
    }

    Integer extractInt(String s) {
        String num = s.replaceAll("\\D", "");
        // return 0 if no digits found
        return num.isEmpty() ? 0 : Integer.parseInt(num);
    }
    
}
