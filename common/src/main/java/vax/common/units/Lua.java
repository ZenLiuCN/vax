package vax.common.units;

import io.netty.util.collection.IntObjectMap;
import org.intellij.lang.annotations.MagicConstant;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Zen.Liu
 * @since 2025-02-16
 */
public interface Lua {
    //region TYPE
    byte T_NONE = -1;
    byte T_NIL = 0;
    byte T_BOOLEAN = 1;
    byte T_LIGHTUSERDATA = 2;
    byte T_NUMBER = 3;
    byte T_STRING = 4;
    byte T_TABLE = 5;
    byte T_FUNCTION = 6;
    byte T_USERDATA = 7;
    byte T_THREAD = 8;
    byte T_NUMTYPES = 9;// place holder
    String[] TYPE_NAMES = {
            "NIL",
            "BOOLEAN",
            "LIGHTUSERDATA",
            "NUMBER",
            "STRING",
            "TABLE",
            "FUNCTION",
            "USERDATA",
            "THREAD"
    };
    byte T_UPVAL = T_NUMTYPES;
    byte T_PROTO = T_NUMTYPES + 1;
    byte T_DEADKEY = T_NUMTYPES + 2;
    //endregion

    //region OP
    byte OP_ADD = 0;    /* ORDER TM, ORDER OP */
    byte OP_SUB = 1;
    byte OP_MUL = 2;
    byte OP_MOD = 3;
    byte OP_POW = 4;
    byte OP_DIV = 5;
    byte OP_IDIV = 6;
    byte OP_BAND = 7;
    byte OP_BOR = 8;
    byte OP_BXOR = 9;
    byte OP_SHL = 10;
    byte OP_SHR = 11;
    byte OP_UNM = 12;
    byte OP_BNOT = 13;

    byte OP_EQ = 0;
    byte OP_LT = 1;
    byte OP_LE = 2;
    //endregion

    interface LValue {
        @MagicConstant(intValues = {
                T_NONE,
                T_NIL,
                T_BOOLEAN,
                T_LIGHTUSERDATA,
                T_NUMBER,
                T_STRING,
                T_TABLE,
                T_FUNCTION,
                T_USERDATA,
                T_THREAD,
                T_NUMTYPES,
        })
        byte type();

        default String typeName() {
            var t = type();
            if (t == T_NONE) return "NONE";
            if (t >= T_NUMTYPES) return "ERROR_TYPE";
            return TYPE_NAMES[t];
        }


        static LValue of(Boolean v) {
            return v == null ? NIL : of((boolean) v);
        }

        static LValue of(boolean v) {
            return v ? TRUE : FALSE;
        }

        static LValue of(Double v) {
            return v == null ? NIL : LNumber.of(v);
        }

        static LValue of(Integer v) {
            return v == null ? NIL : LNumber.of(v);
        }

        static LValue valueOf(double v) {
            return LNumber.of(v);
        }

        static LValue of(int v) {
            return LNumber.of(v);
        }

        static LValue valueOf(String v) {
            return LString.of(v);
        }

        static boolean isNil(LValue v) {
            return v == null || v == NIL;
        }

        static boolean isFalse(LValue v) {
            return v == NIL || v == FALSE;
        }

        static boolean isString(LValue v) {
            return v != NIL && v instanceof LString;
        }

        default LString string() {
            if (this instanceof LString s) return s;
            throw new LError("require LString but got " + this.getClass().getSimpleName());
        }

        default LTable table() {
            if (this instanceof LTable s) return s;
            throw new LError("require LTable but got " + this.getClass().getSimpleName());
        }

        default LNumber number() {
            if (this instanceof LNumber s) return s;
            throw new LError("require LNumber but got " + this.getClass().getSimpleName());
        }

        default LInteger integer() {
            if (this instanceof LInteger s) return s;
            throw new LError("require LInteger but got " + this.getClass().getSimpleName());
        }

        default LFunction function() {
            if (this instanceof LFunction s) return s;
            throw new LError("require LFunction but got " + this.getClass().getSimpleName());
        }

