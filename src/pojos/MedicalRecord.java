package pojos;

import Data.ACC;
import Data.EMG;

public class MedicalRecord {

    private String patientName;
    private String patientSurname;
    private ACC acceleration;
    private EMG emg;

    public MedicalRecord(String patientName, String patientSurname, ACC acceleration, EMG emg) {
        this.patientName = patientName;
        this.patientSurname = patientSurname;
        this.acceleration = acceleration;
        this.emg = emg;
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

    public void graphicSignals () { //TODO

    }

    @Override
    public String toString() {
        return "medicalRecord{" +
                "patientName='" + patientName + '\'' +
                ", patientSurname='" + patientSurname + '\'' +
                ", acceleration=" + acceleration +
                ", emg=" + emg +
                '}';
    }
}

