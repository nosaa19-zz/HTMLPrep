/*
 * MstPathStr.
 */
package Domain;
import java.util.ArrayList;

public class MstPathStrFirst implements Comparable<MstPathStrFirst> {
    private ArrayList<String> Key;
    private String Index;

    public MstPathStrFirst(String Index) {
        this.Index = Index;
    }

    public MstPathStrFirst(String Index, ArrayList<String> Key) {
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
    public int compareTo(MstPathStrFirst t) {
        return this.Key.toString().compareTo(t.GetKey().toString());
    }
    
}
