package com.cmms.demo.specification;

import com.cmms.demo.domain.CustomerPOJO;
import com.cmms.demo.domain.Invoice;
import com.cmms.demo.domain.PaymentStatus;
import com.cmms.demo.domain.ProjectPOJO;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.LinkedList;
import java.util.List;

public class DebtSpecs {
    public static Specification<Invoice> getListDebt(String customerName, Long statusId){
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();
            Join<Invoice, ProjectPOJO> join = root.join("project");
            if (customerName != null){
                Join<ProjectPOJO, CustomerPOJO> join1 = join.join("customer");
                predicates.add(criteriaBuilder.like(join1.get("name"), "%"+customerName+"%"));
            }
            if(statusId != null){
                Join<Invoice, PaymentStatus> join1 = root.join("paymentStatus");
                predicates.add(criteriaBuilder.equal(join1.get("status_id"), statusId));
            }
            predicates.add(criteriaBuilder.greaterThan(root.get("total_amount"),root.get("advance_amount")));
            criteriaQuery.orderBy(criteriaBuilder.desc(root.get("remain_amount")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
