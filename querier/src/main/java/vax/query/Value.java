package vax.query;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.nio.Buffer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.Arrays;

/// value
public interface Value<T> extends Expr<T> {
    //region nullability

    default bool isNull() {
        return new ExprNull<>(this, true);
    }

    default bool notNull() {
        return new ExprNull<>(this, false);
    }

    //endregion
    //region equality

    default bool eq(Value<T> v) {
        return new ExprEqual<>(this, v, true);
    }

    default bool neq(Value<T> v) {
        return new ExprEqual<>(this, v, false);
    }

    default bool eq(T v) {
        return new ExprEqual<>(this, v, true);
    }

    default bool neq(T v) {
        return new ExprEqual<>(this, v, false);
    }

    //endregion

    sealed interface numeric<T extends Number> extends Value<T> permits ExprMathBinary, ExprMathUnary,
            Identity.numeric, Raw.numeric, json.JsonExpr.NumExpr,
            numeric.decimal, numeric.float32, numeric.float64, numeric.int16,
            numeric.int32, numeric.int64, numeric.int8 {

        //region equality

        default bool eq(numeric<T> v) {
            return vax.query.Value.super.eq(v);
        }

        default bool neq(numeric<T> v) {
            return vax.query.Value.super.neq(v);
        }

        //endregion

        //region compare

        default bool lt(numeric<T> v) {
            return new ExprCompare<>(this, v, ExprCompare.LT);
        }

        default bool gt(numeric<T> v) {
            return new ExprCompare<>(this, v, ExprCompare.GT);
        }

        default bool lte(numeric<T> v) {
            return new ExprCompare<>(this, v, ExprCompare.LTE);
        }

        default bool gte(numeric<T> v) {
            return new ExprCompare<>(this, v, ExprCompare.GTE);
        }

        default bool lt(T v) {
            return new ExprCompare<>(this, v, ExprCompare.LT);
        }

        default bool gt(T v) {
            return new ExprCompare<>(this, v, ExprCompare.GT);
        }

        default bool lte(T v) {
            return new ExprCompare<>(this, v, ExprCompare.LTE);
        }

        default bool gte(T v) {
            return new ExprCompare<>(this, v, ExprCompare.GTE);
        }

        //endregion

        //region collection

        @SuppressWarnings("unchecked")
        default bool in(numeric<T>... v) {
            return new ExprIn<>(this, v, true);
        }

        @SuppressWarnings("unchecked")
        default bool in(T... v) {
            return new ExprIn<>(this, Arrays.stream(v)
                    .map(Param::of)
                    .toArray(Expr[]::new), true);
        }

        @SuppressWarnings("unchecked")
        default bool notIn(numeric<T>... v) {
            return new ExprIn<>(this, v, false);
        }

        @SuppressWarnings("unchecked")
        default bool notIn(T... v) {
            return new ExprIn<>(this, v, false);
        }

        //endregion

        //region range

        default bool between(numeric<T> lo, numeric<T> hi) {
            return new ExprBtw<>(this, lo, hi, true);
        }

        default bool notBetween(numeric<T> lo, numeric<T> hi) {
            return new ExprBtw<>(this, lo, hi, false);
        }

        default bool between(T lo, T hi) {
            return new ExprBtw<>(this, lo, hi, true);
        }


        default bool notBetween(T lo, T hi) {
            return new ExprBtw<>(this, lo, hi, false);
        }

        //endregion

        //region math

        default numeric<T> plus(numeric<T> v) {
            return new ExprMathBinary<>(this, v, ExprMathBinary.PLUS);
        }

        default numeric<T> minus(numeric<T> v) {
            return new ExprMathBinary<>(this, v, ExprMathBinary.MINUS);
        }

        default numeric<T> times(numeric<T> v) {
            return new ExprMathBinary<>(this, v, ExprMathBinary.TIMES);
        }

        default numeric<T> divide(numeric<T> v) {
            return new ExprMathBinary<>(this, v, ExprMathBinary.DIVIDE);
        }

        default numeric<T> mod(numeric<T> v) {
            return new ExprMathBinary<>(this, v, ExprMathBinary.MODULO);
        }

        default numeric<T> rem(numeric<T> v) {
            return new ExprMathBinary<>(this, v, ExprMathBinary.REMINDER);
        }

        default numeric<T> neg() {
            return new ExprMathUnary<>(this, ExprMathUnary.NEGATIVE);
        }


        default numeric<T> plus(T v) {
            return new ExprMathBinary<>(this, v, ExprMathBinary.PLUS);
        }

        default numeric<T> minus(T v) {
            return new ExprMathBinary<>(this, v, ExprMathBinary.MINUS);
        }

        default numeric<T> times(T v) {
            return new ExprMathBinary<>(this, v, ExprMathBinary.TIMES);
        }

        default numeric<T> divide(T v) {
            return new ExprMathBinary<>(this, v, ExprMathBinary.DIVIDE);
        }

        default numeric<T> mod(T v) {
            return new ExprMathBinary<>(this, v, ExprMathBinary.MODULO);
        }

        default numeric<T> rem(T v) {
            return new ExprMathBinary<>(this, v, ExprMathBinary.REMINDER);
        }
        //endregion

        //region bitwise

        default numeric<T> bAND(numeric<T> v) {
            return new ExprMathBinary<>(this, v, ExprMathBinary.BIT_AND);
        }

        default numeric<T> bOR(numeric<T> v) {
            return new ExprMathBinary<>(this, v, ExprMathBinary.BIT_OR);
        }

        default numeric<T> bXOR(numeric<T> v) {
            return new ExprMathBinary<>(this, v, ExprMathBinary.BIT_XOR);
        }

        default numeric<T> bNOT() {
            if (this instanceof Expr.ExprMathUnary<T> e
                    && e.isbNOT()
                    && e.v0() instanceof numeric<T> n)
                return n;
            return new ExprMathUnary<>(this, ExprMathUnary.BIT_NOT);
        }

        default numeric<T> bAND(T v) {
            return new ExprMathBinary<>(this, v, ExprMathBinary.BIT_AND);
        }

        default numeric<T> bOR(T v) {
            return new ExprMathBinary<>(this, v, ExprMathBinary.BIT_OR);
        }

        default numeric<T> bXOR(T v) {
            return new ExprMathBinary<>(this, v, ExprMathBinary.BIT_XOR);
        }

        //endregion

        non-sealed interface int8 extends numeric<Byte> {
            /// equals to a placeholder
            default bool eqOf(int v) {
                return new ExprEqual<>(this, Byte.class, v, true);
            }

            /// not equals to a placeholder
            default bool neqOf(int v) {
                return new ExprEqual<>(this, Byte.class, v, false);
            }

            default numeric<Byte> bANDOf(int v) {
                return new ExprMathBinary<>(this, Byte.class, v, ExprMathBinary.BIT_AND);
            }

            default numeric<Byte> bOROf(int v) {
                return new ExprMathBinary<>(this, Byte.class, v, ExprMathBinary.BIT_OR);
            }

            default numeric<Byte> bXOROf(int v) {
                return new ExprMathBinary<>(this, Byte.class, v, ExprMathBinary.BIT_XOR);
            }

            default numeric<Byte> plusOf(int v) {
                return new ExprMathBinary<>(this, Byte.class, v, ExprMathBinary.PLUS);
            }

            default numeric<Byte> minusOf(int v) {
                return new ExprMathBinary<>(this, Byte.class, v, ExprMathBinary.MINUS);
            }

            default numeric<Byte> timesOf(int v) {
                return new ExprMathBinary<>(this, Byte.class, v, ExprMathBinary.TIMES);
            }

            default numeric<Byte> divideOf(int v) {
                return new ExprMathBinary<>(this, Byte.class, v, ExprMathBinary.DIVIDE);
            }

            default numeric<Byte> modOf(int v) {
                return new ExprMathBinary<>(this, Byte.class, v, ExprMathBinary.MODULO);
            }

            default numeric<Byte> remOf(int v) {
                return new ExprMathBinary<>(this, Byte.class, v, ExprMathBinary.REMINDER);
            }

            default bool between(int lo, int hi) {
                return new ExprBtw<>(this, Byte.class, lo, hi, true);
            }


            default bool notBetween(int lo, int hi) {
                return new ExprBtw<>(this, Byte.class, lo, hi, false);
            }

            default bool ltOf(int v) {
                return new ExprCompare<>(this, Byte.class, v, ExprCompare.LT);
            }

            default bool gtOf(int v) {
                return new ExprCompare<>(this, Byte.class, v, ExprCompare.GT);
            }

            default bool lteOf(int v) {
                return new ExprCompare<>(this, Byte.class, v, ExprCompare.LTE);
            }

            default bool gteOf(int v) {
                return new ExprCompare<>(this, Byte.class, v, ExprCompare.GTE);
            }
            //endregion

        }

        non-sealed interface int16 extends numeric<Short> {
            /// equals to a placeholder
            default bool eqOf(int v) {
                return new ExprEqual<>(this, Short.class, v, true);
            }

            /// not equals to a placeholder
            default bool neqOf(int v) {
                return new ExprEqual<>(this, Short.class, v, false);
            }

            default numeric<Short> bANDOf(int v) {
                return new ExprMathBinary<>(this, Short.class, v, ExprMathBinary.BIT_AND);
            }

            default numeric<Short> bOROf(int v) {
                return new ExprMathBinary<>(this, Short.class, v, ExprMathBinary.BIT_OR);
            }

            default numeric<Short> bXOROf(int v) {
                return new ExprMathBinary<>(this, Short.class, v, ExprMathBinary.BIT_XOR);
            }

            default numeric<Short> plusOf(int v) {
                return new ExprMathBinary<>(this, Short.class, v, ExprMathBinary.PLUS);
            }

            default numeric<Short> minusOf(int v) {
                return new ExprMathBinary<>(this, Short.class, v, ExprMathBinary.MINUS);
            }

            default numeric<Short> timesOf(int v) {
                return new ExprMathBinary<>(this, Short.class, v, ExprMathBinary.TIMES);
            }

            default numeric<Short> divideOf(int v) {
                return new ExprMathBinary<>(this, Short.class, v, ExprMathBinary.DIVIDE);
            }

            default numeric<Short> modOf(int v) {
                return new ExprMathBinary<>(this, Short.class, v, ExprMathBinary.MODULO);
            }

            default numeric<Short> remOf(int v) {
                return new ExprMathBinary<>(this, Short.class, v, ExprMathBinary.REMINDER);
            }

            default bool between(int lo, int hi) {
                return new ExprBtw<>(this, Short.class, lo, hi, true);
            }


            default bool notBetween(int lo, int hi) {
                return new ExprBtw<>(this, Short.class, lo, hi, false);
            }

            default bool ltOf(int v) {
                return new ExprCompare<>(this, Short.class, v, ExprCompare.LT);
            }

            default bool gtOf(int v) {
                return new ExprCompare<>(this, Short.class, v, ExprCompare.GT);
            }

            default bool lteOf(int v) {
                return new ExprCompare<>(this, Short.class, v, ExprCompare.LTE);
            }

            default bool gteOf(int v) {
                return new ExprCompare<>(this, Short.class, v, ExprCompare.GTE);
            }
        }

        non-sealed interface int32 extends numeric<Integer> {
            /// equals to a placeholder
            default bool eqOf(int v) {
                return new ExprEqual<>(this, Integer.class, v, true);
            }

            /// not equals to a placeholder
            default bool neqOf(int v) {
                return new ExprEqual<>(this, Integer.class, v, false);
            }

            default numeric<Integer> bANDOf(int v) {
                return new ExprMathBinary<>(this, Integer.class, v, ExprMathBinary.BIT_AND);
            }

            default numeric<Integer> bOROf(int v) {
                return new ExprMathBinary<>(this, Integer.class, v, ExprMathBinary.BIT_OR);
            }

            default numeric<Integer> bXOROf(int v) {
                return new ExprMathBinary<>(this, Integer.class, v, ExprMathBinary.BIT_XOR);
            }

            default numeric<Integer> plusOf(int v) {
                return new ExprMathBinary<>(this, Integer.class, v, ExprMathBinary.PLUS);
            }

            default numeric<Integer> minusOf(int v) {
                return new ExprMathBinary<>(this, Integer.class, v, ExprMathBinary.MINUS);
            }

            default numeric<Integer> timesOf(int v) {
                return new ExprMathBinary<>(this, Integer.class, v, ExprMathBinary.TIMES);
            }

            default numeric<Integer> divideOf(int v) {
                return new ExprMathBinary<>(this, Integer.class, v, ExprMathBinary.DIVIDE);
            }

            default numeric<Integer> modOf(int v) {
                return new ExprMathBinary<>(this, Integer.class, v, ExprMathBinary.MODULO);
            }

            default numeric<Integer> remOf(int v) {
                return new ExprMathBinary<>(this, Integer.class, v, ExprMathBinary.REMINDER);
            }

            default bool between(int lo, int hi) {
                return new ExprBtw<>(this, Integer.class, lo, hi, true);
            }


            default bool notBetween(int lo, int hi) {
                return new ExprBtw<>(this, Integer.class, lo, hi, false);
            }

            default bool ltOf(int v) {
                return new ExprCompare<>(this, Integer.class, v, ExprCompare.LT);
            }

            default bool gtOf(int v) {
                return new ExprCompare<>(this, Integer.class, v, ExprCompare.GT);
            }

            default bool lteOf(int v) {
                return new ExprCompare<>(this, Integer.class, v, ExprCompare.LTE);
            }

            default bool gteOf(int v) {
                return new ExprCompare<>(this, Integer.class, v, ExprCompare.GTE);
            }
        }

        non-sealed interface int64 extends numeric<Long> {
            /// equals to a placeholder
            default bool eqOf(int v) {
                return new ExprEqual<>(this, Long.class, v, true);
            }

            /// not equals to a placeholder
            default bool neqOf(int v) {
                return new ExprEqual<>(this, Long.class, v, false);
            }

            default numeric<Long> bANDOf(int v) {
                return new ExprMathBinary<>(this, Long.class, v, ExprMathBinary.BIT_AND);
            }

            default numeric<Long> bOROf(int v) {
                return new ExprMathBinary<>(this, Long.class, v, ExprMathBinary.BIT_OR);
            }

            default numeric<Long> bXOROf(int v) {
                return new ExprMathBinary<>(this, Long.class, v, ExprMathBinary.BIT_XOR);
            }

            default numeric<Long> plusOf(int v) {
                return new ExprMathBinary<>(this, Long.class, v, ExprMathBinary.PLUS);
            }

            default numeric<Long> minusOf(int v) {
                return new ExprMathBinary<>(this, Long.class, v, ExprMathBinary.MINUS);
            }

            default numeric<Long> timesOf(int v) {
                return new ExprMathBinary<>(this, Long.class, v, ExprMathBinary.TIMES);
            }

            default numeric<Long> divideOf(int v) {
                return new ExprMathBinary<>(this, Long.class, v, ExprMathBinary.DIVIDE);
            }

            default numeric<Long> modOf(int v) {
                return new ExprMathBinary<>(this, Long.class, v, ExprMathBinary.MODULO);
            }

            default numeric<Long> remOf(int v) {
                return new ExprMathBinary<>(this, Long.class, v, ExprMathBinary.REMINDER);
            }

            default bool between(int lo, int hi) {
                return new ExprBtw<>(this, Long.class, lo, hi, true);
            }


            default bool notBetween(int lo, int hi) {
                return new ExprBtw<>(this, Long.class, lo, hi, false);
            }

            default bool ltOf(int v) {
                return new ExprCompare<>(this, Long.class, v, ExprCompare.LT);
            }

            default bool gtOf(int v) {
                return new ExprCompare<>(this, Long.class, v, ExprCompare.GT);
            }

            default bool lteOf(int v) {
                return new ExprCompare<>(this, Long.class, v, ExprCompare.LTE);
            }

            default bool gteOf(int v) {
                return new ExprCompare<>(this, Long.class, v, ExprCompare.GTE);
            }
        }

        non-sealed interface float32 extends numeric<Float> {
            /// equals to a placeholder
            default bool eqOf(int v) {
                return new ExprEqual<>(this, Float.class, v, true);
            }

            /// not equals to a placeholder
            default bool neqOf(int v) {
                return new ExprEqual<>(this, Float.class, v, false);
            }

            default numeric<Float> bANDOf(int v) {
                return new ExprMathBinary<>(this, Float.class, v, ExprMathBinary.BIT_AND);
            }

            default numeric<Float> bOROf(int v) {
                return new ExprMathBinary<>(this, Float.class, v, ExprMathBinary.BIT_OR);
            }

            default numeric<Float> bXOROf(int v) {
                return new ExprMathBinary<>(this, Float.class, v, ExprMathBinary.BIT_XOR);
            }

            default numeric<Float> plusOf(int v) {
                return new ExprMathBinary<>(this, Float.class, v, ExprMathBinary.PLUS);
            }

            default numeric<Float> minusOf(int v) {
                return new ExprMathBinary<>(this, Float.class, v, ExprMathBinary.MINUS);
            }

            default numeric<Float> timesOf(int v) {
                return new ExprMathBinary<>(this, Float.class, v, ExprMathBinary.TIMES);
            }

            default numeric<Float> divideOf(int v) {
                return new ExprMathBinary<>(this, Float.class, v, ExprMathBinary.DIVIDE);
            }

            default numeric<Float> modOf(int v) {
                return new ExprMathBinary<>(this, Float.class, v, ExprMathBinary.MODULO);
            }

            default numeric<Float> remOf(int v) {
                return new ExprMathBinary<>(this, Float.class, v, ExprMathBinary.REMINDER);
            }

            default bool between(int lo, int hi) {
                return new ExprBtw<>(this, Float.class, lo, hi, true);
            }


            default bool notBetween(int lo, int hi) {
                return new ExprBtw<>(this, Float.class, lo, hi, false);
            }

            default bool ltOf(int v) {
                return new ExprCompare<>(this, Float.class, v, ExprCompare.LT);
            }

            default bool gtOf(int v) {
                return new ExprCompare<>(this, Float.class, v, ExprCompare.GT);
            }

            default bool lteOf(int v) {
                return new ExprCompare<>(this, Float.class, v, ExprCompare.LTE);
            }

            default bool gteOf(int v) {
                return new ExprCompare<>(this, Float.class, v, ExprCompare.GTE);
            }
        }

        non-sealed interface float64 extends numeric<Double> {
            /// equals to a placeholder
            default bool eqOf(int v) {
                return new ExprEqual<>(this, Double.class, v, true);
            }

            /// not equals to a placeholder
            default bool neqOf(int v) {
                return new ExprEqual<>(this, Double.class, v, false);
            }

            default numeric<Double> bANDOf(int v) {
                return new ExprMathBinary<>(this, Double.class, v, ExprMathBinary.BIT_AND);
            }

            default numeric<Double> bOROf(int v) {
                return new ExprMathBinary<>(this, Double.class, v, ExprMathBinary.BIT_OR);
            }

            default numeric<Double> bXOROf(int v) {
                return new ExprMathBinary<>(this, Double.class, v, ExprMathBinary.BIT_XOR);
            }

            default numeric<Double> plusOf(int v) {
                return new ExprMathBinary<>(this, Double.class, v, ExprMathBinary.PLUS);
            }

            default numeric<Double> minusOf(int v) {
                return new ExprMathBinary<>(this, Double.class, v, ExprMathBinary.MINUS);
            }

            default numeric<Double> timesOf(int v) {
                return new ExprMathBinary<>(this, Double.class, v, ExprMathBinary.TIMES);
            }

            default numeric<Double> divideOf(int v) {
                return new ExprMathBinary<>(this, Double.class, v, ExprMathBinary.DIVIDE);
            }

            default numeric<Double> modOf(int v) {
                return new ExprMathBinary<>(this, Double.class, v, ExprMathBinary.MODULO);
            }

            default numeric<Double> remOf(int v) {
                return new ExprMathBinary<>(this, Double.class, v, ExprMathBinary.REMINDER);
            }

            default bool between(int lo, int hi) {
                return new ExprBtw<>(this, Double.class, lo, hi, true);
            }


            default bool notBetween(int lo, int hi) {
                return new ExprBtw<>(this, Double.class, lo, hi, false);
            }

            default bool ltOf(int v) {
                return new ExprCompare<>(this, Double.class, v, ExprCompare.LT);
            }

            default bool gtOf(int v) {
                return new ExprCompare<>(this, Double.class, v, ExprCompare.GT);
            }

            default bool lteOf(int v) {
                return new ExprCompare<>(this, Double.class, v, ExprCompare.LTE);
            }

            default bool gteOf(int v) {
                return new ExprCompare<>(this, Double.class, v, ExprCompare.GTE);
            }
        }

        non-sealed interface decimal extends numeric<BigDecimal> {
            /// equals to a placeholder
            default bool eqOf(int v) {
                return new ExprEqual<>(this, BigDecimal.class, v, true);
            }

            /// not equals to a placeholder
            default bool neqOf(int v) {
                return new ExprEqual<>(this, BigDecimal.class, v, false);
            }

            default numeric<BigDecimal> bANDOf(int v) {
                return new ExprMathBinary<>(this, BigDecimal.class, v, ExprMathBinary.BIT_AND);
            }

            default numeric<BigDecimal> bOROf(int v) {
                return new ExprMathBinary<>(this, BigDecimal.class, v, ExprMathBinary.BIT_OR);
            }

            default numeric<BigDecimal> bXOROf(int v) {
                return new ExprMathBinary<>(this, BigDecimal.class, v, ExprMathBinary.BIT_XOR);
            }

            default numeric<BigDecimal> plusOf(int v) {
                return new ExprMathBinary<>(this, BigDecimal.class, v, ExprMathBinary.PLUS);
            }

            default numeric<BigDecimal> minusOf(int v) {
                return new ExprMathBinary<>(this, BigDecimal.class, v, ExprMathBinary.MINUS);
            }

            default numeric<BigDecimal> timesOf(int v) {
                return new ExprMathBinary<>(this, BigDecimal.class, v, ExprMathBinary.TIMES);
            }

            default numeric<BigDecimal> divideOf(int v) {
                return new ExprMathBinary<>(this, BigDecimal.class, v, ExprMathBinary.DIVIDE);
            }

            default numeric<BigDecimal> modOf(int v) {
                return new ExprMathBinary<>(this, BigDecimal.class, v, ExprMathBinary.MODULO);
            }

            default numeric<BigDecimal> remOf(int v) {
                return new ExprMathBinary<>(this, BigDecimal.class, v, ExprMathBinary.REMINDER);
            }

            default bool between(int lo, int hi) {
                return new ExprBtw<>(this, BigDecimal.class, lo, hi, true);
            }


            default bool notBetween(int lo, int hi) {
                return new ExprBtw<>(this, BigDecimal.class, lo, hi, false);
            }

            default bool ltOf(int v) {
                return new ExprCompare<>(this, BigDecimal.class, v, ExprCompare.LT);
            }

            default bool gtOf(int v) {
                return new ExprCompare<>(this, BigDecimal.class, v, ExprCompare.GT);
            }

            default bool lteOf(int v) {
                return new ExprCompare<>(this, BigDecimal.class, v, ExprCompare.LTE);
            }

            default bool gteOf(int v) {
                return new ExprCompare<>(this, BigDecimal.class, v, ExprCompare.GTE);
            }
        }
    }

