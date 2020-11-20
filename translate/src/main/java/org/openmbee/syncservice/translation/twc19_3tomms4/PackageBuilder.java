package org.openmbee.syncservice.translation.twc19_3tomms4;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openmbee.syncservice.sysml.syntax.SysMLv1X;


public class PackageBuilder extends Mms4ElementBuilder {

    public PackageBuilder(Twc19_3ToMms4Translator translator, TranslationContext context) {
        super(translator, context);

        //TODO Verify these are correct (based on assumption)
        addDetail(SysMLv1X.PROFILE_APPLICATION, this::translateIdArray, JSONArray.class, JSONArray.class);
        addDetail(SysMLv1X.URI, String.class);
        addDetail(SysMLv1X.PACKAGE_MERGES, this::translateIdArray, JSONArray.class, JSONArray.class);

    }


    @Override
    protected void transferDetails(JSONObject original, JSONObject out) {
        super.transferDetails(original, out);
    }
}
