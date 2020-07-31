/*
 * CheckDataType project is used to find Data Type for every element merged HTML
 * Data Type 8: mixed characters, 5: all capital letters, 6: small letters, 7: first capital letter, 13: percentage, 14: date, 15: time, 17: url, 16: email, 
 *    12: currency, 11: decimal, 10: integer, and 9: punctuation
 */
package Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Oviliani
 */
public class CheckDataType {
    private String Content;
    
    public void SetContent(String Data){
        this.Content=Data;
    }
    
    public String ProcessDataType() {
        //CheckDataType Process = new CheckDataType();
        ArrayList<String> CharSymbol = new ArrayList<>();

        CharSymbol.add("(");
        CharSymbol.add(")");
        CharSymbol.add("[");
        CharSymbol.add("]");
        CharSymbol.add("{");
        CharSymbol.add("}");
        CharSymbol.add("<");
        CharSymbol.add(">");

        CharSymbol.add(":");
        CharSymbol.add(";");
        CharSymbol.add(","); // untuk sementara ini tidak disertakan karena "," sebagai pemisah data disjuctive
        CharSymbol.add(".");
        CharSymbol.add("?");
        CharSymbol.add("!");
        CharSymbol.add("#");
        //CharSymbol.add("%");
        //CharSymbol.add("+");
        //CharSymbol.add("-");
        CharSymbol.add("=");
        CharSymbol.add("\\");
        CharSymbol.add("/");
        CharSymbol.add("|");
        CharSymbol.add("&");

        int Result;
        int Length;
        String[] SubContent;

        ArrayList<Integer> ResultDT = TokenizerStr();
        
        if (!this.Content.isEmpty()) {
            
            SubContent = this.Content.split("[ ]+");

            for (int i = 0; i < SubContent.length; i++) {
                if ("".equals(SubContent[i])) {
                    continue;
                }

                Result = 0;
                while (SubContent[i].length() > 0) {   //check prefix

                    //Check punctuation mark at the beginning of string
                    if (CharSymbol.contains(SubContent[i].substring(0, 1))) {
                        Result = 9;
                        if (!ResultDT.contains(9)) {
                            ResultDT.add(9);
                        }
                        SubContent[i] = SubContent[i].substring(1);
                        if (SubContent[i].isEmpty()) {
                            break;
                        }
                    } else {
                        break;
                    }

                }

                while (SubContent[i].length() > 0) {   //check sufix
                    Length = SubContent[i].length() - 1;

                    if (CharSymbol.contains(SubContent[i].substring(Length))) {
                        Result = 9;
                        if (!ResultDT.contains(9)) {
                            ResultDT.add(9);
                        }

                        SubContent[i] = SubContent[i].substring(0, Length);
                        if (SubContent[i].isEmpty()) {
                            break;
                        }
                    } else {
                        break;
                    }
                }

                if (!SubContent[i].isEmpty()) {
                    Result = TokenizerSubStr(SubContent[i]);
                }

                if (!ResultDT.contains(Result)) {
                    ResultDT.add(Result);
                }

            }
        }
        //System.out.println(Content);
        Collections.sort(ResultDT);
        if (ResultDT.get(0) == 0) {
            ResultDT.remove(0);
        }

        String FinalResult = "";
        for (int i = 0; i < ResultDT.size(); i++) {
            FinalResult = FinalResult + ResultDT.get(i) + " ";
        }

        //System.out.println(FinalResult);
        return FinalResult.trim();
    }
    