    interface binary extends vax.query.Value<byte[]> {
        /// equals to a placeholder
        default bool eqOf(int v) {
            return new ExprEqual<>(this, byte[].class, v, true);
        }

        /// not equals to a placeholder
        default bool neqOf(int v) {
            return new ExprEqual<>(this, byte[].class, v, false);
        }
    }

    interface blob extends vax.query.Value<Buffer> {
        /// equals to a placeholder
        default bool eqOf(int v) {
            return new ExprEqual<>(this, Buffer.class, v, true);
        }

        /// not equals to a placeholder
        default bool neqOf(int v) {
            return new ExprEqual<>(this, Buffer.class, v, false);
        }
    }

    interface text extends vax.query.Value<CharSequence> {

        //region equality

        default bool eq(text v) {
            return new ExprEqual<>(this, v, true);
        }

        default bool neq(text v) {
            return new ExprEqual<>(this, v, false);
        }

        ///  equals case-insensitive
        default bool eqCi(text v) {
            return new ExprEqual<>(this, v, true, true);
        }

        ///  not equals case-insensitive
        default bool neqCi(text v) {
            return new ExprEqual<>(this, v, false, true);
        }

        default bool eq(CharSequence v) {
            return new ExprEqual<>(this, v, true);
        }

        default bool neq(CharSequence v) {
            return new ExprEqual<>(this, v, false);
        }

