package ee.sten.webshop.auth;

import ee.sten.webshop.controller.model.TokenResponse;
import ee.sten.webshop.entity.Person;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenGenerator {

    public TokenResponse generateNewToken(Person person) {
       // String token = "sskljlks" + personCode;
        Date expirationDate = DateUtils.addHours(new Date(), 4);

       /* Map<String, Object> claims = new HashMap<>();
        claims.put("role", person.getRole());*/

        String token = Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, "super-secret-key")
                .setIssuer("webshop")
                .setExpiration(expirationDate)
                .setSubject(person.getPersonCode())
                .setId(person.getRole())
                .compact();


        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(token);
        return tokenResponse;
    }
}
