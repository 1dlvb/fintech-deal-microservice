package com.fintech.deal.repository.specification;

import com.fintech.deal.model.Deal;
import com.fintech.deal.model.DealContractor;
import com.fintech.deal.model.DealContractorRole;
import com.fintech.deal.model.DealType;
import com.fintech.deal.payload.SearchDealPayload;
import com.fintech.deal.util.WildcatEnhancer;
import com.onedlvb.jwtlib.util.Roles;
import com.onedlvb.jwtlib.util.SecurityUtil;
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

/**
 * Utility class for creating JPA Specifications for querying {@link Deal} entities.
 * @author Matushkin Anton
 */
public final class DealSpecification {

    private DealSpecification() {}

    /**
     * This method constructs various predicates based on the payload's fields.
     * @param payload The payload containing search criteria.
     * @return A {@link Specification} for {@link Deal} that can be used in a query.
     */
    public static Specification<Deal> searchDeals(SearchDealPayload payload) {
        SearchDealPayload finalPayload = roleBasedPayloadModification(payload);
        if (finalPayload == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.disjunction();
        }
        return (root, query, criteriaBuilder) -> {
            Stream<Predicate> predicateStream = Stream.of(
                    criteriaBuilder.isTrue(root.get("isActive")),
                    createEqualPredicate(criteriaBuilder, root, "id", finalPayload.getId()),
                    createEqualPredicate(criteriaBuilder, root, "description", finalPayload.getDescription()),
                    createLikePredicate(criteriaBuilder, root, "agreementNumber", finalPayload.getAgreementNumber()),
                    createDateBetweenPredicate(criteriaBuilder, root, "agreementDate",
                            finalPayload.getAgreementDateFrom(), finalPayload.getAgreementDateTo()),
                    createDateBetweenPredicate(criteriaBuilder, root, "availabilityDate",
                            finalPayload.getAvailabilityDateFrom(), finalPayload.getAvailabilityDateTo()),
                    createInPredicate(root, "type", finalPayload.getType()),
                    createInPredicate(root, "status", finalPayload.getStatus()),
                    createDateTimeBetweenPredicate(criteriaBuilder, root, "closeDt",
                            finalPayload.getCloseDtFrom(), finalPayload.getCloseDtTo()),
                    createContractorPredicate(criteriaBuilder, root, finalPayload.getContractorSearchValue())

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

    /**
     * Creates a predicate for a BETWEEN comparison of a specified date field with given range.
     * @param cb The {@link CriteriaBuilder} instance used to create the predicate.
     * @param root The root entity {@link Root}.
     * @param fieldName The name of the date field in the entity.
     * @param dateFrom The start of the date range.
     * @param dateTo The end of the date range.
     * @return A {@link Predicate} representing the BETWEEN comparison, or null if dates are null.
     */
    private static Predicate createDateBetweenPredicate(CriteriaBuilder cb, Root<Deal> root, String fieldName,
                                                    LocalDate dateFrom, LocalDate dateTo) {
        return dateFrom != null && dateTo != null ? cb.between(root.get(fieldName), dateFrom, dateTo) : null;
    }

    /**
     * Creates a predicate for a BETWEEN comparison of a specified date-time field with given range.
     * @param cb The {@link CriteriaBuilder} instance used to create the predicate.
     * @param root The root entity {@link Root}.
     * @param fieldName The name of the date-time field in the entity.
     * @param dateFrom The start of the date-time range.
     * @param dateTo The end of the date-time range.
     * @return A {@link Predicate} representing the BETWEEN comparison, or null if date-times are null.
     */
    private static Predicate createDateTimeBetweenPredicate(CriteriaBuilder cb, Root<Deal> root, String fieldName,
                                                            LocalDateTime dateFrom, LocalDateTime dateTo) {
        return dateFrom != null && dateTo != null ? cb.between(root.get(fieldName), dateFrom, dateTo) : null;
    }

    /**
     * Creates a predicate for an IN comparison of a specified field with a list of values.
     * @param root The root entity {@link Root}.
     * @param fieldName The name of the field in the entity.
     * @param value The list of values to compare against.
     * @return A {@link Predicate} representing the IN comparison, or null if the list is null or empty.
     */
    private static Predicate createInPredicate(Root<Deal> root, String fieldName,
                                               List<?> value) {
        return value != null && !value.isEmpty() ? root.get(fieldName).in(value) : null;
    }

    /**
     * Creates a predicate to match contractors based on the search value.
     * @param cb The {@link CriteriaBuilder} instance used to create the predicate.
     * @param root The root entity {@link Root}.
     * @param searchValue The value to search for in contractors' fields.
     * @return A {@link Predicate} representing the contractor search criteria, or null if searchValue is null.
     */
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

    /**
     * Modifies the given payload based on the roles of the user.
     * If the user has SUPERUSER or DEAL_SUPERUSER roles, the payload is returned as is.
     * If the payload is empty, it returns a payload with type based on the user roles.
     * If the payload is not empty except for type, it returns null.
     * Otherwise, it handles the payload based on the user's roles and returns the modified payload.
     *<p>
     * @param payload The original payload containing search criteria.
     * @return The modified payload or null based on the user's roles and payload content.
     */
    private static SearchDealPayload roleBasedPayloadModification(SearchDealPayload payload) {
        boolean hasSuperuser = SecurityUtil.hasRole(Roles.SUPERUSER);
        boolean hasDealSuperuser = SecurityUtil.hasRole(Roles.DEAL_SUPERUSER);

        if (hasSuperuser || hasDealSuperuser) {
            return payload;
        }

        if (payload.isEmpty()) {
            return handleEmptyExceptTypePayload();
        }
        if (!payload.isEmptyExceptType()) {
            return null;
        }
        boolean hasCreditRole = SecurityUtil.hasRole(Roles.CREDIT_USER);
        boolean hasOverdraftRole = SecurityUtil.hasRole(Roles.OVERDRAFT_USER);

        return handleNonEmptyPayload(payload, hasCreditRole, hasOverdraftRole);
    }

    /**
     * Handles the case where the payload is empty except for type.
     * It creates a new payload with the type based on the user's roles.
     * <p>
     * @return The modified payload or null if the user has no relevant roles.
     */
    private static SearchDealPayload handleEmptyExceptTypePayload() {
        boolean hasCreditRole = SecurityUtil.hasRole(Roles.CREDIT_USER);
        boolean hasOverdraftRole = SecurityUtil.hasRole(Roles.OVERDRAFT_USER);

        List<DealType> types = new ArrayList<>();
        if (hasCreditRole) {
            types.add(DealType.builder().id("CREDIT").build());
        }
        if (hasOverdraftRole) {
            types.add(DealType.builder().id("OVERDRAFT").build());
        }

        if (!types.isEmpty()) {
            return SearchDealPayload.builder().type(types).build();
        }
        return null;
    }

    /**
     * Handles the case where the payload is not empty except for type.
     * It checks the type against the user's roles and returns the payload if it matches.
     * <p>
     * @param payload The original payload containing search criteria.
     * @param hasCreditRole Indicates if the user has the CREDIT_USER role.
     * @param hasOverdraftRole Indicates if the user has the OVERDRAFT_USER role.
     * @return The modified payload or null based on the user's roles and payload content.
     */
    private static SearchDealPayload handleNonEmptyPayload(SearchDealPayload payload, boolean hasCreditRole, boolean hasOverdraftRole) {
        List<DealType> types = payload.getType();

        if (types == null) {
            return null;
        }

        if (hasCreditRole && hasOverdraftRole) {
            boolean hasRelevantType = types.stream().allMatch(type ->
                    "CREDIT".equals(type.getId()) || "OVERDRAFT".equals(type.getId())
            );
            payload = SearchDealPayload.builder().type(types).build();
            return hasRelevantType ? payload : null;
        } else if (hasCreditRole) {
            boolean hasCreditType = types.stream().allMatch(type -> "CREDIT".equals(type.getId()));
            return hasCreditType ? payload : null;
        } else if (hasOverdraftRole) {
            boolean hasOverdraftType = types.stream().allMatch(type -> "OVERDRAFT".equals(type.getId()));
            return hasOverdraftType ? payload : null;
        }

        return null;
    }

}
