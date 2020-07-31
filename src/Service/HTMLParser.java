package Service;

import Domain.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class HTMLParser {
    ArrayList<Document> documentList = new ArrayList<>();
    MyParser parser;
    File[] inputFiles;
    ArrayList<String> skipTagList = new ArrayList<>();
    ArrayList<String> PathPrev;
    String[] tagList = {"#comment", "SCRIPT", "STYLE"};//add tag name(uppercase) that you want to skip
    String HREF;
    String HREFPrev;
    String[] Dec = {"B", "BIG", "CITE", "DFN", "EM", "FONT", "I", "MRK", "SMALL", "SUB", "SUP", "STRIKE", "STRONG", "U"}; //exclude a, span
    ArrayList<String> Decorative = new ArrayList(Arrays.asList(Dec));

    //record the frequency of single path
    public HTMLParser() throws Exception {
        parser = new MyParser();
        skipTagList = new ArrayList<>(Arrays.asList(tagList));
    }

    /*
     * Read all files(pages)
     * use page1 to specify codepage
     */
    public void readFiles(File[] files) {
        this.inputFiles = files;

        for (int i = 0; i < files.length; i++) {
            try {
                if (files[i].isFile()) {
                    loadURL(files[i].getPath());
                }

            } catch (Exception e) {
            }
        }
    }

    /*
     * Parse single page
     */
    public void loadURL(String URL) throws Exception {
        InputSource inStrem = new InputSource(new FileInputStream(new File(URL))); // I add this for running in Netbean
        //parser.parse(URL); //because addition above I must change the parameter using stream
        parser.parse(inStrem);
        documentList.add(parser.getDocument());
    }

    /*
     * output the path of all leafnodes
     * if tagEncoded = true , every tag will have a unique index
     */
    public ArrayList<ArrayList<Element>> outputPath(boolean tagEncoded) {
        ArrayList<ArrayList<Element>> TableL = new ArrayList<>();
        double CountLN=0.0;
        int MaxLN = 0;

        for (int i = 0; i < documentList.size(); i++) {
            TableL.add(printAllLeafPath(getDocument(i), tagEncoded));

            if (TableL.get(i).size() > MaxLN) {
                MaxLN = TableL.get(i).size();
            }

            CountLN=CountLN+TableL.get(i).size();
        }

        System.out.println("Max LN\t" + MaxLN);
        System.out.println("Density\t" + (CountLN/(MaxLN*documentList.size())));


        System.out.println();

        return TableL;
    }

    public Document getDocument(int index) {
        return documentList.get(index);
    }

    public ArrayList<Element> printAllLeafPath(Node root, boolean showUniqueIndex) {
        ArrayList<Node> allLeafnodes = getAllLeafNodes(root);
        CheckDataType FiilDT = new CheckDataType();
        ArrayList<Element> aDoc = new ArrayList<>();
        ArrayList<String> DecorStart;
        ArrayList<String> CPath;
        ArrayList<String> LastPath;
        ArrayList<String> SplitPath;
        ArrayList<String> ID;
        ArrayList<String> SavePath;
        ArrayList<String> HrefSource;
        ArrayList<String> Class;
        String value;
        String LastTag;
        String aPath;
        ArrayList<String> DataType;
        ArrayList<String> ParentSet;
        //boolean CDecor;
        int iTag;
        int LeafIndex = 0;
        PathPrev = new ArrayList<>();
        HREFPrev = "";

        for (Node node : allLeafnodes) {
            value = getNodeValue(node);
            HREF = "";
            CPath = new ArrayList(Arrays.asList(getPathToRoot(node, showUniqueIndex).split("\\t")));  //convert Path from string into ArrayList
            //Note: getPathToRoot & CheckDuplicateHREF will modify the content of HREF
            //CDecor = false;
            if (!"".equals(HREF)) {
                CheckDuplicateHREF((ArrayList<String>) CPath.clone());
            }

            iTag = CPath.size() - 1;
            LastTag = CPath.get(iTag); //Store the last tag before remove it into LastTag

            if (value != null) {
                if (value.trim().equals("")) {
                    continue;
                }

                value = value.replaceAll("[\\u0009\\u000A\\u000D\\u2028\\u2029\\u0020\\u00A0\\u0022]+", " ");
                value = value.trim();
                if (value.equals("")) {
                    continue;
                }
            } else if (!LastTag.contains("BR_")) { //only keep BR tag
                continue;
            }

            ID = new ArrayList(Arrays.asList(getID(node).split("\t")));
            Class = new ArrayList(Arrays.asList(getClass(node).split("\t")));
            //CPath.remove(CPath.size() - 1); //remove text tag //this line should be closed this for Allen & Tz

            iTag--;
            if (LastTag.contains("BR")) {
                value = "<BR/>";
                DataType = new ArrayList<>();
                ParentSet = new ArrayList<>();
                CPath.remove(CPath.size() - 1);
            } else {
                FiilDT.SetContent(value);
                DataType = new ArrayList(Arrays.asList(FiilDT.ProcessDataType().split(" ")));
                ParentSet = GetParentSet(DataType);
                LastPath = new ArrayList(Arrays.asList(CPath.get(iTag).split("_")));  //convert Path from string into ArrayList

                DecorStart = new ArrayList<>();
                if (Decorative.contains(LastPath.get(0))) {
                    //CDecor = true;
                    while (Decorative.contains(LastPath.get(0))) {
                        if (!LastPath.get(0).equals("FONT")) {
                            DecorStart.add(LastPath.get(0));
                        }
                        CPath.remove(iTag);
                        iTag--;
                        LastPath = new ArrayList(Arrays.asList(CPath.get(iTag).split("_")));  //convert Path from string into ArrayList

                    }

                    if (DecorStart.size() > 0) {
                        value = giveTag(value, DecorStart);
                    }
                }

            }

            SavePath = (ArrayList<String>) CPath.clone();
            //Remove the unique number of tags
            for (int i = 0; i < SavePath.size(); i++) {
                SplitPath = new ArrayList(Arrays.asList(SavePath.get(i).split("_")));
                LastPath = (ArrayList<String>) SplitPath.clone();
                aPath = LastPath.get(0);
                SavePath.set(i, aPath);
            }

            /*if (!HREF.equals("")) {
                value="<A HREF=\""+HREF+"\"> "+value;
            }*/
            aDoc.add(new Element(String.valueOf(LeafIndex), SavePath, ID, Class, value, DataType, ParentSet));
            LeafIndex++;

            if (!HREF.equals("")) { //write the HREF only if the last path is #text
                FiilDT.SetContent(HREF);
                DataType = new ArrayList(Arrays.asList(FiilDT.ProcessDataType().split(" ")));
                ParentSet = GetParentSet(DataType);

                HrefSource = (ArrayList<String>) SavePath.clone();
                HrefSource.remove(HrefSource.size() - 1);
                aDoc.add(new Element(String.valueOf(LeafIndex), HrefSource, ID, Class, HREF, DataType, ParentSet)); //path without cut and no merge
                LeafIndex++;
            }
        }
        return aDoc;
    }

    /* Check if HRef is saved or not */
    private void CheckDuplicateHREF(ArrayList<String> Path) {
        int iP = Path.size() - 1;

        //remove the last tags after A_
        while (iP < Path.size()) {
            if (Path.get(iP).contains("A_")) {
                break;
            }

            Path.remove(iP);
            iP--;
        }

        if (!HREF.equals(HREFPrev)) {//differenct HREF
            HREFPrev = HREF;
            PathPrev = Path;
        } else if (checkPathSim(PathPrev, Path)) {
            HREF = "";
        }
    }

    /*
     * get the class of all leaf nodes
     */
    public String getClass(Node node) {
        String Class = "";

        NamedNodeMap atts = node.getAttributes();
        if (atts != null) {
            for (int i = 0; i < atts.getLength(); i++) {
                Node att = atts.item(i);
                if (att.getNodeName().equals("class")) {
                    Class = att.getNodeValue();
                    break;
                }
            }
        }

        Node parentNode = node.getParentNode();
        if (parentNode != null) {
            if (!"".equals(Class)) {
                return getClass(parentNode) + Class + "\t";

            } else {
                return getClass(parentNode) + "";
            }
        } else {
            return "";
        }
    }

    /*
     * get the ID of all leaf nodes
     */
    public String getID(Node node) {
        String ID = "";

        NamedNodeMap atts = node.getAttributes();
        if (atts != null) {
            for (int i = 0; i < atts.getLength(); i++) {
                Node att = atts.item(i);
                if (att.getNodeName().equals("id")) {//to get id attribute in path
                    ID = att.getNodeValue();
                    break;
                }
            }
        }

        Node parentNode = node.getParentNode();
        if (parentNode != null) {
            if (!"".equals(ID)) {
                return getID(parentNode) + ID + "\t";

            } else {
                return getID(parentNode) + "";
            }
        } else {
            return "";
        }
    }

    /*
     * get the path of all leaf nodes
     * if tagEncoded= true , each tag in output path will have unique index
     */
    public String getPathToRoot(Node node, boolean tagEncoded) {
        String Path;
        Node parentNode = node.getParentNode();
        if (parentNode != null) {
            String tag = node.getNodeName();

            if (tag.equals("A")) {
                HREF = getHRef(node);
            }
            if (tagEncoded) {
                tag = tag + "_" + indexOfNode(node);
            }

            Path = getPathToRoot(parentNode, tagEncoded) + tag + "\t";
            return Path;
        }
        return "";

    }

    /*
     * get the child order of node
     */
    public int indexOfNode(Node node) {
        int i = 0;
        while ((node = node.getPreviousSibling()) != null) {
            i++;
        }
        return i;
    }

    public ArrayList<Node> getAllLeafNodes(Node node) {

        ArrayList<Node> list = new ArrayList<>();
        getAllLeafNodes(node, list);

        return list;
    }

    private ArrayList<String> GetParentSet(ArrayList<String> DataType) {
        ArrayList<String> ParentSet = new ArrayList<>();
        String Type;

        for (int i = 0; i < DataType.size(); i++) {
            Type = DataType.get(i);
            if (Type.equals("5") || Type.equals("6") || Type.equals("7") || Type.equals("8") || Type.equals("9")) {
                if (!ParentSet.contains("1")) {
                    ParentSet.add("1");
                }
            } else if (Type.equals("10") || Type.equals("11") || Type.equals("12") || Type.equals("13")) {
                if (!ParentSet.contains("2")) {
                    ParentSet.add("2");
                }
            } else if (Type.equals("14") || Type.equals("15")) {
                if (!ParentSet.contains("3")) {
                    ParentSet.add("3");
                }
            } else if (Type.equals("16") || Type.equals("17")) {
                if (!ParentSet.contains("4")) {
                    ParentSet.add("4");
                }
            }
        }

        Collections.sort(ParentSet);
        return ParentSet;
    }

    /*
     * get all leaf nodes of root node
     * store in list
     */
    public void getAllLeafNodes(Node node, ArrayList<Node> list) {
        if (node.hasChildNodes() && !skipTagList.contains(node.getNodeName())) {
            NodeList childList = node.getChildNodes();
            for (int i = 0; i < childList.getLength(); i++) {
                getAllLeafNodes(childList.item(i), list);
            }
        } else if (!node.getNodeName().equals("HTML") && !skipTagList.contains(node.getNodeName())) {
            list.add(node);
        }
    }

    /*
     * return the value of node
     */
    public String getNodeValue(Node node) {
        if (node.getNodeValue() == null) {
            /*
             * Choose the attribute that you need
             * ex: IMG --> src
             * other: tag name
             */
            if (node.getNodeName().equals("IMG")) {
                Node Src = node.getAttributes().getNamedItem("src");
                if (Src != null) {
                    return Src.getNodeValue();
                } else {
                    return null;
                }
            } else if (node.getNodeName().equals("LINK")) {
                return getHRef(node);
            } else {
                return null;
            }

        } else {
            /*
             * Text node, comment node... that has text content
             */

            return node.getNodeValue();
        }

    }

    /*
     * get the class of all leaf nodes
     */
    public String getHRef(Node node) {
        String Href = "";

        NamedNodeMap atts = node.getAttributes();
        if (atts != null) {
            for (int i = 0; i < atts.getLength(); i++) {
                Node att = atts.item(i);
                if (att.getNodeName().equals("href")) {
                    Href = att.getNodeValue();
                    break;
                }
            }
        }

        return Href;
    }

    public String giveTag(String Content, ArrayList<String> DecorTag) {
        String StartTag = "";
        String EndTag = "";
        int i;

        if (DecorTag.size() == 1) {
            StartTag = "<" + DecorTag.get(0) + ">";
        } else {
            i = DecorTag.size() - 1;
            while (i >= 0) {
                StartTag = StartTag + " <" + DecorTag.get(i) + ">";
                i--;
            }
        }

        if (DecorTag.size() == 1) {
            EndTag = "</" + DecorTag.get(0) + ">";
        } else {
            i = 0;
            while (i < DecorTag.size()) {
                EndTag = EndTag + " </" + DecorTag.get(i) + ">";
                i++;
            }
        }

        if (!StartTag.equals("")) {
            Content = StartTag + " " + Content;
        }

        if (!EndTag.equals("")) {
            Content = Content + " " + EndTag;
        }

        return Content;
    }

    public boolean checkPathSim(ArrayList<String> PathA, ArrayList<String> PathB) {
        int SizePathA = PathA.size();

        if (SizePathA != PathB.size()) {
            return false;
        } else {
            for (int i = 0; i < SizePathA; i++) {
                if (!PathA.get(i).equals(PathB.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }
}
