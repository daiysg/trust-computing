package SmartGridBillingSenario;

import tss.*;
import tss.tpm.*;

/**
 * Created by ydai on 24/9/17.
 */
public class TRE {


    private Tpm tpm;

    private CreatePrimaryResponse ek;

    private Tss.ActivationCredential aik;

    public TRE() {
        tpm = TpmFactory.localTpmSimulator();
    }

    /**
     * TRE create AIK by its EK (see tutorial)
     */
    public void start() {
        initTpm();
        ek = createEK();
        aik = createAik(ek);
        TPM2B_PUBLIC_KEY_RSA ekPubKey = (TPM2B_PUBLIC_KEY_RSA) (ek.outPublic.unique);

    }

    private void initTpm() {
        GetCapabilityResponse caps = tpm.GetCapability(TPM_CAP.HANDLES, TPM_HT.TRANSIENT.toInt() << 24, 8);
        TPML_HANDLE handles = (TPML_HANDLE) caps.capabilityData;

        if (handles.handle.length == 0)
            System.out.println("No dangling handles");
        else for (TPM_HANDLE h : handles.handle)
            System.out.printf("Dangling handle 0x%08X\n", h.handle);
    }


    private CreatePrimaryResponse createEK() {
        // This policy is a "standard" policy that is used with vendor-provided
        // EKs
        byte[] standardEKPolicy = new byte[]{(byte) 0x83, 0x71, (byte) 0x97, 0x67, 0x44, (byte) 0x84, (byte) 0xb3,
                (byte) 0xf8, 0x1a, (byte) 0x90, (byte) 0xcc, (byte) 0x8d, 0x46, (byte) 0xa5, (byte) 0xd7, 0x24,
                (byte) 0xfd, 0x52, (byte) 0xd7, 0x6e, 0x06, 0x52, 0x0b, 0x64, (byte) 0xf2, (byte) 0xa1, (byte) 0xda,
                0x1b, 0x33, 0x14, 0x69, (byte) 0xaa};

        // Note: this sample allows userWithAuth - a "standard" EK does not (see
        // the other EK sample)
        TPMT_PUBLIC rsaEkTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA256,
                new TPMA_OBJECT(TPMA_OBJECT.fixedTPM, TPMA_OBJECT.fixedParent, TPMA_OBJECT.sensitiveDataOrigin,
                        TPMA_OBJECT.userWithAuth,
                        /* TPMA_OBJECT.adminWithPolicy, */ TPMA_OBJECT.restricted, TPMA_OBJECT.decrypt),
                standardEKPolicy,
                new TPMS_RSA_PARMS(new TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES, 128, TPM_ALG_ID.CFB),
                        new TPMS_NULL_ASYM_SCHEME(), 2048, 0),
                new TPM2B_PUBLIC_KEY_RSA());

        CreatePrimaryResponse rsaEk = tpm.CreatePrimary(TPM_HANDLE.from(TPM_RH.OWNER),
                new TPMS_SENSITIVE_CREATE(), rsaEkTemplate, new byte[0], new TPMS_PCR_SELECTION[0]);

        return rsaEk;
    }

    private Tss.ActivationCredential createAik(CreatePrimaryResponse rsaEk) {
        byte[] activationData = Helpers.getRandom(16);
        return Tss.createActivationCredential(rsaEk.outPublic,
                rsaEk.name, activationData);
    }
}