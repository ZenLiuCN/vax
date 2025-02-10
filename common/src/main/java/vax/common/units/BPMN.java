package vax.common.units;

/**
 * @author Zen.Liu
 * @since 2025-02-03
 */
public interface BPMN {
    interface Element extends BPMN {
        String identity();

        String name();
    }

    interface FlowObject extends Element {}

    interface Process extends Element {}

    interface Define extends Element {}

    interface Activity extends FlowObject {
        sealed interface Task extends Activity {}

        non-sealed interface User extends Task {}

        non-sealed interface Service extends Task {}

        non-sealed interface Send extends Task {}

        non-sealed interface Receive extends Task {}

        non-sealed interface Manual extends Task {}

        non-sealed interface Script extends Task {}
    }

    interface Event extends FlowObject {
        sealed interface Mark extends Event {
            non-sealed interface Start extends Mark {}

            non-sealed interface Intermediate extends Mark {}

            non-sealed interface End extends Mark {}

            non-sealed interface Catch extends Mark {}

            non-sealed interface Throw extends Mark {}

            sealed interface Interrupting extends Mark {
                non-sealed interface Subprocess extends Interrupting {}

                non-sealed interface Boundary extends Interrupting {}
            }

            sealed interface NoneInterrupting extends Mark {
                non-sealed interface Subprocess extends NoneInterrupting {}

                non-sealed interface Boundary extends NoneInterrupting {}
            }

        }

        interface Terminate extends Mark.End {}

        interface Signal extends Event {}

        interface Message extends Event {}

        interface Escalation extends Event {}

        interface Conditional extends Event {}

        interface Error extends Event {}

        interface Cancel extends Event {}

        interface ParallelMultiple extends Event {}

        interface Multiple extends Event {}


        interface Link extends Event {}

        interface Timer extends Event {
            interface TimeDuration extends Timer {}

            interface TimeCycle extends Timer {}

            interface TimeDate extends Timer {}
        }

    }

    sealed interface Gateway extends FlowObject {
        non-sealed interface Exclusive extends Gateway {}

        non-sealed interface Parallel extends Gateway {}

        non-sealed interface Inclusive extends Gateway {}

        non-sealed interface Complex extends Gateway {}

        non-sealed interface EventBased extends Gateway {}
    }

    sealed interface Flow extends FlowObject {
        String sourceRef();

        String targetRef();

        non-sealed interface Sequence extends Flow {}

        non-sealed interface Conditional extends Flow {}

        non-sealed interface Default extends Flow {}
    }


}
