package org.openmbee.syncservice.core.constants;


public class SyncServiceConstants {

    public static final String PROPERTY_FILE = "/application.properties";
    public static final String AUTHORIZATION = "Authorization";
    public static final String MMS_RESP_TWC_REVISION_ID = "twc-revisionId";

    public static final class API_ENDPOINTS {

        /**
         * It Holds the Constant API_AUTHENTICATE_TWC
         **/
//        public static final String SYNC_PROJECT_WITH_MMS = "/rest/syncProject/{resourceId}/{branchId}/{twcUrl}";
//        public static final String IS_BACKGROUND_SYNC_ENABLED = "/rest/isBackgroundSyncEnabled/{mmsURL}/{twcURL}";
//        public static final String GET_STATUS_FROM_QUEUE = "/rest/statusQueue";

    }

    public static final class LoggerStatements {
        public static final String METHOD_START_LOG = "Entering method: {}";
        public static final String METHOD_END_LOG = "Exiting method: {}";
        public static final String INFO_LOG = "Information: {}";
        public static final String WARN_LOG = "Warning: {}";
        public static final String ERROR_LOG = "Error occurred: Error description : {}";
    }
}
