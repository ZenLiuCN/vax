package vax.common.units;

import io.vertx.core.json.JsonArray;
import org.jetbrains.annotations.Nullable;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.slf4j.helpers.MessageFormatter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * System errors
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */

public class DomainError extends RuntimeException {
    public static final AtomicInteger STACK_TRACE_SIZE = new AtomicInteger(1024);
    public final int code;
    public final String user;
    public final String system;
    protected transient JsonArray stacktrace;

    static public Tuple2<String, Throwable> format(String pattern, Object... args) {
        var o = MessageFormatter.format(pattern, args);
        return Tuple.tuple(o.getMessage(), o.getThrowable());
    }

    static public Tuple3<String, String, Throwable> formatAll(@Nullable String userPattern, @Nullable String systemPattern, Object... args) {
        if ((userPattern == null || userPattern.isBlank()) && (systemPattern == null || systemPattern.isBlank())) {
            return Tuple.tuple(null, null, null);
        }

        var u = userPattern == null || userPattern.isBlank() ? null : MessageFormatter.format(userPattern, args);
        var s = systemPattern == null || systemPattern.isBlank() ? null : MessageFormatter.format(systemPattern, args);
        return Tuple.tuple(u == null ? null : u.getMessage(), s == null ? null : s.getMessage(), (u == null ? s : u).getThrowable());
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

    DomainError(int code, String user, String system, Throwable cause) {
        super(system == null || system.isBlank() ? user : system, cause);
        this.code = code;
        this.user = user;
        this.system = system;
    }

    DomainError(int code, String user, String system, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(system == null || system.isBlank() ? user : system, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.user = user;
        this.system = system;
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


    public static DomainError badRequest(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_BAD_REQUEST, user, system, cause);
    }

    public static DomainError unauthorized(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_UNAUTHORIZED, user, system, cause);
    }

    public static DomainError paymentRequired(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_PAYMENT_REQUIRED, user, system, cause);
    }

