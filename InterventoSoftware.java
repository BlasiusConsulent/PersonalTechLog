import java.time.LocalDate;
import java.util.Objects;

/**
 * Intervento software: installazioni, configurazioni, troubleshooting OS/applicativi.
 * La tariffa segue una logica diversa da HW perché non c'è trasferta fissa
 * ma si applica un moltiplicatore basato sul tipo di sistema operativo.
 */
public class InterventoSoftware extends Intervento {

    private static final long serialVersionUID = 3L;

    private static final double TARIFFA_BASE_SW = 55.0;

    private String sistemaOperativo;

    public InterventoSoftware(String id, String cliente, LocalDate data,
                              String descrizione, String sistemaOperativo) {
        super(id, cliente, data, descrizione);
        // FIX BUG #1: validazione esplicita — calcolaTariffa() chiama .toLowerCase()
        // su questo campo; senza controllo, un null qui causerebbe NPE a runtime.
        this.sistemaOperativo = Objects.requireNonNull(sistemaOperativo, "sistemaOperativo non può essere null");
    }

    public String getSistemaOperativo()                         { return sistemaOperativo; }
    public void   setSistemaOperativo(String sistemaOperativo)  { this.sistemaOperativo = Objects.requireNonNull(sistemaOperativo); }

    /**
     * Tariffa SW variabile: server OS (Windows Server, Linux Server) valgono di più
     * perché richiedono competenze specifiche. Approccio semplicistico ma realistico
     * per uno strumento personale.
     *
     * Nota: 55.0 * 1.4 in IEEE 754 può produrre 76.999...99 invece di 77.0 esatto,
     * ma il formato %.2f nel toString arrotonda correttamente — nessun impatto visibile.
     */
    @Override
    public double calcolaTariffa() {
        // sistemaOperativo è garantito non-null dal costruttore, toLowerCase() è safe.
        String soLower = sistemaOperativo.toLowerCase();
        if (soLower.contains("server")) {
            return TARIFFA_BASE_SW * 1.4; // +40% per ambienti server
        }
        return TARIFFA_BASE_SW;
    }

    @Override
    protected String getTipo() {
        return "SW";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | OS: %s | Tariffa: %.2f€",
                sistemaOperativo, calcolaTariffa());
    }
}
