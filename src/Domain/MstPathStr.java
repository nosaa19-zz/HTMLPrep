/*
 * MstPathStr.
 */
package Domain;
import java.util.ArrayList;

/**
 * @author Oviliani
 */
public class MstPathStr implements Comparable<MstPathStr> {
    private ArrayList<String> Key;
    private String Index;

    public MstPathStr(String Index) {
        this.Index = Index;
    }

    public MstPathStr(String Index, ArrayList<String> Key) {
        this.Index = Index;
        this.Key = Key;
    }

    public String GetIndex() {
        return this.Index;
    }

    public ArrayList<String> GetKey() {
        return this.Key;
    }

    @Override
    public int compareTo(MstPathStr t) {
        return this.Index.compareTo(t.GetIndex());
    }
    
}
