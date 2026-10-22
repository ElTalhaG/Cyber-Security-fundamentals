# Question 15 - Roles and Attributes

## Task

Classify a policy combining professor/student permissions, ownership of grades, and a time-of-day restriction.

## Answer: D, best descriptive match; C is expressive enough too

D best describes the policy as presented: role-based permissions for professors and students are combined with attribute/context conditions such as university hours.

Role membership determines which operations might be granted. Time constrains when any grant is effective. The own-grades condition also needs a relationship between the requesting student's identity and the grade record's owner; role membership alone does not distinguish one student's record from another's.

For example, a student's read could require all of: role = Student, requested record belongs to that student, and current time is within the allowed interval. A professor's upload similarly requires its role permission and the time condition.

**Expressiveness caveat:** ABAC can use role as a subject attribute and can express this entire policy. C is therefore technically capable, rather than inherently wrong. D is the most explicit match among the options because it names the combination that the wording emphasizes. The paper does not specify an implementation that proves separate RBAC and ABAC engines exist.

## Assessing the alternatives

- A: identity-based grants alone do not describe the role and environmental conditions.
- B: plain role membership alone does not capture the time or record-ownership constraints.
- C: technically expressive enough; the distinction is best description, not capability.
- D: the most explicit description of roles combined with contextual restrictions.

## Remember

A role can be an ABAC attribute. Do not claim ABAC cannot represent a policy merely because roles appear in it.

## Sources and scope

Original 2025 exam, PDF page 8. This is a reasoned practice solution, not an official marking key.

[NIST SP 800-162 overview](https://www.nist.gov/publications/guide-attribute-based-access-control-abac-definition-and-considerations-1) defines ABAC over subject, object, operation, and environmental attributes.