    private int TokenizerSubStr(String SubContent) {

        String integerExp[] = {"^(\\+|-)?[\\d|,]+$"};
        String dateExp[] = {"^((\\d{2}[-/]\\d{2}[-/]\\d{4})|(\\d{4}[-/]\\d{2}[-/]\\d{2})|"
            + "((JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec|"
            + "January|February|March|April|May|June|July|August|September|October|November|December)\\s\\d{2},\\s\\d{4}))$"};
        String timeExp[] = {"^([aApP][mM])?(([0-1]?[0-9])|([2][0-3])):([0-5]?[0-9])(:([0-5]?[0-9]))?([aApP][mM])?$"};
        //String urlExp[] = {"^(?i)((mailto\\:|(news|(ht|f)tp(s?))\\://){1}\\S+)?$", "^[a-zA-Z]+://(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*(\\?\\s*)?$", 
        //    "^(http(s{0,1})://|www\\.)[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*$"};
        //String urlExp[] = {"^(?!mailto:)(?:(?:http|https|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?:(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[0-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]+-?)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]+-?)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,})))|localhost)(?::\\d{2,5})?(?:(/|\\?|#)[^\\s]*)?$"};
        String urlExp[] = {"https?:\\/\\/(?:www\\.|(?!www))[^\\s\\.]+\\.[^\\s]{2,}|www\\.[^\\s]+\\.[^\\s]{2,}"};

        String emailExp[] = {"^[\\w-]+(\\.[\\w-]+|\\.)*@[\\w-]+(\\.[\\w-]+)+$"};
        String currencyExp[] = {"^(\\+|-)?(\\$|£|€)(\\d+|\\.|\\,)+$"};
        String percentageExp[] = {"^(\\+|-)?[\\d+|\\,|\\.]+%$"};
        String decimalExp[] = {"^(\\+|-)?(\\d+|\\,|\\.)+$"};
        String capitalExp[] = {"^[A-Z]+$"};
        String smallExp[] = {"^[a-z]+$"};
        String FirstCapitalExp[] = {"^[A-Z][a-z]+$"};
        //String MixCharExp[] = {"^([A-Z]|[a-z]|[0-9])*$"}; //dijadikan result default

        Pattern SubContentPattern;
        Matcher matcher;

        int Result = 8;

        SubContentPattern = Pattern.compile(percentageExp[0]);
        matcher = SubContentPattern.matcher(SubContent);
        if (matcher.find()) {
            Result = 13; //Percentage
        } else {
            SubContentPattern = Pattern.compile(integerExp[0]);
            matcher = SubContentPattern.matcher(SubContent);
            if (matcher.find()) {
                //System.out.println(Content + "... " + matcher.regionStart() + " " + matcher.regionEnd());
                Result = 10; //Integer
            } else {
                SubContentPattern = Pattern.compile(dateExp[0]);
                matcher = SubContentPattern.matcher(SubContent);
                if (matcher.find()) {
                    Result = 14; //Date
                } else {
                    SubContentPattern = Pattern.compile(timeExp[0]);
                    matcher = SubContentPattern.matcher(SubContent);
                    if (matcher.find()) {
                        Result = 15; //Time
                    } else {
                        SubContentPattern = Pattern.compile(urlExp[0]);
                        matcher = SubContentPattern.matcher(SubContent);
                        if (matcher.find()) {
                            Result = 17; //Url
                        } else {
                            SubContentPattern = Pattern.compile(emailExp[0]);
                            matcher = SubContentPattern.matcher(SubContent);
                            if (matcher.find()) {
                                Result = 16; //Email
                            } else {
                                SubContentPattern = Pattern.compile(currencyExp[0]);
                                matcher = SubContentPattern.matcher(SubContent);
                                if (matcher.find()) {
                                    Result = 12; //Currency
                                } else {
                                    SubContentPattern = Pattern.compile(decimalExp[0]);
                                    matcher = SubContentPattern.matcher(SubContent);
                                    if (matcher.find()) {
                                        Result = 11; //Decimal
                                    } else {
                                        /* Check string property: capital, first letter capital, or small sub content*/
                                        SubContentPattern = Pattern.compile(capitalExp[0]);
                                        matcher = SubContentPattern.matcher(SubContent);
                                        if (matcher.find()) {
                                            Result = 5; //All_Capital
                                        } else {
                                            SubContentPattern = Pattern.compile(smallExp[0]);
                                            matcher = SubContentPattern.matcher(SubContent);
                                            if (matcher.find()) {
                                                Result = 6; //small letters
                                            } else {
                                                SubContentPattern = Pattern.compile(FirstCapitalExp[0]);
                                                matcher = SubContentPattern.matcher(SubContent);
                                                if (matcher.find()) {
                                                    Result = 7; //First_Capital
                                                    //} else {
                                                    //    SubContentPattern = Pattern.compile(MixCharExp[0]);
                                                    //    matcher = SubContentPattern.matcher(Content);
                                                    //    if (matcher.find()) {
                                                    //        Result = 1; //Mixed Characters
                                                    //    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return Result;
    }

    private ArrayList<Integer> TokenizerStr() {
        ArrayList<Integer> ResultDT = new ArrayList<>();

        String dateExp[] = {"(((JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec|"
            + "January|February|March|April|May|June|July|August|September|October|November|December)(\\s)+\\d{1,2},\\s\\d{4}))"};
        String timeExp[] = {"([aApP][mM])?(\\s)+(([0-1]?[0-9])|([2][0-3])):([0-5]?[0-9])(:([0-5]?[0-9]))?(\\s)+([aApP][mM])?"};
        String currencyExp[] = {"(\\+|-)?(\\s)*($|£|€)(\\s)*(?!0\\.00)[1-9]\\d{0,2}(,\\d{3})*(\\.\\d\\d)?"};
        String percentageExp[] = {"(\\+|-)?[\\d+|\\,|\\.]+(\\s)+%"};

        Pattern SubContentPattern;
        Matcher matcher;
        String FoundStr;
        int Result = Integer.MAX_VALUE;

        while (Result != 0) {
            Result = 0;
            SubContentPattern = Pattern.compile(percentageExp[0]);
            matcher = SubContentPattern.matcher(this.Content);
            if (matcher.find()) {
                Result = 13; //Percentage
            } else {
                SubContentPattern = Pattern.compile(dateExp[0]);
                matcher = SubContentPattern.matcher(this.Content);
                if (matcher.find()) {
                    Result = 14; //Date
                } else {
                    SubContentPattern = Pattern.compile(timeExp[0]);
                    matcher = SubContentPattern.matcher(this.Content);
                    if (matcher.find()) {
                        Result = 15; //Time
                    } else {
                        SubContentPattern = Pattern.compile(currencyExp[0]);
                        matcher = SubContentPattern.matcher(this.Content);
                        if (matcher.find()) {
                            Result = 12; //Currency
                        }
                    }
                }
            }

            if (Result != 0) {
                FoundStr = this.Content.substring(matcher.start(), matcher.end());
                this.Content = this.Content.replace(FoundStr, "");
                //Content.replace(FoundStr, "");
                if (!ResultDT.contains(Result)) {
                    ResultDT.add(Result);
                }
            }
        }

        return ResultDT;
    }

}
