package pl.edu.pjwstk.jaz.zad2;


import org.springframework.http.HttpStatus;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationFilter extends HttpFilter {
    private final UserSession userSession;

    public AuthorizationFilter(UserSession userSession) {
        this.userSession = userSession;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (isUserLogged() && isSiteAllowed(request)) {
            super.doFilter(request, response, chain);
            if (!isSiteAllowed(request))
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }

    private boolean isSiteAllowed(HttpServletRequest request) {
        String url = request.getRequestURL().toString();

        if (url.substring(url.lastIndexOf("/")).equals("/forAuthorized") &&
                !userSession.getRole().isEmpty()) {
            return true;
        }
        if (url.substring(url.lastIndexOf("/")).equals("/userList") &&
                userSession.getRole().equals("admin")) {
            return true;
        }
        return false;
    }

    private boolean isUserLogged() {
        return userSession.isLoggedIn();
    }
}
