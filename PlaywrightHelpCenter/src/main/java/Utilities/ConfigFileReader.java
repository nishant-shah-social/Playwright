package Utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigFileReader {
    private ConfigFileReader configFileReader;
    private Properties properties = new Properties();
    private String PROPERTY_FILE_PATH = "configs/config.properties";

    public ConfigFileReader() throws Exception {
        BufferedReader reader;
        try{
            reader = new BufferedReader(new FileReader(PROPERTY_FILE_PATH));
            properties.load(reader);
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new Exception("Config properties not found at location:- "+PROPERTY_FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /* public ConfigFileReader getInstance(){
        if(configFileReader == null){
            synchronized (ConfigFileReader.class){
                try{
                    configFileReader = new ConfigFileReader();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return configFileReader;
    }*/

    public String getValue(String key){
        return properties.getProperty(key);
    }

}