        ///  equals case-insensitive
        default bool eqCi(CharSequence v) {
            return new ExprEqual<>(this, v, true, true);
        }

        ///  not equals case-insensitive
        default bool neqCi(CharSequence v) {
            return new ExprEqual<>(this, v, false, true);
        }

        /// equals to a placeholder
        default bool eqOf(int v) {
            return new ExprEqual<>(this, CharSequence.class, v, true);
        }

        /// not equals to a placeholder
        default bool neqOf(int v) {
            return new ExprEqual<>(this, CharSequence.class, v, false);
        }

        default bool eqCiOf(int v) {
            return new ExprEqual<>(this, CharSequence.class, v, true, true);
        }

        /// not equals to a placeholder
        default bool neqCiOf(int v) {
            return new ExprEqual<>(this, CharSequence.class, v, false, true);
        }

        //endregion

        //region collection

        default bool in(text... v) {
            return new ExprIn<>(this, v, true);
        }

        default bool notIn(text... v) {
            return new ExprIn<>(this, v, false);
        }

        default bool in(CharSequence... v) {
            return new ExprIn<>(this, v, true);
        }

        default bool notIn(CharSequence... v) {
            return new ExprIn<>(this, v, false);
        }

        //endregion

        //region match

        default bool startWith(text v) {
            return new ExprMatchBinary(this, v, ExprMatchBinary.START);
        }

