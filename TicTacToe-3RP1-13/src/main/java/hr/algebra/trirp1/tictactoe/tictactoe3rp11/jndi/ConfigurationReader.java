package hr.algebra.trirp1.tictactoe.tictactoe3rp11.jndi;

import javax.naming.Context;
import javax.naming.NamingException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationReader {

    private ConfigurationReader() {

    }

    private static Properties properties;

    static {
        properties  = new Properties();

        Properties configurationProperties = new Properties();
        configurationProperties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
        configurationProperties.put(Context.PROVIDER_URL, "file:./conf/");

        try(InitialDirContextCloseable context = new InitialDirContextCloseable(configurationProperties)) {
            Object configurationObject = context.lookup("app.conf");
            properties.load(new FileReader(configurationObject.toString()));
        }
        catch (NamingException | IOException e) {
            e.printStackTrace();
        }
    }

    public static String getStringValueForKey(ConfigurationKey key) {
        return (String) properties.get(key.getKey());
    }

    public static Integer getIntegerValueForKey(ConfigurationKey key) {
        return Integer.valueOf((String) properties.get(key.getKey()));
    }

}
