package Pages;

import com.microsoft.playwright.Page;
import java.util.List;
import java.util.stream.Collectors;

public class BasePage {
    protected Page page;

    //locators on the headers/footers which are common to all the pages of HC
    private String appsDropdown=".hc-static-dropdown--apps-dropdown";
    private String dropdownOptions = "static-dropdown .hc-static-dropdown__item-title";
    private String languageDropdown=".hc-static-dropdown--language-dropdown";

    public BasePage(Page page){
        this.page = page;
    }

    public List<String> getAllApps(){
        page.click(appsDropdown);
        List<String> appNames = page.querySelectorAll(dropdownOptions)
                .stream()
                .map(e -> e.getAttribute("title"))
                .collect(Collectors.toList());
        page.click(appsDropdown);
        return appNames;
    }

    public List<String> getAllLanguages(){
        page.click(languageDropdown);
        List<String> languages = page.querySelectorAll(dropdownOptions)
                .stream()
                .map(e -> e.getAttribute("title"))
                .collect(Collectors.toList());
        page.click(appsDropdown);
        return languages;
    }

    public void selectLanguage(String language){
        page.click(languageDropdown);
        page.click("[title='"+language+"']");
    }

    public void selectApp(String appTitle){
        if(!page.isVisible("[title='"+appTitle+"']")) {
            page.click(appsDropdown);
        }
        page.click("[title='"+appTitle+"']");
    }
}
