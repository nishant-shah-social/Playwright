package Pages;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class SectionsPage extends BasePage {
    private String faqs = "a[class='link section-faq']";
    private String appBreadCrumb = "a[class='link home-page-url']";

    public SectionsPage(Page page) {
        super(page);
    }

    public TreeMap<Integer,String> fetchFAQsForASection(){
        TreeMap<Integer,String> faqDetails = new TreeMap<>();
        List<ElementHandle> faqList = page.querySelectorAll(faqs);
        faqList.forEach((faq) -> {
            String[] urlParts = faq.getAttribute("href").split("/");
            String faqPart = urlParts[urlParts.length-1];
            int faqId = Integer.parseInt(faqPart.split("-")[0]);
            String faqTitle = faq.
                    querySelector("div").
                    querySelector("p").innerText();
            faqDetails.put(faqId,faqTitle);
        });

        return faqDetails;
    }

    public void navigateToAppBreadCrumb(){
        page.click(appBreadCrumb);
    }
}
