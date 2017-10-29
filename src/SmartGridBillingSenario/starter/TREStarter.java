package SmartGridBillingSenario.starter;

import SmartGridBillingSenario.utils.PropertyReader;
import SmartGridBillingSenario.utils.Senario;
import SmartGridBillingSenario.TRE;

/**
 * Created by yuandai on 28/9/17.
 */
public class TREStarter {

    public static void main(String[] args) {

        Senario senario = Senario.currentSenario;

        String trePort = PropertyReader.getProperty("tre.port");

        TRE tre = new TRE(Integer.valueOf(trePort), senario);
    }
}
