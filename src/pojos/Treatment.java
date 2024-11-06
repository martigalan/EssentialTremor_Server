package pojos;

public enum Treatment {
    /**
     * The patient needs surgery
     */
    SURGERY("The patient needs surgery."),
    /**
     * The patient needs to take propranolol
     */
    PROPRANOLOL("The patient needs pharmacology treatment, exactly Propranolol."),
    /**
     * The patient needs primidone
     */
    PRIMIDONE("The patient needs pharmacology treatment, exactly Primidone.");

    /**
     * Small description of each treatment
     */
    private final String description;

    Treatment(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Representation of the enumeration
     * @return String of the treatment and its description
     */
    @Override
    public String toString() {
        return name() + ": " + description;
    }
}
