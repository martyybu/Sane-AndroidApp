package com.example.tp.sane.Models;

import java.util.List;

public class TherapistGetResponse {

        private boolean error;

        private Therapist therapist;

        public TherapistGetResponse(boolean error, Therapist therapist) {
            this.error = error;
            this.therapist = therapist;
        }

        public boolean isErr() {
            return error;
        }

        public Therapist getTherapist() {
            return therapist;
        }

}
