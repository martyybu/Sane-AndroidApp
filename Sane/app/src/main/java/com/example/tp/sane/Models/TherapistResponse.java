package com.example.tp.sane.Models;

import java.util.List;

public class TherapistResponse {

    private boolean error;

    private List<Therapist> therapists;

    public TherapistResponse(boolean error, List<Therapist> therapists) {
        this.error = error;
        this.therapists = therapists;
    }

    public boolean isErr() {
        return error;
    }

    public List<Therapist> getTherapists() {
        return therapists;
    }
}
