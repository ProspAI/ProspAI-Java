package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.UsuarioRequestDTO;
import br.com.fiap.prospai.dto.response.UsuarioResponseDTO;
import br.com.fiap.prospai.entity.Usuario;
import br.com.fiap.prospai.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;

    // No need for setUp() method when using @ExtendWith(MockitoExtension.class)

    @Test
    public void getAllUsuarios_ShouldReturnListOfUsuarios() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Carlos");
        when(usuarioRepository.findAll()).thenReturn(Collections.singletonList(usuario));

        // Act
        List<UsuarioResponseDTO> usuarios = usuarioService.getAllUsuarios();

        // Assert
        assertNotNull(usuarios);
        assertEquals(1, usuarios.size());
        assertEquals("Carlos", usuarios.get(0).getNome());
    }

    @Test
    public void getUsuarioById_ShouldReturnUsuario_WhenExists() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Mariana");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        Optional<UsuarioResponseDTO> result = usuarioService.getUsuarioById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Mariana", result.get().getNome());
    }

    @Test
    public void getUsuarioById_ShouldReturnEmpty_WhenNotExists() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<UsuarioResponseDTO> result = usuarioService.getUsuarioById(1L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    public void createUsuario_ShouldEncodePasswordAndSaveUsuario() {
        // Arrange
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setNome("João");
        requestDTO.setEmail("joao@example.com");
        requestDTO.setSenha("senha123");

        when(passwordEncoder.encode("senha123")).thenReturn("senhaCodificada");

        Usuario savedUsuario = new Usuario();
        savedUsuario.setId(1L);
        savedUsuario.setNome("João");
        savedUsuario.setEmail("joao@example.com");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUsuario);

        // Act
        UsuarioResponseDTO responseDTO = usuarioService.createUsuario(requestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("João", responseDTO.getNome());
        verify(passwordEncoder, times(1)).encode("senha123");
    }

    @Test
    public void updateUsuario_ShouldUpdateExistingUsuario() {
        // Arrange
        Usuario existingUsuario = new Usuario();
        existingUsuario.setId(1L);
        existingUsuario.setNome("Paulo");
        existingUsuario.setSenha("senhaAntiga");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existingUsuario));

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setNome("Paulo Silva");
        requestDTO.setSenha("novaSenha");

        when(passwordEncoder.encode("novaSenha")).thenReturn("senhaCodificada");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(existingUsuario);

        // Act
        UsuarioResponseDTO responseDTO = usuarioService.updateUsuario(1L, requestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertEquals("Paulo Silva", responseDTO.getNome());
        verify(passwordEncoder, times(1)).encode("novaSenha");
        verify(usuarioRepository, times(1)).save(existingUsuario);
    }

    @Test
    public void updateUsuario_ShouldNotEncodePassword_WhenPasswordIsEmpty() {
        // Arrange
        Usuario existingUsuario = new Usuario();
        existingUsuario.setId(1L);
        existingUsuario.setNome("Paulo");
        existingUsuario.setSenha("senhaAntiga");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existingUsuario));

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setNome("Paulo Silva");
        requestDTO.setSenha("");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(existingUsuario);

        // Act
        UsuarioResponseDTO responseDTO = usuarioService.updateUsuario(1L, requestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertEquals("Paulo Silva", responseDTO.getNome());
        verify(passwordEncoder, times(0)).encode(anyString());
        assertEquals("senhaAntiga", existingUsuario.getSenha());
        verify(usuarioRepository, times(1)).save(existingUsuario);
    }

    @Test
    public void updateUsuario_ShouldThrowException_WhenUsuarioNotFound() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.updateUsuario(1L, requestDTO);
        });
        assertEquals("Usuario não encontrado com id: 1", exception.getMessage());
    }

    @Test
    public void deleteUsuario_ShouldDeleteExistingUsuario() {
        // Arrange
        Usuario existingUsuario = new Usuario();
        existingUsuario.setId(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existingUsuario));

        // Act
        usuarioService.deleteUsuario(1L);

        // Assert
        verify(usuarioRepository, times(1)).delete(existingUsuario);
    }

    @Test
    public void deleteUsuario_ShouldThrowException_WhenUsuarioNotFound() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.deleteUsuario(1L);
        });
        assertEquals("Usuario não encontrado com id: 1", exception.getMessage());
    }
}