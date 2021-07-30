import ApiHelper.ApiCalls;
import Entities.App;
import Entities.FAQ;
import Entities.FAQLang;
import Entities.Section;
import Pages.AppLandingPage;
import Pages.FAQPage;
import Pages.SectionsPage;
import Utilities.ConfigFileReader;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ImportFAQsTest extends BaseTest {

    @Test
    public void migrateFAQs() throws Exception {
        AppLandingPage appLandingPage = new AppLandingPage(page);
        FAQPage faqPage = new FAQPage(page);
        SectionsPage sectionsPage = new SectionsPage(page);
        String targetAppName = configFileReader.
                getValue("targetAppName");
        List<String> appNames = new ArrayList<>();

        //fetching all the apps of the source domain
        if(targetAppName == "all") {
            appNames = appLandingPage.getAllApps();
        }
        else{
            appNames = Arrays.asList(configFileReader.
                    getValue("targetAppName").
                    split(","));
        }


        List<App> applications = new ArrayList<>();
        List<FAQLang> allFAQs = new ArrayList<>();


        //iterating over all the apps and fetching FAQ Data from Source Domain
        appNames.forEach((appName) -> {
            appLandingPage.selectApp(appName);
            App app = new App();
            app.appName=appName;
            List<String> languages = appLandingPage.getAllLanguages();
            List<Section> appSections = new ArrayList<>();

            //fetching all the faq sections for the selected app & selected language
            TreeMap<Integer,String> sectionDetails = appLandingPage.fetchSectionCardsForAnApp();

            //iterating over all the sections of the selected app & language
            sectionDetails.forEach((sectionId,sectionTitle) -> {
                Section sec = new Section();
                sec.sectionId=sectionId;

                //open the sections page for a section
                page.click("a[title='"+sectionTitle+"']");

                //fetch all the FAQs for a particular section
                TreeMap<Integer,String> faqList = sectionsPage.fetchFAQsForASection();

                //iterating over all the faqs
                faqList.forEach((faqId,faqTitle) -> {
                        FAQLang faqLang = new FAQLang();
                        faqLang.faq_id=faqId;

                        //opening faq page for a specific faq
                        page.click(".section-faq-title >> text="+faqTitle);

                        //iterating over all the languages
                        languages.forEach((language) -> {
                            //selecting a language
                            faqPage.selectLanguage(language);
                            sec.sectionNames.put(language,faqPage.getSectionNameFromBreadCrumb());
                            //fetching faq details
                            FAQ secFaq = faqPage.getFaqDetails();
                            faqLang.faqLangMapping.put(language,secFaq);
                            app.languages.add(language);
                        });

                        allFAQs.add(faqLang);
                        sec.faqList.add(faqLang);
                        faqPage.selectLanguage("English");
                        faqPage.navigateToSectionsPage();
                    });
                    appSections.add(sec);
                    sectionsPage.navigateToAppBreadCrumb();
                });
            app.sections=appSections;
            applications.add(app);
        });

         ApiCalls apiCalls = new ApiCalls();
         
        //Inserting FAQ Data into Target Domain
        applications.forEach((app) -> {
            apiCalls.xhrLogin();
           String appId = apiCalls.createAppViaXHR(app.appName,new ArrayList<String>(app.languages));
            app.sections.forEach((section) -> {
                String sectionId = apiCalls.createFAQSectionViaHSAPI(appId,section.sectionNames);
                section.faqList.forEach(((faqLang) -> {
                    apiCalls.createAndEditFAQViaHSAPI(appId,sectionId,faqLang.faqLangMapping);
                }));
            });
        });

    }

    @AfterMethod
    public void takeScreenshot(ITestResult result){
        if(ITestResult.FAILURE == result.getStatus()){
            Path path = Paths.get("screenshot.png");
            Page.ScreenshotOptions sco = new Page.ScreenshotOptions();
            sco.fullPage=true;
            sco.path =path;
            page.screenshot(sco);
        }
    }
}
