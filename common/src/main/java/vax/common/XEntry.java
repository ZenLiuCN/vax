package vax.common;

import vax.common.trait.XIdentified;
import vax.common.trait.XVersioned;
import vax.common.units.BinaryX;

import java.util.Base64;

/**
 * Entry is a reference of an element. or element itself.
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface XEntry extends
        XIdentified,
        XVersioned,
        XElement {
    @Override
    Class<? extends XEntry> $type();

    record Identity(long id, int version) {
        public Identity(XEntry entry) {
            this(entry.id(), entry.version());
        }

        public static Identity identity(String id) {
            var b = BinaryX.of(Base64.getUrlDecoder().decode(id));
            return new Identity(b.v8(), b.v4());
        }

        public String identity() {
            return toString();
        }

        @Override
        public String toString() {
            var b = BinaryX.of();
            return Base64.getUrlEncoder().withoutPadding().encodeToString(b.v8(id).v4(version).getBytes());
        }
    }
}
