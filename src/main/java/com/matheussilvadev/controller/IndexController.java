package com.matheussilvadev.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.matheussilvadev.model.Usuario;
import com.matheussilvadev.model.UsuarioDTO;
import com.matheussilvadev.repository.UsuarioRepository;

@RestController
@RequestMapping(value = "/usuarios")
public class IndexController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@GetMapping(value = "/{id}/relatorio-pdf", produces = "application/json")
	public ResponseEntity<Usuario> relatorioUsuario(@PathVariable(value = "id") Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);

		
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<UsuarioDTO> usuario(@PathVariable(value = "id") Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);

		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}
	
	@GetMapping(value = "/", produces = "application/json")
	@CacheEvict(value = "cachelistarusuarios", allEntries = true)
	@CachePut("cachelistarusuarios")
	public ResponseEntity<List<Usuario>> listarUsuarios() throws InterruptedException {
		
		List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();

		return new ResponseEntity<List<Usuario>>(usuarios, HttpStatus.OK);
	}
	
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody Usuario usuario) throws Exception {
		
		for(int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		//Consumindo API Externa
		URL url = new URL("https://viacep.com.br/ws/" + usuario.getCep() +"/json/");
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		
		String cep = "";
		StringBuilder jsonCep = new StringBuilder();
		
		while((cep = br.readLine()) != null) {
			jsonCep.append(cep);
		}
		
		System.out.println(jsonCep.toString());
		
		Usuario usuarioAuxiliar = new Gson().fromJson(jsonCep.toString(), Usuario.class);
		
		usuario.setCep(usuarioAuxiliar.getCep());
		usuario.setLogradouro(usuarioAuxiliar.getLogradouro());
		usuario.setBairro(usuarioAuxiliar.getBairro());
		usuario.setLocalidade(usuarioAuxiliar.getLocalidade());
		usuario.setUf(usuarioAuxiliar.getUf());
		usuario.setComplemento(usuarioAuxiliar.getComplemento());
		
		//Fim do consumo
		
		
		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizarUsuario(@RequestBody Usuario usuario) {
		
		for(int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		Usuario usuarioTemporario = usuarioRepository.findUserByLogin(usuario.getLogin());
		
		if(!usuarioTemporario.getSenha().equals(usuario.getSenha())) {//Senhas diferentes
			
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
			
		}
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<String> deletarUsuario(@PathVariable(value = "id") Long id) {
		
		usuarioRepository.deleteById(id);

		return new ResponseEntity<String>("Usuário " + id + " deletado com sucesso", HttpStatus.OK);
	}

}
