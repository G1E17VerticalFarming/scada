package scada.domain.interfaces;

import shared.GrowthProfile;

import java.io.IOException;
import java.util.List;

/**
 * TODO WRITE ME
 */
public interface ReadWriteGrowthProfile {

    List<GrowthProfile> readGrowthProfileFile() throws IOException, ClassNotFoundException;

    void writeGrowthProfileFile(List<GrowthProfile> gpList) throws IOException;

}
