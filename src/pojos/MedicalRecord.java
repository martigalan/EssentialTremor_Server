package pojos;

import data.ACC;
import data.EMG;

import java.util.ArrayList;
import java.util.List;

public class MedicalRecord {

    private int id;
    private String patientName;
    private String patientSurname;
    private int age;
    private double weight;
    private int height;
    private List<String> symptoms;
    private ACC acceleration;
    private EMG emg;
    private Boolean genetic_background;
    private List<DoctorsNote> doctorsNotes;
    private List<Doctor> doctors;
    private int patientId;
    private int doctorId;

    public MedicalRecord() {
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }
    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }
    public List<DoctorsNote> getDoctorsNotes() {
        return doctorsNotes;
    }
    public void setDoctorsNotes(List<DoctorsNote> doctorsNotes) {
        this.doctorsNotes = doctorsNotes;
    }

    public Boolean getGenetic_background() {
        return genetic_background;
    }

    public void setGenetic_background(Boolean genetic_background) {
        this.genetic_background = genetic_background;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public String getPatientSurname() {
        return patientSurname;
    }

    public void setPatientSurname(String patientSurname) {
        this.patientSurname = patientSurname;
    }

    public void setAcceleration(ACC acceleration) {
        this.acceleration = acceleration;
    }

    public void setEmg(EMG emg) {
        this.emg = emg;
    }

    public int getAge() {
        return age;
    }

    public double getWeight() {
        return weight;
    }

    public int getHeight() {
        return height;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public ACC getAcceleration() {
        return acceleration;
    }

    public EMG getEmg() {
        return emg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public MedicalRecord(int age, double weight, int height, List<String> symptoms) {
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.symptoms = symptoms;
        this.acceleration = new ACC();
        this.emg = new EMG();
        this.doctorsNotes = new ArrayList<>();
        this.doctors = new ArrayList<>();
    }

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
    }

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

    void showAcc() {
        //this.acceleration.plotSignal();
    }

    void showEMG(){
        //this.emg.plotSignal();
    }
}
