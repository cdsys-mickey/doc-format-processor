package kcworks.docs.docfomat;

import kcworks.spring.jwt.IJwtTokenProvider;
import kcworks.spring.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class DocFormatProcessorWebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	protected IJwtTokenProvider jwtTokenProvider;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// super.configure(http);
		http.csrf().disable();
		http.httpBasic().disable();
		http.logout().disable();
		http.formLogin().disable();
		http.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.authorizeRequests()
				.antMatchers(
						"/error"
						, "/api/v1/d4j/**"
						, "/api/v1/jod/**"
						, "/api/v1/tika/**"
						// , "/actuator/**"
						// , "/apix/**"
				).permitAll()
				.antMatchers(
						"/actuator/**"
						, "/apix/**"
						)
				// .hasIpAddress("127.0.0.1")
				.access("hasIpAddress('127.0.0.1') or hasIpAddress('::1')")
				.anyRequest().authenticated();

		//在驗證之前介入處理
		http.addFilterAfter(new JwtAuthenticationFilter(jwtTokenProvider, null), UsernamePasswordAuthenticationFilter.class);
	}
}
