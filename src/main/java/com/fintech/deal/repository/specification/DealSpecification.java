package com.fintech.deal.repository.specification;

import com.fintech.deal.model.Deal;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.model.DealContractorRole;
import com.fintech.deal.payload.SearchDealPayload;
import com.fintech.deal.util.WildcatEnhancer;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DealSpecification {

    private DealSpecification() {}

    public static Specification<Deal> searchDeals(SearchDealPayload payload) {
        return (root, query, criteriaBuilder) -> {
            Stream<Predicate> predicateStream = Stream.of(
                    criteriaBuilder.isTrue(root.get("isActive")),
                    createEqualPredicate(criteriaBuilder, root, "id", payload.id()),
                    createEqualPredicate(criteriaBuilder, root, "description", payload.description()),
                    createLikePredicate(criteriaBuilder, root, "agreementNumber", payload.agreementNumber()),
                    createDateBetweenPredicate(criteriaBuilder, root, "agreementDate",
                            payload.agreementDateFrom(), payload.agreementDateTo()),
                    createDateBetweenPredicate(criteriaBuilder, root, "availabilityDate",
                            payload.availabilityDateFrom(), payload.availabilityDateTo()),
                    createInPredicate(root, "type", payload.type()),
                    createInPredicate(root, "status", payload.status()),
                    createDateTimeBetweenPredicate(criteriaBuilder, root, "closeDt",
                            payload.closeDtFrom(), payload.closeDtTo()),
                    createContractorPredicate(criteriaBuilder, root, payload.contractorSearchValue())

            );

            List<Predicate> predicates = predicateStream
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            predicates.add(criteriaBuilder.isTrue(root.get("isActive")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }

    /**
     * Util method to create an equal predicate for a given field and value.
     * @param cb The {@link CriteriaBuilder} instance.
     * @param root The root entity {@link Root}.
     * @param fieldName The name of the field in database.
     * @param value The value to compare against.
     * @return A {@link Predicate} representing the equal comparison, or null if value is null.
     */
    private static Predicate createEqualPredicate(CriteriaBuilder cb, Root<Deal> root, String fieldName, Object value) {
        return value != null ? cb.equal(root.get(fieldName), value) : null;
    }

    /**
     * Util method to create a like predicate for a given field and value.
     * @param cb The {@link CriteriaBuilder} instance.
     * @param root The root entity {@link Root}.
     * @param fieldName The name of the field in database.
     * @param value The value to match against.
     * @return A {@link Predicate} representing the like comparison, or null if value is null.
     */
    private static Predicate createLikePredicate(CriteriaBuilder cb, Root<Deal> root, String fieldName, String value) {
        return value != null ? cb.like(root.get(fieldName), WildcatEnhancer.enhanceWithWildcatMatching(value)) : null;
    }

    private static Predicate createDateBetweenPredicate(CriteriaBuilder cb, Root<Deal> root, String fieldName,
                                                    LocalDate dateFrom, LocalDate dateTo) {
        return dateFrom != null && dateTo != null ? cb.between(root.get(fieldName), dateFrom, dateTo) : null;
    }

    private static Predicate createDateTimeBetweenPredicate(CriteriaBuilder cb, Root<Deal> root, String fieldName,
                                                            LocalDateTime dateFrom, LocalDateTime dateTo) {
        return dateFrom != null && dateTo != null ? cb.between(root.get(fieldName), dateFrom, dateTo) : null;
    }

    private static Predicate createInPredicate(Root<Deal> root, String fieldName,
                                               List<?> value) {
        return value != null && !value.isEmpty() ? root.get(fieldName).in(value) : null;
    }

    private static Predicate createContractorPredicate(CriteriaBuilder cb, Root<Deal> root, String searchValue) {
        if (searchValue == null) {
            return null;
        }

        Join<Deal, DealContractor> contractor = root.join("dealContractors");
        Join<DealContractor, DealContractorRole> contractorRole = contractor.join("dealContractorRoles");

        List<Predicate> orPredicates = new ArrayList<>();
        try {
            UUID uuid = UUID.fromString(searchValue);
            orPredicates.add(cb.equal(contractor.get("contractorId"), uuid.toString()));
        } catch (IllegalArgumentException ignored) {
            orPredicates.add(cb.like(contractor.get("name"), WildcatEnhancer.enhanceWithWildcatMatching(searchValue)));
            orPredicates.add(cb.like(contractor.get("inn"), WildcatEnhancer.enhanceWithWildcatMatching(searchValue)));
        }

        Predicate groupPredicate = cb.or(
                cb.equal(contractorRole.get("contractorRole").get("category"), "BORROWER"),
                cb.equal(contractorRole.get("contractorRole").get("category"), "WARRANTY")
        );
        return cb.and(groupPredicate, cb.or(orPredicates.toArray(new Predicate[0])));
    }

}
