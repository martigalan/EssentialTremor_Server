package pojos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Patient {
    private String name;
    private String surname;
    private Boolean genetic_background;
    private User user;
    private List<MedicalRecord> medicalRecords;
    private List<Doctor> doctors;

    public Patient(String name, String surname, Boolean genBack) {
        this.name = name;
        this.surname = surname;
        this.genetic_background = genBack;
        this.medicalRecords = new ArrayList<MedicalRecord>();
        this.doctors = new ArrayList<Doctor>();
    }

    public void setMedicalRecords(List<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getGenetic_background() {
        return genetic_background;
    }

    public void setGenetic_background(Boolean genetic_background) {
        this.genetic_background = genetic_background;
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public void setMedicalRecord(List<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    @Override
    public String toString() {
        return "- Name: " + name + '\'' +
                "- Surname: " + surname + '\'';
                //"- State: " + state +
                //"- Treatment: " + treatment;
    }

    private void openRecord(){
        MedicalRecord record = askData();
        record.setPatientName(this.name);
        record.setPatientSurname(this.surname);
        record.setGenetic_background(this.genetic_background);
        //TODO acc and emg
        this.getMedicalRecords().add(record);
    }

    private MedicalRecord askData() {
        Scanner sc = new Scanner(System.in);
        System.out.println("- Age: ");
        int age = sc.nextInt();
        System.out.println("- Weight (kg): ");
        double weight = sc.nextDouble();
        System.out.println("- Height (cm): ");
        int height = sc.nextInt();
        System.out.println("- Symptoms (enter symptoms separated by commas): ");
        String symptomsInput = sc.nextLine();

        //Takes the symptoms input and creates a List
        List<String> symptoms = Arrays.asList(symptomsInput.split(","));
        symptoms = symptoms.stream().map(String::trim).collect(Collectors.toList()); // Trim extra spaces
        sc.close();
        return new MedicalRecord(age, weight, height, symptoms);
    }

    private void sendMedicalRecord(MedicalRecord medicalRecord){
        //TODO send info
    }
    private DoctorsNote receiveDoctorsNote(){
        //TODO receive info
        return null;
    }
    private void seeDoctorsNotes() {
        //TODO here the patient chooses what record they want to see
    }
}
