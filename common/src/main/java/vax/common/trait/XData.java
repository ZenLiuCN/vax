package vax.common.trait;

import io.vertx.core.json.JsonObject;

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
public interface XData {
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

    /**
     * remove all entity fields from json object
     */
    default JsonObject $forStore() {
        var j = $toJson();
        if (this instanceof XVersioned) {
            XVersioned.purify(j);
        } else if (this instanceof XIdentified) {
            XIdentified.purify(j);
        }
        if (this instanceof XRemovable) {
            XRemovable.purify(j);
        }
        if (this instanceof XAuditable) {
            XAuditable.purify(j);
        }
        return j;
    }

    /**
     * remove all fields from XAuditable from json object
     */
    default JsonObject $forMutate() {
        var j = $toJson();
        if (this instanceof XAuditable) {
            XAuditable.purify(j);
        }
        return j;
    }


    /**
     * @see #$forMutate()  same action
     */
    default JsonObject $forValue() {
        var j = $toJson();
        if (this instanceof XAuditable) {
            XAuditable.purify(j);
        }
        return j;
    }

    /**
     * a XData that compatible to javascript data types.
     */
    interface Compatible extends XData {
        /**
         * @return javascript compatible json object. default is {@link #$toJson()}
         */
        default JsonObject $jsJson() {
            return $toJson();
        }

        default JsonObject $forJsValue() {
            var j = $jsJson();
            if (this instanceof XAuditable) {
                XAuditable.purify(j);
            }
            return j;
        }
    }

    interface Copiable<T extends Copiable<T>> extends XData {
        T $self();

        /**
         * copied instance
         */
        T $copy();
    }
}