        default bool endWith(text v) {
            return new ExprMatchBinary(this, v, ExprMatchBinary.END);
        }

        default bool contains(text v) {
            return new ExprMatchBinary(this, v, ExprMatchBinary.LIKE);
        }

        default bool startWith(CharSequence v) {
            return new ExprMatchBinary(this, v, ExprMatchBinary.START);
        }

        default bool endWith(CharSequence v) {
            return new ExprMatchBinary(this, v, ExprMatchBinary.END);
        }

        default bool contains(CharSequence v) {
            return new ExprMatchBinary(this, v, ExprMatchBinary.LIKE);
        }

        default bool startWithCI(text v) {
            return new ExprMatchBinary(this, v, ExprMatchBinary.START, true);
        }

        default bool endWithCI(text v) {
            return new ExprMatchBinary(this, v, ExprMatchBinary.END, true);
        }

        default bool containsCI(text v) {
            return new ExprMatchBinary(this, v, ExprMatchBinary.LIKE, true);
        }

        default bool startWithCI(CharSequence v) {
            return new ExprMatchBinary(this, v, ExprMatchBinary.START, true);
        }

        default bool endWithCI(CharSequence v) {
            return new ExprMatchBinary(this, v, ExprMatchBinary.END, true);
        }

        default bool containsCI(CharSequence v) {
            return new ExprMatchBinary(this, v, ExprMatchBinary.LIKE, true);
        }
        //endregion
    }

