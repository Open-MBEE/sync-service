package org.openmbee.syncservice.translation.twc19_3tomms4;

public class AsiSlotBaseBuilder extends Mms4ElementBuilder {

    public AsiSlotBaseBuilder(Twc19_3ToMms4Translator translator, TranslationContext context) {
        super(translator, context);
    }

    public String getAsiId(String esiId) {
        if(esiId == null) {
            return null;
        }
        String id = translateId(esiId);
        if(id == null) {
            return null;
        }
        return id + "_asi";
    }

    public String getSlotId(String asiId, String slotId) {
        return asiId + "-slot-" + slotId;
    }

}
