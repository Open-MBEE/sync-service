package org.openmbee.syncservice.core.utils;

import org.openmbee.syncservice.core.constants.SyncServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * SyncServiceUtil class is used to read the properties from property file
 *
 * @author Anil
 */
//TODO this class needs to go away
public class SyncServiceUtil {

    private static final Logger logger = LoggerFactory.getLogger(SyncServiceUtil.class);
    private static Properties prop;

    static {
        InputStream is = null;
        try {
            logger.info(SyncServiceConstants.LoggerStatements.INFO_LOG, "reading properties file");
            prop = new Properties();
            is = SyncServiceUtil.class.getResourceAsStream(SyncServiceConstants.PROPERTY_FILE);

            if (is != null) {
                prop.load(is);

            }

        } catch (FileNotFoundException e) {
            logger.error(SyncServiceConstants.LoggerStatements.ERROR_LOG, e.getMessage());
        } catch (IOException e) {
            logger.error(SyncServiceConstants.LoggerStatements.ERROR_LOG, e.getMessage());
        }
    }


    /**
     * Method is used to read the property from file
     *
     * @throws IOException
     */
    public static String getPropertyValue(String key) {
        logger.info(SyncServiceConstants.LoggerStatements.INFO_LOG, "fetching property value from properties file");
        return prop.getProperty(key);
    }

}