    sealed interface json<T> extends Value<T> permits Identity.json, Raw.json, json.jsonArray, json.jsonObject {
        interface JsonExpr<T> extends Value<T> {
            json<?> source();

            record BoolExpr(json<?> source, String key, int index) implements json.JsonExpr<Boolean>, bool {}

            record TextExpr(json<?> source, String key, int index) implements json.JsonExpr<CharSequence>, text {}

            record NumExpr<T extends Number>(json<?> source, String key,
                                             int index) implements json.JsonExpr<T>, numeric<T> {}

            record ArrayExpr(json<?> source, String key,
                             int index) implements json.JsonExpr<JsonArray>, json.jsonArray {}

            record ObjectExpr(json<?> source, String key,
                              int index) implements json.JsonExpr<JsonObject>, json.jsonObject {}
        }

        non-sealed interface jsonObject extends json<JsonObject> {
            default bool bool(String key) {
                return new JsonExpr.BoolExpr(this, key, -1);
            }

            default jsonArray array(String key) {
                return new JsonExpr.ArrayExpr(this, key, -1);
            }

            default jsonObject object(String key) {
                return new JsonExpr.ObjectExpr(this, key, -1);
            }

            default text text(String key) {
                return new JsonExpr.TextExpr(this, key, -1);
            }

            default numeric<Byte> int8(String key) {
                return new JsonExpr.NumExpr<>(this, key, -1);
            }

            default numeric<Short> int16(String key) {
                return new JsonExpr.NumExpr<>(this, key, -1);
            }

            default numeric<Integer> int32(String key) {
                return new JsonExpr.NumExpr<>(this, key, -1);
            }

            default numeric<Long> int64(String key) {
                return new JsonExpr.NumExpr<>(this, key, -1);
            }
        }

