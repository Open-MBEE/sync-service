package org.openmbee.syncservice.twc.syntax.fields;

import org.openmbee.syncservice.core.syntax.Fields;
import org.openmbee.syncservice.core.syntax.fields.CommonFields;
import org.openmbee.syncservice.core.syntax.fields.Field;
import org.openmbee.syncservice.core.syntax.fields.FieldId;
import org.openmbee.syncservice.sysml.syntax.SysMLv1X;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwcFields19_0_3 implements Fields {

    private final static Logger logger = LoggerFactory.getLogger(TwcFields19_0_3.class);

    @Override
    public <F extends FieldId, V> Field<F,V> getField(F field, Class<V> valueClass) {
        if (field instanceof CommonFields) {
            switch ((CommonFields) field) {

                case ID:
                    return TwcFieldBuilder.get(field, "ID", valueClass).atEsiData();
                default:
                    logger.error("Unimplemented Common field: " + field.toString());
                    return null;
            }

        } else if (field instanceof SysMLv1X) {
            switch ((SysMLv1X) field) {
                case ELEMENT_TYPE:
                    return TwcFieldBuilder.get(field, "@type", valueClass).atData(1);
                case OWNER_ID:
                    return TwcFieldBuilder.get(field, "@id", valueClass)
                            .atPath("kerml:owner")
                            .atData(1);
                case NAME:
                    return TwcFieldBuilder.get(field, "name", valueClass).atEsiData();
                case PROFILE_APPLICATION:
                    return TwcFieldBuilder.get(field, "profileApplication", valueClass).atEsiData();
                case REPRESENTATION:
                    return TwcFieldBuilder.get(field, "representation", valueClass).atEsiData();
                case POWERTYPE_EXTENT:
                    return TwcFieldBuilder.get(field, "powertypeExtent", valueClass).atEsiData();
                case TEMPLATE_BINDING:
                    return TwcFieldBuilder.get(field, "templateBinding", valueClass).atEsiData();
                case APPLIED_STEREOTYPE_INSTANCE:
                    return TwcFieldBuilder.get(field, "@id", valueClass).atPath("appliedStereotypeInstance").atEsiData();
                case TEMPLATE_PARAMETER:
                    return TwcFieldBuilder.get(field, "@id", valueClass).atPath("templateParameter").atEsiData();
                case IS_ACTIVE:
                    return TwcFieldBuilder.get(field, "@isActive", valueClass).atEsiData();
                case OWNED_ELEMENTS:
                    return TwcFieldBuilder.get(field, "ownedElement", valueClass).atEsiData();
                case CLIENT_DEPENDENCIES:
                    return TwcFieldBuilder.get(field, "clientDependency", valueClass).atEsiData();
                case SYNC_ELEMENT:
                    return TwcFieldBuilder.get(field, "@id", valueClass).atPath("syncElement").atEsiData();
                case SUPPLIER_DEPENDENCIES:
                    return TwcFieldBuilder.get(field, "supplierDependency", valueClass).atEsiData();
                case NAME_EXPRESSION:
                    return TwcFieldBuilder.get(field, "nameExpression", valueClass).atEsiData();
                case PACKAGE_IMPORTS:
                    return TwcFieldBuilder.get(field, "packageImport", valueClass).atEsiData();
                case VISIBILITY:
                    return TwcFieldBuilder.get(field, "visibility", valueClass).atEsiData();
                case ELEMENT_IMPORTS:
                    return TwcFieldBuilder.get(field, "elementImport", valueClass).atEsiData();
                case USE_CASES:
                    return TwcFieldBuilder.get(field, "useCase", valueClass).atEsiData();
                case CLASSIFIER_BEHAVIOR:
                    return TwcFieldBuilder.get(field, "@id", valueClass).atPath("classifierBehavior").atEsiData();
                case INTERFACE_REALIZATIONS:
                    return TwcFieldBuilder.get(field, "interfaceRealization", valueClass).atEsiData();
                case OWNED_ATTRIBUTES:
                    return TwcFieldBuilder.get(field, "ownedAttribute", valueClass).atEsiData();
                case SUBSTITUTIONS:
                    return TwcFieldBuilder.get(field, "substitution", valueClass).atEsiData();
                case REDEFINED_CLASSIFIERS:
                    return TwcFieldBuilder.get(field, "redefinedClassifier", valueClass).atEsiData();
                case IS_ABSTRACT:
                    return TwcFieldBuilder.get(field, "isAbstract", valueClass).atEsiData();
                case GENERALIZATIONS:
                    return TwcFieldBuilder.get(field, "generalization", valueClass).atEsiData();
                case OWNED_OPERATIONS:
                    return TwcFieldBuilder.get(field, "ownedOperation", valueClass).atEsiData();
                case COLLABORATION_USES:
                    return TwcFieldBuilder.get(field, "collaborationUse", valueClass).atEsiData();
                case IS_FINAL_SPECIALIZATION:
                    return TwcFieldBuilder.get(field, "isFinalSpecialization", valueClass).atEsiData();
                case URI:
                    return TwcFieldBuilder.get(field, "URI", valueClass).atEsiData();
                case PACKAGE_MERGES:
                    return TwcFieldBuilder.get(field, "packageMerge", valueClass).atEsiData();
                case DEFINING_FEATURE:
                    return TwcFieldBuilder.get(field, "@id", valueClass).atPath("definingFeature").atEsiData();
                case VALUE:
                    return TwcFieldBuilder.get(field, "value", valueClass).atEsiData();
                case ELEMENT_VALUE:
                    return TwcFieldBuilder.get(field, "@id", valueClass).atPath("element").atEsiData();
                case INSTANCE_VALUE:
                    return TwcFieldBuilder.get(field, "@id", valueClass).atPath("instance").atEsiData();
                default:
                    logger.error("Unimplemented SysML field: " + field.toString());
                    return null;
            }
        } else if(field instanceof TwcFields) {
            switch ((TwcFields) field) {
                case MD_EXTENTION:
                    return TwcFieldBuilder.get(field, "mdExtensions", valueClass).atEsiData();
                case ESI_ID:
                    return TwcFieldBuilder.get(field, "kerml:esiID", valueClass).atData(1);
                case DEFAULT_WORKING_PACKAGE:
                    return TwcFieldBuilder.get(field, "@id", valueClass).atPath("defaultWorkingPackage").atEsiData();
                case PROJECT_ID:
                    return TwcFieldBuilder.get(field, "PROJECT_ID", valueClass).atPath("metadata").build();
                default:
                    logger.error("Unimplemented TWC field: " + field.toString());
                    return null;
            }
        } else {
            logger.error("Unknown field: " + field.toString() + " of type " + field.getClass().getName());
            return null;
        }
    }
}
