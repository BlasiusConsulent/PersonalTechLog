/**
 * Eccezione checked personalizzata per gestire ricerche di ID inesistenti.
 *
 * Uso una checked exception (estende Exception, non RuntimeException) perché
 * il chiamante DEVE gestire esplicitamente questo caso: non è un bug del programma
 * ma un input errato dell'utente, quindi voglio forzare la gestione consapevole.
 */
public class InterventoNonTrovatoException extends Exception {

    // FIX WARNING #2: Exception extends Throwable che è Serializable.
    // Senza serialVersionUID il compilatore emette warning -Xlint:serial.
    // Dichiarandolo esplicitamente evitiamo la classe "anonima" generata implicitamente.
    private static final long serialVersionUID = 1L;

    private final String idRicercato;

    public InterventoNonTrovatoException(String id) {
        super("Nessun intervento trovato con ID: " + id);
        this.idRicercato = id;
    }

    /** Comodo se il chiamante vuole loggare o mostrare l'ID incriminato. */
    public String getIdRicercato() {
        return idRicercato;
    }
}