        default LBoolean bool() {
            if (this instanceof LBoolean s) return s;
            throw new LError("require LBoolean but got " + this.getClass().getSimpleName());
        }

        default LUserData userData() {
            if (this instanceof LUserData s) return s;
            throw new LError("require LUserData but got " + this.getClass().getSimpleName());
        }

        default LLightUserData lightUserData() {
            if (this instanceof LLightUserData s) return s;
            throw new LError("require LLightUserData but got " + this.getClass().getSimpleName());
        }

        default LNumber asNumber() {
            throw new UnsupportedOperationException("");
        }

        default LString asString() {
            throw new UnsupportedOperationException("");
        }

        default LValue get(LValue key) {
            throw new UnsupportedOperationException("");
        }

        default void set(LValue key, LValue value) {
            throw new UnsupportedOperationException("");
        }


        default LValue not() {
            throw new UnsupportedOperationException("");
        }

        default LValue len() {
            throw new UnsupportedOperationException("");
        }

        default LValue concat(LValue[] lValues) {
            throw new UnsupportedOperationException("");
        }

        default int compare(LValue lValue) {
            throw new UnsupportedOperationException("");
        }

        default boolean eq(LValue lValue) {
            throw new UnsupportedOperationException("");
        }
    }

    LValue NIL = () -> T_NIL;
    LValue TRUE = new LBoolean(true);
    LValue FALSE = new LBoolean(false);
    LValue NONE = () -> T_NONE;

    record LBoolean(boolean v) implements LValue {
        @Override
        public byte type() {
            return T_BOOLEAN;
        }
    }

    interface LNumber extends LValue {
        @Override
        default byte type() {
            return T_NUMBER;
        }

        default LValue add(LValue r) {
            throw new UnsupportedOperationException("");
        }

        default LValue sub(LValue r) {
            throw new UnsupportedOperationException("");
        }

        default LValue mul(LValue r) {
            throw new UnsupportedOperationException("");
        }

        default LValue pow(LValue r) {
            throw new UnsupportedOperationException("");
        }

        default LValue mod(LValue r) {
            throw new UnsupportedOperationException("");
        }

        default LValue idiv(LValue r) {
            throw new UnsupportedOperationException("");
        }


        default LValue div(LValue r) {
            throw new UnsupportedOperationException("");
        }

        default LValue bAnd(LValue r) {
            throw new UnsupportedOperationException("");
        }

        default LValue bOr(LValue r) {
            throw new UnsupportedOperationException("");
        }

        default LValue bXor(LValue r) {
            throw new UnsupportedOperationException("");
        }

        default LValue bNot() {
            throw new UnsupportedOperationException("");
        }

        default LValue shl(LValue r) {
            throw new UnsupportedOperationException("");
        }

        default LValue shr(LValue r) {
            throw new UnsupportedOperationException("");
        }

        static LNumber of(double v) {
            var i = (int) v;
            return v == i ? of(i) : new LDouble(v);
        }

        static LNumber of(int i) {
            return i >= 0 && i < 512 ? LInteger.INTEGERS[i] : new LInteger(i);
        }
    }

    record LDouble(double v) implements LNumber {
        public static LNumber of(double v) {
            var i = (int) v;
            return v == i ? LNumber.of(i) : new LDouble(v);
        }

    }

    record LInteger(int v) implements LNumber {
        static final LInteger[] INTEGERS = new LInteger[512];

        static {
            for (int i = 0; i < 512; i++) {
                INTEGERS[i] = new LInteger(i);
            }
        }

        public boolean eq(int r) {
            throw new UnsupportedOperationException("");
        }

        public int compare(int r) {
            throw new UnsupportedOperationException("");
        }

        public LValue add(int r) {
            throw new UnsupportedOperationException("");
        }

        public LValue bAnd(int r) {
            throw new UnsupportedOperationException("");
        }

        public LValue bOr(int r) {
            throw new UnsupportedOperationException("");
        }