        non-sealed interface jsonArray extends json<JsonArray> {
            default bool bool(int index) {
                return new JsonExpr.BoolExpr(this, null, index);
            }

            default jsonArray array(int index) {
                return new JsonExpr.ArrayExpr(this, null, index);
            }

            default jsonObject object(int index) {
                return new JsonExpr.ObjectExpr(this, null, index);
            }

            default text text(int index) {
                return new JsonExpr.TextExpr(this, null, index);
            }

            default numeric<Byte> int8(int index) {
                return new JsonExpr.NumExpr<>(this, null, index);
            }

            default numeric<Short> int16(int index) {
                return new JsonExpr.NumExpr<>(this, null, index);
            }

            default numeric<Integer> int32(int index) {
                return new JsonExpr.NumExpr<>(this, null, index);
            }

            default numeric<Long> int64(int index) {
                return new JsonExpr.NumExpr<>(this, null, index);
            }
        }

    }

    sealed interface temporal<T extends Temporal> extends Value<T> permits Identity.temporal, Raw.temporal, temporal.date, temporal.datetime, temporal.time, temporal.timestamp {

        //region equality

        default bool eq(temporal<T> v) {
            return new ExprEqual<>(this, v, true);
        }

        default bool neq(temporal<T> v) {
            return new ExprEqual<>(this, v, false);
        }

