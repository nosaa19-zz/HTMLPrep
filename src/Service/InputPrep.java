package Service;

import Domain.*;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputPrep {

    int DSize = 0;
    int idxPath = 0;
    int idxFeature = 0;

    static ArrayList<ArrayList<Element>> TableL = new ArrayList<>();
    static ArrayList<MstPathStr> MasterPath = new ArrayList<>();
    static ArrayList<FeatureDEC> MasterSimTEC = new ArrayList<>();
    static HashMap<String, Integer> MasterContent = new HashMap<>();
    HashMap<ArrayList<String>, Integer> MasterDataType = new HashMap<>();
    static HashMap<Integer, ArrayList<String>> LookUpDataType = new HashMap<>();
    static HashMap<Integer, String> LookUpContent = new HashMap<>();
    private boolean NotFoundInMaster;
    private String[] iFiles;

    public void Run(String Folder, File[] files, String selectedFiles) throws Exception {
        HTMLParser coreUnit = new HTMLParser();

        String OutPutFolder = Folder + "Output/";
        String TestFolder = Folder + "Test/";

        CheckFolder(OutPutFolder);

        coreUnit.readFiles(filteredFiles(files, selectedFiles));
        TableL = coreUnit.outputPath(true); //unique tags
        DSize = TableL.size();

        //ReadExistMaster(OutPutFolder);

        GenerateMaster();

        Encoding();

        ConvertMaster();

        /*Update Master File*/
        PrintMasterContent(OutPutFolder);
        PrintMasterPath(OutPutFolder);
        PrintMasterDataType(OutPutFolder);
        PrintMasterSimTEC(OutPutFolder);

        /*Print Test File*/
        PrintTableL(TestFolder);

    }

    private File[] filteredFiles(File[] inputFiles, String SelectedFiles){
        if(SelectedFiles == null) return inputFiles;

        iFiles = SelectedFiles.split(";");
        ArrayList<File> result = new ArrayList<>();

        for(String o : iFiles){
            result.add(inputFiles[Integer.valueOf(o)]);
        }

        return result.toArray(new File[0]);
    }

    private void CheckFolder(String Folder) {
        File CheckingFolder = new File(Folder);
        if (!CheckingFolder.exists()) {
            CheckingFolder.mkdir();
        }
    }

    private void ClearFolder(String Folder) {
        File CheckingFolder = new File(Folder);
        deleteDir(new File(Folder));
        CheckingFolder.mkdirs();
    }

    public void deleteDir(File dir) {
        File[] files = dir.listFiles();
        if(files != null) {
            for (final File file : files) {
                deleteDir(file);
            }
        }
        dir.delete();
    }


    private void ReadExistMaster(String OutPutFolder){
        ReadMasterPath(OutPutFolder);
        ReadMasterSimTEC(OutPutFolder);
        ReadMasterContent(OutPutFolder);
        ReadMasterDataType(OutPutFolder);

    }

    private void ReadMasterPath(String OutputFolder){
        File masterPathFile = new File(OutputFolder + "TXT/MasterPath.txt");
        String line = "";
        idxPath = 0;

        ArrayList<MstPathStrFirst> tempMasterPath = new ArrayList<>();

        try (BufferedReader buf = new BufferedReader(
                new InputStreamReader(new FileInputStream(masterPathFile), "UTF8"));) {
            int count = 0;
            while ((line=buf.readLine())!=null){
                if(count > 0) {
                    String[] path = line.split("\t");
                    if(idxPath < Integer.valueOf(path[0])){
                        idxPath = Integer.valueOf(path[0]);
                    }
                    tempMasterPath.add(new MstPathStrFirst(path[0], new ArrayList<String>(Arrays.asList((path[1].replaceAll("\\[|\\]|\\s+", "").split(","))))));
                }
                count++;
            }

        } catch (IOException ex) {
            Logger.getLogger(InputPrep.class.getName()).log(Level.SEVERE, null, ex);
        }

        Collections.sort(tempMasterPath);
        idxPath += 1;

        for(MstPathStrFirst o : tempMasterPath){
            MasterPath.add(new MstPathStr(o.GetIndex(), o.GetKey()));
        }
    }

    private void ReadMasterSimTEC(String OutputFolder){
        File masterSimTECFile = new File(OutputFolder + "TXT/MasterSimTEC.txt");
        String line = "";
        idxFeature = 0;

        try (BufferedReader buf = new BufferedReader(
                new InputStreamReader(new FileInputStream(masterSimTECFile), "UTF8"));) {
            int count = 0;
            while ((line=buf.readLine())!=null){
                if(count > 0) {
                    String[] simTEC = line.split("\t");

                    if(idxFeature < Integer.valueOf(simTEC[0])){
                        idxFeature = Integer.valueOf(simTEC[0]);
                    }

                    MasterSimTEC.add(new FeatureDEC(simTEC[0],
                            new ArrayList<>(Arrays.asList((simTEC[1].replaceAll("\\[|\\]|\\s+", "").split(",")))),
                            new ArrayList<>(Arrays.asList((simTEC[2].replaceAll("\\[|\\]|\\s+", "").split(",")))),
                            new ArrayList<>(Arrays.asList((simTEC[3].replaceAll("\\[|\\]|\\s+", "").split(","))))));
                }
                count++;
            }

        } catch (IOException ex) {
            Logger.getLogger(InputPrep.class.getName()).log(Level.SEVERE, null, ex);
        }

        Collections.sort(MasterSimTEC);
        idxFeature += 1;
    }

    private void ReadMasterContent(String OutputFolder){
        File masterContentFile = new File(OutputFolder + "TXT/MasterContent.txt");
        String line = "";

        try (BufferedReader buf = new BufferedReader(
                new InputStreamReader(new FileInputStream(masterContentFile), "UTF8"));) {
            int count = 0;
            while ((line=buf.readLine())!=null){
                if(count > 0) {
                    String[] content = line.split("\t");
                    MasterContent.put(content[1], Integer.valueOf(content[0]));
                }
                count++;
            }

        } catch (IOException ex) {
            Logger.getLogger(InputPrep.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void ReadMasterDataType(String OutputFolder){
        File masterDataTypeFile = new File(OutputFolder + "TXT/MasterDataType.txt");
        String line = "";

        try (BufferedReader buf = new BufferedReader(
                new InputStreamReader(new FileInputStream(masterDataTypeFile), "UTF8"));) {
            int count = 0;
            while ((line=buf.readLine())!=null){
                if(count > 0) {
                    String[] dataType = line.split("\t");
                    MasterDataType.put(new ArrayList<>(Arrays.asList((dataType[1].replaceAll("\\[|\\]|\\s+", "").split(",")))), Integer.valueOf(dataType[0]));
                }
                count++;
            }

        } catch (IOException ex) {
            Logger.getLogger(InputPrep.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private void GenerateMaster() {
        ArrayList<Element> aDoc;
        ArrayList<String> Path;
        ArrayList<String> Class;
        ArrayList<String> ID;
        Element aElm;

        int Pos;

        for (int iDoc = 0; iDoc < DSize; iDoc++) { //dynamic encode for all documents
            aDoc = TableL.get(iDoc);

            for (int iElm = 0; iElm < aDoc.size(); iElm++) { //dynamic encode for leaf nodes in a document
                aElm = aDoc.get(iElm);
                Path = aElm.getPath();
                Class = aElm.getClassess();
                ID = aElm.getID();

                //Modify MasterPath
                Pos = binarySearchIndex(Path);
                if (Pos < 0) {
                    Pos = binarySearchKey(Path);
                    if (NotFoundInMaster) {
                        if (Pos < 0) {
                            MasterPath.add(new MstPathStr(Integer.toString(idxPath), Path));
                        } else {
                            MasterPath.add(Pos, new MstPathStr(Integer.toString(idxPath), Path));
                        }
                        idxPath++;
                    }
                }

                //After we have time, we will think how to short ini object
                //Modify MasterSimTEC
                Pos = FindFeature(Path, Class, ID);
                if (Pos < 0) {
                    MasterSimTEC.add(new FeatureDEC(Integer.toString(idxFeature), Path, ID, Class));
                    idxFeature++;
                }

            }
        }

    }

    private int FindFeature(ArrayList<String> Path, ArrayList<String> Class, ArrayList<String> ID) {
        FeatureDEC aFDEC;
        int Pos = -1;
        double Equival;
        double MaxEquival = 0.0;
        double SimilarPath;
        double MaxSimPath = 0.0;

        for (int i = 0; i < MasterSimTEC.size(); i++) {
            aFDEC = MasterSimTEC.get(i);

            SimilarPath = CountLCS(Path, aFDEC.getPath());

            if (SimilarPath > MaxSimPath) {//for supporting heuristic SimTECId Href different with text content
                MaxSimPath = SimilarPath;
            }

            Equival = SimilarPath * BaseConstants.WP
                    //Equival = Sim.CountLCS(Path, aFDEC.GetPath()) * Par.GetWp()
                    + CountLCS(ID, aFDEC.getIDSet()) * BaseConstants.WI
                    + CountLCS(Class, aFDEC.getClassSet()) * BaseConstants.WC; //count equivalent features Path

            if (Equival == 1.0) { //if the given leaf node is exactly equivalent exit from this loop
                Pos = i;
                break;
            } else if (Equival >= BaseConstants.THR_DEC && Equival > MaxEquival) { //find the best equivalent
                MaxEquival = Equival;
                Pos = i;
            }

        }

        //heuristic too push SimTECId Href different with text content
        if (Path.contains("A") && MaxSimPath != 1.0) {
            Pos = -1;
        }
        return Pos;
    }

    public void Encoding() {
        String[] Dec = {"<B>", "<I>", "<EM>", "<STRONG>"}; //Equal decorative tag
        ArrayList<String> Decorative = new ArrayList(Arrays.asList(Dec));
        ArrayList<Element> aDoc;
        ArrayList<String> Path;
        ArrayList<String> IDSet;
        ArrayList<String> ClassSet;
        ArrayList<String> TypeSet;
        ArrayList<String> ParentSet;
        String[] ContentSplit;
        Element aElm;
        FeatureDEC aFDEC;
        String Content;
        String StrIContent;
        String StrIPath;
        String StridxType;
        String StridxDEC;
        boolean Found;
        double Equival;
        double MaxEquival;
        int idxContent = 1;
        int idxType = 1; //0 for grand parent
        int Pos;

        for (int iDoc = 0; iDoc < DSize; iDoc++) { //dynamic encode for all documents
            aDoc = TableL.get(iDoc);

            for (int iElm = 0; iElm < aDoc.size(); iElm++) { //dynamic encode for leaf nodes in a document

                aElm = aDoc.get(iElm);
                Path = aElm.getPath();
                IDSet = aElm.getID();
                ClassSet = aElm.getClassess();
                TypeSet = aElm.getDataType();
                ParentSet = aElm.getParentSet();

                //CEC encoding
                Pos = FindPath(Path);
                if (Pos > -1) {
                    StrIPath = MasterPath.get(Pos).GetIndex();
                    aDoc.get(iElm).setPathId(StrIPath);
                } else {
                    StrIPath = "";
                }

                //sederhanakan lagi ini
                Content = aElm.getContent();
                ContentSplit = Content.split(" ");
                if (Decorative.contains(ContentSplit[0])) {
                    Content = ContentSplit[1];
                    for (int i = 2; i < (ContentSplit.length - 2); i++) {
                        Content = Content + " " + ContentSplit[i];
                    }
                }

                Found = MasterContent.containsKey(Content);

                if (!Found) {
                    /* Content does not find in MasterCEC */
                    MasterContent.put(Content, idxContent);
                    StrIContent = Integer.toString(idxContent);
                    idxContent++;
                } else {
                    /* Content is found in MasterCEC */
                    StrIContent = Integer.toString(MasterContent.get(Content));

                }
                aDoc.get(iElm).setCECId(StrIPath + "-" + StrIContent);

                //Check and store ParentSet Code
                if (!MasterDataType.containsKey(ParentSet)) {
                    // ParentSet does not find in MasterDEC
                    aDoc.get(iElm).setPTypeSetId(Integer.toString(idxType));

                    MasterDataType.put(ParentSet, idxType);
                    idxType++;
                } else {
                    StridxType = Integer.toString(MasterDataType.get(ParentSet));
                    aDoc.get(iElm).setPTypeSetId(StridxType);
                }

                //Check and store TypeSet Code
                if (!MasterDataType.containsKey(TypeSet)) {
                    /* TypeSet does not find in MasterDEC */
                    StridxType = Integer.toString(idxType);
                    MasterDataType.put(TypeSet, idxType);
                    idxType++;
                } else {
                    /* TypeSet is found in MasterDEC */
                    StridxType = Integer.toString(MasterDataType.get(TypeSet));
                }

                //TEC encoding
                MaxEquival = 0.0;
                StridxDEC = "";
                for (int iFD = 0; iFD < MasterSimTEC.size(); iFD++) {
                    aFDEC = MasterSimTEC.get(iFD);
                    Equival = CountLCS(Path, aFDEC.getPath()) * BaseConstants.WP
                            + CountLCS(IDSet, aFDEC.getIDSet()) * BaseConstants.WI
                            + CountLCS(ClassSet, aFDEC.getClassSet()) * BaseConstants.WP; //count equivalent features Path

                    if (Equival == 1.0) { //if the given leaf node is exactly equivalent exit from this loop
                        StridxDEC = aFDEC.getidxFDEC();
                        break;
                    } else if (Equival >= BaseConstants.THR_DEC && Equival > MaxEquival) { //find the best equivalent
                        MaxEquival = Equival;
                        StridxDEC = aFDEC.getidxFDEC();
                    }

                }

                //aDoc.get(iElm).SetTECId(StridxDEC + "-0");
                //aDoc.get(iElm).SetTECId(StridxDEC + "-" + StridxType);
                aDoc.get(iElm).setSimSeqId(StridxDEC);
                aDoc.get(iElm).setTypeSetId(StridxType);
            }
        }

        /*System.out.println("Master Path");
        for (int j = 0; j < MasterPath.size(); j++) {
            System.out.println(MasterPath.get(j).GetIndex() + "\t" + MasterPath.get(j).GetKey());
        }
        System.out.println("=====");

        System.out.println("Master Feature");
        for (int j = 0; j < MasterSimTEC.size(); j++) {
            System.out.println(MasterSimTEC.get(j).getidxFDEC() + "\t" + MasterSimTEC.get(j).getPath()+
                    "\t" + MasterSimTEC.get(j).getClassSet()+"\t" + MasterSimTEC.get(j).getIDSet());
        }
        System.out.println("=====");*/
    }

    private void ConvertMaster() {

        Map.Entry<ArrayList<String>, Integer> entry;
        Map<ArrayList<String>, Integer> map = MasterDataType;
        Iterator<Map.Entry<ArrayList<String>, Integer>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            entry = entries.next();
            LookUpDataType.put(entry.getValue(), entry.getKey());
        }
        MasterDataType.clear(); //After conversion we delete MasterDataType

        Map.Entry<String, Integer> entryCont;
        Map<String, Integer> mapCont = MasterContent;
        Iterator<Map.Entry<String, Integer>> entriesCont = mapCont.entrySet().iterator();
        while (entriesCont.hasNext()) {
            entryCont = entriesCont.next();
            LookUpContent.put(entryCont.getValue(), entryCont.getKey());
        }
        MasterContent.clear(); //After conversion we delete MasterContent

        /* Sort MasterSimTEC based on MstPathStr.Index to make faster search by binary search */
        Collections.sort(MasterSimTEC);

        /* Sort MasterPath based on FeatureDEC.idxFDEC to make faster search by binary search */
        Collections.sort(MasterPath);
    }

    private int FindPath(ArrayList<String> Path) {
        int Pos = -1;
        double Equival;
        double MaxEquival = 0.0;

        for (int i = 0; i < MasterPath.size(); i++) {

            Equival = CountLCS(Path, MasterPath.get(i).GetKey());
            if (Equival >= BaseConstants.THR_LEC && Equival > MaxEquival) {
                Pos = i;
                MaxEquival = Equival;
            }

        }
        return Pos;
    }

    /* Check the path in MasterPath */
    public int binarySearchIndex(ArrayList<String> PathSearch) {
        double MaxEquival = 0.0;
        int InsertPosition = -1;
        double Equival;
        int low = 0;
        int middle;
        int size = MasterPath.size();
        int high = size - 1;
        int CmpRst;
        NotFoundInMaster = true;

        while (high >= low) {
            middle = (low + high) / 2;

            CmpRst = CompareToMaster(PathSearch, MasterPath.get(middle).GetKey());
            if (CmpRst == 0) { //PathSearch is equal with MasterPath(middle)
                NotFoundInMaster = false;
                return middle;
            } else {
                Equival = CountLCS(PathSearch, MasterPath.get(middle).GetKey());
                if (Equival >= BaseConstants.THR_LEC && Equival > MaxEquival) {
                    MaxEquival = Equival;
                    InsertPosition = middle;
                    NotFoundInMaster = false;
                }

                if (CmpRst < 0) { //PathSearch is lower with MasterPath(middle)
                    high = middle - 1;
                } else { //PathSearch is bigger with MasterPath(middle)
                    low = middle + 1;
                    middle++;
                }
            }
        }

        //if there is A tag, MaxEquival must be 1
        /*if(PathSearch.contains("A") && MaxEquival != 1.0){
            InsertPosition = -1;
        }*/

        return InsertPosition;
    }

    //Compare each string Master and KeySearch in the same position
    //diharapkan Master <= KeySearch ==> true
    private int CompareToMaster(ArrayList<String> KeySearch, ArrayList<String> Master) {
        int Size;
        int Result;

        if (Master.size() <= KeySearch.size()) {
            Size = Master.size();
        } else {
            Size = KeySearch.size();
        }

        for (int i = 0; i < Size; i++) {
            Result = KeySearch.get(i).compareTo(Master.get(i));

            if (Result > 0 || Result < 0) {
                return Result;
            }
        }

        //only for Result = 0
        if (KeySearch.size() == Master.size()) {
            return 0;
        } else if (KeySearch.size() < Master.size()) {
            return -1;
        } else {
            return 1;
        }
    }

    public int binarySearchKey(ArrayList<String> PathSearch) {
        int low = 0;
        int middle = -1;
        int size = MasterPath.size();
        int high = size - 1;
        int CmpRst;
        NotFoundInMaster = true;

        while (high >= low) {
            middle = (low + high) / 2;

            CmpRst = CompareToMaster(PathSearch, MasterPath.get(middle).GetKey());
            if (CmpRst == 0) { //PathSearch is equal with MasterPath(middle)
                NotFoundInMaster = false;
                return middle;
            } else if (CmpRst < 0) { //PathSearch is lower with MasterPath(middle)
                high = middle - 1;
            } else { //PathSearch is bigger with MasterPath(middle)
                low = middle + 1;
                middle++;
            }
        }
        return middle;
    }

    public ArrayList<ArrayList<Element>> GetTableL() {
        return TableL;
    }

    public double CountLCS(ArrayList<String> DT1, ArrayList<String> DT2) {
        double Count = 0.0;

        int M = DT1.size();
        int N = DT2.size();

        if (M == 0 && N == 0) {
            return 1.0;
        }

        // opt[i][j] = length of LCS of DT1[i..M] and DT2[j..N]
        int[][] opt = new int[M + 1][N + 1];

        // compute length of LCS and all subproblems via dynamic programming
        for (int i = M - 1; i >= 0; i--) {
            for (int j = N - 1; j >= 0; j--) {
                if (DT1.get(i).equals(DT2.get(j))) {
                    opt[i][j] = opt[i + 1][j + 1] + 1;
                } else {
                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
                }
            }
        }

        // recover LCS itself and print it to standard output
        int i = 0, j = 0;
        while (i < M && j < N) {
            if (DT1.get(i).equals(DT2.get(j))) {
                Count++;
                i++;
                j++;
            } else if (opt[i + 1][j] >= opt[i][j + 1]) {
                i++;
            } else {
                j++;
            }
        }
        return Count / Math.max(M, N);
    }

    /* Print Content encoding in MasterContent */
    private void PrintMasterContent(String Folder) throws UnsupportedEncodingException, FileNotFoundException {
        int bufferSize = 1024;

        PrintStream StdOut = System.out;
        PrintStream fileStream;
        fileStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(Folder + "TXT/MasterContent.txt", false), bufferSize),
                true, "UTF-8");
        System.setOut(fileStream);
        Map.Entry<Integer, String> entry;

        System.out.println("ContentId \t Content");
        Iterator<Map.Entry<Integer, String>> entries = LookUpContent.entrySet().iterator();
        while (entries.hasNext()) {
            entry = entries.next();
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }

        System.out.close();
        System.setOut(StdOut);
    }

    /* Print Path encoding in MasterPath */
    private void PrintMasterPath(String Folder) throws UnsupportedEncodingException, FileNotFoundException {
        MstPathStr aMst;
        int SizeMst = MasterPath.size();
        int bufferSize = 1024;

        PrintStream StdOut = System.out;
        PrintStream fileStream;
        fileStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(Folder + "TXT/MasterPath.txt", false), bufferSize),
                true, "UTF-8");
        System.setOut(fileStream);

        System.out.println("PathId \t Path");
        for (int i = 0; i < SizeMst; i++) {
            aMst = MasterPath.get(i);
            System.out.println(aMst.GetIndex() + "\t" + aMst.GetKey());
        }

        System.out.close();
        System.setOut(StdOut);
    }

    /* Print data type encoding in MasterDataType */
    private void PrintMasterDataType(String Folder) throws UnsupportedEncodingException, FileNotFoundException {
        int bufferSize = 1024;

        PrintStream StdOut = System.out;
        PrintStream fileStream;
        fileStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(Folder + "TXT/MasterDataType.txt", false), bufferSize),
                true, "UTF-8");
        System.setOut(fileStream);
        Map.Entry<Integer, ArrayList<String>> entry;

        System.out.println("TypeSetId \t TypeSet");
        Iterator<Map.Entry<Integer, ArrayList<String>>> entries = LookUpDataType.entrySet().iterator();
        while (entries.hasNext()) {
            entry = entries.next();
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }

        System.out.close();
        System.setOut(StdOut);
    }

    private void PrintMasterSimTEC(String Folder) throws UnsupportedEncodingException, FileNotFoundException {
        FeatureDEC aMst;
        int SizeMst = MasterSimTEC.size();
        int bufferSize = 1024;

        PrintStream StdOut = System.out;
        PrintStream fileStream;
        fileStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(Folder + "TXT/MasterSimTEC.txt", false), bufferSize),
                true, "UTF-8");
        System.setOut(fileStream);

        System.out.println("SimTECId \t Path \t ClassSeq \t IDSeq");
        for (int i = 0; i < SizeMst; i++) {
            aMst = MasterSimTEC.get(i);
            System.out.println(aMst.getidxFDEC() + "\t" + aMst.getPath() + "\t" + aMst.getIDSet() + "\t" + aMst.getClassSet());
        }

        System.out.close();
        System.setOut(StdOut);
    }

    private void PrintTableL(String Folder) throws UnsupportedEncodingException, FileNotFoundException {

        ClearFolder(Folder);

        ArrayList<ArrayList<Element>> TableL = GetTableL();
        ArrayList<Element> aDoc;
        Element aLN;
        int bufferSize = 30 * 1024;
        int DSize = TableL.size();
        int NumLN;

        String outputFileName;

        PrintStream StdOut = System.out;
        PrintStream fileStream;

        for (int iDoc = 0; iDoc < DSize; iDoc++) {

            outputFileName = iFiles == null ? "page-" + String.format("%04d", (iDoc+1)) + ".txt" :
                                              "page-" + String.format("%04d", (Integer.valueOf(iFiles[iDoc])+1)) + ".txt";

            fileStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(Folder + outputFileName, false), bufferSize),
                    true, "UTF-8");
            System.setOut(fileStream);

            aDoc = TableL.get(iDoc);
            NumLN = aDoc.size();

            System.out.println("Leaf Index \t Content \t Path \t IDSeq \t ClassSeq \t TypeSet \t PTypeSetId \t TypeSetId "
                    + "\t CECId \t PathId \t SimTECId");
            for (int iLN = 0; iLN < NumLN; iLN++) {
                aLN = aDoc.get(iLN);
                System.out.println(aLN.getLeafIndex() + "\t" + aLN.getContent() + "\t" + aLN.getPath() + "\t" + aLN.getID()
                        + "\t" + aLN.getClassess() + "\t" + aLN.getDataType() + "\t" + aLN.getPTypeSetId() + "\t" + aLN.getTypeSetId()
                        + "\t" + aLN.getCECId() + "\t" + aLN.getPathId() + "\t" + aLN.getSimSeqId());
            }
            System.out.println();
        }

        System.out.close();
        System.setOut(StdOut);

    }
}
