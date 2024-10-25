package iFaces;
import Data.ACC;
import Data.EMG;

public interface SignalManager {
    public void saveEMGSignal(EMG emg);
    public void saveACCSignal(ACC acc);
    public EMG getEMGSignal(int id);
    public ACC getACCSignal(int id);
    public void updateEMGSignal(EMG emg);
    public void deleteEMGSignal(int id);
    public void deleteACCSignal(int id);
}

