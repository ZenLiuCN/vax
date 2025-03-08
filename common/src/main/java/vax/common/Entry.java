package vax.common;

import vax.common.trait.Identified;
import vax.common.trait.Versioned;
import vax.common.units.Binary;

import java.util.Base64;

/**
 * Entry is a reference of an element. or element itself.
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface Entry extends
                       Identified,
                       Versioned,
        Element {
    @Override
    Class<? extends Entry> $type();

    record Identity(long id, int version) {
        public Identity(Entry entry) {
            this(entry.id(), entry.version());
        }

        public static Identity identity(String id) {
            var b = Binary.of(Base64.getUrlDecoder().decode(id));
            return new Identity(b.$v64(), b.$v32());
        }

        public String identity() {
            return toString();
        }

        @Override
        public String toString() {
            var b = Binary.of();
            return Base64.getUrlEncoder().withoutPadding().encodeToString(b.$v64(id).$v32(version).getBytes());
        }
    }
}
