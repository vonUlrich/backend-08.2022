package ee.sten.webshop.auth;

import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
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

@Log4j2
public class TokenParser extends BasicAuthenticationFilter {
    public TokenParser(AuthenticationManager authenticationManager) {

        super(authenticationManager);

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String headerToken = request.getHeader("Authorization");
        if(headerToken != null && headerToken.startsWith("Bearer ")) {
            String token = headerToken.replace("Bearer ", "");

            try {
                Claims claims = Jwts.parser()
                        .setSigningKey("super-secret-key")
                        .parseClaimsJws(token)
                        .getBody();

                String issuer = claims.getIssuer();
                System.out.println(issuer);

                String personCode = claims.getSubject();
                //System.out.println(personCode);
                log.info("Successfully logged in {}", personCode);

                List<GrantedAuthority> authorities = null;
                if (claims.getId() != null && claims.getId().equals("admin")) {
                    GrantedAuthority authority = new SimpleGrantedAuthority("admin");
                    authorities = Collections.singletonList(authority);
                }

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        personCode, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (ExpiredJwtException e) {
                log.error("QUERY WITH EXPIRED TOKEN {}", token);
            } catch (UnsupportedJwtException e) {
                log.error("QUERY WITH UNSUPPORTED TOKEN {}", token);
            } catch (MalformedJwtException e) {
                log.error("QUERY WITH MALFORMED TOKEN {}", token);
            } catch (SignatureException e) {
                log.error("QUERY WITH FALSE SIGNATURE TOKEN {}", token);
            } catch (IllegalArgumentException e) {
                log.error("QUERY WITH ILLEGAL ARGUMENT TOKEN {}", token);
            }


        }


        super.doFilterInternal(request, response, chain);
    }
}
