package it.polito.ai.pedibusproject.database.model;

public enum AvailabilityState {
    Available, //Escort è disponibile a coprire quella corsa
    Checked, //Admin ha confermato, e attende che Escort riconfermi la sua disponibilità
    ReadChecked,// Escort conferma la diponibilità.
    Confirmed  // Admin chiude la disponibilità, essa non cambierà più
}

/*

Available->Checked
Checked->ReadChecked
ReadChecked->Confirmed

Confirmed->Available

 */