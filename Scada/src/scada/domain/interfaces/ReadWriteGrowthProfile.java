package scada.domain.interfaces;

import shared.GrowthProfile;

import java.io.IOException;
import java.util.List;

/**
 * Interface defining operations regarding Growth Profiles
 */
public interface ReadWriteGrowthProfile {

    /**
     * Method to read the Growth Profile json file
     * @return List of GrowthProfile objects found in the json file
     * @throws IOException If an error occurs during file write
     */
    public abstract List<GrowthProfile> readGrowthProfileFile() throws IOException;

    /**
     * Method to write a list of Growth Profile objects to the json encoded file
     * @param gpList List of GrowthProfile objects to json encode and write to file
     * @throws IOException If an error occurs during file write
     */
    public abstract void writeGrowthProfileFile(List<GrowthProfile> gpList) throws IOException;

}
