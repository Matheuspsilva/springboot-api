package com.matheussilvadev.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.matheussilvadev.ApplicationContextLoad;
import com.matheussilvadev.model.Usuario;
import com.matheussilvadev.repository.UsuarioRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {

	// Tempo de expiração do token: 2 dias
	private static final long EXPIRATION_TIME = 172800000;

	// Senha única para compor autenticação e ajudar na segurança
	private static final String SECRET = "senhaExtremamenteSecreta";

	// Pŕefixo padrão de Token
	private static final String TOKEN_PREFIX = "Bearer";

	// Identificação do cabeçalho da resposta
	private static final String HEADER_STRING = "Authorization";

	// Gerando token de autenticação e adicionando ao cabeçalho e resposta Http
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {
		// Montagem do Token
		String JWT = Jwts.builder()// Chama o gerador de token
				.setSubject(username) // Adiciona o usuário
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))// Tempo de expiração
				.signWith(SignatureAlgorithm.HS512, SECRET).compact(); // Compactação e algorítimo de geração de senha

		// Junta o token com o prefixo
		String token = TOKEN_PREFIX + " " + JWT; // Bearer luJygj93kasasd ...

		// Adiciona no cabeçalho Http
		response.addHeader(HEADER_STRING, token); // Authorization: Bearer luJygj93kasasd ...

		// Escreve token como resposta no corpo http
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");

	}

	// Retorna o usuário validado com token ou, caso não seja válido retorna null
	public Authentication getAuthentication (HttpServletRequest request) {
		
		//Pega o token enviado no cabeçalho Http
		String token = request.getHeader(HEADER_STRING);
		
		if(token != null) {
			//Faz a validação do token do usuário na requisição
			String user = Jwts.parser().setSigningKey(SECRET) //Bearer luJygj93kasasd ...
					.parseClaimsJws(token.replace(TOKEN_PREFIX, ""))//luJygj93kasasd ...
					.getBody().getSubject(); //Usuário
			if(user != null) {
				Usuario usuario = ApplicationContextLoad.getApplicationContext()
						.getBean(UsuarioRepository.class).findUserByLogin(user);
				
				//Retorna o usuário logado
				if(usuario != null) {
					return new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities());
				}
			}
		}
		
		return null;//Não Autorizado
	}

}