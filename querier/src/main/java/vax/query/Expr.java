package vax.query;

import java.util.Arrays;

/**
 * Expression model
 *
 * @author Zen.Liu
 * @since 2024-12-08
 */
public interface Expr<T> {
    interface ModifyExpr<T> extends Expr<T> {}
    interface AssignExpr<T> extends Expr<T> {}
    //region Exprs

    record ExprNull<T>(Expr<T> v, boolean equal) implements Value.bool {}

    record ExprEqual<T>(Expr<T> v0, Expr<T> v1, boolean equal, boolean caseInsensitive) implements Value.bool {
        public ExprEqual(Expr<T> v0, Expr<T> v1, boolean equal) {
            this(v0, v1, equal, false);
        }

        public ExprEqual(Expr<T> v0, T v1, boolean equal) {
            this(v0, Param.of(v1), equal, false);
        }

        public ExprEqual(Expr<T> v0, Class<T> t, int v1, boolean equal) {
            this(v0, Holder.of(t, v1), equal, false);
        }

        public ExprEqual(Expr<T> v0, Class<T> t, int v1, boolean equal, boolean caseInsensitive) {
            this(v0, Holder.of(t, v1), equal, caseInsensitive);
        }

        public ExprEqual(Expr<T> v0, T v1, boolean equal, boolean caseInsensitive) {
            this(v0, Param.of(v1), equal, caseInsensitive);
        }
    }

    record ExprCompare<T>(Expr<T> v0, Expr<T> v1, byte mode) implements Value.bool {
        ExprCompare(Expr<T> v0, T v1, byte mode) {
            this(v0, Param.of(v1), mode);
        }

        ExprCompare(Expr<T> v0, Class<T> t, int v1, byte mode) {
            this(v0, Holder.of(t, v1), mode);
        }

        public static final byte LT = 1;
        public static final byte LTE = 2;
        public static final byte GT = 3;
        public static final byte GTE = 4;
    }

    record ExprIn<T>(Expr<T> v0, Expr<T>[] v1, boolean in) implements Value.bool {
        @SuppressWarnings("unchecked")
        ExprIn(Expr<T> v0, T[] v1, boolean in) {
            this(v0, Arrays.stream(v1).map(Param::of).toArray(Expr[]::new), in);
        }
    }

    record ExprBtw<T>(Expr<T> v0, Expr<T> v1, Expr<T> v2, boolean in) implements Value.bool {
        ExprBtw(Expr<T> v0, T v1, Expr<T> v2, boolean in) {
            this(v0, Param.of(v1), v2, in);
        }

        ExprBtw(Expr<T> v0, Class<T> t, int v1, int v2, boolean in) {
            this(v0, Holder.of(t, v1), Holder.of(t, v2), in);
        }

        ExprBtw(Expr<T> v0, Expr<T> v1, T v2, boolean in) {
            this(v0, v1, Param.of(v2), in);
        }

        ExprBtw(Expr<T> v0, T v1, T v2, boolean in) {
            this(v0, Param.of(v1), Param.of(v1), in);
        }
    }

    record ExprMathUnary<T extends Number>(Expr<T> v0, byte op) implements Value.numeric<T> {
        public static final byte NEGATIVE = 1;
        public static final byte BIT_NOT = 2;

        public boolean isNEGATIVE() {return op == NEGATIVE;}

        public boolean isbNOT() {return op == BIT_NOT;}
    }

    record ExprMathBinary<T extends Number>(Expr<T> v0, Expr<T> v1, byte op) implements Value.numeric<T> {
        ExprMathBinary(Expr<T> v0, T v1, byte op) {
            this(v0, Param.of(v1), op);
        }

        ExprMathBinary(Expr<T> v0, Class<T> type, int v1, byte op) {
            this(v0, Holder.of(type, v1), op);
        }

        public static final byte PLUS = 1;
        public static final byte MINUS = 2;
        public static final byte DIVIDE = 3;
        public static final byte TIMES = 4;
        public static final byte MODULO = 5;
        public static final byte REMINDER = 6;
        public static final byte BIT_AND = 7;
        public static final byte BIT_OR = 8;
        public static final byte BIT_XOR = 9;

        public boolean isPLUS() {return op == PLUS;}

        public boolean isMINUS() {return op == MINUS;}

        public boolean isDIVIDE() {return op == DIVIDE;}

        public boolean isTIMES() {return op == TIMES;}

        public boolean isMODULO() {return op == MODULO;}

        public boolean isREMINDER() {return op == REMINDER;}

        public boolean isbAND() {return op == BIT_AND;}

        public boolean isbOR() {return op == BIT_OR;}

        public boolean isbXOR() {return op == BIT_XOR;}
    }

    record ExprLogicBinary(Expr<Boolean> v0, Expr<Boolean> v1, byte op) implements Value.bool {
        ExprLogicBinary(Expr<Boolean> v0, boolean v1, byte op) {
            this(v0, Param.of(v1), op);
        }

        ExprLogicBinary(Expr<Boolean> v0, int v1, byte op) {
            this(v0, Holder.of(Boolean.class, v1), op);
        }

        public static final byte AND = 1;
        public static final byte OR = 2;

        public boolean isAND() {return op == AND;}

        public boolean isOR() {return op == OR;}
    }

    record ExprLogicUnary(Expr<Boolean> v0, byte op) implements Value.bool {
        public static final byte NOT = 1;
    }

    record ExprMatchBinary(Expr<CharSequence> v0, Expr<CharSequence> v1, byte op,
                           boolean caseInsensitive) implements Value.bool {
        ExprMatchBinary(Expr<CharSequence> v0, Expr<CharSequence> v1, byte op) {
            this(v0, v1, op, false);
        }

        ExprMatchBinary(Expr<CharSequence> v0, CharSequence v1, byte op) {
            this(v0, Param.of(v1), op, false);
        }

        ExprMatchBinary(Expr<CharSequence> v0, CharSequence v1, byte op, boolean caseInsensitive) {
            this(v0, Param.of(v1), op, caseInsensitive);
        }

        ExprMatchBinary(Expr<CharSequence> v0, int v1, byte op) {
            this(v0, Holder.of(CharSequence.class, v1), op, false);
        }

        ExprMatchBinary(Expr<CharSequence> v0, int v1, byte op, boolean caseInsensitive) {
            this(v0, Holder.of(CharSequence.class, v1), op, caseInsensitive);
        }

        public static final byte LIKE = 1;
        public static final byte START = 2;
        public static final byte END = 3;
    }

    //endregion



}
