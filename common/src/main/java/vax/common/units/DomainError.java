package vax.common.units;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.Nullable;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.slf4j.helpers.MessageFormatter;
import vax.common.trait.XData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * System errors
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */

@SuppressWarnings("unused")
public class DomainError extends RuntimeException implements XData {

    public static final AtomicInteger STACK_TRACE_SIZE = new AtomicInteger(1024);
    /// error code
    public final int code;
    /// notify mode for user message
    public final int mode;
    /// user message
    public final String user;
    /// system message
    public final String system;
    protected transient JsonArray stacktrace;

    public static Tuple2<String, Throwable> format(String pattern, Object... args) {
        var o = MessageFormatter.format(pattern, args);
        return Tuple.tuple(o.getMessage(), o.getThrowable());
    }

    public static Tuple3<String, String, Throwable> formatAll(@Nullable String userPattern, @Nullable String systemPattern, Object... args) {
        if ((userPattern == null || userPattern.isBlank()) && (systemPattern == null || systemPattern.isBlank())) {
            return Tuple.tuple(null, null, null);
        }
        var u = userPattern == null || userPattern.isBlank() ? null : MessageFormatter.format(userPattern, args);
        var s = systemPattern == null || systemPattern.isBlank() ? null : MessageFormatter.format(systemPattern, args);
        //noinspection DataFlowIssue
        return Tuple.tuple(
                u == null ? null : u.getMessage(),
                s == null ? null : s.getMessage(),
                (u == null ? s : u).getThrowable());
    }

    public JsonArray stacktrace() {
        if (stacktrace == null) {
            var sb = new ByteArrayOutputStream(STACK_TRACE_SIZE.get());
            this.printStackTrace(new PrintWriter(sb, true, StandardCharsets.UTF_8));
            var s = sb.toString();
            if (s != null && !s.isBlank()) {
                var j = new JsonArray();
                for (var line : s.split("\n")) {
                    j.add(line.replaceAll("\t", "    "));
                }
                stacktrace = j;
            } else {
                stacktrace = JsonArray.of(s);
            }
            try {
                sb.close();
            } catch (IOException ignored) {
            }
        }
        return stacktrace;
    }

    public DomainError(JsonObject j) {
        this(
                j.getInteger("code", 0),
                j.getInteger("mode", 0),
                j.getString("user", null),
                j.getString("system", null),
                parseCause(j.getJsonObject("cause"))
        );
        this.stacktrace = j.getJsonArray("trace");
    }

    static Throwable parseCause(@Nullable JsonObject cause) {
        if (cause == null) return null;
        return new RuntimeException(cause.getString(CLASS_KEY) + ":" + cause.getString("message"));
    }

    @Override
    public JsonObject $toJson() {
        return JsonObject.of(
                "code", code
                , "mode", mode
                , "user", user
                , "cause", getCause() == null ? null : JsonObject.of(
                        CLASS_KEY, getCause().getClass().getName(),
                        "message", getCause().getMessage()
                )
                , "trace", stacktrace()
        );
    }

    public DomainError(int code, int mode, String user, String system, Throwable cause) {
        super(system == null || system.isBlank() ? user : system, cause);
        this.mode = mode;
        this.code = code;
        this.user = user;
        this.system = system;
    }

    public DomainError(int code, int mode, String user, String system, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(system == null || system.isBlank() ? user : system, cause, enableSuppression, writableStackTrace);
        this.mode = mode;
        this.code = code;
        this.user = user;
        this.system = system;
    }

    public static DomainError error(int code, int mode, @Nullable String user, @Nullable String system, Object... args) {
        var u = formatAll(user, system, args);
        return new DomainError(code, mode, u.v1, u.v2, u.v3);
    }

    //region Codes
    public static final int MODE_IGNORE = 0;
    public static final int MODE_NOTIFY = 1;
    public static final int MODE_WARN = 2;
    public static final int MODE_ERROR = 3;

    public Optional<String> modeName() {
        return switch (mode) {
            case MODE_IGNORE -> Optional.of("IGNORE");
            case MODE_NOTIFY -> Optional.of("NOTIFY");
            case MODE_WARN -> Optional.of("WARN");
            case MODE_ERROR -> Optional.of("ERROR");
            default -> Optional.empty();
        };
    }