        public LValue bXor(int r) {
            throw new UnsupportedOperationException("");
        }

        public LValue bNot() {
            throw new UnsupportedOperationException("");
        }

        public LValue shr(int r) {
            throw new UnsupportedOperationException("");
        }

        public LValue shl(int r) {
            throw new UnsupportedOperationException("");
        }
    }

    record LString(String v) implements LValue {
        static final LString EMPTY = new LString("");

        public static LValue of(String v) {
            return v == null ? NIL : v.isEmpty() ? EMPTY : new LString(v);
        }

        @Override
        public byte type() {
            return T_STRING;
        }
    }

    class LTable implements LValue {
        protected IntObjectMap<LValue> v;
        protected volatile int[] index;
        protected volatile String[] name;

        public LTable(int size, int v) {

        }

        public LTable(IntObjectMap<LValue> v, int[] index, String[] name) {
            this.v = v;
            this.index = index;
            this.name = name;
        }

        @Override
        public byte type() {
            return T_TABLE;
        }

        public IntObjectMap<LValue> v() {return v;}

        public int[] index() {return index;}

        public String[] name() {return name;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null) return false;
            return obj instanceof LTable that && Objects.equals(this.v, that.v) &&
                    Arrays.equals(this.index, that.index) &&
                    Arrays.equals(this.name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(v, Arrays.hashCode(index), Arrays.hashCode(name));
        }

        @Override
        public String toString() {
            return "LTable[" +
                    "v=" + v + ", " +
                    "index=" + Arrays.toString(index) + ", " +
                    "name=" + Arrays.toString(name) + ']';
        }

    }

    record LUserData(Object v) implements LValue {


        public static LValue of(Object v) {
            return v == null ? NIL : new LUserData(v);
        }

        @Override
        public byte type() {
            return T_USERDATA;
        }
    }

    record LLightUserData(Object v) implements LValue {

        public static LValue of(Object v) {
            return v == null ? NIL : new LLightUserData(v);
        }

        @Override
        public byte type() {
            return T_LIGHTUSERDATA;
        }
    }

    record LMetaValue(AtomicReference<LTable> meta, LValue value) implements LValue {
        @Override
        public byte type() {
            return value.type();
        }
    }

    class LThread implements LValue {
        @Override
        public byte type() {
            return T_THREAD;
        }
    }

    class State implements Runnable {

        @Override
        public void run() {

        }
    }

    interface LFunction extends LValue {
        @Override
        default byte type() {
            return T_FUNCTION;
        }

        boolean isClosure();
    }

    class LError extends RuntimeException {
        LError(String message) {
            super(message);
        }
    }

    record LClosure(
            Prototype p,
            UpValue[] upValues,
            Globals globals
    ) implements LFunction {
        @Override
        public boolean isClosure() {
            return true;
        }

        void eval(LValue[] stack, LValue[] args) {
            int i, a, b, c, pc = 0, top = 0;
            LValue o;
            int[] code = p.code;
            LValue[] k = p.k;
            var ups = p.p.length > 0 ? new UpValue[stack.length] : null;

        }
    }

    record Prototype(
            LValue[] k,
            int[] code,
            Prototype[] p,
            int[] lineInfo,
            LocalVar[] locVars,
            UpValueDesc[] upValues,
            LString source,
            int lineDefined,
            int lastLineDefined,
            int numParams,
            int isVararg,
            int maxStackSize
    ) {

    }

    record UpValueDesc(LString name, boolean inStack, short index) {}

    record UpValue(LValue[] stack, int index) {
        LValue value() {
            return stack[index];
        }

        void value(LValue v) {
            stack[index] = v;
        }
    }

    record LocalVar(LString name, int startPC, int endPC) {}

    class Globals extends LTable {
        public InputStream STD_IN = null;
        public PrintStream STD_OUT = System.out;
        public PrintStream STD_ERR = System.err;

        public Globals(IntObjectMap<LValue> v, int[] index, String[] name) {
            super(v, index, name);
        }
    }

