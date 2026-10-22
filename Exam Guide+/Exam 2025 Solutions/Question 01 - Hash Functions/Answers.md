# Question 1 - Hash Functions

## Task

Identify the incorrect statement about cryptographic hash functions.

## Answer: C

Collision resistance requires it to be computationally infeasible to find distinct messages with the same digest. Option C asserts that finding such a pair should be easy, which reverses this property.

A collision is not the same problem as a preimage. For a preimage attack, a target digest is supplied and the attacker searches for a matching input. For a collision attack, the attacker can choose both inputs. Collisions must exist for a fixed-length hash over a larger input space, but finding one should be infeasible for a secure hash at an appropriate security level.

## Assessing the alternatives

- A is a required efficiency property: ordinary users can compute a digest.
- B describes preimage resistance, so it is not the incorrect statement.
- D uses standard names for a hash output.

## Remember

Watch for **incorrect** in the question. Secure hashes are easy to compute and hard to invert or collide.

## Sources and scope

Original 2025 exam, PDF page 2. This is a reasoned practice solution, not an official marking key.