    public static final int CODE_BAD_REQUEST = 400;
    public static final int CODE_UNAUTHORIZED = 401;
    public static final int CODE_PAYMENT_REQUIRED = 402;
    public static final int CODE_FORBIDDEN = 403;
    public static final int CODE_NOT_FOUND = 404;
    public static final int CODE_METHOD_NOT_ALLOWED = 405;
    public static final int CODE_NOT_ACCEPTABLE = 406;
    public static final int CODE_PROXY_AUTHENTICATION_REQUIRED = 407;
    public static final int CODE_REQUEST_TIMEOUT = 408;
    public static final int CODE_CONFLICT = 409;
    public static final int CODE_GONE = 410;
    public static final int CODE_LENGTH_REQUIRED = 411;
    public static final int CODE_PRECONDITION_FAILED = 412;
    public static final int CODE_UNSUPPORTED_MEDIA_TYPE = 415;
    public static final int CODE_UNSUPPORTED_TYPE = 416;
    public static final int CODE_TOO_MANY_REQUESTS = 429;
    public static final int CODE_UNAVAILABLE_FOR_LEGAL_REASONS = 451;
    public static final int CODE_INTERNAL_SERVER_ERROR = 500;
    public static final int CODE_NOT_IMPLEMENTED = 501;
    public static final int CODE_BAD_GATEWAY = 502;
    public static final int CODE_SERVICE_UNAVAILABLE = 503;
    public static final int CODE_GATEWAY_TIMEOUT = 504;
    public static final int CODE_NETWORK_AUTHENTICATION_REQUIRED = 505;
    public static final IntObjectMap<String> CODE_NAMES = new IntObjectHashMap<>();

