// UsuarioService.java
package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.UsuarioRequestDTO;
import br.com.fiap.prospai.dto.response.UsuarioResponseDTO;
import br.com.fiap.prospai.entity.Usuario;
import br.com.fiap.prospai.repository.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injetando o PasswordEncoder

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Método para buscar todos os usuários
    public List<UsuarioResponseDTO> getAllUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Método para buscar um usuário pelo ID
    public Optional<UsuarioResponseDTO> getUsuarioById(Long id) {
        return usuarioRepository.findById(id)
                .map(this::toResponseDTO);
    }

    // Método para criar um novo usuário
    public UsuarioResponseDTO createUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuario = new Usuario();
        BeanUtils.copyProperties(usuarioRequestDTO, usuario);

        // Codifica a senha antes de salvar
        String senhaCodificada = passwordEncoder.encode(usuarioRequestDTO.getSenha());
        usuario.setSenha(senhaCodificada);

        // Define o papel padrão se não for especificado
        if (usuario.getPapel() == null || usuario.getPapel().isEmpty()) {
            usuario.setPapel("ROLE_USER");
        }

        usuario.setAtivo(true); // Define como ativo por padrão
        Usuario novoUsuario = usuarioRepository.save(usuario);
        return toResponseDTO(novoUsuario);
    }

    // Método para atualizar um usuário existente
    public UsuarioResponseDTO updateUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado com id: " + id));

        // Atualiza propriedades, exceto o ID e a senha
        BeanUtils.copyProperties(usuarioRequestDTO, usuario, "id", "senha");

        if (usuarioRequestDTO.getSenha() != null && !usuarioRequestDTO.getSenha().isEmpty()) {
            // Codifica a nova senha
            String senhaCodificada = passwordEncoder.encode(usuarioRequestDTO.getSenha());
            usuario.setSenha(senhaCodificada);
        }

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return toResponseDTO(usuarioAtualizado);
    }

    // Método para deletar um usuário pelo ID
    public void deleteUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado com id: " + id));
        usuarioRepository.delete(usuario);
    }

    // Método para converter uma entidade Usuario para UsuarioResponseDTO
    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO();
        BeanUtils.copyProperties(usuario, responseDTO);
        return responseDTO;
    }
}
