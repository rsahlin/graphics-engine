package com.graphicsengine.charset;

import com.graphicsengine.dataflow.ArrayInputData;
import com.nucleus.scene.NodeData;

/**
 * Data for a playfield, this is the chars displayed not the actual charset.
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldData extends NodeData {

    private ArrayInputData arrayInput;

    public ArrayInputData getArrayInput() {
        return arrayInput;
    }

}
