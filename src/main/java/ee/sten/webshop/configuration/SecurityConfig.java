package ee.sten.webshop.configuration;

import ee.sten.webshop.auth.TokenParser;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .cors().and().headers().xssProtection().disable().and()
                .csrf().disable()
                .addFilter(new TokenParser(authenticationManager()))
                .authorizeRequests()
                .antMatchers("/active-products").permitAll()
                .antMatchers("/get-product/**").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/signup").permitAll()
                .antMatchers("/parcel-machines/**").permitAll()
                .antMatchers(HttpMethod.GET, "/category").permitAll()
                .antMatchers(HttpMethod.GET, "/cart-products/**").permitAll()
                .antMatchers("/persons").hasAuthority("admin")
                .antMatchers("/add-stock").hasAuthority("admin")
                .antMatchers("/decrease-stock").hasAuthority("admin")
                .antMatchers("/delete-product/**").hasAuthority("admin")
                .antMatchers("/edit-products/**").hasAuthority("admin")
                .antMatchers(HttpMethod.POST, "/category").hasAuthority("admin")
                .antMatchers(HttpMethod.DELETE, "/category").hasAuthority("admin")
                .antMatchers(HttpMethod.POST, "/add-product").hasAuthority("admin")
                .antMatchers(HttpMethod.GET, "/check-if-admin").hasAuthority("admin")
                .anyRequest().authenticated()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
