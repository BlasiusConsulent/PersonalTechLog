import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Classe base astratta che rappresenta un generico intervento tecnico.
 * Implementa Serializable per permettere la persistenza tramite ObjectOutputStream.
 *
 * Ho scelto una classe astratta (invece di un'interfaccia) perché gli interventi
 * condividono stato reale (campi comuni), non solo un contratto comportamentale.
 */
public abstract class Intervento implements Serializable {

    // serialVersionUID esplicito: buona pratica per evitare problemi di
    // compatibilità tra versioni diverse della classe durante la deserializzazione.
    private static final long serialVersionUID = 1L;

    private String id;
    private String cliente;
    private LocalDate data;
    private String descrizione;

    public Intervento(String id, String cliente, LocalDate data, String descrizione) {
        // Validazione in ingresso: meglio fallire subito con un messaggio chiaro
        // che scoprire un NPE a runtime due metodi dopo.
        this.id          = Objects.requireNonNull(id,          "id non può essere null");
        this.cliente     = Objects.requireNonNull(cliente,     "cliente non può essere null");
        this.data        = Objects.requireNonNull(data,        "data non può essere null");
        this.descrizione = Objects.requireNonNull(descrizione, "descrizione non può essere null");
    }

    // --- Getters & Setters ---
    // Accesso controllato ai campi: nessun consumer esterno tocca i dati raw.

    public String getId()               { return id; }
    public void   setId(String id)      { this.id = Objects.requireNonNull(id); }

    public String getCliente()                  { return cliente; }
    public void   setCliente(String cliente)    { this.cliente = Objects.requireNonNull(cliente); }

    public LocalDate getData()               { return data; }
    public void      setData(LocalDate data) { this.data = Objects.requireNonNull(data); }

    public String getDescrizione()                    { return descrizione; }
    public void   setDescrizione(String descrizione)  { this.descrizione = Objects.requireNonNull(descrizione); }

    /**
     * Polimorfismo in azione: ogni sottoclasse decide come calcolare la propria tariffa.
     * Il chiamante non sa (e non deve sapere) se sta gestendo HW o SW.
     */
    public abstract double calcolaTariffa();

    /**
     * equals() basato sull'ID: due interventi con lo stesso ID sono lo stesso intervento,
     * indipendentemente da dove vivono in memoria. Fondamentale per ArrayList.remove(Object)
     * che usa equals() — senza questo override, rimuovere un oggetto ricaricato dal file
     * (reference diversa, stesso ID) fallirebbe silenziosamente.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Intervento)) return false;
        Intervento that = (Intervento) o;
        return id.equalsIgnoreCase(that.id);
    }

    /** hashCode coerente con equals: sempre da implementare insieme. */
    @Override
    public int hashCode() {
        return Objects.hash(id.toUpperCase());
    }

    /**
     * Rappresentazione testuale di base, le sottoclassi la estendono con @Override
     * aggiungendo i propri campi specifici.
     */
    @Override
    public String toString() {
        return String.format("[%s] %s | Cliente: %-20s | Data: %s | %s",
                getTipo(), id, cliente, data, descrizione);
    }

    /** Etichetta di tipo usata nel toString, overridata dalle sottoclassi. */
    protected abstract String getTipo();
}