        //endregion

        //region range

        default bool between(temporal<T> lo, temporal<T> hi) {
            return new ExprBtw<>(this, lo, hi, true);
        }

        default bool notBetween(temporal<T> lo, temporal<T> hi) {
            return new ExprBtw<>(this, lo, hi, false);
        }

        default bool between(T lo, T hi) {
            return new ExprBtw<>(this, lo, hi, true);
        }

        default bool notBetween(T lo, T hi) {
            return new ExprBtw<>(this, lo, hi, false);
        }

        //endregion

        //region compare

        default bool lt(temporal<T> v) {
            return new ExprCompare<>(this, v, ExprCompare.LT);
        }

        default bool gt(temporal<T> v) {
            return new ExprCompare<>(this, v, ExprCompare.GT);
        }

        default bool lte(temporal<T> v) {
            return new ExprCompare<>(this, v, ExprCompare.LTE);
        }

        default bool gte(temporal<T> v) {
            return new ExprCompare<>(this, v, ExprCompare.GTE);
        }

        default bool lt(T v) {
            return new ExprCompare<>(this, v, ExprCompare.LT);
        }

        default bool gt(T v) {
            return new ExprCompare<>(this, v, ExprCompare.GT);
        }

        default bool lte(T v) {
            return new ExprCompare<>(this, v, ExprCompare.LTE);
        }

