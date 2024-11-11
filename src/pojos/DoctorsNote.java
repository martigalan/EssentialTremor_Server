package pojos;

public class DoctorsNote {

    private String doctorName;
    private String doctorSurname;
    private String notes;
    private int medicalRecordId;
    private int doctorId;
    /**
     * State assigned to the patient by the doctor
     */
    private State state;
    /**
     * Treatment the patient should undergo
     */
    private Treatment treatment;

    public DoctorsNote(){
    }
    /**
     * Constructor
     * @param notes annotations about a medical record
     * @param state state assigned to the patient
     * @param treatment treatment assigned to the patient
     */
    public DoctorsNote(String notes, State state, Treatment treatment) {
        this.notes = notes;
        this.state = state;
        this.treatment = treatment;
    }

    /**
     * Constructor
     * @param doctorName doctors name
     * @param doctorSurname doctors surname
     * @param notes annotations about a medical record
     * @param state state assigned to the patient
     * @param treatment treatment assigned to the patient
     */
    public DoctorsNote(String doctorName, String doctorSurname, String notes, State state, Treatment treatment) {
        this.doctorName = doctorName;
        this.doctorSurname = doctorSurname;
        this.notes = notes;
        this.state = state;
        this.treatment = treatment;
    }

    public DoctorsNote(String notes) {
        this.notes = notes;
    }

    public DoctorsNote(String doctorName, String doctorSurname, String notes) {
        this.doctorName = doctorName;
        this.doctorSurname = doctorSurname;
        this.notes = notes;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSurname() {
        return doctorSurname;
    }

    public void setDoctorSurname(String doctorSurname) {
        this.doctorSurname = doctorSurname;
    }

    public int getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(int medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }
}
