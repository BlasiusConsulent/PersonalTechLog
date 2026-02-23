import java.time.LocalDate;
import java.util.Objects;

/**
 * Intervento di natura hardware: sostituzione componenti, manutenzione fisica, ecc.
 * Estende Intervento aggiungendo il campo pezzoRicambio e la propria logica tariffaria.
 */
public class InterventoHardware extends Intervento {

    private static final long serialVersionUID = 2L;

    // Tariffa oraria fissa per interventi hardware (manodopera)
    private static final double TARIFFA_ORARIA_HW = 65.0;

    private String pezzoRicambio;

    public InterventoHardware(String id, String cliente, LocalDate data,
                              String descrizione, String pezzoRicambio) {
        super(id, cliente, data, descrizione);
        // Validazione: un ricambio senza nome è inutile a fini di log
        this.pezzoRicambio = Objects.requireNonNull(pezzoRicambio, "pezzoRicambio non può essere null");
    }

    public String getPezzoRicambio()                      { return pezzoRicambio; }
    public void   setPezzoRicambio(String pezzoRicambio)  { this.pezzoRicambio = Objects.requireNonNull(pezzoRicambio); }

    /**
     * Tariffa HW = tariffa oraria base + supplemento fisso per la trasferta.
     * Logica volutamente diversa da SW per dimostrare il polimorfismo.
     */
    @Override
    public double calcolaTariffa() {
        return TARIFFA_ORARIA_HW + 25.0; // 25€ di rimborso trasferta
    }

    @Override
    protected String getTipo() {
        return "HW";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Ricambio: %s | Tariffa: %.2f€",
                pezzoRicambio, calcolaTariffa());
    }
}