    static {

        CODE_NAMES.put(CODE_BAD_REQUEST, "BAD_REQUEST");
        CODE_NAMES.put(CODE_UNAUTHORIZED, "UNAUTHORIZED");
        CODE_NAMES.put(CODE_PAYMENT_REQUIRED, "PAYMENT_REQUIRED");
        CODE_NAMES.put(CODE_FORBIDDEN, "FORBIDDEN");
        CODE_NAMES.put(CODE_NOT_FOUND, "NOT_FOUND");
        CODE_NAMES.put(CODE_METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED");
        CODE_NAMES.put(CODE_NOT_ACCEPTABLE, "NOT_ACCEPTABLE");
        CODE_NAMES.put(CODE_PROXY_AUTHENTICATION_REQUIRED, "PROXY_AUTHENTICATION_REQUIRED");
        CODE_NAMES.put(CODE_REQUEST_TIMEOUT, "REQUEST_TIMEOUT");
        CODE_NAMES.put(CODE_CONFLICT, "CONFLICT");
        CODE_NAMES.put(CODE_GONE, "GONE");
        CODE_NAMES.put(CODE_LENGTH_REQUIRED, "LENGTH_REQUIRED");
        CODE_NAMES.put(CODE_PRECONDITION_FAILED, "PRECONDITION_FAILED");
        CODE_NAMES.put(CODE_UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE");
        CODE_NAMES.put(CODE_UNSUPPORTED_TYPE, "UNSUPPORTED_TYPE");
        CODE_NAMES.put(CODE_TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS");
        CODE_NAMES.put(CODE_UNAVAILABLE_FOR_LEGAL_REASONS, "UNAVAILABLE_FOR_LEGAL_REASONS");
        CODE_NAMES.put(CODE_INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");
        CODE_NAMES.put(CODE_NOT_IMPLEMENTED, "NOT_IMPLEMENTED");
        CODE_NAMES.put(CODE_BAD_GATEWAY, "BAD_GATEWAY");
        CODE_NAMES.put(CODE_SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE");
        CODE_NAMES.put(CODE_GATEWAY_TIMEOUT, "GATEWAY_TIMEOUT");
        CODE_NAMES.put(CODE_NETWORK_AUTHENTICATION_REQUIRED, "NETWORK_AUTHENTICATION_REQUIRED");
    }
    //endregion

    /**
     * @return standard code names
     */
    public Optional<String> codeName() {
        return Optional.ofNullable(CODE_NAMES.get(code));
    }

    //region Builder
    public static Builder badRequest() {
        return new Builder(CODE_BAD_REQUEST, 0, null, null);
    }

    public static Builder unauthorized() {
        return new Builder(CODE_UNAUTHORIZED, 0, null, null);
    }

    public static Builder paymentRequired() {
        return new Builder(CODE_PAYMENT_REQUIRED, 0, null, null);
    }

    public static Builder forbidden() {
        return new Builder(CODE_FORBIDDEN, 0, null, null);
    }

    public static Builder notFound() {
        return new Builder(CODE_NOT_FOUND, 0, null, null);
    }

    public static Builder methodNotAllowed() {
        return new Builder(CODE_METHOD_NOT_ALLOWED, 0, null, null);
    }

    public static Builder notAcceptable() {
        return new Builder(CODE_NOT_ACCEPTABLE, 0, null, null);
    }

    public static Builder proxyAuthenticationRequired() {
        return new Builder(CODE_PROXY_AUTHENTICATION_REQUIRED, 0, null, null);
    }

    public static Builder requestTimeout() {
        return new Builder(CODE_REQUEST_TIMEOUT, 0, null, null);
    }

    public static Builder conflict() {
        return new Builder(CODE_CONFLICT, 0, null, null);
    }

    public static Builder gone() {
        return new Builder(CODE_GONE, 0, null, null);
    }

    public static Builder lengthRequired() {
        return new Builder(CODE_LENGTH_REQUIRED, 0, null, null);
    }

    public static Builder preconditionFailed() {
        return new Builder(CODE_PRECONDITION_FAILED, 0, null, null);
    }

    public static Builder unsupportedMediaType() {
        return new Builder(CODE_UNSUPPORTED_MEDIA_TYPE, 0, null, null);
    }

    public static Builder unsupportedType() {
        return new Builder(CODE_UNSUPPORTED_TYPE, 0, null, null);
    }

    public static Builder tooManyRequests() {
        return new Builder(CODE_TOO_MANY_REQUESTS, 0, null, null);
    }

    public static Builder unavailableForLegalReasons() {
        return new Builder(CODE_UNAVAILABLE_FOR_LEGAL_REASONS, 0, null, null);
    }

    public static Builder internalServerError() {
        return new Builder(CODE_INTERNAL_SERVER_ERROR, 0, null, null);
    }

    public static Builder notImplemented() {
        return new Builder(CODE_NOT_IMPLEMENTED, 0, null, null);
    }

    public static Builder badGateway() {
        return new Builder(CODE_BAD_GATEWAY, 0, null, null);
    }

    public static Builder serviceUnavailable() {
        return new Builder(CODE_SERVICE_UNAVAILABLE, 0, null, null);
    }

    public static Builder gatewayTimeout() {
        return new Builder(CODE_GATEWAY_TIMEOUT, 0, null, null);
    }

    public static Builder networkAuthenticationRequired() {
        return new Builder(CODE_NETWORK_AUTHENTICATION_REQUIRED, 0, null, null);
    }
    //endregion

    public record Builder(
            int code,
            int mode,
            String user,
            String sys
    ) {
        public Builder mode(int mode) {
            return new Builder(code, mode, user, sys);
        }

        public Builder code(int code) {
            return new Builder(code, mode, user, sys);
        }

        public Builder system(String pattern) {
            return new Builder(code, mode, user, pattern);
        }

        public Builder user(String pattern) {
            return new Builder(code, mode, pattern, sys);
        }

        public DomainError all(String pattern, Object... args) {
            return error(code, mode, pattern, pattern, args);
        }

        public DomainError both(String user, String system, Object... args) {
            return error(code, mode, user, system, args);
        }

        public DomainError user(int mode, String user, Object... args) {
            return error(code, mode, user, sys, args);
        }

        public DomainError user(String user, Object... args) {
            return error(code, mode, user, sys, args);
        }

        public DomainError sys(String system, Object... args) {
            return error(code, mode, user, system, args);
        }

        public DomainError get(Object... args) {
            return error(code, mode, user, sys, args);
        }
    }


}
