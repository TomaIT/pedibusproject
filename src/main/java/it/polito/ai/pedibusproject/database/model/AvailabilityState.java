package it.polito.ai.pedibusproject.database.model;

public enum AvailabilityState {
    Available, //Escort è disponibile a coprire quella corsa
    Checked, //Admin ha confermato, e attende che Escort riconfermi la sua disponibilità
    Confirmed // Escort conferma infine la diponibilità, essa non cambierà più.
}
