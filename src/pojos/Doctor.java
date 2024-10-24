package pojos;

import jdbc.ConnectionManager;

import java.util.Objects;

public class Doctor {

    private String name;
    private String surname;
    private List<Patient> patients;

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
        List<Patient> listOfPatients = getPatients();
        for (int i = 0; i < listOfPatients.size(); i++) {
            System.out.println((i + 1) + ": " + listOfPatients.get(i).getName() + " " + listOfPatients.get(i).getSurname());
        }
        System.out.println("--- Please choose the patient by number: ");

        int number = UserInput.getIntWithValidation(1, listOfPatients.size()); //TODO change UserInput
        return listOfPatients.get(number - 1);
    }

    private void updatePatient(Patient patient) {
        System.out.println("Patient information: " + patient);
        switchState(patient);
        switchTreatment(patient);
        System.out.println("Patient information: " + patient);
    }

    private void switchState(Patient patient) {
        Boolean valid = false;
        System.out.println("Current state: " + patient.getState());
        System.out.println("Do you wish to change the state?: (y/n)");

        while (!valid) {
            String option = UserInput.getString(""); //TODO change UserInput

            if (option.equals("y")) {
                valid = true;
                System.out.println("1: " + State.GOOD);
                System.out.println("2: " + State.BAD);
                System.out.println("3: " + State.STABLE);
                System.out.println("4: " + State.CLOSED);
                System.out.println("Choose an option for state: ");

                int stateOption = UserInput.getIntWithValidation(1, 4); //TODO change UserInput

                switch (stateOption) {
                    case 1: patient.setState(State.GOOD); break;
                    case 2: patient.setState(State.BAD); break;
                    case 3: patient.setState(State.STABLE); break;
                    case 4: patient.setState(State.CLOSED); break;
                }

            } else if (option.equals("n")) {
                valid = true;
                System.out.println("---NO CHANGES WERE MADE");
            } else {
                System.out.println("---NOT A VALID INPUT, PLEASE TRY AGAIN...");
            }
        }
    }

    private void switchTreatment(Patient patient) {
        Boolean valid = false;
        System.out.println("Current treatment: " + patient.getTreatment());
        System.out.println("Do you wish to change the treatment?: (y/n)");

        while (!valid) {
            String option = UserInput.getString(""); //TODO change UserInput

            if (option.equals("y")) {
                valid = true;
                System.out.println("1: " + Treatment.PRIMIDONE);
                System.out.println("2: " + Treatment.PROPRANOLOL);
                System.out.println("3: " + Treatment.SURGERY);
                System.out.println("Choose an option for treatment: ");

                int treatmentOption = UserInput.getIntWithValidation(1, 3); //TODO change UserInput

                switch (treatmentOption) {
                    case 1: patient.setTreatment(Treatment.PRIMIDONE); break;
                    case 2: patient.setTreatment(Treatment.PROPRANOLOL); break;
                    case 3: patient.setTreatment(Treatment.SURGERY); break;
                }

            } else if (option.equals("n")) {
                valid = true;
                System.out.println("---NO CHANGES WERE MADE");
            } else {
                System.out.println("---NOT A VALID INPUT, PLEASE TRY AGAIN...");
            }
        }
    }


    private void addPatient(){
        System.out.println("- Name: ");
        String name = UserInput.getString(""); //TODO change UserInput
        System.out.println("- Surname: ");
        String surname = UserInput.getString(""); //TODO change UserInput
        System.out.println("- Genetic background: (y/n)");
        String genBackCheck = UserInput.getString(""); //TODO change UserInput
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
        System.out.println("- Age: ");
        int age = UserInput.getInt(""); //TODO change UserInput
        Patient patient = new Patient(name,surname,genBack,age);
        patient.setDoctor(this);
        this.getPatients().add(patient);
        inputState(patient);
        inputTreatment(patient);
    }

    private void inputState(Patient patient) {
        System.out.println("1: " + State.GOOD);
        System.out.println("2: " + State.BAD);
        System.out.println("3: " + State.STABLE);
        System.out.println("4: " + State.CLOSED);
        int stateOption = UserInput.getIntWithValidation(1, 4); //TODO change UserInput
        switch (stateOption) {
            case 1: patient.setState(State.GOOD); break;
            case 2: patient.setState(State.BAD); break;
            case 3: patient.setState(State.STABLE); break;
            case 4: patient.setState(State.CLOSED); break;
        }
    }

    private void inputTreatment(Patient patient) {
        System.out.println("1: " + Treatment.PRIMIDONE);
        System.out.println("2: " + Treatment.PROPRANOLOL);
        System.out.println("3: " + Treatment.SURGERY);
        int treatmentOption = UserInput.getIntWithValidation(1, 3); //TODO change UserInput
        switch (treatmentOption) {
            case 1: patient.setTreatment(Treatment.PRIMIDONE); break;
            case 2: patient.setTreatment(Treatment.PROPRANOLOL); break;
            case 3: patient.setTreatment(Treatment.SURGERY); break;
        }
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