        default bool gte(T v) {
            return new ExprCompare<>(this, v, ExprCompare.GTE);
        }

        //endregion

        non-sealed interface datetime extends temporal<LocalDateTime> {

            default bool ltOf(int v) {
                return new ExprCompare<>(this, LocalDateTime.class, v, ExprCompare.LT);
            }

            default bool gtOf(int v) {
                return new ExprCompare<>(this, LocalDateTime.class, v, ExprCompare.GT);
            }

            default bool lteOf(int v) {
                return new ExprCompare<>(this, LocalDateTime.class, v, ExprCompare.LTE);
            }

            default bool gteOf(int v) {
                return new ExprCompare<>(this, LocalDateTime.class, v, ExprCompare.GTE);
            }
        }

        non-sealed interface date extends temporal<LocalDate> {

            default bool ltOf(int v) {
                return new ExprCompare<>(this, LocalDate.class, v, ExprCompare.LT);
            }

            default bool gtOf(int v) {
                return new ExprCompare<>(this, LocalDate.class, v, ExprCompare.GT);
            }

            default bool lteOf(int v) {
                return new ExprCompare<>(this, LocalDate.class, v, ExprCompare.LTE);
            }

            default bool gteOf(int v) {
                return new ExprCompare<>(this, LocalDate.class, v, ExprCompare.GTE);
            }
        }

        non-sealed interface time extends temporal<LocalTime> {

            default bool ltOf(int v) {
                return new ExprCompare<>(this, LocalTime.class, v, ExprCompare.LT);
            }

            default bool gtOf(int v) {
                return new ExprCompare<>(this, LocalTime.class, v, ExprCompare.GT);
            }

            default bool lteOf(int v) {
                return new ExprCompare<>(this, LocalTime.class, v, ExprCompare.LTE);
            }

            default bool gteOf(int v) {
                return new ExprCompare<>(this, LocalTime.class, v, ExprCompare.GTE);
            }
        }

        non-sealed interface timestamp extends temporal<Instant> {

            default bool ltOf(int v) {
                return new ExprCompare<>(this, Instant.class, v, ExprCompare.LT);
            }

            default bool gtOf(int v) {
                return new ExprCompare<>(this, Instant.class, v, ExprCompare.GT);
            }

            default bool lteOf(int v) {
                return new ExprCompare<>(this, Instant.class, v, ExprCompare.LTE);
            }

            default bool gteOf(int v) {
                return new ExprCompare<>(this, Instant.class, v, ExprCompare.GTE);
            }
        }
    }

    interface bool extends vax.query.Value<Boolean> {

        //region equality

        default bool eq(bool v) {
            return new ExprEqual<>(this, v, true);
        }

        default bool neq(bool v) {
            return new ExprEqual<>(this, v, false);
        }

        default bool isTrue() {
            return
                    this == Raw.TRUE ? this :
                            this == Raw.FALSE ? this : new ExprEqual<>(this, Raw.TRUE, true);
        }

        default bool isFalse() {
            return
                    this == Raw.TRUE ? Raw.FALSE :
                            this == Raw.FALSE ? Raw.TRUE
                                    : new ExprEqual<>(this, Raw.TRUE, false);
        }

        //endregion

        //region logical

        default bool and(bool v) {
            if (this == Raw.FALSE || v == Raw.FALSE) return Raw.FALSE;
            else if (this == Raw.TRUE && v == Raw.TRUE) return Raw.TRUE;
            return new ExprLogicBinary(this, v, ExprLogicBinary.AND);
        }

        default bool or(bool v) {
            if (this == Raw.TRUE || v == Raw.TRUE) return Raw.TRUE;
            return new ExprLogicBinary(this, v, ExprLogicBinary.OR);
        }

        default bool not() {
            if (this == Raw.TRUE) return Raw.FALSE;
            else if (this == Raw.FALSE) return Raw.TRUE;
            return new ExprLogicUnary(this, ExprLogicUnary.NOT);
        }

        default bool and(boolean v) {
            return new ExprLogicBinary(this, v, ExprLogicBinary.AND);
        }

        default bool or(boolean v) {
            return new ExprLogicBinary(this, v, ExprLogicBinary.OR);
        }

        default bool andOf(int v) {
            return new ExprLogicBinary(this, v, ExprLogicBinary.AND);
        }

        default bool orOf(int v) {
            return new ExprLogicBinary(this, v, ExprLogicBinary.OR);
        }
        //endregion
    }


}
