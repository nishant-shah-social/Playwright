package Entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class App {
    public String appName;
    public List<Section> sections;
    public HashSet<String> languages;

    public App(){
        sections = new ArrayList<>();
        languages = new HashSet<>();
    }
}
