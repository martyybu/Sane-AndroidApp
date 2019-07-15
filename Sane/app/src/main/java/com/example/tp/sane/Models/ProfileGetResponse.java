package com.example.tp.sane.Models;

public class ProfileGetResponse {

        private boolean error;

        private Profile profile;

        public ProfileGetResponse(boolean error, Profile profile) {
            this.error = error;
            this.profile = profile;
        }

        public boolean isErr() {
            return error;
        }

    public Profile getProfile() {
        return profile;
    }
}
