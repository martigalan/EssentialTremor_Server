package pojos;

public enum Treatment {
    SURGERY("The patient needs surgery."),
    PROPRANOLOL("The patient needs pharmacology treatment, exactly Propranolol."),
    PRIMIDONE("The patient needs pharmacology treatment, exactly Primidone.");

    private final String description;

    Treatment(String description) {
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
