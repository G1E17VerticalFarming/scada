/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scada.domain.interfaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import shared.GrowthProfile;

/**
 *
 * @author DanielToft
 */
public interface ReadWriteGrowthProfile {
    
    public abstract List<GrowthProfile> readGrowthProfileFile() throws IOException, ClassNotFoundException;
    
    public abstract void writeGrowthProfileFile(List<GrowthProfile> gpList) throws IOException;
    
}
