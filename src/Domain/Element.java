/*
 * Element Class is used for save the property of a path tag and a node properties.
 */
package Domain;

import java.util.ArrayList;

/**
 * @author Oviliani
 */
public class Element {

    private String LeafNodeNo;
    private ArrayList<String> Path;
    private String Content;
    private ArrayList<String> ID;
    private ArrayList<String> Classess;
    private ArrayList<String> DataType;
    private ArrayList<String> ParentSet;
    private String PathId;
    private String SimSeqId;
    private String PTypeSetId;
    private String TypeSetId;
    private String ContentId;
    private String CECId;

    public Element(){}

    public Element(String leafIndex, String pathId, String simSeqId, String pTypeSetId, String typeSetId, String contentId, String content){
        this.LeafNodeNo = leafIndex;
        this.PathId= pathId;
        this.SimSeqId = simSeqId;
        this.PTypeSetId = pTypeSetId;
        this.TypeSetId = typeSetId;
        this.ContentId = contentId;
        this.Content = content;
    }

    public Element(String LeafIndex, ArrayList<String> Path, ArrayList<String> ID, ArrayList<String> Class,
                   String Content, ArrayList<String> DataType, ArrayList<String> ParentSet) {
        this.LeafNodeNo = LeafIndex;
        this.Path = Path;
        this.ID = ID;
        this.Classess = Class;
        this.Content = Content;
        this.DataType = DataType;
        this.ParentSet = ParentSet;
    }

    public String getLeafIndex() {
        return LeafNodeNo;
    }

    public void setLeafIndex(String leafIndex) {
        LeafNodeNo = leafIndex;
    }

    public ArrayList<String> getPath() {
        return Path;
    }

    public void setPath(ArrayList<String> path) {
        Path = path;
    }

    public ArrayList<String> getID() {
        return ID;
    }

    public void setID(ArrayList<String> ID) {
        this.ID = ID;
    }

    public ArrayList<String> getClassess() {
        return Classess;
    }

    public void setClassess(ArrayList<String> classess) {
        Classess = classess;
    }

    public ArrayList<String> getDataType() {
        return DataType;
    }

    public void setDataType(ArrayList<String> dataType) {
        DataType = dataType;
    }

    public ArrayList<String> getParentSet() {
        return ParentSet;
    }

    public void setParentSet(ArrayList<String> parentSet) {
        ParentSet = parentSet;
    }

    public String getPathId() {
        return PathId;
    }

    public void setPathId(String pathId) {
        PathId = pathId;
    }

    public String getSimSeqId() { return SimSeqId; }

    public void setSimSeqId(String simSeqId) { SimSeqId = simSeqId; }

    public String getPTypeSetId() { return PTypeSetId; }

    public void setPTypeSetId(String PTypeSetId) { this.PTypeSetId = PTypeSetId; }

    public String getTypeSetId() {
        return TypeSetId;
    }

    public void setTypeSetId(String typeSetId) {
        TypeSetId = typeSetId;
    }

    public String getContentId() {
        return ContentId;
    }

    public void setContentId(String contentId) {
        ContentId = contentId;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getCECId() {
        return CECId;
    }

    public void setCECId(String CECId) {
        this.CECId = CECId;
    }
}
