package ApiHelper;

import Entities.FAQ;
import Utilities.ConfigFileReader;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RedirectConfig;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.List;

public class ApiCalls {

    private Cookies cookies;
    private Cookies newCookies;
    private String apiKey;
    ConfigFileReader configFileReader;
    Language langs;
    public ApiCalls() throws Exception {
        langs = new Language();
        configFileReader = new ConfigFileReader();
        apiKey = configFileReader.getValue("targetDomainApiKey");
    }

    public void xhrLogin(){
        String baseUri = "https://" +
                configFileReader.getValue("targetDomain") +
                "." +
                configFileReader.getValue("targetExtendedDomain") +
                "." +
                configFileReader.getValue("targetExtension");
        RestAssured.baseURI = baseUri;
        cookies = RestAssured.
                given().
                header("content-type", "application/x-www-form-urlencoded").
                header("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36").
                get(Routes.login).
                then().
                extract().
                response().
                detailedCookies();

        HashMap<String,String> formParams = new HashMap<>();
        formParams.put("username",configFileReader.getValue("admin_username"));
        formParams.put("password",configFileReader.getValue("admin_password"));
        formParams.put("_csrf_token",cookies.getValue("_csrf_token"));

        newCookies = RestAssured.
                given().
                config(RestAssured.config().redirect(RedirectConfig.redirectConfig().followRedirects(false))).
                header("Content-Type", "application/x-www-form-urlencoded").
                header("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36").
                header("Referer",baseUri+Routes.login).
                contentType(ContentType.URLENC.withCharset("UTF-8")).
                cookies(cookies).
                params(formParams).
                post(Routes.login).
                then().
                extract().
                response().
                getDetailedCookies();

    }

    public String createAppViaXHR(String appName, List<String> languages){
        String baseUri = "https://" +
                configFileReader.getValue("targetDomain") +
                "." +
                configFileReader.getValue("targetExtendedDomain") +
                "." +
                configFileReader.getValue("targetExtension");
        RestAssured.baseURI = baseUri;

        JSONObject application = new JSONObject();
        application.put("title",appName+java.time.LocalDate.now());
        application.put("url","nishant.com");
        application.put("platforms","[{\"platform_type\":\"ios\"},{\"platform_type\":\"android\"}]");
        application.put("_csrf_token",cookies.getValue("_csrf_token"));

        String appId = RestAssured.
                given().
                header("Host",configFileReader.getValue("targetDomain") +
                        "." +
                        configFileReader.getValue("targetExtendedDomain") +
                        "." +
                        configFileReader.getValue("targetExtension")).
                header("X-Requested-With","XMLHttpRequest").
                header("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36").
                cookies(cookies).
                cookies(newCookies).
                formParams(application).
                log().
                all().
                when().
                post(Routes.createApp).
                then().
                log().
                all().
                extract().
                response().
                jsonPath().
                getString("app.id");

        languages.forEach((language) -> {
            String langCode = langs.langMappings.get(language);
            List<String> lang = new ArrayList<>();
            lang.add(langCode);
            HashMap<String,Object> editApp = new HashMap<>();
            editApp.put("app_id",appId);
            editApp.put("languages","[\""+langCode+"\"]");
            editApp.put("activate",true);
            editApp.put("_csrf_token",cookies.getValue("_csrf_token"));

            RestAssured.
                    given().
                    header("Host",configFileReader.getValue("targetDomain") +
                            "." +
                            configFileReader.getValue("targetExtendedDomain") +
                            "." +
                            configFileReader.getValue("targetExtension")).
                    header("X-Requested-With","XMLHttpRequest").
                    header("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36").
                    cookies(cookies).
                    cookies(newCookies).
                    formParams(editApp).
                    log().all().
                    post(Routes.editApp).
                    then().
                    statusCode(200);
        });

        return appId;

    }

    public String createFAQSectionViaHSAPI(String appId, TreeMap<String,String> sectionNames){
        String baseUri = "https://api." +
                configFileReader.getValue("targetExtendedDomain") +
                "." +
                configFileReader.getValue("targetExtension") +
                "/v1/" +
                configFileReader.getValue("targetDomain");
        RestAssured.baseURI = baseUri;

        HashMap<String,Object> requestParams = new HashMap<>();
        HashMap<String,Object> translations = new HashMap<>();

        requestParams.put("app-id",appId);

        sectionNames.forEach((lang,sectionName) -> {
            translations.put(langs.langMappings.get(lang),sectionName);
        });
        requestParams.put("translations",translations);

        String sectionId = RestAssured.
                given().
                config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8"))).
                auth().
                preemptive().
                basic(apiKey, "").
                //contentType("application/json").
                header("Accept", ContentType.JSON.getAcceptHeader()).
                params(requestParams).
                log().
                all().
                post(Routes.createFAQSection).
                then().
                log().
                all().
                extract().
                response().
                jsonPath().
                getString("id");

        return sectionId;
    }


    public String createAndEditFAQViaHSAPI(String appId, String sectionId, TreeMap<String, FAQ> faqDetails){
        String baseUri = "https://api." +
                configFileReader.getValue("targetExtendedDomain") +
                "." +
                configFileReader.getValue("targetExtension") +
                "/v1/" +
                configFileReader.getValue("targetDomain");
        RestAssured.baseURI = baseUri;

        HashMap<String,Object> requestParams = new HashMap<>();
        HashMap<String,Object> translations = new HashMap<>();

        requestParams.put("app-id",appId);
        requestParams.put("section-id",sectionId);
        requestParams.put("published",true);

        faqDetails.forEach((lang,faq) -> {
            JSONObject faqs = new JSONObject();
            faqs.put("title",faq.faqTitle);
            faqs.put("body",faq.faqBody);
            faqs.put("enabled",true);
            translations.put(langs.langMappings.get(lang),faqs);
        });
        requestParams.put("translations",translations);

        String faqId =  RestAssured.
                given().
                config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8"))).
                auth().
                preemptive().
                basic(apiKey, "").
                header("Accept", ContentType.JSON.getAcceptHeader()).
                params(requestParams).
                post(Routes.createFAQ).
                then().
                statusCode(201).
                extract().
                response().
                jsonPath().
                getString("id");

        //editing the FAQ to enable the platforms
        //without this the FAQ wont be visible in the HelpCenter
        baseUri = "https://api." +
                configFileReader.getValue("targetExtendedDomain") +
                "." +
                configFileReader.getValue("targetExtension") +
                "/v1/" +
                configFileReader.getValue("targetDomain");
        RestAssured.baseURI = baseUri;

        requestParams.put("platform_types","[\"android\",\"ios\"]");

         RestAssured.
                given().
                config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8"))).
                auth().
                preemptive().
                basic(apiKey, "").
                header("Accept", ContentType.JSON.getAcceptHeader()).
                params(requestParams).
                post(Routes.editFAQ+"/"+faqId).
                then().
                statusCode(200);

        return faqId;
    }

}
