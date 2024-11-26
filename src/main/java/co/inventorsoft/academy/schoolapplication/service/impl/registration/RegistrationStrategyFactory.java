package co.inventorsoft.academy.schoolapplication.service.impl.registration;

import co.inventorsoft.academy.schoolapplication.entity.user.Role;
import co.inventorsoft.academy.schoolapplication.service.RegistrationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
public class RegistrationStrategyFactory {
    private final Map<Role, RegistrationStrategy> strategies;

    @Autowired
    public RegistrationStrategyFactory(Set<RegistrationStrategy> strategySet) {
        strategies = new HashMap<>();
        strategySet.forEach(
                strategy -> strategies.put(strategy.getSupportedRole(), strategy));
    }

    public RegistrationStrategy get(Role userRole) {
        RegistrationStrategy strategy = strategies.get(userRole);
        if (Objects.isNull(strategy)) {
            throw new IllegalArgumentException("No registration strategy found for role: " + userRole);
        }
        return strategy;
    }
}
