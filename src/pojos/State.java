package pojos;

public enum State {
    STABLE("The patient is stable and under control."),
    GOOD("The patient is in good health."),
    BAD("The patient is in bad condition."),
    CLOSED("The case is closed, no further action is needed.");

    private final String description;

    State(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name() + ": " + description;
    }
}
