package com.lifementor.dto.response;

public class AuthResponse {
    private String token;
    private String message;
    private UserResponse user;
    private String tokenType;

    // Constructors
    public AuthResponse() {}

    public AuthResponse(String token, String message, UserResponse user, String tokenType) {
        this.token = token;
        this.message = message;
        this.user = user;
        this.tokenType = tokenType;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;
        private String message;
        private UserResponse user;
        private String tokenType;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder user(UserResponse user) {
            this.user = user;
            return this;
        }

        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(token, message, user, tokenType);
        }
    }

    // UserResponse inner class
    public static class UserResponse {
        private String id;
        private String name;
        private String email;
        private boolean emailVerified;

        // Constructors
        public UserResponse() {}

        public UserResponse(String id, String name, String email, boolean emailVerified) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.emailVerified = emailVerified;
        }

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public boolean isEmailVerified() {
            return emailVerified;
        }

        public void setEmailVerified(boolean emailVerified) {
            this.emailVerified = emailVerified;
        }

        // Builder
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String id;
            private String name;
            private String email;
            private boolean emailVerified;

            public Builder id(String id) {
                this.id = id;
                return this;
            }

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Builder email(String email) {
                this.email = email;
                return this;
            }

            public Builder emailVerified(boolean emailVerified) {
                this.emailVerified = emailVerified;
                return this;
            }

            public UserResponse build() {
                return new UserResponse(id, name, email, emailVerified);
            }
        }
    }
}