package pojos;

import data.ACC;
import data.EMG;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MedicalRecord {

    /**
     * Name of the patient that creates the record
     */
    private String patientName;
    /**
     * Surname of the patient that creates the record
     */
    private String patientSurname;
    /**
     * Age of the patient that creates the record
     */
    private int age;
    /**
     * Weight of the patient that creates the record
     */
    private double weight;
    /**
     * Height of the patient that creates the record
     */
    private int height;
    /**
     * Symptoms of the patient that creates the record, in the moment of creation
     */
    private List<String> symptoms;
    /**
     * Acceleration data
     */
    private ACC acceleration;
    /**
     * EMG data
     */
    private EMG emg;
    /**
     * Boolean to identify if the patient has a genetic predisposition of essential tremor
     * TRUE if there is, FALSE if not
     */
    private Boolean genetic_background;
    /**
     * List of doctors notes associated to the medical record
     */
    private List<DoctorsNote> doctorsNotes;
    /**
     * List of doctors that receive this medical record
     */
    private List<Doctor> doctors;
    /**
     * ID of the patient its associated to
     */
    private int patientId;
    /**
     * ID of the doctor its associated to
     */
    private int doctorId;
    /**
     * ID of the medical record in the database
     */
    private int id;
    /**
     * Date of creation
     */
    private LocalDate date;

    /**
     * Empty constructor
     */
    public MedicalRecord() {
    }

    /**
     * Constructor
     * @param age patients age
     * @param weight patients weight
     * @param height patient height
     * @param symptoms patients symptoms
     */
    public MedicalRecord(int age, double weight, int height, List<String> symptoms) {
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.symptoms = symptoms;
        this.acceleration = new ACC();
        this.emg = new EMG();
        this.doctorsNotes = new ArrayList<>();
        this.doctors = new ArrayList<>();
        this.date = LocalDate.now();
    }
    /**
     * Constructor
     * @param age patients age
     * @param weight patients weight
     * @param height patient height
     * @param symptoms patients symptoms
     * @param date date of creation
     */
    public MedicalRecord(int age, double weight, int height, List<String> symptoms, LocalDate date) {
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.symptoms = symptoms;
        this.acceleration = new ACC();
        this.emg = new EMG();
        this.doctorsNotes = new ArrayList<>();
        this.doctors = new ArrayList<>();
        this.date = date;
    }

    /**
     * Constructor
     * @param patientName patients name
     * @param patientSurname patients surname
     * @param age patients age
     * @param weight patients weight
     * @param height patients height
     * @param symptoms patients symptoms
     * @param acceleration patients acceleration data
     * @param emg patients emg data
     * @param genetic_background patients genetic background info
     */
    public MedicalRecord(String patientName, String patientSurname, int age, double weight, int height, List<String> symptoms, ACC acceleration, EMG emg, Boolean genetic_background) {
        this.patientName = patientName;
        this.patientSurname = patientSurname;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.symptoms = symptoms;
        this.acceleration = acceleration;
        this.emg = emg;
        this.genetic_background = genetic_background;
        this.doctorsNotes = new ArrayList<>();
        this.doctors = new ArrayList<>();
        this.date = LocalDate.now();
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientSurname() {
        return patientSurname;
    }

    public void setPatientSurname(String patientSurname) {
        this.patientSurname = patientSurname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public ACC getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(ACC acceleration) {
        this.acceleration = acceleration;
    }

    public EMG getEmg() {
        return emg;
    }

    public void setEmg(EMG emg) {
        this.emg = emg;
    }

    public Boolean getGenetic_background() {
        return genetic_background;
    }

    public void setGenetic_background(Boolean genetic_background) {
        this.genetic_background = genetic_background;
    }

    public List<DoctorsNote> getDoctorsNotes() {
        return doctorsNotes;
    }

    public void setDoctorsNotes(List<DoctorsNote> doctorsNotes) {
        this.doctorsNotes = doctorsNotes;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Medical Record string representation
     * @return string representation of medical record
     */
    @Override
    public String toString() {
        return "MedicalRecord{" +
                "patientName='" + patientName + '\'' +
                ", surname= '"+ patientSurname + '\''+
                ", age=" + age +
                ", weight=" + weight +
                ", height=" + height +
                ", symptoms=" + symptoms +
                ", genetic_background=" + genetic_background +
                ", acc=" + acceleration +
                ", emg=" + emg +
                '}';
    }


    /**
     * Function that calls another one to represent the acceleration data
     */
    void showAcc() {
        this.acceleration.plotSignal();
    }

    /**
     * Function that calls another one to represent the emg data
     */
    void showEMG(){
        this.emg.plotSignal();
    }

    /**
     * Returns the symptoms as a concatenated string, separated by commas.
     *
     * @return A string with all symptoms concatenated and separated by commas.
     */
    public String getSymptomsAsString() {
        if (symptoms == null || symptoms.isEmpty()) {
            return "";
        }
        return String.join(", ", symptoms);
    }

    /**
     * Converts a string of concatenated symptoms back into a list of strings, so, when we retrieve this data from database, we can obtain again the list instead of a string.
     * The symptoms in the string should be separated by commas and optional spaces.
     *
     * @param symptomsString string containing symptoms separated by commas.
     * @return A list of strings, where each string is a symptom from the input string.
     */
    public List<String> parseSymptomsFromString(String symptomsString) {
        if (symptomsString == null || symptomsString.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(symptomsString.split(",\\s*"));
    }

    /**
     * Get the date and return it in the format "yyyy-MM-dd".
     *
     * @return the date in the format "yyyy-MM-dd" in a String.
     */
    public String getDateAsString() {
        return date.toString();
    }

    /**
     * Sets the date for the medical record from a string, in the format "yyyy-MM-dd".
     *
     * @param dateString The string representation of the date to set.
     * @throws IllegalArgumentException if the string cannot be parsed into a valid LocalDate.
     */
    public void setDateAsString(String dateString) {
        try {
            this.date = LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use 'yyyy-MM-dd'.", e);
        }
    }
}
