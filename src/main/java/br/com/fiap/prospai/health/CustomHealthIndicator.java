package br.com.fiap.prospai.health;

import org.springframework.boot.actuate.health.*;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Lógica personalizada para verificar a saúde da aplicação
        boolean servicoExternoOk = verificarServicoExterno();

        if (servicoExternoOk) {
            return Health.up()
                    .withDetail("servicoExterno", "Operacional")
                    .build();
        } else {
            return Health.down()
                    .withDetail("servicoExterno", "Fora do ar")
                    .build();
        }
    }

    private boolean verificarServicoExterno() {
        // Implementação da verificação de saúde
        // Aqui você pode verificar a disponibilidade de um serviço externo, banco de dados, etc.
        return true; // Retorne true se estiver tudo ok, ou false se houver problemas
    }
}
