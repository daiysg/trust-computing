
package tss;

import lombok.NoArgsConstructor;

import java.io.Serializable;

// import tss.tpm.*;
@NoArgsConstructor
public abstract class TpmStructure implements TpmMarshaller, Serializable{


    /**
     * Serialize this object to the structure printer
     *
     * @param _p The structure accumulator
     * @param d  The data to serialize
     */
    public abstract void toStringInternal(TpmStructurePrinter _p, int d);

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else if (obj == null)
            return false;
        else if (obj instanceof TpmMarshaller) {
            TpmMarshaller b = (TpmMarshaller) obj;
            byte[] thisObject = ((TpmMarshaller) this).toTpm();
            byte[] thatObject = b.toTpm();
            return Helpers.byteArraysEqual(thisObject, thatObject);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toTpm().hashCode();
    }
}
