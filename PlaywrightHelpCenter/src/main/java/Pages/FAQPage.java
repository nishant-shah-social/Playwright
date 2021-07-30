package Pages;

import Entities.FAQ;
import com.microsoft.playwright.Page;

public class FAQPage extends BasePage {
    String faqTitle = ".faq-title";
    String faqBody = ".article-body";
    String sectionBreadCrumb = ":nth-match(a[class='link faq-breadcrumb-link'],2)";
    public FAQPage(Page page) {
        super(page);
    }

    public FAQ getFaqDetails(){
        FAQ faq = new FAQ();
        faq.faqTitle = page.querySelector(faqTitle).innerText();
        faq.faqBody = page.querySelector(faqBody).innerHTML();
        return faq;
    }

    public void navigateToSectionsPage() {
        page.click(sectionBreadCrumb);
    }

    public String getSectionNameFromBreadCrumb() {
        return page.querySelector(sectionBreadCrumb).innerText().trim();
    }
}
