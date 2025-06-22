package GodCard;

/**
 * Turn Phase when God Card is available to be used.
 * e.g. If tagged with PowerPhase.MOVE, the god card can only be used during move phase.
 * Each god card can only be used in ONE phase not both.
 */
public enum PowerPhase {
    MOVE,
    BUILD
}
