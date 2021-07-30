package Pages;

import Entities.Section;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class AppLandingPage extends BasePage {
    private String sections = "a[class='link home-section-link']";
    public AppLandingPage(Page page) {
        super(page);
    }

    public TreeMap<Integer,String> fetchSectionCardsForAnApp(){
        TreeMap<Integer,String> sectionDetails = new TreeMap<>();
        List<ElementHandle> sectionCards = page.querySelectorAll(sections);
        sectionCards.forEach((card) -> {
            String[] sectionurlParts = card.getAttribute("href").split("/");
            String sectionPart = sectionurlParts[sectionurlParts.length-1];
            int sectionId = Integer.parseInt(sectionPart.split("-")[0]);
            String sectionTitle = card.getAttribute("title");
            sectionDetails.put(sectionId,sectionTitle);
        });

        return sectionDetails;

    }
}