    /*===========================================================================
      We assume that instructions are unsigned 32-bit integers.
      All instructions have an opcode in the first 7 bits.
      Instructions can have the following formats:

            3 3 2 2 2 2 2 2 2 2 2 2 1 1 1 1 1 1 1 1 1 1 0 0 0 0 0 0 0 0 0 0
            1 0 9 8 7 6 5 4 3 2 1 0 9 8 7 6 5 4 3 2 1 0 9 8 7 6 5 4 3 2 1 0
    iABC          C(8)     |      B(8)     |k|     A(8)      |   Op(7)     |
    iABx                Bx(17)               |     A(8)      |   Op(7)     |
    iAsBx              sBx (signed)(17)      |     A(8)      |   Op(7)     |
    iAx                           Ax(25)                     |   Op(7)     |
    isJ                           sJ (signed)(25)            |   Op(7)     |

      A signed argument is represented in excess K: the represented value is
      the written unsigned value minus K, where K is half the maximum for the
      corresponding unsigned argument.
    ===========================================================================*/
    static int OP(int i) {
        return i & 0x7F;
    }

    static int A(int i) {
        return ((i >> 7) & 0xFF);
    }

    static int B(int i) {
        return (i >> 16) & 0xFF;
    }

    static int C(int i) {
        return i >> 24 & 0xFF;
    }

    static boolean k(int i) {
        return (i & 0x8000) != 0;
    }

    static int Bx(int i) {
        return (i >> 15) & 0x1FFFF;
    }

    static int Ax(int i) {
        return i >> 7 & 0x1FFFFFF;
    }

    static int sBx(int i) {
        return i >>> 15 & 0x1FFFF - 0xFFFF;
    }

    static int sJ(int i) {
        return i >> 7 & 0x1FFFFFF - 0xFFFFFF;
    }


    interface Operator {
        default void eval(int i, LValue[] stack, LValue[] k, UpValue[] u, AtomicInteger pc, int[] code) {
            eval(A(i), i, stack, k, u, pc, code);
        }

        void eval(int a, int i, LValue[] stack, LValue[] k, UpValue[] u, AtomicInteger pc, int[] code);
    }

