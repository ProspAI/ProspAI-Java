package br.com.fiap.prospai.service;

import br.com.fiap.prospai.dto.request.UsuarioRequestDTO;
import br.com.fiap.prospai.dto.response.UsuarioResponseDTO;
import br.com.fiap.prospai.entity.Usuario;
import br.com.fiap.prospai.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
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

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // Configura uma instância de Usuario antes de cada teste
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuario 1");
        usuario.setEmail("usuario@exemplo.com");
    }

    @Test
    void testGetAllUsuarios() {
        // Cria uma segunda instância de Usuario para teste
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNome("Usuario 2");

        // Mock do retorno do usuarioRepository
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario, usuario2));

        // Executa o serviço
        List<UsuarioResponseDTO> usuarios = usuarioService.getAllUsuarios();

        // Verificações
        assertEquals(2, usuarios.size());
        assertEquals("Usuario 1", usuarios.get(0).getNome());
        assertEquals("Usuario 2", usuarios.get(1).getNome());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void testGetUsuarioById_ExistingId() {
        // Mock do retorno do usuarioRepository para um ID existente
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Executa o serviço
        Optional<UsuarioResponseDTO> result = usuarioService.getUsuarioById(1L);

        // Verificações
        assertTrue(result.isPresent());
        assertEquals("Usuario 1", result.get().getNome());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateUsuario() {
        // Cria o DTO de requisição para Usuario
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setNome("Novo Usuario");
        requestDTO.setEmail("usuario@exemplo.com");
        requestDTO.setSenha("senha123");

        // Mock do retorno do usuarioRepository para salvar o Usuario
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario savedUsuario = invocation.getArgument(0);
            savedUsuario.setId(1L); // Define um ID para o usuário salvo
            return savedUsuario;
        });

        // Executa o serviço
        UsuarioResponseDTO responseDTO = usuarioService.createUsuario(requestDTO);

        // Verificações
        assertNotNull(responseDTO);
        assertEquals("Novo Usuario", responseDTO.getNome());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testUpdateUsuario_ExistingId() {
        // Configuração inicial do Usuario existente para teste de atualização
        Usuario existingUsuario = new Usuario();
        existingUsuario.setId(1L);
        existingUsuario.setNome("Usuario Antigo");

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setNome("Usuario Atualizado");

        // Mock do retorno do usuarioRepository para encontrar e salvar o Usuario
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existingUsuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(existingUsuario);

        // Executa o serviço
        UsuarioResponseDTO responseDTO = usuarioService.updateUsuario(1L, requestDTO);

        // Verificações
        assertNotNull(responseDTO);
        assertEquals("Usuario Atualizado", responseDTO.getNome());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(existingUsuario);
    }

    @Test
    void testDeleteUsuario_ExistingId() {
        // Mock do retorno do usuarioRepository para encontrar e deletar o usuário
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioRepository).delete(usuario);

        // Executa o serviço
        usuarioService.deleteUsuario(1L);

        // Verificações
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).delete(usuario);
    }
}
