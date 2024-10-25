package pojos;

import jdbc.ConnectionManager;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Doctor {

    private User user;
    private String name;
    private String surname;
    private List<Patient> patients;
    private List<MedicalRecord> medicalRecords;
    private List<DoctorsNote> doctorsNotes;
    private ConnectionManager access;


    public Doctor(String name, String surname, List<Patient> patients) {
        this.name = name;
        this.surname = surname;
        this.patients = patients;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\''+
                '}';
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public void setMedicalRecords(List<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    public List<DoctorsNote> getDoctorsNote() {
        return doctorsNotes;
    }

    public void setDoctorsNote(List<DoctorsNote> doctorsNotes) {
        this.doctorsNotes = doctorsNotes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return Objects.equals(name, doctor.name) && Objects.equals(surname, doctor.surname) && Objects.equals(patients, doctor.patients) && Objects.equals(access, doctor.access);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, patients, access);
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public ConnectionManager getAccess() {
        return access;
    }

    private Patient choosePatient() {
        Scanner sc = new Scanner(System.in);
        List<Patient> listOfPatients = getPatients();
        for (int i = 0; i < listOfPatients.size(); i++) {
            System.out.println((i + 1) + ": " + listOfPatients.get(i).getName() + " " + listOfPatients.get(i).getSurname());
        }
        System.out.println("--- Please choose the patient by number: ");

        int number = sc.nextInt();
        sc.close();
        return listOfPatients.get(number - 1);
    }

    private MedicalRecord receiveMedicalRecord(){
        //TODO with sockets, ONLY receive the record and build a new one from the single parameters
        //MedicalRecord medicalRecord = new MedicalRecord();
        //this.getMedicalRecords().add(medicalRecord);
        return null;
    }
    private void showInfoMedicalRecord(MedicalRecord medicalRecord){
        //TODO show info, graphs and everything
        System.out.println(medicalRecord);
        medicalRecord.showAcc();
        medicalRecord.showEMG();
    }
    private DoctorsNote createDoctorsNote(MedicalRecord medicalRecord){
        //create a note for the medical record
        Scanner sc = new Scanner(System.in);
        System.out.println("\n Write any comments about the medical record (No enters): ");
        String comments = sc.nextLine();
        DoctorsNote doctorsNote = new DoctorsNote(comments);
        sc.close();
        medicalRecord.getDoctorsNotes().add(doctorsNote);
        this.getDoctorsNote().add(doctorsNote);
        return doctorsNote;
    }

    private void addPatient(){
        Scanner sc = new Scanner(System.in);
        System.out.println("- Name: ");
        String name = sc.nextLine();
        System.out.println("- Surname: ");
        String surname = sc.nextLine();
        System.out.println("- Genetic background: (y/n)");
        String genBackCheck = sc.nextLine();
        Boolean genBack = null;
        //check
        Boolean valid = false;
        while (!valid) {
            if (genBackCheck.equals("y")) {
                valid = true;
                genBack = true;
            } else if (genBackCheck.equals("n")) {
                valid = true;
                genBack = false;
            } else {
                System.out.println("---NOT A VALID INPUT, PLEASE TRY AGAIN...");
            }
        }
        Patient patient = new Patient(name,surname,genBack);
        patient.getDoctors().add(this);
        this.getPatients().add(patient);
        sc.close();
    }

    /*public static void main(String[] args) { //TODO delete when finished

        Patient p1 = new Patient("b", "b", 20);
        Patient p2 = new Patient("c", "c", 20);
        List<Patient> patients = new ArrayList<>();
        patients.add(p1);
        patients.add(p2);
        patients.get(1).setState(State.GOOD);
        System.out.println(patients.get(1).getState());
        Doctor doctor = new Doctor("a", "a", patients);

        Patient p = doctor.choosePatient();
        System.out.println(p);
        doctor.updatePatient(p);
        doctor.addPatient();
        Patient p3 = doctor.choosePatient();
        System.out.println(p3);


    }*/
}