    public static DomainError forbidden(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_FORBIDDEN, user, system, cause);
    }

    public static DomainError notFound(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_NOT_FOUND, user, system, cause);
    }

    public static DomainError methodNotAllowed(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_METHOD_NOT_ALLOWED, user, system, cause);
    }

    public static DomainError notAcceptable(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_NOT_ACCEPTABLE, user, system, cause);
    }

    public static DomainError proxyAuthenticationRequired(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_PROXY_AUTHENTICATION_REQUIRED, user, system, cause);
    }

    public static DomainError requestTimeout(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_REQUEST_TIMEOUT, user, system, cause);
    }

    public static DomainError conflict(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_CONFLICT, user, system, cause);
    }

    public static DomainError gone(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_GONE, user, system, cause);
    }

    public static DomainError lengthRequired(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_LENGTH_REQUIRED, user, system, cause);
    }

    public static DomainError preconditionFailed(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_PRECONDITION_FAILED, user, system, cause);
    }

    public static DomainError unsupportedMediaType(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_UNSUPPORTED_MEDIA_TYPE, user, system, cause);
    }

    public static DomainError unsupportedType(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_UNSUPPORTED_TYPE, user, system, cause);
    }

    public static DomainError tooManyRequests(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_TOO_MANY_REQUESTS, user, system, cause);
    }

    public static DomainError unavailableForLegalReasons(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_UNAVAILABLE_FOR_LEGAL_REASONS, user, system, cause);
    }

    public static DomainError internalServerError(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_INTERNAL_SERVER_ERROR, user, system, cause);
    }

    public static DomainError notImplemented(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_NOT_IMPLEMENTED, user, system, cause);
    }

    public static DomainError badGateway(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_BAD_GATEWAY, user, system, cause);
    }

    public static DomainError serviceUnavailable(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_SERVICE_UNAVAILABLE, user, system, cause);
    }

    public static DomainError gatewayTimeout(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_GATEWAY_TIMEOUT, user, system, cause);
    }

    public static DomainError networkAuthenticationRequired(@Nullable String user, @Nullable String system, @Nullable Throwable cause) {
        return new DomainError(CODE_NETWORK_AUTHENTICATION_REQUIRED, user, system, cause);
    }

    public static DomainError badRequest(Tuple3<String, String, Throwable> u) {
        return badRequest(u.v1, u.v2, u.v3);
    }

    public static DomainError unauthorized(Tuple3<String, String, Throwable> u) {
        return unauthorized(u.v1, u.v2, u.v3);
    }

    public static DomainError paymentRequired(Tuple3<String, String, Throwable> u) {
        return paymentRequired(u.v1, u.v2, u.v3);
    }

    public static DomainError forbidden(Tuple3<String, String, Throwable> u) {
        return forbidden(u.v1, u.v2, u.v3);
    }

    public static DomainError notFound(Tuple3<String, String, Throwable> u) {
        return notFound(u.v1, u.v2, u.v3);
    }

    public static DomainError methodNotAllowed(Tuple3<String, String, Throwable> u) {
        return methodNotAllowed(u.v1, u.v2, u.v3);
    }

    public static DomainError notAcceptable(Tuple3<String, String, Throwable> u) {
        return notAcceptable(u.v1, u.v2, u.v3);
    }

    public static DomainError proxyAuthenticationRequired(Tuple3<String, String, Throwable> u) {
        return proxyAuthenticationRequired(u.v1, u.v2, u.v3);
    }

    public static DomainError requestTimeout(Tuple3<String, String, Throwable> u) {
        return requestTimeout(u.v1, u.v2, u.v3);
    }

    public static DomainError conflict(Tuple3<String, String, Throwable> u) {
        return conflict(u.v1, u.v2, u.v3);
    }

    public static DomainError gone(Tuple3<String, String, Throwable> u) {
        return gone(u.v1, u.v2, u.v3);
    }

    public static DomainError lengthRequired(Tuple3<String, String, Throwable> u) {
        return lengthRequired(u.v1, u.v2, u.v3);
    }

    public static DomainError preconditionFailed(Tuple3<String, String, Throwable> u) {
        return preconditionFailed(u.v1, u.v2, u.v3);
    }

    public static DomainError unsupportedMediaType(Tuple3<String, String, Throwable> u) {
        return unsupportedMediaType(u.v1, u.v2, u.v3);
    }

    public static DomainError unsupportedType(Tuple3<String, String, Throwable> u) {
        return unsupportedType(u.v1, u.v2, u.v3);
    }

    public static DomainError tooManyRequests(Tuple3<String, String, Throwable> u) {
        return tooManyRequests(u.v1, u.v2, u.v3);
    }

    public static DomainError unavailableForLegalReasons(Tuple3<String, String, Throwable> u) {
        return unavailableForLegalReasons(u.v1, u.v2, u.v3);
    }

    public static DomainError internalServerError(Tuple3<String, String, Throwable> u) {
        return internalServerError(u.v1, u.v2, u.v3);
    }

    public static DomainError notImplemented(Tuple3<String, String, Throwable> u) {
        return notImplemented(u.v1, u.v2, u.v3);
    }

    public static DomainError badGateway(Tuple3<String, String, Throwable> u) {
        return badGateway(u.v1, u.v2, u.v3);
    }

    public static DomainError serviceUnavailable(Tuple3<String, String, Throwable> u) {
        return serviceUnavailable(u.v1, u.v2, u.v3);
    }

    public static DomainError gatewayTimeout(Tuple3<String, String, Throwable> u) {
        return gatewayTimeout(u.v1, u.v2, u.v3);
    }

    public static DomainError networkAuthenticationRequired(Tuple3<String, String, Throwable> u) {
        return networkAuthenticationRequired(u.v1, u.v2, u.v3);
    }

    public static DomainError badRequest(String userPattern, String systemPattern, Object... args) {
        return badRequest(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError unauthorized(String userPattern, String systemPattern, Object... args) {
        return unauthorized(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError paymentRequired(String userPattern, String systemPattern, Object... args) {
        return paymentRequired(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError forbidden(String userPattern, String systemPattern, Object... args) {
        return forbidden(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError notFound(String userPattern, String systemPattern, Object... args) {
        return notFound(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError methodNotAllowed(String userPattern, String systemPattern, Object... args) {
        return methodNotAllowed(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError notAcceptable(String userPattern, String systemPattern, Object... args) {
        return notAcceptable(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError proxyAuthenticationRequired(String userPattern, String systemPattern, Object... args) {
        return proxyAuthenticationRequired(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError requestTimeout(String userPattern, String systemPattern, Object... args) {
        return requestTimeout(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError conflict(String userPattern, String systemPattern, Object... args) {
        return conflict(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError gone(String userPattern, String systemPattern, Object... args) {
        return gone(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError lengthRequired(String userPattern, String systemPattern, Object... args) {
        return lengthRequired(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError preconditionFailed(String userPattern, String systemPattern, Object... args) {
        return preconditionFailed(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError unsupportedMediaType(String userPattern, String systemPattern, Object... args) {
        return unsupportedMediaType(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError unsupportedType(String userPattern, String systemPattern, Object... args) {
        return unsupportedType(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError tooManyRequests(String userPattern, String systemPattern, Object... args) {
        return tooManyRequests(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError unavailableForLegalReasons(String userPattern, String systemPattern, Object... args) {
        return unavailableForLegalReasons(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError internalServerError(String userPattern, String systemPattern, Object... args) {
        return internalServerError(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError notImplemented(String userPattern, String systemPattern, Object... args) {
        return notImplemented(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError badGateway(String userPattern, String systemPattern, Object... args) {
        return badGateway(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError serviceUnavailable(String userPattern, String systemPattern, Object... args) {
        return serviceUnavailable(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError gatewayTimeout(String userPattern, String systemPattern, Object... args) {
        return gatewayTimeout(formatAll(userPattern, systemPattern, args));
    }

    public static DomainError networkAuthenticationRequired(String userPattern, String systemPattern, Object... args) {
        return networkAuthenticationRequired(formatAll(userPattern, systemPattern, args));
    }

}
