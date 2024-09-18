// CustomUserDetailsService.java
package br.com.fiap.prospai.service;

import br.com.fiap.prospai.entity.Usuario;
import br.com.fiap.prospai.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        GrantedAuthority authority = new SimpleGrantedAuthority(usuario.getPapel());
        return new User(usuario.getEmail(), usuario.getSenha(), usuario.isAtivo(), true, true, true, Collections.singletonList(authority));
    }
}
