package com.fintech.deal.auditor;

import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import java.util.Optional;

/**
 * Provides the current auditor's username for auditing purposes.
 * This implementation returns a fixed username "username".
 * @author Matushkin Anton
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    /**
     * @return an {@link Optional} the current auditor's username.
     */
    @Override
    public @NonNull Optional<String> getCurrentAuditor() {
        return Optional.of("username");
    }

}
