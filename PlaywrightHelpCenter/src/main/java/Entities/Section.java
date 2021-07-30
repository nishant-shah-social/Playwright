package Entities;

import java.util.*;

public class Section {
    public Integer sectionId;
    public TreeMap<String,String> sectionNames;
    public List<FAQLang> faqList;

    public Section(){
        sectionNames = new TreeMap<>();
        faqList = new ArrayList<>();
    }
}
