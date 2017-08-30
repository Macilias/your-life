package com.macilias.apps.model;

/**
 * Common settings for the application, database and other
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public interface Settings {

    String APPLICATION = "http://your-life.rocks/app#";
    String SIDEKICK = "http://your-life.rocks/sidekick#";
    String ANNA = "http://your-life.rocks/anna#";

    int FUSEKI_PORT = 4321;

    String CROWD_TANGLE_API_TOKEN = System.getenv("CROWD_TANGLE_API_TOKEN");
    String CROWD_TANGLE_LIST_ID = System.getenv("CROWD_TANGLE_LIST_ID");

    String FACEBOOK_APP_ID = System.getenv("FACEBOOK_APP_ID");
    String FACEBOOK_APP_SECRET = System.getenv("FACEBOOK_APP_SECRET");
    String FACEBOOK_ACCESS_TOKEN = System.getenv("FACEBOOK_ACCESS_TOKEN");
    String FACEBOOK_PERMISSIONS = System.getenv("FACEBOOK_PERMISSIONS");
    String FACEBOOK_CALLBACK_URL = System.getenv("FACEBOOK_CALLBACK_URL");

    static String getPrefix() {
        return "prefix sidekick:<" + SIDEKICK + "> \n";
    }

}
