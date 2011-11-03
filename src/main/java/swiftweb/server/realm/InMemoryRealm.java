package swiftweb.server.realm;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.security.UserRealm;

import java.security.Principal;

public class InMemoryRealm implements UserRealm {

    private final String role;
    private final String username;
    private final String password;

    public InMemoryRealm(String role, String username, String password) {
        this.role = role;
        this.username = username;
        this.password = password;
    }

    @Override
    public String getName() {
        return role;
    }

    @Override
    public Principal getPrincipal(String username) {
        return new PrincipalImpl(username, role);
    }

    @Override
    public Principal authenticate(String username, Object credentials, Request request) {
        if (this.username.equals(username) && this.password.equals(credentials)) {
            return new PrincipalImpl(username, role);
        }
        return null;
    }

    @Override
    public boolean reauthenticate(Principal user) {
        return true;
    }

    @Override
    public boolean isUserInRole(Principal user, String role) {
        PrincipalImpl principalImpl = (PrincipalImpl) user;
        return principalImpl.isInRole(role);
    }

    @Override
    public void disassociate(Principal user) {
    }

    @Override
    public Principal pushRole(Principal user, String role) {
        return new PrincipalImpl(user.getName(), role);
    }

    @Override
    public Principal popRole(Principal user) {
        return new PrincipalImpl(user.getName(), null);
    }

    @Override
    public void logout(Principal user) {
    }

    private class PrincipalImpl implements Principal {

        private String name;
        private String role;

        public PrincipalImpl(String name, String role) {
            this.name = name;
            this.role = role;
        }

        public boolean isInRole(String role) {
            return this.role != null && this.role.equals(role);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PrincipalImpl principal = (PrincipalImpl) o;

            if (name != null ? !name.equals(principal.name) : principal.name != null) return false;
            if (role != null ? !role.equals(principal.role) : principal.role != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (role != null ? role.hashCode() : 0);
            return result;
        }
    }
}
