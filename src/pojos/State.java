package pojos;

public enum State {
    /**
     * The condition didn't evolve
     */
    STABLE("The patient is stable and under control."),
    /**
     * The condition made good progress
     */
    GOOD("The patient is in good health."),
    /**
     * The condition worsened
     */
    BAD("The patient is in bad condition."),
    /**
     * The case is closed, due to the patient moving hospitals, dying or any other cause that would cause the patient to stop follwoing the conditions progress
     */
    CLOSED("The case is closed, no further action is needed.");

    /**
     * Small description of each condition state
     */
    private final String description;

    State(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Representation of the enumeration
     * @return String of the state and its description
     */
    @Override
    public String toString() {
        return name() + ": " + description;
    }
}
