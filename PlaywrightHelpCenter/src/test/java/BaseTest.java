import Utilities.ConfigFileReader;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.BrowserChannel;
import com.microsoft.playwright.options.ViewportSize;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import java.util.List;

public class BaseTest {

    protected Browser browser;
    protected Page page;
    ConfigFileReader configFileReader;

    @Parameters("browserType")
    @BeforeClass
    public void setUp(String browserType) throws Exception {
        if(browserType.equals("chrome")) {
            browser = Playwright
                    .create()
                    .chromium()
                    .launch(new BrowserType.LaunchOptions().setHeadless(false)
                            .setArgs(List.of("--start-maximized")));
        }
        else if(browserType.equals("safari")){
            browser = Playwright
                    .create()
                    .webkit()
                    .launch(new BrowserType.LaunchOptions().setHeadless(false)
                            .setArgs(List.of("--start-maximized")));
        }
        else{
            browser = Playwright
                    .create()
                    .firefox()
                    .launch(new BrowserType.LaunchOptions().setHeadless(false)
                            .setArgs(List.of("--start-maximized")));
        }

        page = browser.newPage();
        page.setViewportSize(1920,1080);
        configFileReader = new ConfigFileReader();
        String url = "https://" +
                configFileReader.getValue("rootDomain") +
                    "." +
                configFileReader.getValue("rootExtendedDomain") +
                    "." +
                configFileReader.getValue("rootExtension");

        page.navigate(url);
    }

}
