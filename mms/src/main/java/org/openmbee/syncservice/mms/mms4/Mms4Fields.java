package org.openmbee.syncservice.mms.mms4;

import org.openmbee.syncservice.core.syntax.Fields;
import org.openmbee.syncservice.core.syntax.fields.CommonFields;
import org.openmbee.syncservice.core.syntax.fields.Field;
import org.openmbee.syncservice.core.syntax.fields.FieldBuilder;
import org.openmbee.syncservice.core.syntax.fields.FieldId;
import org.openmbee.syncservice.sysml.syntax.SysMLv1X;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mms4Fields implements Fields {
    private static final Logger logger = LoggerFactory.getLogger(Mms4Fields.class);

    @Override
    public <F extends FieldId, T> Field<F, T> getField(F field, Class<T> valueClass) {
        if(field instanceof CommonFields) {
            switch ((CommonFields)field) {
                case ID:
                    return FieldBuilder.get(field, "id", valueClass).build();
                default:
                    logger.error("Unimplemented Common field: " + field.toString());
                    return null;
            }
        } else if(field instanceof SysMLv1X) {
            switch ((SysMLv1X) field) {
                case ELEMENT_TYPE:
                    return FieldBuilder.get(field, "type", valueClass).build();
                case OWNER_ID:
                    return FieldBuilder.get(field, "ownerId", valueClass).build();
                case NAME:
                    return FieldBuilder.get(field, "name", valueClass).build();
                case PROFILE_APPLICATION:
                    return FieldBuilder.get(field, "profileApplicationIds", valueClass).build();
                case REPRESENTATION:
                    return FieldBuilder.get(field, "representationId", valueClass).build(); //TODO verify
                case POWERTYPE_EXTENT:
                    return FieldBuilder.get(field, "powertypeExtentIds", valueClass).build(); //TODO verify
                case TEMPLATE_BINDING:
                    return FieldBuilder.get(field, "templateBindingIds", valueClass).build(); //TODO verify
                case APPLIED_STEREOTYPE_INSTANCE:
                    return FieldBuilder.get(field, "appliedStereotypeInstanceId", valueClass).build();
                case TEMPLATE_PARAMETER:
                    return FieldBuilder.get(field, "templateParameterId", valueClass).build(); //TODO verify
                case IS_ACTIVE:
                    return FieldBuilder.get(field, "isActive", valueClass).build();
                case CLIENT_DEPENDENCIES:
                    return FieldBuilder.get(field, "clientDependencyIds", valueClass).build();
                case SYNC_ELEMENT:
                    return FieldBuilder.get(field, "syncElementId", valueClass).build(); //TODO verify
                case SUPPLIER_DEPENDENCIES:
                    return FieldBuilder.get(field, "supplierDependencyIds", valueClass).build();
                case NAME_EXPRESSION:
                    return FieldBuilder.get(field, "nameExpression", valueClass).build(); //TODO verify
                case PACKAGE_IMPORTS:
                    return FieldBuilder.get(field, "packageImportIds", valueClass).build(); //TODO verify
                case VISIBILITY:
                    return FieldBuilder.get(field, "visibility", valueClass).build();
                case ELEMENT_IMPORTS:
                    return FieldBuilder.get(field, "elementImportIds", valueClass).build(); //TODO verify
                case USE_CASES:
                    return FieldBuilder.get(field, "useCaseIds", valueClass).build(); //TODO verify
                case CLASSIFIER_BEHAVIOR:
                    return FieldBuilder.get(field, "classifierBehaviorId", valueClass).build(); //TODO verify
                case INTERFACE_REALIZATIONS:
                    return FieldBuilder.get(field, "interfaceRealizationIds", valueClass).build(); //TODO verify
                case OWNED_ATTRIBUTES:
                    return FieldBuilder.get(field, "ownedAttributeIds", valueClass).build(); //TODO verify
                case SUBSTITUTIONS:
                    return FieldBuilder.get(field, "substitutionIds", valueClass).build(); //TODO verify
                case REDEFINED_CLASSIFIERS:
                    return FieldBuilder.get(field, "redefinedClassifierIds", valueClass).build(); //TODO verify
                case IS_ABSTRACT:
                    return FieldBuilder.get(field, "isAbstract", valueClass).build();
                case GENERALIZATIONS:
                    return FieldBuilder.get(field, "generalizationIds", valueClass).build(); //TODO verify
                case OWNED_OPERATIONS:
                    return FieldBuilder.get(field, "ownedOperationIds", valueClass).build(); //TODO verify
                case COLLABORATION_USES:
                    return FieldBuilder.get(field, "collaborationUseIds", valueClass).build(); //TODO verify
                case IS_FINAL_SPECIALIZATION:
                    return FieldBuilder.get(field, "isFinalSpecialization", valueClass).build();
                case URI:
                    return FieldBuilder.get(field, "URI", valueClass).build();
                case PACKAGE_MERGES:
                    return FieldBuilder.get(field, "packageMergeIds", valueClass).build();
                case DEFINING_FEATURE:
                    return FieldBuilder.get(field, "definingFeatureId", valueClass).build();
                case VALUE:
                    return FieldBuilder.get(field, "value", valueClass).build();
                case ELEMENT_VALUE:
                    return FieldBuilder.get(field, "elementId", valueClass).build();
                case INSTANCE_VALUE:
                    return FieldBuilder.get(field, "instanceId", valueClass).build();
                default:
                    logger.error("Unimplemented SysML field: " + field.toString());
                    return null;
            }
        } else if(field instanceof MmsFields) {
            switch ((MmsFields)field) {
                case MD_EXTENTION:
                    return FieldBuilder.get(field, "mdExtensionsIds", valueClass).build(); //TODO verify
                case IS_LEAF:
                    return FieldBuilder.get(field, "isLeaf", valueClass).build();
                case SLOTS:
                    return FieldBuilder.get(field, "slotIds", valueClass).build();

                default:
                    logger.error("Unimplemented MMS field: " + field.toString());
                    return null;
            }
        } else {
            logger.error("Unknown field: " + field.toString() + " of type " + field.getClass().getName());
            return null;
        }
    }


}
