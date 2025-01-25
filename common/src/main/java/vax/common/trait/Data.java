package vax.common.trait;

import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.ClusterSerializable;

/**
 * A data type that support to transport.<br/>
 * <ol>
 *     <li>a manually implement should have public constructors as:</li>
 *     <ul>
 *         <li> constructor(JsonObject) for deserialize from JsonObject</li>
 *         <li> constructor(JsonObject,Void) for deserialize from Javascript compatible JsonObject</li>
 *      </ul>
 *      <li>an serialized JsonObject may contains '$T'({@link #CLASS_KEY}) as class name </li>
 * </ol>
 *
 * @author Zen.Liu
 * @since 2025-01-12
 */
public interface Data {
    String CLASS_KEY = "$T";

    /**
     * @return underlying json. if not override, the same as $toJson
     */
    default JsonObject $asJson() {
        return $toJson();
    }

    /**
     * @return a copied json object
     */
    JsonObject $toJson();

    /// Persis element
    interface Persis extends Data {
        /**
         * remove all entity fields from json object
         */
        default JsonObject $forStore() {
            var j = $toJson();
            if (this instanceof Versioned) {
                Versioned.purify(j);
            }
            else if (this instanceof Identified) {
                Identified.purify(j);
            }
            if (this instanceof Removable) {
                Removable.purify(j);
            }
            if (this instanceof Auditable) {
                Auditable.purify(j);
            }
            return j;
        }

        /**
         * remove all fields from XAuditable from json object
         */
        default JsonObject $forMutate() {
            var j = $toJson();
            if (this instanceof Auditable) {
                Auditable.purify(j);
            }
            return j;
        }

        /**
         * @see #$forMutate()  same action
         */
        default JsonObject $forValue() {
            var j = $toJson();
            if (this instanceof Auditable) {
                Auditable.purify(j);
            }
            return j;
        }
    }



    /**
     * a Data that compatible to javascript data types.
     */
    interface Compatible extends Data {
        /**
         * @return javascript compatible json object. default is {@link #$toJson()}
         */
        default JsonObject $jsJson() {
            return $toJson();
        }

        default JsonObject $forJsValue() {
            var j = $jsJson();
            if (this instanceof Auditable) {
                Auditable.purify(j);
            }
            return j;
        }
    }

    ///  plain old java object
    interface Pojo extends Data {

    }

    /// binary object
    interface Binary extends Data, ClusterSerializable {

    }

    interface Copiable<T extends Copiable<T>> extends Data {
        T $self();

        /**
         * copied instance
         */
        T $copy();
    }
}