    enum OP implements Operator {
        /**
         * A B	R[A] := R[B]
         */
        MOVE((a, i, s, k, u, p, code) ->
                     s[a] = s[B(i)]
        ),
        /**
         * A sBx	R[A] := sBx
         */
        LOADI((a, i, s, k, u, p, code) ->
                      s[a] = LValue.of(sBx(i))
        ),
        /**
         * A sBx	R[A] := (lua_Number)sBx
         */
        LOADF((a, i, s, k, u, p, code) ->
                      s[a] = LValue.valueOf((double) sBx(i))
        ),
        /**
         * A Bx	R[A] := K[Bx]
         */
        LOADK((a, i, s, k, u, p, code) ->
                      s[a] = k[Bx(i)]
        ),
        /**
         * A	R[A] := K[extra arg]
         */
        LOADKX((a, i, s, k, u, p, code) ->
               {
                   var x = code[p.incrementAndGet()];
                   if (OP(x) != 82)//OP.EXTRA_ARG.ordinal()
                       throw new LError("");
                   s[a] = k[Ax(x)];
               }
        ),
        /**
         * A	R[A] := false
         */
        LOADFALSE((a, i, s, k, u, p, code) ->
                          s[a] = FALSE
        ),
        /**
         * A	R[A] := false; pc++	(*)
         */
        LFALSESKIP((a, i, s, k, u, p, code) ->
                   {
                       s[a] = FALSE;
                       p.incrementAndGet();
                   }
        ),
        /**
         * A	R[A] := true
         */
        LOADTRUE((a, i, s, k, u, p, code) ->
                         s[a] = TRUE
        ),
        /**
         * A B	R[A], R[A+1], ..., R[A+B] := nil
         */
        LOADNIL((a, i, s, k, u, p, code) ->
                {
                    var x = B(i);
                    while (x > 0) {
                        s[a++] = NIL;
                        x--;
                    }
                }
        ),
        /**
         * A B	R[A] := UpValue[B]
         */
        GETUPVAL((a, i, s, k, u, p, code) ->
                         s[a] = u[B(i)].value()
        ),
        /**
         * A B	UpValue[B] := R[A]
         */
        SETUPVAL((a, i, s, k, u, p, code) ->
                         u[B(i)].value(s[a])
        ),
        /**
         * A B C	R[A] := UpValue[B][K[C]:shortstring]
         */
        GETTABUP((a, i, s, k, u, p, code) ->
                         s[a] = u[B(i)].value().get(k[C(i)].string())
        ),
        /**
         * A B C	R[A] := R[B][R[C]]
         */
        GETTABLE((a, i, s, k, u, p, code) ->
                         s[a] = s[B(i)].get(k[C(i)].table())
        ),
        /**
         * A B C	R[A] := R[B][C]
         */
        GETI((a, i, s, k, u, p, code) ->
                     s[a] = s[B(i)].get(k[C(i)]).integer()
        ),
        /**
         * A B C	R[A] := R[B][K[C]:shortstring]
         */
        GETFIELD((a, i, s, k, u, p, code) ->
                         s[a] = s[B(i)].get(k[C(i)].string())
        ),
        /**
         * A B C	UpValue[A][K[B]:shortstring] := RK(C)
         */
        SETTABUP((a, i, s, k, u, p, code) ->
                         u[a].value().table().set(k[B(i)].string(), k[C(i)])
        ),
        /**
         * A B C	R[A][R[B]] := RK(C)
         */
        SETTABLE((a, i, s, k, u, p, code) ->
                         s[a].table().set(k[B(i)], k[C(i)])
        ),
        /**
         * A B C	R[A][B] := RK(C)
         */
        SETI((a, i, s, k, u, p, code) ->
                     s[a].table().set(k[B(i)], k[C(i)].integer())
        ),
        /**
         * A B C	R[A][K[B]:shortstring] := RK(C)
         */
        SETFIELD((a, i, s, k, u, p, code) ->
                         s[a].table().set(k[B(i)].string(), k[C(i)])
        ),
        /**
         * A B C k	R[A] := {}
         */
        NEWTABLE((a, i, s, k, u, p, code) ->
                 {
                     var b = B(i);
                     var c = C(i);
                     if (b > 0)
                         b = 1 << (b - 1);
                     if (k(i)) c += Ax(p.incrementAndGet()) * (1 << 8);
                     s[a] = new LTable(b, c);
                 }
        ),
        /**
         * A B C	R[A+1] := R[B]; R[A] := R[B][RK(C):string]
         */
        SELF((a, i, s, k, u, p, code) ->
             {
                 s[a + 1] = s[B(i)];
                 s[a] = s[B(i)].table().get(k[C(i)].string());
             }
        ),
        /**
         * A B sC	R[A] := R[B] + sC
         */
        ADDI((a, i, s, k, u, p, code) ->
                     s[a] = s[B(i)].integer().add(C(i))
        ),
        /**
         * A B C	R[A] := R[B] + K[C]:number
         */
        ADDK((a, i, s, k, u, p, code) ->
                     s[a] = s[B(i)].number().add(k[C(i)].number())
        ),
        /**
         * A B C	R[A] := R[B] - K[C]:number
         */
        SUBK((a, i, s, k, u, p, code) ->
                     s[a] = s[B(i)].number().sub(k[C(i)].number())
        ),
        /**
         * A B C	R[A] := R[B] * K[C]:number
         */
        MULK((a, i, s, k, u, p, code) ->
                     s[a] = s[B(i)].number().mul(k[C(i)].number())
        ),
        /**
         * A B C	R[A] := R[B] % K[C]:number
         */
        MODK((a, i, s, k, u, p, code) ->
                     s[a] = s[B(i)].number().mod(k[C(i)].number())
        ),
        /**
         * A B C	R[A] := R[B] ^ K[C]:number
         */
        POWK((a, i, s, k, u, p, code) ->
                     s[a] = s[B(i)].number().pow(k[C(i)].number())
        ),
        /**
         * A B C	R[A] := R[B] / K[C]:number
         */
        DIVK((a, i, s, k, u, p, code) ->
                     s[a] = s[B(i)].number().div(k[C(i)].number())
        ),
        /**
         * A B C	R[A] := R[B] // K[C]:number
         */
        IDIVK((a, i, s, k, u, p, code) ->
                      s[a] = s[B(i)].number().idiv(k[C(i)].number())
        ),
        /**
         * A B C	R[A] := R[B] & K[C]:integer
         */
        BANDK((a, i, s, k, u, p, code) ->
                      s[a] = s[B(i)].integer().bAnd(k[C(i)].integer().v)
        ),
        /**
         * A B C	R[A] := R[B] | K[C]:integer
         */
        BORK((a, i, s, k, u, p, code) ->
                     s[a] = s[B(i)].integer().bOr(k[C(i)].integer().v)
        ),
        /**
         * A B C	R[A] := R[B] ~ K[C]:integer
         */
        BXORK((a, i, s, k, u, p, code) ->
                      s[a] = s[B(i)].integer().bXor(k[C(i)].integer().v)
        ),
        /**
         * A B sC	R[A] := R[B] >> sC
         */
        SHRI((a, i, s, k, u, p, code) ->
                     s[a] = s[B(i)].integer().shr(C(i))
        ),
        /**
         * A B sC	R[A] := sC << R[B]
         */
        SHLI((a, i, s, k, u, p, code) ->
                     s[a] = LValue.of(C(i) << s[B(i)].integer().v)
        ),
        /**
         * A B C	R[A] := R[B] + R[C]
         */
        ADD((a, i, s, k, u, p, code) ->
                    s[a] = s[B(i)].number().add(s[C(i)].number())
        ),
        /**
         * A B C	R[A] := R[B] - R[C]
         */
        SUB((a, i, s, k, u, p, code) ->
                    s[a] = s[B(i)].number().sub(s[C(i)].number())
        ),
        /**
         * A B C	R[A] := R[B] * R[C]
         */
        MUL((a, i, s, k, u, p, code) ->
                    s[a] = s[B(i)].number().mul(s[C(i)].number())
        ),
        /**
         * A B C	R[A] := R[B] % R[C]
         */
        MOD((a, i, s, k, u, p, code) ->
                    s[a] = s[B(i)].number().mod(s[C(i)].number())
        ),
        /**
         * A B C	R[A] := R[B] ^ R[C]
         */
        POW((a, i, s, k, u, p, code) ->
                    s[a] = s[B(i)].number().pow(s[C(i)].number())
        ),
        /**
         * A B C	R[A] := R[B] / R[C]
         */
        DIV((a, i, s, k, u, p, code) ->
                    s[a] = s[B(i)].number().div(s[C(i)].number())
        ),
        /**
         * A B C	R[A] := R[B] // R[C]
         */
        IDIV((a, i, s, k, u, p, code) ->
                     s[a] = s[B(i)].number().idiv(s[C(i)].number())
        ),
        /**
         * A B C	R[A] := R[B] & R[C]
         */
        BAND((a, i, s, k, u, p, code) ->
                     s[a] = s[B(i)].number().bAnd(s[C(i)].number())
        ),
        /**
         * A B C	R[A] := R[B] | R[C]
         */
        BOR((a, i, s, k, u, p, code) ->
                    s[a] = s[B(i)].number().bOr(s[C(i)].number())
        ),
        /**
         * A B C	R[A] := R[B] ~ R[C]
         */
        BXOR((a, i, s, k, u, p, code) ->
                     s[a] = s[B(i)].number().bXor(s[C(i)].number())
        ),
        /**
         * A B C	R[A] := R[B] << R[C]
         */
        SHL((a, i, s, k, u, p, code) ->
                    s[a] = s[B(i)].number().shl(s[C(i)].number())
        ),
        /**
         * A B C	R[A] := R[B] >> R[C]
         */
        SHR((a, i, s, k, u, p, code) ->
                    s[a] = s[B(i)].number().shr(s[C(i)].number())
        ),
        /**
         * A B C	call C metamethod over R[A] and R[B]	(*)
         */
        MMBIN((a, i, s, k, u, p, code) -> {
            //!! TODO
        }),
        /**
         * A sB C k	call C metamethod over R[A] and sB
         */
        MMBINI((a, i, s, k, u, p, code) -> {}),
        /**
         * A B C k		call C metamethod over R[A] and K[B]
         */
        MMBINK((a, i, s, k, u, p, code) -> {}),
        /**
         * A B	R[A] := -R[B]
         */
        UNM((a, i, s, k, u, p, code) -> {}),
        /**
         * A B	R[A] := ~R[B]
         */
        BNOT((a, i, s, k, u, p, code) ->
                     s[a] = s[B(i)].number().bNot()
        ),
        /**
         * A B	R[A] := not R[B]
         */
        NOT((a, i, s, k, u, p, code) ->
                    s[a] = s[B(i)].not()
        ),
        /**
         * A B	R[A] := #R[B] (length operator)
         */
        LEN((a, i, s, k, u, p, code) ->
                    s[a] = s[B(i)].len()
        ),
        /**
         * A B	R[A] := R[A].. ... ..R[A + B - 1]
         */
        CONCAT((a, i, s, k, u, p, code) ->
               {
                   var b = B(i);
                   s[a] = s[a].concat(Arrays.copyOfRange(s, a, a + b));
               }
        ),
        /**
         * A	close all upvalues >= R[A]
         */
        CLOSE((a, i, s, k, u, p, code) -> {}),
        /**
         * A	mark variable A "to be closed"
         */
        TBC((a, i, s, k, u, p, code) -> {}),
        /**
         * sJ	pc += sJ
         */
        JMP((a, i, s, k, u, p, code) ->
                    p.addAndGet(sJ(i))
        ),
        /**
         * A B k	if ((R[A] == R[B]) ~= k) then pc++
         */
        EQ((a, i, s, k, u, p, code) ->
           {
               if (s[a].equals(s[B(i)]) == k(i)) p.incrementAndGet();
           }
        ),
        /**
         * A B k	if ((R[A] <  R[B]) ~= k) then pc++
         */
        LT((a, i, s, k, u, p, code) ->
           {
               if (s[a].compare(s[B(i)]) < 0 != k(i)) p.incrementAndGet();
           }
        ),
        /**
         * A B k	if ((R[A] <= R[B]) ~= k) then pc++
         */
        LE((a, i, s, k, u, p, code) ->
           {
               if (s[a].compare(s[B(i)]) <= 0 != k(i)) p.incrementAndGet();
           }
        ),
        /**
         * A B k	if ((R[A] == K[B]) ~= k) then pc++
         */
        EQK((a, i, s, k, u, p, code) ->
            {
                if (s[a].eq(k[B(i)]) != k(i)) p.incrementAndGet();
            }
        ),
        /**
         * A sB k	if ((R[A] == sB) ~= k) then pc++
         */
        EQI((a, i, s, k, u, p, code) ->
            {
                if (s[a].integer().eq(B(i)) != k(i)) p.incrementAndGet();
            }
        ),
        /**
         * A sB k	if ((R[A] < sB) ~= k) then pc++
         */
        LTI((a, i, s, k, u, p, code) ->
            {
                if (s[a].integer().compare(B(i)) < 0 != k(i)) p.incrementAndGet();
            }
        ),
        /**
         * A sB k	if ((R[A] <= sB) ~= k) then pc++
         */
        LEI((a, i, s, k, u, p, code) ->
            {
                if (s[a].integer().compare(B(i)) <= 0 != k(i)) p.incrementAndGet();
            }
        ),
        /**
         * A sB k	if ((R[A] > sB) ~= k) then pc++
         */
        GTI((a, i, s, k, u, p, code) ->
            {
                if (s[a].integer().compare(B(i)) > 0 != k(i)) p.incrementAndGet();
            }
        ),
        /**
         * A sB k	if ((R[A] >= sB) ~= k) then pc++
         */
        GEI((a, i, s, k, u, p, code) ->
            {
                if (s[a].integer().compare(B(i)) >= 0 != k(i)) p.incrementAndGet();
            }
        ),
        /**
         * A k	if (not R[A] == k) then pc++
         */
        TEST((a, i, s, k, u, p, code) ->
             {
                 if (s[a].bool().v != k(i)) p.incrementAndGet();
             }
        ),
        /**
         * A B k	if (not R[B] == k) then pc++ else R[A] := R[B] (*)
         */
        TESTSET((a, i, s, k, u, p, code) ->
                {
                    if (s[B(i)].bool().v != k(i)) p.incrementAndGet();
                    else s[a] = s[B(i)];
                }
        ),
        /**
         * A B C	R[A], ... ,R[A+C-2] := R[A](R[A+1], ... ,R[A+B-1])
         */
        CALL((a, i, s, k, u, p, code) ->
             {
                 //TODO
             }
        ),
        /**
         * A B C k	return R[A](R[A+1], ... ,R[A+B-1])
         */
        TAILCALL((a, i, s, k, u, p, code) -> {
            //todo
        }),
        /**
         * A B C k	return R[A], ... ,R[A+B-2]	(see note)
         */
        RETURN((a, i, s, k, u, p, code) -> {
            //todo
        }),
        /**
         * return
         */
        RETURN0((a, i, s, k, u, p, code) -> {
            p.set(-1);
            s[s.length-1]=NONE;//TODO
        }),
        /**
         * A	return R[A]
         */
        RETURN1((a, i, s, k, u, p, code) -> {
            p.set(-1);
            s[s.length-1]=s[a];//TODO
        }),
        /**
         * A Bx	update counters; if loop continues then pc-=Bx;
         */
        FORLOOP((a, i, s, k, u, p, code) -> {
            var b=Bx(i);

        }),
        /**
         * A Bx	<check values and prepare counters>; if not to run then pc+=Bx+1;
         */
        FORPREP((a, i, s, k, u, p, code) -> {}),
        /**
         * A Bx	create upvalue for R[A + 3]; pc+=Bx
         */
        TFORPREP((a, i, s, k, u, p, code) -> {}),
        /**
         * A C	R[A+4], ... ,R[A+3+C] := R[A](R[A+1], R[A+2]);
         */
        TFORCALL((a, i, s, k, u, p, code) -> {}),
        /**
         * A Bx	if R[A+2] ~= nil then { R[A]=R[A+2]; pc -= Bx }
         */
        TFORLOOP((a, i, s, k, u, p, code) -> {}),
        /**
         * A B C k	R[A][C+i] := R[A+i], 1 <= i <= B
         */
        SETLIST((a, i, s, k, u, p, code) -> {}),
        /**
         * A Bx	R[A] := closure(KPROTO[Bx])
         */
        CLOSURE((a, i, s, k, u, p, code) -> {}),
        /**
         * A C	R[A], R[A+1], ..., R[A+C-2] = vararg
         */
        VARARG((a, i, s, k, u, p, code) -> {}),
        /**
         * A	(adjust vararg parameters)
         */
        VARARGPREP((a, i, s, k, u, p, code) -> {}),
        /**
         * Ax	extra (larger) argument for previous opcode
         */
        EXTRA_ARG((a, i, s, k, u, p, code) -> {
            throw new LError("invalid OP.EXTRA_ARG");
        }),

        ;

        public final Operator operator;

        OP(Operator o) {
            this.operator = o;
        }

        @Override
        public void eval(int a, int i, LValue[] stack, LValue[] k, UpValue[] u, AtomicInteger pc, int[] code) {
            operator.eval(a, i, stack, k, u, pc, code);
        }
    }
}
