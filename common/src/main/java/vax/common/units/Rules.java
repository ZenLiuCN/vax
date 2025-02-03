package vax.common.units;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.*;

/**
 * @author Zen.Liu
 * @since 2025-02-03
 */
public interface Rules extends Function<JsonObject, Future<JsonObject>> {
    String name();

    Future<JsonObject> apply(JsonObject fact);

    interface Action extends Function<JsonObject, Future<JsonObject>> {
        static Action consume(Consumer<JsonObject> action) {
            return j -> Future.future(p -> {
                action.accept(j);
                p.complete(j);
            });
        }

        static Action apply(Function<JsonObject, JsonObject> action) {
            return j -> Future.future(p -> p.complete(action.apply(j)));
        }


        static Action merge(Function<JsonObject, JsonObject> action, int depth) {
            return j -> Future.future(p -> p.complete(j.mergeIn(action.apply(j.copy()), depth)));
        }

        static Action merge(Supplier<JsonObject> action, boolean deep) {
            return j -> Future.future(p -> p.complete(j.mergeIn(action.get(), deep)));
        }

        static Action merge(Supplier<JsonObject> action, int depth) {
            return j -> Future.future(p -> p.complete(j.mergeIn(action.get(), depth)));
        }
    }

    interface Rule extends Action {
        String name();

        final class RuleFailure extends RuntimeException {
            public final String name;

            RuleFailure(String name, Throwable cause) {
                super(name + " failed", cause);
                this.name = name;
            }
        }

        static Function<AsyncResult<JsonObject>, Future<JsonObject>> translate(String name) {
            return r -> r.succeeded() ? Future.succeededFuture(r.result()) :
                    Future.failedFuture(new RuleFailure(name, r.cause()));
        }

        record Activity(
                String name,
                Function<JsonObject, Future<JsonObject>> act
        ) implements Rule {
            @Override
            public Future<JsonObject> apply(JsonObject entries) {
                return act.apply(entries).transform(Rule.translate(name));
            }
        }

        record Either(String name, Condition cond, Action successor, Action failure) implements Rule {

            @Override
            public Future<JsonObject> apply(JsonObject entries) {
                return cond.apply(entries.copy())
                           .flatMap(b -> b
                                   ? successor.apply(entries)
                                   : failure.apply(entries))
                           .transform(Rule.translate(name));
            }
        }

        record Choice(String name, Choose cond, Action def, List<Action> branches) implements Rule {
            public Choice {
                assert def != null && branches != null : "branches and defaults are required";
            }

            @Override
            public Future<JsonObject> apply(JsonObject entries) {
                return cond.apply(entries.copy())
                           .flatMap(b -> b < 0 || b > branches.size()
                                   ? def.apply(entries)
                                   : branches.get(b).apply(entries))
                           .transform(Rule.translate(name));
            }
        }
    }

    interface Choose extends Function<JsonObject, Future<Integer>> {
        static Choose of(ToIntFunction<JsonObject> pred) {
            return s -> Future.succeededFuture(pred.applyAsInt(s));
        }
    }

    interface Condition extends Function<JsonObject, Future<Boolean>> {
        static Condition of(Predicate<JsonObject> pred) {
            return s -> Future.succeededFuture(pred.test(s));
        }
    }


    interface Builder {
        default Builder when(String name, Condition choice, Action trueAction, Action falseAction) {
            return compose(new Rule.Either(name, choice, trueAction, falseAction));
        }

        default Builder any(String name, Choose choice, Action defaults, Action... actions) {
            return compose(new Rule.Choice(name, choice, defaults, Arrays.asList(actions)));
        }

        default Builder then(String name, Action action) {
            return compose(new Rule.Activity(name, action));
        }


        Builder compose(Rule rule);

        Rules build();

        record builder(String name, List<Rule> rules) implements Builder {

            @Override
            public Builder compose(Rule rule) {
                rules.add(rule);
                return this;
            }

            @Override
            public Rules build() {
                return new Executor(name, new LinkedList<>(rules));
            }
        }
    }

    static Builder build(String name) {
        return new Builder.builder(name, new ArrayList<>());
    }

    record Executor(String name, List<Rule> rules) implements Rules {
        public Executor {
            assert name != null && rules != null && !rules.isEmpty() : "name required, rules required";
        }

        @Override
        public Future<JsonObject> apply(JsonObject fact) {
            var it = rules.iterator();
            Future<JsonObject> r = Future.succeededFuture(fact);
            while (it.hasNext()) {
                var a = it.next();
                r = r.flatMap(a);
            }
            return r;
        }
    }

}
