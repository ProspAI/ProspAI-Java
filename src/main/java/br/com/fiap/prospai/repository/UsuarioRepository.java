package br.com.fiap.prospai.repository;

import br.com.fiap.prospai.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca um usuário pelo e-mail
    Optional<Usuario> findByEmail(String email);

    // Busca usuários por papel (e.g., "Administrador", "Analista")
    List<Usuario> findByPapel(String papel);

    // Busca usuários ativos
    List<Usuario> findByAtivoTrue();

    // Busca usuários inativos
    List<Usuario> findByAtivoFalse();
}
