package Entities;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

public class FAQLang {
    public int faq_id;
    public TreeMap<String,FAQ> faqLangMapping;

    public FAQLang(){
        faqLangMapping = new TreeMap<>();
    }
}
