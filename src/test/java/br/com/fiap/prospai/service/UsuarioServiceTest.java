package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.UsuarioRequestDTO;
import br.com.fiap.prospai.dto.response.UsuarioResponseDTO;
import br.com.fiap.prospai.entity.Usuario;
import br.com.fiap.prospai.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void testGetAllUsuarios() {
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setNome("Usuario 1");

        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNome("Usuario 2");

        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario1, usuario2));

        List<UsuarioResponseDTO> usuarios = usuarioService.getAllUsuarios();

        assertEquals(2, usuarios.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void testGetUsuarioById_ExistingId() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuario 1");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<UsuarioResponseDTO> result = usuarioService.getUsuarioById(1L);

        assertTrue(result.isPresent());
        assertEquals("Usuario 1", result.get().getNome());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateUsuario() {
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setNome("Novo Usuario");
        requestDTO.setEmail("usuario@exemplo.com");
        requestDTO.setSenha("senha123");

        Usuario savedUsuario = new Usuario();
        BeanUtils.copyProperties(requestDTO, savedUsuario);
        savedUsuario.setId(1L);

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUsuario);

        UsuarioResponseDTO responseDTO = usuarioService.createUsuario(requestDTO);

        assertNotNull(responseDTO);
        assertEquals("Novo Usuario", responseDTO.getNome());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testUpdateUsuario_ExistingId() {
        Usuario existingUsuario = new Usuario();
        existingUsuario.setId(1L);
        existingUsuario.setNome("Usuario Antigo");

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setNome("Usuario Atualizado");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existingUsuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(existingUsuario);

        UsuarioResponseDTO responseDTO = usuarioService.updateUsuario(1L, requestDTO);

        assertNotNull(responseDTO);
        assertEquals("Usuario Atualizado", responseDTO.getNome());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(existingUsuario);
    }

    @Test
    void testDeleteUsuario_ExistingId() {
        Usuario existingUsuario = new Usuario();
        existingUsuario.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existingUsuario));
        doNothing().when(usuarioRepository).delete(existingUsuario);

        usuarioService.deleteUsuario(1L);

        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).delete(existingUsuario);
    }
}
