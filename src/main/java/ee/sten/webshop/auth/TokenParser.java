package ee.sten.webshop.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TokenParser extends BasicAuthenticationFilter {
    public TokenParser(AuthenticationManager authenticationManager) {

        super(authenticationManager);

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String headerToken = request.getHeader("Authorization");
        if(headerToken != null && headerToken.startsWith("Bearer ")) {
            String token = headerToken.replace("Bearer ", "");

            Claims claims = Jwts.parser()
                    .setSigningKey("super-secret-key")
                    .parseClaimsJws(token)
                    .getBody();

            String issuer = claims.getIssuer();
            System.out.println(issuer);

            String personCode = claims.getSubject();
            System.out.println(personCode);

            List<GrantedAuthority> authorities = null;
            if (claims.getId() != null && claims.getId().equals("admin")) {
                GrantedAuthority authority = new SimpleGrantedAuthority("admin");
                authorities = Collections.singletonList(authority);
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    personCode, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);


        }


        super.doFilterInternal(request, response, chain);
    }
}
